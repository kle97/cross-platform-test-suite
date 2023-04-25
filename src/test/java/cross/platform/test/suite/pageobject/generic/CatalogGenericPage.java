package cross.platform.test.suite.pageobject.generic;

import com.aventstack.extentreports.ExtentTest;
import cross.platform.test.suite.constant.Direction;
import cross.platform.test.suite.model.Product;
import cross.platform.test.suite.pageobject.CatalogPage;
import cross.platform.test.suite.service.POMFactory;
import cross.platform.test.suite.utility.DriverUtil;
import cross.platform.test.suite.utility.SwiperUtil;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidBy;
import io.appium.java_client.pagefactory.AndroidFindAll;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

public class CatalogGenericPage extends TopBarNavigationGeneric implements CatalogPage {

    @AndroidFindBy(xpath = "//android.view.ViewGroup[@content-desc='container header']/android.widget.TextView")
    private WebElement title;

    @AndroidFindBy(xpath = "//android.widget.ScrollView")
    private WebElement productContainer;

    @AndroidFindAll(@AndroidBy(xpath = "//android.widget.TextView[@content-desc='store item text']"))
    private List<WebElement> productNameList;

    @AndroidFindAll(@AndroidBy(xpath = "//android.widget.TextView[@content-desc='store item price']"))
    private List<WebElement> productPriceList;

    public CatalogGenericPage(AppiumDriver appiumDriver, POMFactory pomFactory) {
        super(appiumDriver, pomFactory);
    }

    @Override
    public String getCatalogTitle() {
        return DriverUtil.getText(this.title);
    }

    @Override
    public void clickProduct(String productName) {
        WebElement element = SwiperUtil.findElementInScrollableContainerWithText(driver(), productContainer, productNameList,
                                                                                 productName, Direction.DOWN);
        element.click();
    }

    @Override
    public List<Product> getProductList(ExtentTest report) {
        List<Product> productList = new ArrayList<>();
        for (int i = 0; i < SwiperUtil.DEFAULT_MAX_SCROLL_SEARCH; i++) {
            this.takeScreenshot(report, "getProductList" + i);
            for (int j = 0; j < productPriceList.size(); j++) {
                String productName = DriverUtil.getText(productNameList.get(j));
                String productPrice = DriverUtil.getText(productPriceList.get(j));
                productList.add(new Product(productName, productPrice));
            }

            String beforeScrollingPageSource = DriverUtil.getPageSource(driver());
            SwiperUtil.scrollByElementSizePercentage(driver(), productContainer, Direction.DOWN, 0.93);
            if (DriverUtil.getPageSource(driver()).equals(beforeScrollingPageSource)) {
                break;
            }
        }
        return productList;
    }

    @Override
    public void scrollToTop() {
        for (int i = 0; i < SwiperUtil.DEFAULT_MAX_SCROLL_SEARCH; i++) {
            String beforeScrollingPageSource = DriverUtil.getPageSource(driver());
            SwiperUtil.scrollByElementSizePercentage(driver(), productContainer, Direction.UP, 0.93);
            if (DriverUtil.getPageSource(driver()).equals(beforeScrollingPageSource)) {
                break;
            }
        }
    }
}