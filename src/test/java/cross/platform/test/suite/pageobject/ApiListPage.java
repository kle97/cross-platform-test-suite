package cross.platform.test.suite.pageobject;

import cross.platform.test.suite.utility.DriverUtil;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.WebElement;

public class ApiListPage extends AbstractPage {
    
    @AndroidFindBy(xpath = "//*[@resource-id='android:id/action_bar']/android.widget.TextView")
    private WebElement title;
    
    @AndroidFindBy(xpath = "(//*[@resource-id='android:id/text1'])[2]")
    private WebElement accessibilityTab;
    
    public ApiListPage(AppiumDriver appiumDriver) {
        super(appiumDriver);
    }
    
    public String getTitle() {
        return DriverUtil.getText(this.title);
    }
    
    public String getAccessibilityTab() {
        return DriverUtil.getText(this.accessibilityTab);
    }
    
    public void clickAccessibilityTab() {
        this.accessibilityTab.click();
    }
}
