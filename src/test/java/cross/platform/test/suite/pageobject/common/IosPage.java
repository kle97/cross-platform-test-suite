package cross.platform.test.suite.pageobject.common;

import cross.platform.test.suite.service.DriverManager;
import cross.platform.test.suite.service.POMFactory;

public abstract class IosPage extends AbstractPage {
    
    public IosPage(DriverManager driverManager, POMFactory pomFactory) {
        super(driverManager, pomFactory);
    }
}
