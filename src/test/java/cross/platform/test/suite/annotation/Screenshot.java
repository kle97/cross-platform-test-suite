package cross.platform.test.suite.annotation;

import cross.platform.test.suite.constant.Position;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Screenshot {
    
    Position position() default Position.BOTH;
}
