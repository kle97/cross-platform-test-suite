package cross.platform.test.suite.verification;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BaseVerification {

    private final long nanosecond = System.nanoTime();
    private final String toString = nanosecond + "@" + getClass().getName();

    public String toString() {
        return toString;
    }
}
