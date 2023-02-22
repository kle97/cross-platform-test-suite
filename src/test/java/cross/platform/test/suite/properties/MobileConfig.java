package cross.platform.test.suite.properties;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.NonFinal;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.Map;

@Value
@EqualsAndHashCode(callSuper = true)
public class MobileConfig extends JsonBaseConfig {
    
    @JsonProperty("server")
    private ServerArguments serverArguments;

    @JsonProperty("capabilities")
    private Map<String, String> capabilitiesMap;
    
    @JsonIgnore 
    @NonFinal
    private DesiredCapabilities desiredCapabilities;

    public DesiredCapabilities getDesiredCapabilities() {
        if (this.desiredCapabilities == null) {
            this.desiredCapabilities = new DesiredCapabilities();
            for (Map.Entry<String, String> entry : this.getCapabilitiesMap().entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (value.matches("[+-]?\\d+")) { // check if string contains only integer
                    try {
                        int intValue = Integer.parseInt(value);
                        desiredCapabilities.setCapability(key, intValue);
                    } catch (NumberFormatException ex) {
                        desiredCapabilities.setCapability(key, value);
                    }
                } else if ("true".equals(value) || "false".equals(value)) { // check if string contains only boolean
                    boolean booleanValue = Boolean.parseBoolean(value);
                    desiredCapabilities.setCapability(key, booleanValue);
                } else {
                    desiredCapabilities.setCapability(key, value);
                }
            }
        }
        return this.desiredCapabilities;
    }
}
