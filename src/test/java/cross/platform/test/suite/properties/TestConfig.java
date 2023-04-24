package cross.platform.test.suite.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;

@Getter
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class TestConfig {
    
    private final MobileConfig mobileConfig;

    private final UserInfo userInfo;
}
