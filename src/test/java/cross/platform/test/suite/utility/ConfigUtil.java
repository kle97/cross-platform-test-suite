package cross.platform.test.suite.utility;

import com.fasterxml.jackson.dataformat.javaprop.JavaPropsSchema;
import com.fasterxml.jackson.dataformat.javaprop.util.Markers;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
public final class ConfigUtil {
    
    private ConfigUtil() {
    }
    
    public static Properties readJsonFileAsProperties(String filePath, boolean allowSystemPropsOverride) {
        try (InputStream inputStream = ClassLoader.getSystemResourceAsStream(filePath)) {
            Object config = JacksonUtil.getDefaultObjectReader().readValue(inputStream, Object.class);
            JavaPropsSchema propsSchema = JavaPropsSchema.emptySchema()
                                                         .withWriteIndexUsingMarkers(true)
                                                         .withFirstArrayOffset(0)
                                                         .withIndexMarker(Markers.create("[", "]"));
            Properties configAsProperties = JacksonUtil.getDefaultJavaPropsMapper().writeValueAsProperties(config, propsSchema);
            if (allowSystemPropsOverride) {
                for (String systemPropertiesKey: System.getProperties().stringPropertyNames()) {
                    String systemPropertiesValue = System.getProperty(systemPropertiesKey);
                    if (configAsProperties.containsKey(systemPropertiesKey)) {
                        configAsProperties.setProperty(systemPropertiesKey, systemPropertiesValue);
                    }
                }
            }
            return configAsProperties;
        } catch (IOException ex) {
            log.debug(ex.getMessage());
            return null;
        }
    }
}
