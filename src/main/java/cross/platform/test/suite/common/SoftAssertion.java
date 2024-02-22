package cross.platform.test.suite.common;

import com.google.common.collect.Streams;
import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;
import org.testng.asserts.IAssert;
import org.testng.asserts.SoftAssert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.toCollection;

@Slf4j
public class SoftAssertion {

    private static final LoggingAssertion loggingAssertion = new LoggingAssertion();

    private SoftAssertion() {
    }

    public static void assertAll() {
        loggingAssertion.assertAll();
    }

    public static AssertWithMessage as(String message) {
        return new AssertWithMessage(loggingAssertion, message);
    }

    public static <T> BaseSoftAssertion<T> assertThat(T actual) {
        return new BaseSoftAssertion<>(loggingAssertion, null, actual);
    }

    public static BooleanSoftAssertion assertThat(boolean actual) {
        return new BooleanSoftAssertion(loggingAssertion, null, actual);
    }

    public static StringSoftAssertion assertThat(String actual) {
        return new StringSoftAssertion(loggingAssertion, null, actual);
    }

    public static <T extends Comparable<T>> NumberSoftAssertion<T> assertThat(T actual) {
        return new NumberSoftAssertion<>(loggingAssertion, null, actual);
    }

    public static <T extends Iterable<?>> IterableSoftAssertion<T> assertThat(T actual) {
        return new IterableSoftAssertion<>(loggingAssertion, null, actual);
    }

    public static class AssertWithMessage {

        private final LoggingAssertion loggingAssertion;

        private final String message;

        public AssertWithMessage(LoggingAssertion loggingAssertion, String message) {
            this.loggingAssertion = loggingAssertion;
            this.message = message;
        }

        public <T> BaseSoftAssertion<T> assertThat(T actual) {
            return new BaseSoftAssertion<>(loggingAssertion, message, actual);
        }

        public BooleanSoftAssertion assertThat(boolean actual) {
            return new BooleanSoftAssertion(loggingAssertion, message, actual);
        }

        public StringSoftAssertion assertThat(String actual) {
            return new StringSoftAssertion(loggingAssertion, message, actual);
        }

        public <T extends Comparable<T>> NumberSoftAssertion<T> assertThat(T actual) {
            return new NumberSoftAssertion<>(loggingAssertion, message, actual);
        }

        public <T extends Iterable<?>> IterableSoftAssertion<T> assertThat(T actual) {
            return new IterableSoftAssertion<>(loggingAssertion, message, actual);
        }
    }

    public static class BooleanSoftAssertion extends BaseSoftAssertion<Boolean> {

        public BooleanSoftAssertion(LoggingAssertion loggingAssertion, String message, Boolean actual) {
            super(loggingAssertion, message, actual);
        }

        public BooleanSoftAssertion as(String message) {
            this.innerMessage = message;
            return this;
        }

        public BooleanSoftAssertion assertTrue(String prefix) {
            loggingAssertion.assertTrue(actual, innerMessage);
            return this;
        }

        public BooleanSoftAssertion assertFalse() {
            loggingAssertion.assertFalse(actual, innerMessage);
            return this;
        }
    }

    public static class StringSoftAssertion extends BaseSoftAssertion<String> {

        public StringSoftAssertion(LoggingAssertion loggingAssertion, String message, String actual) {
            super(loggingAssertion, message, actual);
        }

        public StringSoftAssertion as(String message) {
            this.innerMessage = message;
            return this;
        }

        public StringSoftAssertion startWith(String prefix) {
            loggingAssertion.assertTrue(actual.startsWith(prefix), innerMessage);
            return this;
        }

        public StringSoftAssertion endsWith(String suffix) {
            loggingAssertion.assertTrue(actual.endsWith(suffix), innerMessage);
            return this;
        }

        public StringSoftAssertion contains(CharSequence s) {
            loggingAssertion.assertTrue(actual.contains(s), innerMessage);
            return this;
        }

        public StringSoftAssertion isEqualIgnoringCase(String expected) {
            loggingAssertion.assertEquals(actual.toLowerCase(), expected.toLowerCase(), innerMessage);
            return this;
        }
    }

    public static class NumberSoftAssertion<T extends Comparable<T>> extends BaseSoftAssertion<T> {

        public NumberSoftAssertion(LoggingAssertion loggingAssertion, String message, T actual) {
            super(loggingAssertion, message, actual);
        }

        public NumberSoftAssertion<T> as(String message) {
            this.innerMessage = message;
            return this;
        }

        public NumberSoftAssertion<T> isLargerThan(T expected) {
            loggingAssertion.assertTrue(actual.compareTo(expected) > 0, innerMessage);
            return this;
        }

        public NumberSoftAssertion<T> isLessThan(T expected) {
            loggingAssertion.assertTrue(actual.compareTo(expected) < 0, innerMessage);
            return this;
        }
    }

    public static class IterableSoftAssertion<T extends Iterable<?>> extends BaseSoftAssertion<T> {

        public IterableSoftAssertion(LoggingAssertion loggingAssertion, String message, T actual) {
            super(loggingAssertion, message, actual);
        }

