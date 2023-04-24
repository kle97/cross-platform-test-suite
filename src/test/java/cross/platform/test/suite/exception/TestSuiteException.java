package cross.platform.test.suite.exception;

public class TestSuiteException extends RuntimeException {

    public TestSuiteException(String message) {
        super(message);
    }

    public TestSuiteException(String message, Throwable cause) {
        super(message, cause);
    }

    public TestSuiteException(Throwable cause) {
        super(cause);
    }
}
