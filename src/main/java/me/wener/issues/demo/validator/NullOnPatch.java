package me.wener.issues.demo.validator;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import javax.validation.constraints.Null;
import me.wener.issues.demo.validator.Groups.Update;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 08/06/2017
 */
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
@Retention(RUNTIME)
@Documented
@Repeatable(NullOnPatch.List.class)
@ReportAsSingleViolation
@Constraint(validatedBy = {})
@Null
@ConstraintGroup(Update.class)
public @interface NullOnPatch {

    String message() default "{javax.validation.constraints.Null.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    @Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
    @Retention(RUNTIME)
    @Documented
    @interface List {

        NullOnPatch[] value();
    }
}
