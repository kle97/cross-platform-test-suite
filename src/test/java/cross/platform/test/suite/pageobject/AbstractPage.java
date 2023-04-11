package cross.platform.test.suite.pageobject;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.model.Media;
import cross.platform.test.suite.constant.TestConst;
import cross.platform.test.suite.pageobject.factory.POMFactory;
import cross.platform.test.suite.utility.ScreenUtil;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.support.PageFactory;

import java.io.File;

public abstract class AbstractPage implements Page {

    private final AppiumDriver appiumDriver;
    private final POMFactory pomFactory;

    public AbstractPage(AppiumDriver appiumDriver, POMFactory pomFactory) {
        this.appiumDriver = appiumDriver;
        this.pomFactory = pomFactory;
        PageFactory.initElements(new AppiumFieldDecorator(appiumDriver), this);
    }

    protected AppiumDriver driver() {
        return this.appiumDriver;
    }

    protected POMFactory pomFactory() {
        return this.pomFactory;
    }

    protected void takeScreenshot(ExtentTest report, String screenshotTitle) {
        if (report != null) {
            File screenshotFile = ScreenUtil.saveScreenshot(driver(), TestConst.SCREENSHOT_PATH, screenshotTitle);
            if (screenshotFile != null) {
                Media media = MediaEntityBuilder.createScreenCaptureFromPath(screenshotFile.getAbsolutePath(), screenshotTitle).build();
                report.info(media);
            }
        }
    }
}