package cross.platform.test.suite.pageobject;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.support.PageFactory;

public abstract class AbstractPage implements Page {
    
    private final AppiumDriver appiumDriver;
    
    public AbstractPage(AppiumDriver appiumDriver) {
        this.appiumDriver = appiumDriver;
        PageFactory.initElements(new AppiumFieldDecorator(appiumDriver), this);
    }
    
    protected AppiumDriver getDriver() {
        return this.appiumDriver;
    }
}
