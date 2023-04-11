package cross.platform.test.suite.configuration.listener;

import lombok.extern.slf4j.Slf4j;
import org.testng.ITestListener;
import org.testng.ITestNGListener;
import org.testng.ITestResult;

@Slf4j
public class TestNGListener implements ITestNGListener, ITestListener {

    @Override
    public void onTestFailure(ITestResult result) {
        Throwable throwable = result.getThrowable();
        if (throwable != null) {
            log.error("Test Failed: ", throwable);
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        Throwable throwable = result.getThrowable();
        if (throwable != null) {
            log.info("Test Skipped: {}", throwable.getMessage());
        }
    }
}