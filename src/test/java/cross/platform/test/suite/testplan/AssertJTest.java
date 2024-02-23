package cross.platform.test.suite.testplan;

import cross.platform.test.suite.testcase.BaseTest;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

@Slf4j
public class AssertJTest extends BaseTest {

    @Test
    public void assertJTest() {
        softAssert().as("AAA").assertThat("1").isEqualTo("1");
        softAssert().as("BB%s", "B").assertThat(4).isEqualTo(5);
        softAssert().as("CCC").assertThat(new int[]{1, 2, 3}).contains(5, 6);
        softAssert().as("DDD").assertThat(10).isEqualTo(10);
        softAssert().as("EEE").assertThat("abc").isEqualTo("abc");
        softAssert().as("FFF").assertThat('a').isNotEqualTo('b');
        softAssert().as("GGG").assertThat(1.5).isEqualTo(1.5);
        softAssert().as("HHH").assertThat("String").isEqualTo("Sting");
        softAssert().as("III").assertThat(new String[] {"a", "b", "c"}).as("III").contains("c");
        softAssert().as("JJJ").assertThat(2.0).isEqualTo(2.0);
        softAssert().as("KKK").assertThat(10.0).isNotNull();
        
        try {
            softAssert().assertAll();
        } catch (Error e) {

        }
        
    }
}
    
