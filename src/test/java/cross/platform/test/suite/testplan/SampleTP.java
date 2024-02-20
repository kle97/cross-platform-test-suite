package cross.platform.test.suite.testplan;

import cross.platform.test.suite.common.Simulator;
import cross.platform.test.suite.testcase.BaseTP;
import cross.platform.test.suite.testcase.SampleTest;
import cross.platform.test.suite.verification.EventVerification;
import cross.platform.test.suite.verification.SampleVerification;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import java.util.List;

@Slf4j
@Test(testName = "SampleTP")
public class SampleTP extends BaseTP {
    
    @Test
    public void eventTest() {
        runTests(new SampleTest(new Simulator(), List.of("AA", "BB", "CC", "DD"), 
                                SampleVerification::new, EventVerification::new),
                 new SampleTest(new Simulator(), List.of("EE", "FF", "GG", "HH"),
                                SampleVerification::new, EventVerification::new));
    }
}
