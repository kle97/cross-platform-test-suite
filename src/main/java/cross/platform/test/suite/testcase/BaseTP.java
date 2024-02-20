package cross.platform.test.suite.testcase;

import cross.platform.test.suite.common.Reporter;
import cross.platform.test.suite.common.SoftAssertion;
import cross.platform.test.suite.configuration.TestListener;
import org.testng.TestNG;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.xml.XmlSuite;

public abstract class BaseTP extends BaseTest {
    
    protected void runTests(Object... tests) {
        XmlSuite.ParallelMode parallelMode = XmlSuite.ParallelMode.getValidParallel(System.getProperty("tests.parallel", "none"));
        TestNG testNG = new TestNG(false);
        FactoryTest.setTestClasses(tests);
        testNG.setTestClasses(new Class[] { FactoryTest.class });
        testNG.setVerbose(0);
        testNG.setParallel(parallelMode);
        testNG.setGroupByInstances(!parallelMode.equals(XmlSuite.ParallelMode.METHODS));
        testNG.addListener(new TestListener());
        testNG.shouldUseGlobalThreadPool(true);
        testNG.shareThreadPoolForDataProviders(true);
        testNG.setThreadCount(Thread.activeCount());
        testNG.run();
    }
    
    @BeforeSuite
    public void beforeSuite() {
        Reporter.startReport();
    }

    @AfterSuite
    public void afterSuite() {
        Reporter.flush();
        try {
            SoftAssertion.assertAll();
        } catch (Error e) {
            
        }
    }
}
