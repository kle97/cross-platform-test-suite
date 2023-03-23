package cross.platform.test.suite.annotation;

import cross.platform.test.suite.constant.When;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Screenshot {
    
    When when() default When.BOTH;
}
