package cross.platform.test.suite.test.helper;

import cross.platform.test.suite.configuration.manager.ReportManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

public interface ReportHelper {

    Logger log = LoggerFactory.getLogger(ReportHelper.class);

    ReportManager getReportManager();

    @BeforeClass
    default void reportHelperBeforeClass(ITestContext context) {
        String className = this.getClass().getSimpleName();
        String testName = context.getName();
//        log.info("{} - {}", testName, className);
        this.getReportManager().createClassReport(className, testName);
    }

    @BeforeMethod
    default void reportHelperBeforeMethod(ITestResult result) {
        ITestNGMethod method = result.getMethod();
        String className = method.getRealClass().getSimpleName();
        String testName = result.getTestName();
        String methodName = method.getMethodName();
        String description = method.getDescription();
//        log.info("{} - {} - {} - {}", testName, className, methodName, description);
        this.getReportManager().createMethodReport(methodName, description, className, testName);
        this.getReportManager().info("Description: " + description);
    }

    @AfterSuite
    default void reportHelperAfterSuite() {
        log.info("Writing extent report output to reporters...");
        this.getReportManager().flush();
    }
}
