package cross.platform.test.suite.verification;

import cross.platform.test.suite.common.Reporter;
import cross.platform.test.suite.common.SleepUtil;
import cross.platform.test.suite.context.POCEventContext;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Method;

@Slf4j
public class SampleVerification extends BaseVerification {
    
    private final POCEventContext context;

    public SampleVerification(POCEventContext context) {
        this.context = context;
    }

    @BeforeMethod
    public void beforeMethod(Method method) {
        Reporter.appendReport(method.getName() + "[" + context.getEventName() + "]");
    }

    @Test
    public void verifySample() {
        Reporter.info("Running Sample verification 1 for event {}...", context.getEventName());
        SleepUtil.sleep(1000);
        softAssert().as("sample [" + context.getEventName() + "]").assertThat("AA").isEqualTo("AA");
    }

    @Test(priority = 1)
    public void verifyCloseRange() {
        Reporter.info("Running Sample verification 2 for event {}...", context.getEventName());
        SleepUtil.sleep(1000);
        softAssert().as("close range [" + context.getEventName() + "]").assertThat("0.1").isEqualTo("0.1");
    }

    @Test(priority = 2)
    public void verifyOpenRange() {
        Reporter.info("Running Sample verification 3 for event {}...", context.getEventName());
        SleepUtil.sleep(1000);
        softAssert().as("open range [" + context.getEventName() + "]").assertThat("12").isEqualTo("12");
    }
}
