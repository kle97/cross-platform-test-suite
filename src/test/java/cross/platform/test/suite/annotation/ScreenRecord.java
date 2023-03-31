package cross.platform.test.suite.annotation;

import cross.platform.test.suite.utility.ScreenUtil;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface ScreenRecord {
    
    int timeLimit() default ScreenUtil.DEFAULT_RECORDING_LIMIT_IN_SECONDS;
}
