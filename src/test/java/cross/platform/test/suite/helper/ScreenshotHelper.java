package cross.platform.test.suite.helper;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.model.Media;
import cross.platform.test.suite.annotation.Screenshot;
import cross.platform.test.suite.configuration.manager.DriverManager;
import cross.platform.test.suite.configuration.manager.ReportManager;
import cross.platform.test.suite.constant.When;
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
        String methodName = method.getName();
        Screenshot screenshotAnnotation = method.getDeclaredAnnotation(Screenshot.class);
        if (screenshotAnnotation != null && (screenshotAnnotation.when().equals(When.BOTH) || screenshotAnnotation.when().equals(When.BEFORE))) {
            String screenshotTitle = "before" + methodName.substring(0, 1).toUpperCase() + methodName.substring(1);
            this.takeScreenshot(screenshotTitle);
        }
    }

    @AfterMethod
    default void screenshotHelperAfterMethod(Method method) {
        Screenshot screenshotAnnotation = method.getDeclaredAnnotation(Screenshot.class);
        if (screenshotAnnotation != null && (screenshotAnnotation.when().equals(When.BOTH) || screenshotAnnotation.when().equals(When.AFTER))) {
            ExtentTest methodReport = getReportManager().findReport(method.getName());
            String methodName = method.getName();
            String screenshotTitle = "after" + methodName.substring(0, 1).toUpperCase() + methodName.substring(1);
            this.takeScreenshot(methodReport, screenshotTitle);
        }
    }

    @Test(enabled = false)
    default void takeScreenshot(String screenshotTitle) {
        takeScreenshot(null, screenshotTitle);
    }

    @Test(enabled = false)
    default void takeScreenshot(ExtentTest report, String screenshotTitle) {
        File screenshotFile = ScreenUtil.saveScreenshot(getDriverManager().getDriver(), screenshotTitle);
        if (screenshotFile != null) {
            if (report != null) {
                Media media = MediaEntityBuilder.createScreenCaptureFromPath(screenshotFile.getAbsolutePath(), screenshotTitle).build();
                report.info(media);
            } else {
                getReportManager().addScreenshot(screenshotFile.getAbsolutePath(), screenshotTitle);
            }
        }
    }
}
