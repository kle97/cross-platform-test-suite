package cross.platform.test.suite.test.javatest;

import cross.platform.test.suite.annotation.Screenshot;
import cross.platform.test.suite.assertion.LoggingAssertion;
import cross.platform.test.suite.configuration.manager.DriverManager;
import cross.platform.test.suite.configuration.manager.ReportManager;
import cross.platform.test.suite.constant.TestConst;
import cross.platform.test.suite.properties.MobileConfig;
import cross.platform.test.suite.test.helper.AssertionHelper;
import cross.platform.test.suite.test.helper.ReportHelper;
import cross.platform.test.suite.test.helper.ScreenshotHelper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import javax.inject.Inject;
import javax.inject.Named;

@Slf4j
@Guice
@Test(testName = TestConst.JAVA_TEST, groups = JavaTest.GROUP)
@Getter
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class JavaTest implements ReportHelper, AssertionHelper, ScreenshotHelper {

    public static final String GROUP = "JavaTest";

    @Named(TestConst.ANDROID_2_CONFIG_PATH)
    private final MobileConfig mobileConfig;
    private final DriverManager driverManager;
    private final ReportManager reportManager;
    private final LoggingAssertion assertion;

    @Screenshot
    @Test(description = "javaTest description...")
    public void javaTest() {
        log.info(this.mobileConfig.getServerArguments().getAddress());
    }
}
