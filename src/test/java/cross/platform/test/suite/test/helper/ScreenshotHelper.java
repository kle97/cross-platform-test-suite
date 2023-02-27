package cross.platform.test.suite.test.helper;

import cross.platform.test.suite.annotation.Screenshot;
import cross.platform.test.suite.configuration.manager.DriverManager;
import cross.platform.test.suite.configuration.manager.ReportManager;
import cross.platform.test.suite.utility.DriverUtil;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.lang.reflect.Method;

public interface ScreenshotHelper {

    DriverManager getDriverManager();
    ReportManager getReportManager();

    @BeforeMethod
    default void screenshotHelperBeforeMethod(Method method) {
        Screenshot screenshotAnnotation = method.getDeclaredAnnotation(Screenshot.class);
        if (screenshotAnnotation != null) {
            String methodName = method.getName();
            String screenshotTitle = "before" + methodName.substring(0, 1).toUpperCase() + methodName.substring(1);
            this.takeScreenshot(screenshotTitle);
        }
    }

    @AfterMethod
    default void screenshotHelperAfterMethod(Method method) {
        Screenshot screenshotAnnotation = method.getDeclaredAnnotation(Screenshot.class);
        if (screenshotAnnotation != null) {
            String methodName = method.getName();
            String screenshotTitle = "after" + methodName.substring(0, 1).toUpperCase() + methodName.substring(1);
            this.takeScreenshot(screenshotTitle);
        }
    }

    @Test(enabled = false)
    default void takeScreenshot(String screenshotTitle) {
        File screenshotFile = DriverUtil.saveScreenshot(getDriverManager().getDriver(), screenshotTitle);
        if (screenshotFile != null) {
            getReportManager().addScreenshot(screenshotFile.getAbsolutePath(), screenshotTitle);
        }
    }
}
