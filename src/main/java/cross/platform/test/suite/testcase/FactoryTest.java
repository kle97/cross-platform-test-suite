package cross.platform.test.suite.testcase;

import org.testng.annotations.Factory;

public class FactoryTest {
    
    private static final ThreadLocal<Object[]> testClasses = new ThreadLocal<>();
    
    public static void setTestClasses(Object[] classes) {
        testClasses.set(classes);
    }
    
    @Factory
    public Object[] factory() {
        return testClasses.get();
    }
}
