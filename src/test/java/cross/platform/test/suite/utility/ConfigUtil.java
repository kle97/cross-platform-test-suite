package cross.platform.test.suite.utility;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsSchema;
import com.fasterxml.jackson.dataformat.javaprop.util.Markers;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Slf4j
public final class ConfigUtil {

    private static final JavaPropsSchema propsSchema = JavaPropsSchema.emptySchema()
                                                                      .withWriteIndexUsingMarkers(true)
                                                                      .withFirstArrayOffset(0)
                                                                      .withIndexMarker(Markers.create("[", "]"));
    
    private ConfigUtil() {
    }

    public static boolean isParallel() {
        String parallelProperty = System.getProperty("parallel", "false");
        return parallelProperty.equals("tests") || Boolean.parseBoolean(parallelProperty);
    }
    
    public static <T> T readJsonFileAs(String filePath, Class<T> clazz) {
        try (InputStream inputStream = ClassLoader.getSystemResourceAsStream(filePath)) {
            return JacksonUtil.readerFor(clazz).readValue(inputStream);
        } catch (IOException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public static <T> T readJsonFileAs(String filePath, TypeReference<T> typeReference) {
        try (InputStream inputStream = ClassLoader.getSystemResourceAsStream(filePath)) {
            return JacksonUtil.readerFor(typeReference).readValue(inputStream);
        } catch (IOException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public static <T> T readJsonFileAs(String filePath, JavaType javaType) {
        try (InputStream inputStream = ClassLoader.getSystemResourceAsStream(filePath)) {
            return JacksonUtil.readerFor(javaType).readValue(inputStream);
        } catch (IOException e) {
            log.error(e.getMessage());
            return null;
        }
    }
    
    public static Properties readJsonFileAsProperties(String filePath) {
        try (InputStream inputStream = ClassLoader.getSystemResourceAsStream(filePath)) {
            Object config = JacksonUtil.readerFor(Object.class).readValue(inputStream);
            Properties configAsProperties = JacksonUtil.getDefaultJavaPropsMapper().writeValueAsProperties(config, propsSchema);
            for (String systemPropertiesKey: System.getProperties().stringPropertyNames()) {
                String systemPropertiesValue = System.getProperty(systemPropertiesKey);
                if (configAsProperties.containsKey(systemPropertiesKey)) {
                    configAsProperties.setProperty(systemPropertiesKey, systemPropertiesValue);
                }
            }
            return configAsProperties;
        } catch (IOException ex) {
            log.debug(ex.getMessage());
            return null;
        }
    }

    public static <T> Map<String, T> readConfigMapConfigFromFile(String filePath, Class<T> configClass) {
        return readConfigMapConfigFromFile(filePath, configClass, false);
    }
    
    public static <T> Map<String, T> readConfigMapConfigFromFile(String filePath, Class<T> configClass, boolean allowSystemPropsOverride) {
        try {
            MapType mapType = TypeFactory.defaultInstance().constructMapType(HashMap.class, String.class, configClass);
            if (allowSystemPropsOverride) {
                Properties configAsProperties = ConfigUtil.readJsonFileAsProperties(filePath);
                return JacksonUtil.getDefaultJavaPropsMapper().readPropertiesAs(configAsProperties, mapType);
            } else {
                return readJsonFileAs(filePath, mapType);
            }
        } catch (IOException ex) {
            log.debug(ex.getMessage());
            return null;
        }
    }
}
