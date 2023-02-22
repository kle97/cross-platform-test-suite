package cross.platform.test.suite.configuration.manager;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.model.Media;
import com.aventstack.extentreports.observer.ExtentObserver;

import java.util.HashMap;
import java.util.Map;

import static java.lang.StackWalker.Option.RETAIN_CLASS_REFERENCE;

public final class ReportManager {
    private final StackWalker stackWalker = StackWalker.getInstance(RETAIN_CLASS_REFERENCE);
    private final ExtentReports extentReports = new ExtentReports();
    private final ThreadLocal<Map<String, ExtentTest>> classReportMapThreadLocal = ThreadLocal.withInitial(() -> new HashMap<>());
    private final ThreadLocal<ExtentTest> currentReportThreadLocal = new ThreadLocal<>();
    
    public boolean hasClassReport(String className) {
        return this.classReportMapThreadLocal.get().containsKey(className);
    }
    
    public ExtentTest getCurrentReport() {
        return this.currentReportThreadLocal.get();
    }
    
    public boolean hasCurrentReport() {
        return this.currentReportThreadLocal.get() != null;
    }
    
    @SuppressWarnings("rawtypes")
    public void attachReporter(ExtentObserver... observer) {
        this.extentReports.attachReporter(observer);
    }
    
    public void setSystemInfo(String key, String value) {
        this.extentReports.setSystemInfo(key, value);
    }
    
    public ExtentTest createClassReport(String className, String testTag) {
        if (this.hasClassReport(className)) {
            return classReportMapThreadLocal.get().get(className);
        }
        
        ExtentTest classReport = this.extentReports.createTest(className);
        if (testTag != null) {
            classReport.assignCategory(testTag);
        }
        classReportMapThreadLocal.get().put(className, classReport);
        return classReport;
    }
    
    public ExtentTest createMethodReport(String reportName) {
        return this.createMethodReport(reportName, null);
    }
    
    public ExtentTest createMethodReport(String reportName, String description) {
        String callerClassName = this.stackWalker.getCallerClass().getCanonicalName();
        ExtentTest report;

        if (callerClassName != null && this.hasClassReport(callerClassName)) {
            ExtentTest classReport = this.classReportMapThreadLocal.get().get(callerClassName);
            report = classReport.createNode(reportName, description);
        } else {
            report = this.extentReports.createTest(reportName, description);
        }
        currentReportThreadLocal.set(report);
        return report;
    }
    
    public ExtentTest createMethodReport(String reportName, String description, String className) {
        return this.createMethodReport(reportName, description, className, null);
    }
    
    public ExtentTest createMethodReport(String reportName, String description, String className, String testName) {
        ExtentTest classReport;
        if (this.hasClassReport(className)) {
            classReport = this.classReportMapThreadLocal.get().get(className);
        } else {
            classReport = this.createClassReport(className, testName);
        }
        ExtentTest report = classReport.createNode(reportName, description);
        currentReportThreadLocal.set(report);
        return report;
    }
    
    public ExtentTest appendNodeToCurrentReport(String reportName) {
        return this.appendNodeToCurrentReport(reportName, null);
    }
    
    public ExtentTest appendNodeToCurrentReport(String reportName, String description) {
        if (this.hasCurrentReport()) {
            ExtentTest currentReport = this.currentReportThreadLocal.get();
            return currentReport.createNode(reportName, description);
        }
        
        return null;
    }
    
    public void removeReport(ExtentTest report) {
        this.removeReport(report.getModel().getName());
    }
    
    public void removeReport(String reportName) {
        if (this.hasCurrentReport() && this.getCurrentReport().getModel().getName().equals(reportName)) {
            this.currentReportThreadLocal.remove();
        }
        this.extentReports.removeTest(reportName);
    }
    
    public void removeCurrentReport() {
        if (this.hasCurrentReport()) {
            ExtentTest currentReport = this.getCurrentReport();
            this.currentReportThreadLocal.remove();
            this.extentReports.removeTest(currentReport);
        }
    }
    
    public void flush() {
        this.extentReports.flush();
    }
    
    public ExtentTest info(String message) {
        return this.log(Status.INFO, message, null, null);
    }
    
    public ExtentTest addScreenshot(String screenshotPath) {
        Media media = MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build();
        return this.log(Status.INFO, null, null, media);
    }
    
    public ExtentTest info(String message, String screenshotPath) {
        Media media = MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build();
        return this.log(Status.INFO, message, null, media);
    }
    
    public ExtentTest pass(String message) {
        return this.log(Status.PASS, message, null, null);
    }
    
    public ExtentTest fail(String message) {
        return this.log(Status.FAIL, message, null, null);
    }
    
    public ExtentTest fail(String message, Throwable throwable) {
        return this.log(Status.FAIL, message, throwable, null);
    }
    
    public ExtentTest fail(String message, String screenshotPath) {
        return this.fail(message, null, screenshotPath);
    }
    
    public ExtentTest fail(String message, Throwable throwable, String screenshotPath) {
        Media media = MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build();
        return this.log(Status.FAIL, message, throwable, media);
    }
    
    private ExtentTest log(Status status, String message, Throwable throwable, Media media) {
        ExtentTest currentReport = this.getCurrentReport();
        if (currentReport != null) {
            currentReport.log(status, message, throwable, media);
        }
        return currentReport;
    }
}
