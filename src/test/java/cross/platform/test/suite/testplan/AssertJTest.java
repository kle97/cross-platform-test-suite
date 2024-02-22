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
        softAssertions.assertThat(new int[]{1, 2, 3}).as("CCC").contains(5, 6);
        softAssertions.assertThat(10).as("DDD").isEqualTo(10);
        softAssertions.assertThat("abc").as("EEE").isEqualTo("abc");
        softAssertions.assertThat('a').as("FFF").isNotEqualTo('b');
        softAssertions.assertThat(1.5).as("GGG").isEqualTo(1.5);
        softAssertions.assertThat("String").as("HHH").isEqualTo("Sting");
        softAssertions.assertThat(new String[] {"a", "b", "c"}).as("III").contains("c");
        softAssertions.assertThat(2.0).as("JJJ").isEqualTo(2.0);

        try {
            softAssertions.assertAll();
        } catch (Error e) {

        }
    }
}
    
