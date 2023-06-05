package cross.platform.test.suite.test.catalog;

import cross.platform.test.suite.annotation.AppendReport;
import cross.platform.test.suite.annotation.Screenshot;
import cross.platform.test.suite.properties.UserInfo;
import cross.platform.test.suite.service.LoggingAssertion;
import cross.platform.test.suite.service.POMFactory;
import cross.platform.test.suite.service.Reporter;
import cross.platform.test.suite.test.common.BaseTest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import javax.inject.Inject;

@Slf4j
@Getter
@Guice
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class FakeTest extends BaseTest {

    private final UserInfo userInfo;
    private final POMFactory pomFactory;
    private final Reporter reporter;
    private final LoggingAssertion assertion;

    @Test
    @Screenshot
    @AppendReport(description = "Verify fake account.")
    public void verifyCatalogTitle() {
        assertion.assertEquals("Username", "fake@example.com", userInfo.getUsername());
        assertion.assertEquals("Password", "fakePassword", userInfo.getPassword());
    }
}
