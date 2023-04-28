package cross.platform.test.suite.listener;

import cross.platform.test.suite.guicemodule.common.ParentModule;
import org.testng.IAlterSuiteListener;
import org.testng.xml.XmlSuite;

import java.util.List;

public class TestNGAlterListener implements IAlterSuiteListener {
    
    @Override
    public void alter(List<XmlSuite> suites) {
        XmlSuite suite = suites.get(0);
        suite.setParentModule(ParentModule.class.getName());
    }
}
