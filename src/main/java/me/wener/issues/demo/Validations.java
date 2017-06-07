package me.wener.issues.demo;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import com.google.common.base.Preconditions;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import javax.validation.Validator;
import javax.validation.constraints.NotNull;
import javax.validation.groups.Default;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import org.hibernate.validator.internal.engine.ValidatorImpl;
import org.hibernate.validator.internal.metadata.BeanMetaDataManager;
import org.hibernate.validator.internal.metadata.core.MetaConstraint;
import org.hibernate.validator.internal.metadata.provider.MetaDataProvider;
import org.hibernate.validator.internal.metadata.raw.BeanConfiguration;
import org.hibernate.validator.internal.metadata.raw.ConstrainedElement;
import org.joor.Reflect;
import org.springframework.validation.annotation.Validated;

/**
 * Validation Helper
 *
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 2017/6/5
 */
public interface Validations {

    /**
     * Patch hibernate-validator to support {@link ConstraintGroup}
     */
    // FIXME Not good, depends on internal
    static void patchHibernateValidator(Validator validator) {
        Holder.patchHibernateValidator(validator);
    }

    /**
     * Group used for HTTP PUT, update
     */
    interface Modify {

    }

    /**
     * Group used for HTTP POST
     */
    interface Create {

    }

    /**
     * Group used for HTTP PATCH, partial update, update nonnull field.
     */
    interface Patch {

    }

    @Target({ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @Validated({Default.class, Create.class})
    @interface ValidCreate {

    }

    @Target({ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @Validated({Default.class, Modify.class})
    @interface ValidModify {

    }

    @Target({ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @Validated({Default.class, Patch.class})
    @interface ValidPatch {

    }

    /**
     * Add group for constraint annotation
     */
    @Target({ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface ConstraintGroup {

        Class<?>[] value() default {};
    }

    @Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
    @Retention(RUNTIME)
    @Documented
    @Repeatable(NotNullOnChange.List.class)
    @ReportAsSingleViolation
    @Constraint(validatedBy = {})
    @NotNull
    @ConstraintGroup({Create.class, Modify.class})
    @interface NotNullOnChange {

        String message() default "{javax.validation.constraints.NotNull.message}";

        Class<?>[] groups() default {};

        Class<? extends Payload>[] payload() default {};

        @Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
        @Retention(RUNTIME)
        @Documented
        @interface List {

            NotNullOnChange[] value();
        }
    }


    @Slf4j
    final class Holder {


        private static void patchHibernateValidator(Validator validator) {
            Preconditions
                .checkArgument(validator instanceof ValidatorImpl,
                    "Validator type is not supported");
            BeanMetaDataManager manager = Reflect.on(validator).get("beanMetaDataManager");
            List<MetaDataProvider> metaDataProviders = Reflect.on(manager).get("metaDataProviders");
            metaDataProviders.replaceAll(Holder::patchMetaDataProvider);
        }

        @SuppressWarnings("unchecked")
        private static MetaDataProvider patchMetaDataProvider(MetaDataProvider provider) {
            Method m;
            try {
                m = MetaDataProvider.class
                    .getMethod("getBeanConfigurationForHierarchy", Class.class);
            } catch (NoSuchMethodException e) {
                throw new AssertionError(e);
            }

            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(MetaDataProvider.class);
            enhancer.setCallback((MethodInterceptor) (object, method, args, proxy) -> {
                if (method.equals(m)) {
                    return postProcessBeanConfiguration(
                        (List<BeanConfiguration>) proxy.invoke(provider, args),
                        (Class<?>) args[0]);
                }
                return proxy.invoke(provider, args);
            });
            return (MetaDataProvider) enhancer.create();
        }

        private static List<BeanConfiguration> postProcessBeanConfiguration(
            List<BeanConfiguration> configurations, Class<?> beanClass
        ) {
            for (BeanConfiguration configuration : configurations) {
                for (Object o : configuration.getConstrainedElements()) {
                    ConstrainedElement e = (ConstrainedElement) o;
                    for (MetaConstraint<?> metaConstraint : e.getConstraints()) {
                        ConstraintGroup group = metaConstraint
                            .getDescriptor().getAnnotationType()
                            .getAnnotation(ConstraintGroup.class);
                        if (group == null || group.value().length == 0) {
                            continue;
                        }
                        Class<?>[] extra = group.value();

                        // HIERARCHY Fix
                        // MetaDataBuilder#adaptOriginAndImplicitGroup
                        // Works for Oracle Java8
                        try {
                            Map<String, Object> values = Reflect
                                .on((Object) Reflect
                                    .on(metaConstraint.getDescriptor().getAnnotation()).get("h"))
                                .get("memberValues");
                            values.put("groups", extra);
                        } catch (Exception ex) {
                            log.error("Failed to fix groups: {}", ex.getMessage());
                        }

                        // Unmodifiable Set
                        Collection<Class<?>> set = Reflect.on(metaConstraint.getGroupList())
                            .get("c");
                        set.remove(Default.class);
                        Collections.addAll(set, extra);
                    }
                }
            }
            return configurations;
        }
    }
}
