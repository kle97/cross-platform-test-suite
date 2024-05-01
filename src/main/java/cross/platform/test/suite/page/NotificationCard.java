package cross.platform.test.suite.page;

import cross.platform.test.suite.common.DriverUtil;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.Widget;
import org.openqa.selenium.WebElement;

public class NotificationCard extends Widget {

    @AndroidFindBy(id = "card-title-id")
    private WebElement title;
    
    @AndroidFindBy(id = "card-value-id")
    private WebElement value;
    
    protected NotificationCard(WebElement element) {
        super(element);
    }
    
    public String getTitle() {
        return DriverUtil.getText(title);
    }

    public String getValue() {
        return DriverUtil.getText(value);
    }
}
