package cross.platform.test.suite.verification;

import cross.platform.test.suite.common.Reporter;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.BeforeClass;

@Slf4j
public abstract class BaseVerification {

    private final long nanosecond = System.nanoTime();
    private final String toString = nanosecond + "@" + getClass().getName();
    
    public BaseVerification() {
    }

    @BeforeClass
    public void beforeClass() {
        Reporter.addReport(getClass().getSimpleName());
    }

    public String toString() {
        return toString;
    }
}
