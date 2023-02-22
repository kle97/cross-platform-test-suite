package cross.platform.test.suite.test;

import cross.platform.test.suite.configuration.manager.ReportManager;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

public interface ReportTest {

    org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ReportTest.class);

    ReportManager getReportManager();

    @BeforeClass
    default void beforeClass(ITestContext context) {
        String className = this.getClass().getSimpleName();
        String testName = context.getName();
        log.info("{} - {}", testName, className);
        this.getReportManager().createClassReport(className, testName);
    }

    @BeforeMethod
    default void beforeMethod(ITestResult result) {
        ITestNGMethod method = result.getMethod();
        String className = method.getRealClass().getSimpleName();
        String testName = result.getTestName();
        String methodName = method.getMethodName();
        String description = method.getDescription();
        log.info("{} - {} - {} - {}", testName, className, methodName, description);
        this.getReportManager().createMethodReport(methodName, description, className, testName);
        this.getReportManager().info("Description: " + description);
    }
}
