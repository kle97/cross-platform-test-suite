package cross.platform.test.suite.properties;

import lombok.Value;

@Value
public class TestConfig {
    
    MobileConfig mobileConfig;

    UserInfo userInfo;
}
