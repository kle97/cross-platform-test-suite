package cross.platform.test.suite.pageobject;

import cross.platform.test.suite.pageobject.factory.POMFactory;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.support.PageFactory;

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
}
