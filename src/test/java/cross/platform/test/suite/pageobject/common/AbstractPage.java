package cross.platform.test.suite.pageobject.common;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.model.Media;
import cross.platform.test.suite.constant.TestConst;
import cross.platform.test.suite.service.DriverManager;
import cross.platform.test.suite.service.POMFactory;
import cross.platform.test.suite.utility.ScreenUtil;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.support.PageFactory;

import java.io.File;

public abstract class AbstractPage implements Page {

    private final DriverManager driverManager;
    private final POMFactory pomFactory;
    private AppiumDriver appiumDriver;

    public AbstractPage(DriverManager driverManager, POMFactory pomFactory) {
        this.driverManager = driverManager;
        this.pomFactory = pomFactory;
    }
    
    @Override
    public void init() {
        PageFactory.initElements(new AppiumFieldDecorator(this.driverManager.getDriver()), this);
    }

    protected AppiumDriver driver() {
        if (this.appiumDriver == null) {
            this.appiumDriver = this.driverManager.getDriver();
        }
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