package cross.platform.test.suite.test;

import cross.platform.test.suite.configuration.manager.DriverManager;
import cross.platform.test.suite.configuration.manager.ReportManager;
import cross.platform.test.suite.constant.TestConst;
import cross.platform.test.suite.properties.MobileConfig;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import javax.inject.Inject;
import javax.inject.Named;

@Slf4j
@Getter
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@Guice
@Test(testName = TestConst.JAVA_TEST, groups = TestConst.JAVA_TEST)
public class JavaTest extends BaseSetupTest implements ReportTest {
    
    @Named(TestConst.ANDROID_2_CONFIG_PATH)
    private final MobileConfig mobileConfig;
    private final ReportManager reportManager;
    private final DriverManager driverManager;

    @Test(description = "javaTest method description.")
    public void javaTest() {
        log.info(this.driverManager.getDriver().getRemoteAddress().toString());
        this.reportManager.info(this.driverManager.getDriver().getRemoteAddress().toString());
    }
}