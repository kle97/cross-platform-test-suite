package cross.platform.test.suite.testcase;

import cross.platform.test.suite.configuration.VerificationListener;
import lombok.extern.slf4j.Slf4j;
import org.testng.TestNG;
import org.testng.xml.XmlSuite;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

@Slf4j
public abstract class BaseTest {

    private final long nanosecond = System.nanoTime();
    private final String toString = nanosecond + "@" + getClass().getName();

    protected void runVerifications(Object[] verifications) {
        XmlSuite.ParallelMode parallelMode = XmlSuite.ParallelMode.getValidParallel(System.getProperty("verifications.parallel", "none"));
        TestNG testNG = new TestNG(false);
        FactoryTest.setTestClasses(verifications);
        testNG.setTestClasses(new Class[] { FactoryTest.class });
        testNG.setVerbose(0);
        testNG.setParallel(parallelMode);
        testNG.setGroupByInstances(!parallelMode.equals(XmlSuite.ParallelMode.METHODS));
        testNG.addListener(new VerificationListener());
        testNG.shouldUseGlobalThreadPool(true);
        testNG.shareThreadPoolForDataProviders(true);
        testNG.setThreadCount(Thread.activeCount());
        testNG.run();
    }

    protected Object[][] toData(Stream<Object> streams) {
        return streams.map(o -> new Object[] { o }).toArray(Object[][]::new);
    }
    
    protected Object[][] toData(List<Object> lists) {
        return lists.stream().map(o -> new Object[] { o }).toArray(Object[][]::new);
    }
    
    protected <T> Object[] applyContext(Function<T, Object>[] verifications, T argument) {
        return Arrays.stream(verifications).map(v -> v.apply(argument)).toArray(Object[]::new);
    }

    protected <T, R> Object[] applyContext(BiFunction<T, R, Object>[] verifications, T argument1, R argument2) {
        return Arrays.stream(verifications).map(v -> v.apply(argument1, argument2)).toArray(Object[]::new);
    }

    @Override
    public String toString() {
        return toString;
    }
}
