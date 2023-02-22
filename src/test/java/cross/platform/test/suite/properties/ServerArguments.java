package cross.platform.test.suite.properties;

import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
public class ServerArguments extends JsonBaseConfig {
    
    private String address;
    
    private Integer port;
    
    private String basePath;
}
