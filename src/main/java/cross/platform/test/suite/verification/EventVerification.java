package cross.platform.test.suite.verification;

import cross.platform.test.suite.common.Reporter;
import cross.platform.test.suite.common.SleepUtil;
import cross.platform.test.suite.common.SoftAssertion;
import cross.platform.test.suite.context.POCEventContext;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Method;

@Slf4j
public class EventVerification extends BaseVerification {

    private final POCEventContext context;

    public EventVerification(POCEventContext context) {
        this.context = context;
    }
    
    @BeforeMethod
    public void beforeMethod(Method method) {
        Reporter.appendReport(method.getName() + "[" + context.getEventName() + "]");
    }

    @Test
    public void checkEvent() {
        Reporter.info("Running Event verification 1 for event {}...", context.getEventName());
        SleepUtil.sleep(1000);
        SoftAssertion.as("event [" + context.getEventName() + "]").assertThat("BB").isEqualTo("XX");
    }

    @Test(priority = 1)
    public void verifyRange() {
        Reporter.info("Running Event verification 2 for event {}...", context.getEventName());
        SleepUtil.sleep(1000);
        SoftAssertion.as("range [" + context.getEventName() + "]").assertThat("10").isEqualTo("10");
    }

    @Test(priority = 2)
    public void verifyLimit() {
        Reporter.info("Running Event verification 3 for event {}...", context.getEventName());
        SleepUtil.sleep(1000);
        SoftAssertion.as("limit [" + context.getEventName() + "]").assertThat("99").isEqualTo("100");
    }
}
