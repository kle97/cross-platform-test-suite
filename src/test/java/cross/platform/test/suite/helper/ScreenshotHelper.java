package cross.platform.test.suite.helper;

import cross.platform.test.suite.annotation.Screenshot;
import cross.platform.test.suite.configuration.manager.DriverManager;
import cross.platform.test.suite.configuration.manager.ReportManager;
import cross.platform.test.suite.constant.Position;
import cross.platform.test.suite.utility.ScreenUtil;
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
        if (canScreenshotBefore(method)) {
            String methodName = method.getName();
            String screenshotTitle = "before" + methodName.substring(0, 1).toUpperCase() + methodName.substring(1);
            this.takeScreenshot(screenshotTitle);
        }
    }

    @AfterMethod
    default void screenshotHelperAfterMethod(Method method) {
        if (canScreenshotAfter(method)) {
            String methodName = method.getName();
            String screenshotTitle = "after" + methodName.substring(0, 1).toUpperCase() + methodName.substring(1);
            this.takeScreenshot(screenshotTitle);
        }
    }

    @Test(enabled = false)
    default void takeScreenshot(String screenshotTitle) {
        File screenshotFile = ScreenUtil.saveScreenshot(getDriverManager().getDriver(), screenshotTitle);
        if (screenshotFile != null) {
            getReportManager().addScreenshot(screenshotFile.getAbsolutePath(), screenshotTitle);
        }
    }
    
    private boolean canScreenshotBefore(Method method) {
        if (method == null) {
            return false;
        }
        Screenshot methodAnnotation = method.getDeclaredAnnotation(Screenshot.class);
        if (methodAnnotation == null) {
            methodAnnotation = getClass().getAnnotation(Screenshot.class);
        }
        return methodAnnotation != null && (methodAnnotation.position().equals(Position.BOTH) || methodAnnotation.position().equals(Position.BEFORE));
    }

    private boolean canScreenshotAfter(Method method) {
        if (method == null) {
            return false;
        }
        Screenshot annotation = method.getDeclaredAnnotation(Screenshot.class);
        if (annotation == null) {
            annotation = getClass().getAnnotation(Screenshot.class);
        }
        return annotation != null && (annotation.position().equals(Position.BOTH) || annotation.position().equals(Position.AFTER));
    }
}
