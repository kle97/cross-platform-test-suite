package cross.platform.test.suite.test.catalog;

import cross.platform.test.suite.annotation.ScreenRecord;
import cross.platform.test.suite.assertion.LoggingAssertion;
import cross.platform.test.suite.configuration.manager.DriverManager;
import cross.platform.test.suite.configuration.manager.ReportManager;
import cross.platform.test.suite.test.common.BaseTest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Guice;

import javax.inject.Inject;

@Slf4j
@ScreenRecord
@Getter
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@Guice(modules = CatalogModule.class)
public class CatalogTest extends BaseTest {
    
    private final DriverManager driverManager;
    private final ReportManager reportManager = new ReportManager();
    private final LoggingAssertion assertion = new LoggingAssertion(reportManager, log);
    
    
}
