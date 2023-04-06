package cross.platform.test.suite.test.apidemos;

import cross.platform.test.suite.configuration.manager.DriverManager;
import cross.platform.test.suite.properties.MobileConfig;
import cross.platform.test.suite.test.common.AbstractTestSetup;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.testng.annotations.Guice;

import javax.inject.Inject;

@Getter
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@Guice(modules = ApiDemosModule.class)
public class ApiDemosSetup extends AbstractTestSetup {
    
    private final MobileConfig mobileConfig;
    private final DriverManager driverManager;
}
