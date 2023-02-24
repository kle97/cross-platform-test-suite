package cross.platform.test.suite.test.helper;

import cross.platform.test.suite.assertion.LoggingAssertion;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;

public interface AssertionHelper {

    LoggingAssertion getAssertion();

    @BeforeClass
    default void assertionHelperBeforeClass() {
        this.getAssertion().setCurrentLogger(LoggerFactory.getLogger(getClass()));
    }

    @AfterSuite(dependsOnMethods = "reportHelperAfterSuite")
    default void assertionHelperAfterSuite() {
        this.getAssertion().assertAll();
    }
}
