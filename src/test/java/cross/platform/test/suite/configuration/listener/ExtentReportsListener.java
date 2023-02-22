package cross.platform.test.suite.configuration.listener;

import cross.platform.test.suite.configuration.manager.DriverManager;
import cross.platform.test.suite.configuration.manager.ReportManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.annotations.Guice;

import javax.inject.Inject;

@Slf4j
@Guice
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class ExtentReportsListener implements ITestListener {
    
    private final DriverManager driverManager;
    private final ReportManager reportManager;

    @Override
    public void onTestStart(ITestResult result) {
        ITestNGMethod method = result.getMethod();
        String testName = result.getTestName();
        String className = method.getRealClass().getSimpleName();
        String methodName = method.getMethodName();
        String description = method.getDescription();
        log.info("{} - {} - {} - {}", testName, className, methodName, description);
        this.reportManager.createMethodReport(methodName, description, className, testName);
    }
    
    @Override
    public void onTestFailure(ITestResult result) {
    }

    @Override
    public void onFinish(ITestContext context) {
    }
    
    
}
