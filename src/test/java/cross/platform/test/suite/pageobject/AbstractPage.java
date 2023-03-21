package cross.platform.test.suite.pageobject;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.support.PageFactory;

public abstract class AbstractPage {
    
    private final AppiumDriver appiumDriver;
    
    public AbstractPage(AppiumDriver appiumDriver) {
        PageFactory.initElements(new AppiumFieldDecorator(appiumDriver), this);
        this.appiumDriver = appiumDriver;
    }
    
    protected AppiumDriver getDriver() {
        return this.appiumDriver;
    }
    
    public void back() {
        this.appiumDriver.navigate().back();
    }
}
