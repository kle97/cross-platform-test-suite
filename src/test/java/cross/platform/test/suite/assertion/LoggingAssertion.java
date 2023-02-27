package cross.platform.test.suite.assertion;

import cross.platform.test.suite.configuration.manager.ReportManager;
import cross.platform.test.suite.constant.AnsiColor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.asserts.Assertion;
import org.testng.asserts.IAssert;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class LoggingAssertion extends Assertion {
    private static final String DEFAULT_SOFT_ASSERT_MESSAGE = "The following asserts failed:";
    private final Map<AssertionError, IAssert<?>> errorMap = new LinkedHashMap<>();
    private final ReportManager reportManager;
    private Logger log = LoggerFactory.getLogger(LoggingAssertion.class);

    public LoggingAssertion(ReportManager reportManager) {
        this.reportManager = reportManager;
    }

    public void setCurrentLogger(Logger log) {
        this.log = log;
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
            throw new AssertionError(stringBuilder.toString());
        }
    }

    public <T> void assertEquals(String message, T expected, T actual) {
        super.assertEquals(actual, expected, message);
    }

    @Override
    public void assertEquals(String message, String expected, String actual) {
        super.assertEquals(actual, expected, message);
    }

    public void assertEquals(String message, long expected, long actual) {
        super.assertEquals(actual, expected, message);
    }

    public void assertEquals(String message, boolean expected, boolean actual) {
        super.assertEquals(actual, expected, message);
    }

    public void assertEquals(String message, byte expected, byte actual) {
        super.assertEquals(actual, expected, message);
    }

    public void assertEquals(String message, char expected, char actual) {
        super.assertEquals(actual, expected, message);
    }

    public void assertEquals(String message, short expected, short actual) {
        super.assertEquals(actual, expected, message);
    }

    public void assertEquals(String message, int expected, int actual) {
        super.assertEquals(actual, expected, message);
    }

    public void assertEquals(String message, Collection<?> expected, Collection<?> actual) {
        super.assertEquals(actual, expected, message);
    }


    public void assertEquals(String message, Object[] expected, Object[] actual) {
        super.assertEquals(actual, expected, message);
    }


    public void assertEquals(String message, byte[] expected, byte[] actual) {
        super.assertEquals(actual, expected, message);
    }

    public void assertEquals(String message, Set<?> expected, Set<?> actual) {
        super.assertEquals(actual, expected, message);
    }

    public void assertEquals(String message, double expected, double actual, double delta) {
        super.assertEquals(actual, expected, delta, message);
    }

    public void assertEquals(String message, float expected, float actual, float delta) {
        super.assertEquals(actual, expected, delta, message);
    }

    public void assertEqualsNoOrder(String message, Object[] expected, Object[] actual) {
        super.assertEqualsNoOrder(actual, expected, message);
    }

    public void assertNotEquals(String message, Object expected, Object actual) {
        super.assertNotEquals(actual, expected, message);
    }

    @Override
    public void assertNotEquals(String message, String expected, String actual) {
        super.assertNotEquals(actual, expected, message);
    }

    public void assertNotEquals(String message, long expected, long actual) {
        super.assertNotEquals(actual, expected, message);
    }

    public void assertNotEquals(String message, boolean expected, boolean actual) {
        super.assertNotEquals(actual, expected, message);
    }

    public void assertNotEquals(String message, byte expected, byte actual) {
        super.assertNotEquals(actual, expected, message);
    }

    public void assertNotEquals(String message, char expected, char actual) {
        super.assertNotEquals(actual, expected, message);
    }

    public void assertNotEquals(String message, short expected, short actual) {
        super.assertNotEquals(actual, expected, message);
    }

    public void assertNotEquals(String message, int expected, int actual) {
        super.assertNotEquals(actual, expected, message);
    }

    public void assertNotEquals(String message, float expected, float actual, float delta) {
        super.assertNotEquals(actual, expected, delta, message);
    }

    public void assertNotEquals(String message, double expected, double actual, double delta) {
        super.assertNotEquals(actual, expected, delta, message);
    }

    public void assertNotNull(String message, Object object) {
        super.assertNotNull(object, message);
    }

    public void assertNotSame(String message, Object expected, Object actual) {
        super.assertNotSame(actual, expected, message);
    }

    public void assertNull(String message, Object object) {
        super.assertNull(object, message);
    }

    public void assertSame(String message, Object expected, Object actual) {
        super.assertSame(actual, expected, message);
    }

    public void assertTrue(String message, boolean condition) {
        super.assertTrue(condition, message);
    }
}
