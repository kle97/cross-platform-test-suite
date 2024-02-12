package cross.platform.test.suite.testcase;

import cross.platform.test.suite.common.BaseTest;
import cross.platform.test.suite.common.Simulator;
import cross.platform.test.suite.context.POCEventContext;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Slf4j
public class SampleTestCase {
    
    public static Object[] createTests(Simulator simulator, List<String> eventNames, Function<POCEventContext, Object> verification) {
        List<Object> tests = new ArrayList<>();
        for (String eventName : eventNames) {
            POCEventContext context = new POCEventContext(eventName);
            
            tests.add(new BaseTest() {
                @Test
                public void beforeTestCase() {
                    simulator.setEvent(context.getEventName(), true);
                }
            });
            
            tests.add(verification.apply(context));

            tests.add(new BaseTest() {
                @Test
                public void beforeTestCase() {
                    simulator.setEvent(context.getEventName(), false);
                }
            });
        }
        return tests.toArray();
    }
}
