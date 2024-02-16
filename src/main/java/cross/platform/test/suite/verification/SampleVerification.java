package cross.platform.test.suite.verification;

import cross.platform.test.suite.context.POCEventContext;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

@Slf4j
public class SampleVerification extends BaseVerification {
    
    private final POCEventContext context;

    public SampleVerification(POCEventContext context) {
        this.context = context;
    }

    @Test
    public void verification1() {
        log.info("Running Sample verification 1 for event {}...", context.getEventName());
    }

    @Test
    public void verification2() {
        log.info("Running Sample verification 2 for event {}...", context.getEventName());
    }

    @Test
    public void verification3() {
        log.info("Running Sample verification 3 for event {}...", context.getEventName());
    }
}
