package cross.platform.test.suite.assertion;

import cross.platform.test.suite.configuration.manager.ReportManager;
import cross.platform.test.suite.constant.AnsiColor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.asserts.Assertion;
import org.testng.asserts.IAssert;

import java.util.*;

public class SoftAssertion extends Assertion {
    private static final String DEFAULT_SOFT_ASSERT_MESSAGE = "The following asserts failed:";
    private static final StackWalker walker = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
    private final Logger log;
    private final Map<AssertionError, IAssert<?>> errorMap = new LinkedHashMap<>();
    private ReportManager reportManager;
    private boolean throwError;

    public SoftAssertion() {
        Optional<? extends Class<?>> caller = walker.walk(s -> s.map(StackWalker.StackFrame::getDeclaringClass).skip(2).findFirst());
        if (caller.isPresent()) {
            this.log = LoggerFactory.getLogger(caller.get().getName());
        } else {
            this.log = LoggerFactory.getLogger(getClass());
        }
    }

    public SoftAssertion(boolean throwError) {
        this();
        this.throwError = throwError;
    }

    public SoftAssertion(ReportManager reportManager) {
        this(false, reportManager);
    }

    public SoftAssertion(boolean throwError, ReportManager reportManager) {
        this(throwError);
        this.reportManager = reportManager;
    }

    @Override
    public void onAssertFailure(IAssert<?> assertCommand, AssertionError ex) {
        String message = this.getAssertMessage(assertCommand);
        this.logFail(message);
    }

    @Override
    public void onAssertSuccess(IAssert<?> assertCommand) {
        String message = this.getAssertMessage(assertCommand);
        this.logPass(message);
    }

    private String getAssertMessage(IAssert<?> assertCommand) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Message [\"")
                     .append(assertCommand.getMessage())
                     .append("\"], expected [\"")
                     .append(assertCommand.getExpected())
                     .append("\"], actual [\"")
                     .append(assertCommand.getActual())
                     .append("\"]");
        return stringBuilder.toString();
    }

    private void logFail(String message) {
        String logMessage = AnsiColor.RED_BOLD + "   FAIL   " + AnsiColor.RESET + message;
        this.log.info(logMessage);
        if (this.reportManager != null) {
            this.reportManager.fail(message);
        }
    }

    private void logPass(String message) {
        String logMessage = AnsiColor.GREEN_BOLD + "   PASS   " + AnsiColor.RESET + message;
        this.log.info(logMessage);
        if (this.reportManager != null) {
            this.reportManager.pass(message);
        }
    }

    @Override
    protected void doAssert(IAssert<?> assertion) {
        onBeforeAssert(assertion);
        try {
            assertion.doAssert();
            onAssertSuccess(assertion);
        } catch (AssertionError ex) {
            onAssertFailure(assertion, ex);
            this.errorMap.put(ex, assertion);
        } finally {
            onAfterAssert(assertion);
        }
    }

    public void assertAll() {
        assertAll(null);
    }

    public void assertAll(String message) {
        if (!this.errorMap.isEmpty()) {
            StringBuilder stringBuilder = new StringBuilder(message == null ? DEFAULT_SOFT_ASSERT_MESSAGE : message);
            boolean first = true;
            for (AssertionError error : this.errorMap.keySet()) {
                if (first) {
                    first = false;
                } else {
                    stringBuilder.append(",");
                }
                stringBuilder.append("\n\t");
                stringBuilder.append(getErrorDetails(error));
            }
            if (throwError) {
                throw new AssertionError(stringBuilder.toString());
            }
        }
    }

    public <T> void assertEquals(String message, T actual, T expected) {
        super.assertEquals(actual, expected, message);
    }

    @Override
    public void assertEquals(String message, String actual, String expected) {
        super.assertEquals(actual, expected, message);
    }

    public void assertEquals(String message, long actual, long expected) {
        super.assertEquals(actual, expected, message);
    }

    public void assertEquals(String message, boolean actual, boolean expected) {
        super.assertEquals(actual, expected, message);
    }

    public void assertEquals(String message, byte actual, byte expected) {
        super.assertEquals(actual, expected, message);
    }

    public void assertEquals(String message, char actual, char expected) {
        super.assertEquals(actual, expected, message);
    }

    public void assertEquals(String message, short actual, short expected) {
        super.assertEquals(actual, expected, message);
    }

    public void assertEquals(String message, int actual, int expected) {
        super.assertEquals(actual, expected, message);
    }

    public void assertEquals(String message, Collection<?> actual, Collection<?> expected) {
        super.assertEquals(actual, expected, message);
    }


    public void assertEquals(String message, Object[] actual, Object[] expected) {
        super.assertEquals(actual, expected, message);
    }


    public void assertEquals(String message, byte[] actual, byte[] expected) {
        super.assertEquals(actual, expected, message);
    }

    public void assertEquals(String message, Set<?> actual, Set<?> expected) {
        super.assertEquals(actual, expected, message);
    }

    public void assertEquals(String message, double actual, double expected, double delta) {
        super.assertEquals(actual, expected, delta, message);
    }

    public void assertEquals(String message, float actual, float expected, float delta) {
        super.assertEquals(actual, expected, delta, message);
    }

    public void assertEqualsNoOrder(String message, Object[] actual, Object[] expected) {
        super.assertEqualsNoOrder(actual, expected, message);
    }

    public void assertNotEquals(String message, Object actual, Object expected) {
        super.assertNotEquals(actual, expected, message);
    }

    @Override
    public void assertNotEquals(String message, String actual, String expected) {
        super.assertNotEquals(actual, expected, message);
    }

    public void assertNotEquals(String message, long actual, long expected) {
        super.assertNotEquals(actual, expected, message);
    }

    public void assertNotEquals(String message, boolean actual, boolean expected) {
        super.assertNotEquals(actual, expected, message);
    }

    public void assertNotEquals(String message, byte actual, byte expected) {
        super.assertNotEquals(actual, expected, message);
    }

    public void assertNotEquals(String message, char actual, char expected) {
        super.assertNotEquals(actual, expected, message);
    }

    public void assertNotEquals(String message, short actual, short expected) {
        super.assertNotEquals(actual, expected, message);
    }

    public void assertNotEquals(String message, int actual, int expected) {
        super.assertNotEquals(actual, expected, message);
    }

    public void assertNotEquals(String message, float actual, float expected, float delta) {
        super.assertNotEquals(actual, expected, delta, message);
    }

    public void assertNotEquals(String message, double actual, double expected, double delta) {
        super.assertNotEquals(actual, expected, delta, message);
    }

    public void assertNotNull(String message, Object object) {
        super.assertNotNull(object, message);
    }

    public void assertNotSame(String message, Object actual, Object expected) {
        super.assertNotSame(actual, expected, message);
    }

    public void assertNull(String message, Object object) {
        super.assertNull(object, message);
    }

    public void assertSame(String message, Object actual, Object expected) {
        super.assertSame(actual, expected, message);
    }

    public void assertTrue(String message, boolean condition) {
        super.assertTrue(condition, message);
    }
}
