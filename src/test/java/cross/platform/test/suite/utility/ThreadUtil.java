package cross.platform.test.suite.utility;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class ThreadUtil {
    
    private ThreadUtil() {}
    
    public static void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        }
    }
}
