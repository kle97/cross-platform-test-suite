package cross.platform.test.suite.verification;

import cross.platform.test.suite.common.BaseTest;
import lombok.extern.slf4j.Slf4j;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.xml.XmlTest;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class BaseVerification {

    private final long nanosecond = System.nanoTime();
    private final String toString = nanosecond + "@" + getClass().getName();
    
    private final BaseTest testCase;

    public BaseVerification(BaseTest testCase) {
        this.testCase = testCase;
    }
    
    @BeforeClass
    public void beforeClass(ITestContext testContext, XmlTest xmlTest) {
        invokeMethods(BeforeClass.class, testContext, xmlTest);
    }

    @AfterClass
    public void afterClass(ITestContext testContext, XmlTest xmlTest) {
        invokeMethods(AfterClass.class, testContext, xmlTest);
    }

    @BeforeMethod
    public void beforeMethod(ITestContext testContext, XmlTest xmlTest, Method method, Object[] objects, ITestResult result) {
        invokeMethods(BeforeMethod.class, testContext, xmlTest, method, result);
    }

    @AfterMethod
    public void afterMethod(ITestContext testContext, XmlTest xmlTest, Method method, Object[] objects, ITestResult result) {
        invokeMethods(AfterMethod.class, testContext, xmlTest, method, result);
    }

    public String toString() {
        return toString;
    }
    
    private void invokeMethods(Class<? extends Annotation> annotation, Object... objects) {
        for (Method method : testCase.getClass().getMethods()) {
            if (method.getAnnotation(annotation) != null) {
                try {
                    method.setAccessible(true);
                    List<Object> parameters = new ArrayList<>();
                    for (Class<?> parameterType : method.getParameterTypes()) {
                        Arrays.stream(objects)
                              .filter(o -> parameterType.isAssignableFrom(o.getClass()))
                              .findFirst().ifPresent(parameters::add);
                    }
                    method.invoke(testCase, parameters.toArray());
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
