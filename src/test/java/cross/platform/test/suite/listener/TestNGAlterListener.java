package cross.platform.test.suite.listener;

import cross.platform.test.suite.guicemodule.common.ParentModule;
import lombok.extern.slf4j.Slf4j;
import org.testng.IAlterSuiteListener;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import java.util.List;

@Slf4j
public class TestNGAlterListener implements IAlterSuiteListener {
    
    @Override
    public void alter(List<XmlSuite> suites) {
        XmlSuite suite = suites.get(0);
        suite.setParentModule(ParentModule.class.getName());
        
        for (XmlTest xmlTest : suite.getTests()) {
            xmlTest.setGroupByInstances(true);
        }
    }
}
