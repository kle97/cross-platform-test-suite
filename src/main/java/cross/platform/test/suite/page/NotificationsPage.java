package cross.platform.test.suite.page;

import cross.platform.test.suite.common.Direction;
import cross.platform.test.suite.common.SwiperUtil;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

public class NotificationsPage {
    
    @AndroidFindBy(id = "container-id")
    private WebElement notificationCardsContainer;

    @AndroidFindBy(id = "card-id")
    private List<NotificationCard> notificationCards;
    
    public NotificationsPage(WebDriver driver) {
        PageFactory.initElements(new AppiumFieldDecorator(driver), this);
    }
    
    public NotificationCard getNotification(String targetTitle) {
        return SwiperUtil.findElementInList(notificationCardsContainer, 
                                            notificationCards, 
                                            Direction.DOWN, 
                                            notificationCard -> notificationCard.getTitle().equals(targetTitle));
    }
    
}
