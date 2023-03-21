package cross.platform.test.suite.pageobject;

import io.appium.java_client.AppiumDriver;

public class PageObjectFactory {
    
    public static ApiListPage getApiListPage(AppiumDriver appiumDriver) {
        return new ApiListPage(appiumDriver);
    }
}
