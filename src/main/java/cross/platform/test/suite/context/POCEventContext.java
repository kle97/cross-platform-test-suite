package cross.platform.test.suite.context;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;

@Data
@Setter(AccessLevel.NONE)
@AllArgsConstructor
public class POCEventContext {
    
    private String eventName;
}
