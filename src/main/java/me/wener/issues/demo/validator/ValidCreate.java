package me.wener.issues.demo.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.groups.Default;
import me.wener.issues.demo.validator.Groups.Create;
import org.springframework.validation.annotation.Validated;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 08/06/2017
 */
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Validated({Default.class, Create.class})
public @interface ValidCreate {

}
