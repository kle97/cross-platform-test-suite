package cross.platform.test.suite.pageobject;

import com.aventstack.extentreports.ExtentTest;
import cross.platform.test.suite.model.Product;

import java.util.List;

public interface CatalogPage {

    String getCatalogTitle();

    void clickProduct(String productName);

    List<Product> getProductList(ExtentTest report);

    void scrollToTop();
}
