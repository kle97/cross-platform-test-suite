package cross.platform.test.suite.testcase;

import cross.platform.test.suite.common.BaseTest;
import cross.platform.test.suite.common.Simulator;
import cross.platform.test.suite.context.POCEventContext;
import lombok.extern.slf4j.Slf4j;
import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.xml.XmlTest;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

@Slf4j
public class SampleTestCase {
    
    public static Object[] createTests(Simulator simulator, List<String> eventNames, 
                                       BiFunction<POCEventContext, BaseTest, Object> verification) {
        List<Object> tests = new ArrayList<>();
        for (String eventName : eventNames) {
            POCEventContext context = new POCEventContext(eventName);
            
            tests.add(verification.apply(context, new BaseTest() {
                @BeforeClass
                public void beforeTestCase(XmlTest xmlTest, ITestContext testContext) {
                    if (eventNames.get(0).equals(eventName)) {
                        log.info("Setting up for SampleTest!");
                    }
                    
                    simulator.setEvent(context.getEventName(), true);
                }

                @AfterClass
                public void afterTestCase() {
                    simulator.setEvent(context.getEventName(), false);

                    if (eventNames.get(eventNames.size() - 1).equals(eventName)) {
                        log.info("Tearing down for SampleTest!");
                    }
                }
            }));
        }
        return tests.toArray();
    }
}
