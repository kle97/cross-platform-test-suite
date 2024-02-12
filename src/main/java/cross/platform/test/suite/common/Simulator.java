package cross.platform.test.suite.common;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Simulator {
    
    public void setEvent(String eventName, boolean toggle) {
        log.info("Turned {} event '{}'!", toggle ? "on" : "off", eventName);  
    }
}
