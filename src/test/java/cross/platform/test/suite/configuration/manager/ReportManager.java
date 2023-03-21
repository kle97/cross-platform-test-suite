package cross.platform.test.suite.configuration.manager;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.model.Media;
import com.aventstack.extentreports.observer.ExtentObserver;
import com.aventstack.extentreports.reporter.AbstractFileReporter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ReportManager {
    private static final ExtentReports extentReports = new ExtentReports();
    private static final List<String> reporterFilePaths = new ArrayList<>();

    private final Map<String, ExtentTest> classReportMap = new HashMap<>();
    private ExtentTest currentReport;

    public static List<String> getReporterFilePaths() {
        return List.copyOf(reporterFilePaths);
    }

    @SuppressWarnings("rawtypes")
    public static void attachReporter(ExtentObserver... observers) {
        extentReports.attachReporter(observers);
        for (ExtentObserver observer : observers) {
            if (observer instanceof AbstractFileReporter) {
                String path = ((AbstractFileReporter) observer).getFile().toURI().toString()
                                                               .replace("file:/", "file:///");
                reporterFilePaths.add(path);
            }
        }
    }

    public static void setSystemInfo(String key, String value) {
        extentReports.setSystemInfo(key, value);
    }

    public static void flush() {
        extentReports.flush();
    }

    public boolean hasClassReport(String className) {
        return this.classReportMap.containsKey(className);
    }

    public ExtentTest getCurrentReport() {
        return this.currentReport;
    }

    public boolean hasCurrentReport() {
        return this.currentReport != null;
    }
    
    public ExtentTest getClassReport(String className) {
        if (this.hasClassReport(className)) {
            return classReportMap.get(className);
        } else {
            return null;
        }
    }

    public ExtentTest createClassReport(String className, String testTag) {
        if (this.hasClassReport(className)) {
            return classReportMap.get(className);
        }

        ExtentTest classReport = extentReports.createTest(className);
        if (testTag != null) {
            classReport.assignCategory(testTag);
        }
        classReportMap.put(className, classReport);
        return classReport;
    }

    public ExtentTest createMethodReport(String reportName, String description, String className) {
        return this.createMethodReport(reportName, description, className, null);
    }

    public ExtentTest createMethodReport(String reportName, String description, String className, String testName) {
        ExtentTest classReport;
        if (this.hasClassReport(className)) {
            classReport = this.classReportMap.get(className);
        } else {
            classReport = this.createClassReport(className, testName);
        }
        ExtentTest report = classReport.createNode(reportName, description);
        currentReport = report;
        return report;
    }

    public ExtentTest appendNodeToCurrentReport(String reportName) {
        return this.appendNodeToCurrentReport(reportName, null);
    }

    public ExtentTest appendNodeToCurrentReport(String reportName, String description) {
        if (this.hasCurrentReport()) {
            ExtentTest currentReport = this.currentReport;
            return currentReport.createNode(reportName, description);
        }

        return null;
    }

    public void removeReport(ExtentTest report) {
        this.removeReport(report.getModel().getName());
    }

    public void removeReport(String reportName) {
        extentReports.removeTest(reportName);
    }

    public void removeCurrentReport() {
        if (this.hasCurrentReport()) {
            extentReports.removeTest(this.getCurrentReport());
        }
    }

    public ExtentTest addScreenshotFromBase64String(String base64String) {
        return this.addScreenshotFromBase64String(base64String, null);
    }

    public ExtentTest addScreenshotFromBase64String(String base64String, String title) {
        Media media = MediaEntityBuilder.createScreenCaptureFromBase64String(base64String, title).build();
        return this.log(Status.INFO, null, null, media);
    }

    public ExtentTest addScreenshot(String screenshotPath) {
        return this.addScreenshot(screenshotPath, null);
    }

    public ExtentTest addScreenshot(String screenshotPath, String title) {
        Media media = MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath, title).build();
        return this.log(Status.INFO, null, null, media);
    }

    public ExtentTest info(String message) {
        return this.log(Status.INFO, message, null, null);
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
