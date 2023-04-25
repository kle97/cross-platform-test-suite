package cross.platform.test.suite.pageobject;

import cross.platform.test.suite.service.POMFactory;
import io.appium.java_client.AppiumDriver;

public abstract class AndroidPage extends AbstractPage {
    
    public AndroidPage(AppiumDriver appiumDriver, POMFactory pomFactory) {
        super(appiumDriver, pomFactory);
    }
}
