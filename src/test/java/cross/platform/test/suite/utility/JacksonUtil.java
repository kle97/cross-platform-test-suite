package cross.platform.test.suite.utility;

import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;

public final class JacksonUtil {
    
    private static final ObjectMapper objectMapper = JsonMapper
            .builder()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .enable(JsonReadFeature.ALLOW_JAVA_COMMENTS)
            .enable(JsonReadFeature.ALLOW_TRAILING_COMMA)
            .enable(JsonReadFeature.ALLOW_NON_NUMERIC_NUMBERS)
            .enable(JsonReadFeature.ALLOW_SINGLE_QUOTES)
            .enable(JsonReadFeature.ALLOW_UNQUOTED_FIELD_NAMES)
            .enable(JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER)
            .enable(JsonReadFeature.ALLOW_LEADING_DECIMAL_POINT_FOR_NUMBERS)
            .build();
    
    private static final JavaPropsMapper defaultJavaPropsMapper = new JavaPropsMapper();
    private static final ObjectReader defaultObjectReader = objectMapper.reader();
    private static final ObjectWriter defaultObjectWriter = objectMapper.writer();
    
    private JacksonUtil() {
    }
    
    public static JavaPropsMapper getDefaultJavaPropsMapper() {
        return defaultJavaPropsMapper;
    }
    
    public static JavaPropsMapper getJavaPropsMapper() {
        return defaultJavaPropsMapper.copy();
    }
    
    public static ObjectReader getDefaultObjectReader() {
        return defaultObjectReader;
    }

    public static ObjectWriter getDefaultObjectWriter() {
        return defaultObjectWriter;
    }
    
    public static ObjectReader getObjectReader() {
        return objectMapper.reader();
    }

    public static ObjectReader getObjectReader(Class<?> type) {
        return objectMapper.reader().forType(type);
    }

    public static ObjectWriter getObjectWriter() {
        return objectMapper.writer();
    }

    public static ObjectWriter getObjectWriter(Class<?> type) {
        return objectMapper.writer().forType(type);
    }
}
