package cross.platform.test.suite.testcase;

import lombok.extern.slf4j.Slf4j;
import org.testng.IMethodInstance;
import org.testng.IMethodInterceptor;
import org.testng.ITestContext;
import org.testng.TestNG;
import org.testng.xml.XmlSuite;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

@Slf4j
public abstract class BaseTest {
    private static final IMethodInterceptor listener = new TestRunnerListener();
    
    private final long nanosecond = System.nanoTime();
    private final String toString = nanosecond + "@" + getClass().getName();

    @Override
    public String toString() {
        return toString;
    }

    protected void runVerifications(Object[] verifications) {
        runVerifications(verifications, XmlSuite.ParallelMode.NONE);
    }

    protected void runVerifications(Object[] verifications, XmlSuite.ParallelMode parallelMode) {
        FactoryClass.setVerifications(verifications);
        
        TestNG testNG = new TestNG();
        testNG.setTestClasses(new Class[] { FactoryClass.class });
        testNG.setVerbose(0);
        testNG.setGroupByInstances(true);
        testNG.setUseDefaultListeners(false);
        testNG.setParallel(parallelMode);
        testNG.addListener(listener);
        testNG.run();
    }
    
    protected <T> Object[] applyContext(Function<T, Object>[] verifications, T argument) {
        return Arrays.stream(verifications).map(v -> v.apply(argument)).toArray(Object[]::new);
    }

    protected <T, R> Object[] applyContext(BiFunction<T, R, Object>[] verifications, T argument1, R argument2) {
        return Arrays.stream(verifications).map(v -> v.apply(argument1, argument2)).toArray(Object[]::new);
    }
    
    static class TestRunnerListener implements IMethodInterceptor {

        @Override
        public List<IMethodInstance> intercept(List<IMethodInstance> methods, ITestContext context) {
            String filter = System.getProperty("tests", "");
            if (!filter.isEmpty()) {
                List<IMethodInstance> filteredMethods = new ArrayList<>();
                String[] tests = filter.split(",");
                for (IMethodInstance methodInstance : methods) {
                    if (Arrays.stream(tests).anyMatch(t -> methodInstance.getMethod().getMethodName().equals(t))) {
                        filteredMethods.add(methodInstance);
                    }
                }
                methods = filteredMethods;
            }
            
            methods.sort(Comparator.comparing(m -> m.getInstance().toString()));
            return methods;
        }
    }
}
