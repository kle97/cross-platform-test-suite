package cross.platform.test.suite.configuration.guicemodule;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsSchema;
import com.fasterxml.jackson.dataformat.javaprop.util.Markers;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import cross.platform.test.suite.configuration.manager.DriverManager;
import cross.platform.test.suite.constant.TestConst;
import cross.platform.test.suite.properties.MobileConfig;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Singleton;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
public class ParentModule extends AbstractModule {

    MobileConfig mobileConfig;

    @Override
    protected void configure() {
    }

    @Provides
    @Singleton
    protected DriverManager provideDriverManager() {
        return new DriverManager();
    }

    @Provides
    @Singleton
    protected ObjectMapper provideObjectMapper() {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return mapper;
    }

    @Provides
    @Singleton
    protected JavaPropsMapper provideJavaPropsMapper() {
        return new JavaPropsMapper();
    }

    protected MobileConfig getDefaultMobileConfig(ObjectMapper objectMapper, JavaPropsMapper propsMapper) {
        if (this.mobileConfig == null) {
            this.mobileConfig = this.readMobileConfigFromFile(objectMapper, propsMapper, TestConst.ANDROID_1_CONFIG_PATH);
        }
        return this.mobileConfig;
    }

    @Provides
    @Singleton
    protected MobileConfig provideAndroid1Config(ObjectMapper objectMapper, JavaPropsMapper propsMapper) {
        return this.getDefaultMobileConfig(objectMapper, propsMapper);
    }

    @Provides
    @Singleton
    @Named(TestConst.ANDROID_2_CONFIG_PATH)
    protected MobileConfig provideAndroid2Config(ObjectMapper objectMapper, JavaPropsMapper propsMapper) {
        if (Boolean.parseBoolean(System.getProperty("parallel"))) {
            return this.readMobileConfigFromFile(objectMapper, propsMapper, TestConst.ANDROID_2_CONFIG_PATH);
        } else {
            return this.getDefaultMobileConfig(objectMapper, propsMapper);
        }
    }

    protected MobileConfig readMobileConfigFromFile(ObjectMapper objectMapper, JavaPropsMapper propsMapper, String filePath) {
        try {
            Properties configAsProperties = this.readJsonFileAsProperties(objectMapper, propsMapper, filePath);
            return propsMapper.readPropertiesAs(configAsProperties, MobileConfig.class);
        } catch (IOException ex) {
            log.debug(ex.getMessage());
            return null;
        }
    }

    protected Properties readJsonFileAsProperties(ObjectMapper objectMapper, JavaPropsMapper propsMapper, String filePath) {
        try (InputStream inputStream = ClassLoader.getSystemResourceAsStream(filePath)) {
            Object config = objectMapper.readValue(inputStream, Object.class);
            JavaPropsSchema propsSchema = JavaPropsSchema.emptySchema()
                                                         .withWriteIndexUsingMarkers(true)
                                                         .withFirstArrayOffset(0)
                                                         .withIndexMarker(Markers.create("[", "]"));
            Properties configAsProperties = propsMapper.writeValueAsProperties(config, propsSchema);
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
}
