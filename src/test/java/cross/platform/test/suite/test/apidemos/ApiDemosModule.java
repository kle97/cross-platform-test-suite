package cross.platform.test.suite.test.apidemos;

import cross.platform.test.suite.configuration.guicemodule.AbstractLocalModule;
import cross.platform.test.suite.constant.TestConst;

public class ApiDemosModule extends AbstractLocalModule {
    
    @Override
    public String getMobileConfigPath() {
        return TestConst.ANDROID_1_CONFIG_PATH;
    }
}
