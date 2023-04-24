package cross.platform.test.suite.properties;

import lombok.Value;

import java.util.Map;

@Value
public class ConfigMap {
    
    Map<String, MobileConfig> mobileConfigMap;
    
    Map<String, UserInfo> userInfoMap;
    
}
