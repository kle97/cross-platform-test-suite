package cross.platform.test.suite.verification;

import cross.platform.test.suite.common.Reporter;
import cross.platform.test.suite.common.SoftAssertJ;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.BeforeClass;

@Slf4j
public abstract class BaseVerification {

    private final String toString = System.nanoTime() + "@" + getClass().getName();
    
    public BaseVerification() {
    }

    @BeforeClass
    public void beforeClass() {
        Reporter.addReport(getClass().getSimpleName());
    }

    @Override
    public String toString() {
        return toString;
    }

    public SoftAssertJ softAssert() {
        return SoftAssertJ.getInstance();
    }
}
