package cross.platform.test.suite.testplan;

import cross.platform.test.suite.common.Simulator;
import cross.platform.test.suite.testcase.SampleTestCase;
import cross.platform.test.suite.verification.SampleVerification;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Factory;

import java.util.List;

@Slf4j
public class SampleTP {
    
    @Factory
    public Object[] eventTest() {
        return SampleTestCase.createTests(new Simulator(), List.of("AA", "BB", "CC", "DD"), SampleVerification::new);
    }
}
