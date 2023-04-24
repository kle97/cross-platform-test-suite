package cross.platform.test.suite.testcase;

import cross.platform.test.suite.exception.TestSuiteException;

import java.util.HashMap;
import java.util.Map;

public enum Product {
    
    BACKPACK("Sauce Labs Backpack"),
    BIKE_LIGHT("Sauce Labs Bike Light"),
    BOLT_TSHIRT("Sauce Labs Bolt T-Shirt"),
    FLEECE_JACKET("Sauce Labs Fleece Jacket"),
    ONESIE("Sauce Labs Onesie"),
    ALL_THE_THINGS("Test.allTheThings() T-Shirt");

    public final String value;

    private static final Map<String, Product> lookupMap = new HashMap<>();
    static {
        for (Product product: Product.values()) {
            lookupMap.put(product.value, product);
        }
    }

    Product(String value) {
        this.value = value;
    }

    public Product fromString(String product) {
        if (lookupMap.containsKey(product)) {
            return lookupMap.get(product);
        } else {
            throw new TestSuiteException(String.format("Product '%s' is not supported!", product));
        }
        
    }
}
