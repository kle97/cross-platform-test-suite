package cross.platform.test.suite.testcase;

import cross.platform.test.suite.common.Simulator;
import cross.platform.test.suite.context.POCEventContext;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;
import java.util.function.Function;

@Slf4j
public class SampleTest extends BaseTest {
    
    private final Simulator simulator;
    private final List<String> eventNames;
    private final Function<POCEventContext, Object>[] verifications;

    @SafeVarargs
    public SampleTest(Simulator simulator, List<String> eventNames, Function<POCEventContext, Object>... verifications) {
        this.simulator = simulator;
        this.eventNames = eventNames;
        this.verifications = verifications;
    }

    @BeforeClass
    public void beforeClass() {
        log.info("Before Sample test case!");
    }
    
    @DataProvider
    public Object[][] data() {
        return toData(eventNames.stream().map(POCEventContext::new));
    }
    
    @Test(dataProvider = "data")
    public void test(POCEventContext context) {
        simulator.setEvent(context.getEventName(), true);
        
        runVerifications(applyContext(verifications, context));
        
        simulator.setEvent(context.getEventName(), false);
    }

    @AfterClass
    public void afterClass() {
        log.info("After Sample test case!");
    }
}
