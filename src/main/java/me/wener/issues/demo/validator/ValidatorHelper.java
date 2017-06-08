package me.wener.issues.demo.validator;

import com.google.common.base.Preconditions;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.validation.Validator;
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

/**
 * Validation Helper
 *
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 2017/6/5
 */
public interface ValidatorHelper {

    /**
     * Patch hibernate-validator to support {@link ConstraintGroup}
     *
     * @see <a href="https://hibernate.atlassian.net/projects/HV/issues/HV-1355">HV-1355: Constraint annotation with
     * explicit groups</a>
     */
    // FIXME Not good, depends on internal
    static void patchHibernateValidator(Validator validator) {
        Holder.patchHibernateValidator(validator);
    }

    @Slf4j
    final class Holder {

        private static void patchHibernateValidator(Validator validator) {
            Preconditions.checkArgument(validator instanceof ValidatorImpl,
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
            // TODO Can not used original type
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
                        // Works for Oracle Java8, OpenJDK 8
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
