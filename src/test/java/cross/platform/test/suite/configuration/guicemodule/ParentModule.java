package cross.platform.test.suite.configuration.guicemodule;

import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsSchema;
import com.fasterxml.jackson.dataformat.javaprop.util.Markers;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import cross.platform.test.suite.configuration.manager.DriverManager;
import cross.platform.test.suite.configuration.manager.ReportManager;
import cross.platform.test.suite.constant.TestConst;
import cross.platform.test.suite.properties.MobileConfig;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Singleton;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

@Slf4j
public class ParentModule extends AbstractModule {

    @Override
    protected void configure() {
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
        final JavaPropsMapper mapper = new JavaPropsMapper();
        return mapper;
    }
    
    @Provides
    @Singleton
    protected DriverManager provideDriverManager() {
        return new DriverManager();
    }
    
    @Provides
    @Singleton
    protected ReportManager provideReportManager() {
        ReportManager reportManager = new ReportManager();
        String timeStamp = DateTimeFormatter.ofPattern("MM-dd-yyyy-HH-mm-ss")
                                            .withZone(ZoneId.systemDefault())
                                            .format(Instant.now());
        String filePath = "cross-platform-test-suite" + "-" + timeStamp + ".html";
        String reportFilePath = TestConst.REPORT_PATH + "/" + filePath;
        log.info(reportFilePath);
        ExtentSparkReporter spark = new ExtentSparkReporter(reportFilePath);
        reportManager.attachReporter(spark);
        return reportManager;
    }

    @Provides
    @Singleton
    protected MobileConfig provideAndroid1Config(ObjectMapper objectMapper, JavaPropsMapper propsMapper) throws IOException {
        Properties configAsProperties = this.readJsonFileAsProperties(objectMapper, propsMapper, TestConst.ANDROID_1_CONFIG_PATH);
        MobileConfig config = propsMapper.readPropertiesAs(configAsProperties, MobileConfig.class);
        return config;
    }

    @Provides
    @Singleton
    @Named(TestConst.ANDROID_2_CONFIG_PATH)
    protected MobileConfig provideAndroid2Config(ObjectMapper objectMapper, JavaPropsMapper propsMapper) throws IOException {
        Properties configAsProperties = this.readJsonFileAsProperties(objectMapper, propsMapper, TestConst.ANDROID_2_CONFIG_PATH);
        MobileConfig config = propsMapper.readPropertiesAs(configAsProperties, MobileConfig.class);
        return config;
    }

    protected Properties readJsonFileAsProperties(ObjectMapper objectMapper, JavaPropsMapper propsMapper, String filePath) throws IOException {
        try (InputStream inputStream = ClassLoader.getSystemResourceAsStream(filePath)) {
            Object config = objectMapper.readValue(inputStream, Object.class);
            JavaPropsSchema propsSchema = JavaPropsSchema.emptySchema()
                                                         .withWriteIndexUsingMarkers(true)
                                                         .withFirstArrayOffset(0)
                                                         .withIndexMarker(Markers.create("[", "]"));
            Properties configAsProperties = propsMapper.writeValueAsProperties(config, propsSchema);
            for (String systemPropertiesKey : System.getProperties().stringPropertyNames()) {
                String systemPropertiesValue = System.getProperty(systemPropertiesKey);
                if (configAsProperties.containsKey(systemPropertiesKey)) {
                    configAsProperties.setProperty(systemPropertiesKey, systemPropertiesValue);
                }
            }
            return configAsProperties;
        }
    }
}
