package cross.platform.test.suite.pageobject;

import cross.platform.test.suite.configuration.manager.DriverManager;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.support.PageFactory;

public abstract class AbstractPage {
    
    private final DriverManager driverManager;
    private AppiumDriver appiumDriver;
    
    public AbstractPage(DriverManager driverManager) {
        this.driverManager = driverManager;
    }
    
    public void init() {
        PageFactory.initElements(new AppiumFieldDecorator(getDriver()), this);
    }

    public void back() {
        this.appiumDriver.navigate().back();
    }
    
    protected AppiumDriver getDriver() {
        if (this.appiumDriver == null) {
            this.appiumDriver = driverManager.getDriver();
        }
        return this.appiumDriver;
    }
}
