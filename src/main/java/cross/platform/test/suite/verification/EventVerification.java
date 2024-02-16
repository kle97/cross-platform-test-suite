package cross.platform.test.suite.verification;

import cross.platform.test.suite.context.POCEventContext;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

@Slf4j
public class EventVerification {

    private final POCEventContext context;

    public EventVerification(POCEventContext context) {
        this.context = context;
    }

    @Test
    public void verification1() {
        log.info("Running Event verification 1 for event {}...", context.getEventName());
    }

    @Test
    public void verification2() {
        log.info("Running Event verification 2 for event {}...", context.getEventName());
    }

    @Test
    public void verification3() {
        log.info("Running Event verification 3 for event {}...", context.getEventName());
    }
}
