package cross.platform.test.suite.utility;

import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;

public final class JacksonUtil {
    
    // Jackson's ObjectMapper configuration for JSON5 format
    // Jackson still lacks support for hexadecimal number and additional white space characters in JSON5
    // See: https://stackoverflow.com/questions/68312227/can-the-jackson-parser-be-used-to-parse-json5/68312228#68312228
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
            .enable(JsonReadFeature.ALLOW_TRAILING_DECIMAL_POINT_FOR_NUMBERS)
            .enable(JsonReadFeature.ALLOW_LEADING_PLUS_SIGN_FOR_NUMBERS)
            .build();
    
    private static final JavaPropsMapper defaultJavaPropsMapper = new JavaPropsMapper();
    
    private JacksonUtil() {
    }
    
    public static JavaPropsMapper getDefaultJavaPropsMapper() {
        return defaultJavaPropsMapper;
    }

    public static ObjectReader readerFor(Class<?> type) {
        return objectMapper.readerFor(type);
    }

    public static ObjectReader readerFor(TypeReference<?> typeReference) {
        return objectMapper.readerFor(typeReference);
    }

    public static ObjectReader readerFor(JavaType type) {
        return objectMapper.readerFor(type);
    }

    public static ObjectWriter writerFor(Class<?> type) {
        return objectMapper.writerFor(type);
    }

    public static ObjectWriter writerFor(TypeReference<?> typeReference) {
        return objectMapper.writerFor(typeReference);
    }

    public static ObjectWriter writerFor(JavaType type) {
        return objectMapper.writerFor(type);
    }
}
