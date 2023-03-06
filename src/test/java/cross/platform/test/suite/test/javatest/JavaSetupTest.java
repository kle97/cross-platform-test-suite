package cross.platform.test.suite.test.javatest;

import cross.platform.test.suite.configuration.manager.DriverManager;
import cross.platform.test.suite.constant.TestConst;
import cross.platform.test.suite.properties.MobileConfig;
import cross.platform.test.suite.test.setup.AbstractSetupTest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import javax.inject.Inject;
import javax.inject.Named;

@Slf4j
@Guice
@Test(testName = TestConst.JAVA_TEST)
@Getter
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public final class JavaSetupTest extends AbstractSetupTest {

    @Named(TestConst.ANDROID_2_CONFIG_PATH)
    private final MobileConfig mobileConfig;
    private final DriverManager driverManager;
}