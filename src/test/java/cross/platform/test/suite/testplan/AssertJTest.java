package cross.platform.test.suite.testplan;

import cross.platform.test.suite.common.SoftAssertJ;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

@Slf4j
public class AssertJTest {

    @Test
    public void assertJTest() {
        SoftAssertJ softAssertions = new SoftAssertJ();
        softAssertions.assertThat("1").as("AAA").isEqualTo("1");
        softAssertions.assertThat(4).as("BBB").isEqualTo(5);
        softAssertions.assertThat(new int[]{1, 2, 3}).as("CCC").contains(5);
        softAssertions.assertThat(10).as("DDD").isEqualTo(10);

        try {
            softAssertions.assertAll();
        } catch (Error e) {

        }
    }
}
    