        public IterableSoftAssertion<T> as(String message) {
            this.innerMessage = message;
            return this;
        }

        public IterableSoftAssertion<T> isEqualNoOrder(T expected) {
            doAssert(new SimpleAssert<>(actual, expected, innerMessage) {
                @Override
                public void doAssert() {
                    final List<?> actualAsList = newArrayList(actual);
                    final List<?> expectedAsList = newArrayList(expected);
                    if (actualAsList == null || expectedAsList == null || actualAsList.size() != expectedAsList.size()) {
                        failNoOrder(innerMessage, actual, expected);
                        return;
                    }

                    for (Object item : actual) {
                        if (item instanceof Iterable<?> || item.getClass().isArray()) {
                            if (expectedAsList.stream().noneMatch(o -> isEqualNoOrder(item, o))) {
                                failNoOrder(innerMessage, actual, expected);
                                return;
                            }
                        } else {
                            if (expectedAsList.stream().noneMatch(item::equals)) {
                                failNoOrder(innerMessage, actual, expected);
                                return;
                            }
                        }
                    }
                }
            });
            return this;
        }
    }

    public static class BaseSoftAssertion<T> {

        protected LoggingAssertion loggingAssertion;

        protected String innerMessage;

        T actual;

        public BaseSoftAssertion(LoggingAssertion loggingAssertion, String message, T actual) {
            this.loggingAssertion = loggingAssertion;
            this.innerMessage = message;
            this.actual = actual;
        }

        public BaseSoftAssertion<T> as(String message) {
            this.innerMessage = message;
            return this;
        }

        public BaseSoftAssertion<T> isEqualTo(T expected) {
            loggingAssertion.assertEquals(actual, expected, innerMessage);
            return this;
        }

        public BaseSoftAssertion<T> isNotEqualTo(T expected) {
            loggingAssertion.assertNotEquals(actual, expected, innerMessage);
            return this;
        }

        protected boolean isEqualNoOrder(Object iterable, Object other) {
            List<?> iterableAsList = newArrayList(iterable);
            List<?> otherAsList = newArrayList(other);

            if (iterableAsList == null || otherAsList == null || iterableAsList.size() != otherAsList.size()) {
                return false;
            }

            for (Object item : iterableAsList) {
                otherAsList.remove(item);
            }
            return otherAsList.isEmpty();
        }

        protected void doAssert(IAssert<?> assertCommand) {
            loggingAssertion.loggingAssert(assertCommand);
        }

        protected <E extends Iterable<?>> void failNoOrder(String message, E actual, E expected) {
            StringBuilder actualString = new StringBuilder();
            actual.forEach(actualString::append);
            StringBuilder expectedString = new StringBuilder();
            expected.forEach(expectedString::append);
            Assert.fail(message + " expected (no order) " + expectedString + " but found " + actualString);
        }

        protected List<?> newArrayList(Object object) {
            List<Object> list;
            if (object == null) {
                return null;
            } else if (object.getClass().isArray()) {
                list = new ArrayList<>(Arrays.asList((Object[]) object));
            } else if (object instanceof Collection<?>) {
                list = new ArrayList<>((Collection<?>) object);
            } else if (object instanceof Iterable<?>) {
                list = Streams.stream((Iterable<?>) object).collect(toCollection(ArrayList::new));
            } else {
                list = new ArrayList<>();
                list.add(object);
            }

            return list;
        }
    }

    private abstract static class SimpleAssert<T> implements IAssert<T> {
        private final T actual;
        private final T expected;
        private final String message;

        public SimpleAssert(String message) {
            this(null, null, message);
        }

        public SimpleAssert(T actual, T expected) {
            this(actual, expected, null);
        }

        public SimpleAssert(T actual, T expected, String message) {
            this.actual = actual;
            this.expected = expected;
            this.message = message;
        }

        @Override
        public String getMessage() {
            return message;
        }

        @Override
        public T getActual() {
            return actual;
        }

        @Override
        public T getExpected() {
            return expected;
        }

        @Override
        public abstract void doAssert();
    }

    public static class LoggingAssertion extends SoftAssert {

        public void loggingAssert(IAssert<?> a) {
            doAssert(a);
        }
        
        @Override
        public void onAssertSuccess(IAssert<?> assertCommand) {
            String message = assertCommand.getMessage() + " expected [" + objectToString(assertCommand.getExpected()) + "]";
            Reporter.pass(message);
        }

        @Override
        public void onAssertFailure(IAssert<?> assertCommand, AssertionError ex) {
            String errorMessage = assertCommand.getMessage() + " expected [" + objectToString(assertCommand.getExpected()) 
                    + "] but found [" + objectToString(assertCommand.getActual()) + "]";
            Reporter.fail(errorMessage);
        }

        private <T> String objectToString(T object) {
            String result;
            Class<?> objectClass = object.getClass();
            if (objectClass.isArray()) {
                try {
                    result = Arrays.deepToString((Object[]) object);
                } catch (ClassCastException e) {
                    result = Arrays.deepToString(new Object[]{object});
                }
                result = result.replaceAll("[\\[\\]]", "");
            } else {
                result = String.valueOf(object);
            }

            return result;
        }
    }
}
