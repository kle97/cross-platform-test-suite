package cross.platform.test.suite.configuration.manager;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.model.Media;
import com.aventstack.extentreports.observer.ExtentObserver;
import com.aventstack.extentreports.reporter.AbstractFileReporter;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public final class ReportManager {
    private static final ExtentReports extentReports = new ExtentReports();
    private static final List<String> reporterFilePaths = new ArrayList<>();

    private ExtentTest currentClassReport;
    private final List<ExtentTest> currentReportList = new ArrayList<>();
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

    public boolean hasClassReport() {
        return this.currentClassReport != null;
    }

    public ExtentTest getCurrentClassReport() {
        return this.currentClassReport;
    }
    
    public ExtentTest getCurrentReport() {
        return this.currentReport;
    }

    public boolean hasCurrentReport() {
        return this.currentReport != null;
    }
    
    public ExtentTest createClassReport(String className, String testTag) { 
        if (this.hasClassReport()) {
            return this.currentClassReport;
        }

        ExtentTest classReport = extentReports.createTest(className);
        if (testTag != null) {
            classReport.assignCategory(testTag);
        }
        this.currentClassReport = classReport;
        return classReport;
    }

    public ExtentTest createMethodReport(String reportName, String description, String className) {
        return this.createMethodReport(reportName, description, className, null);
    }

    public ExtentTest createMethodReport(String reportName, String description, String className, String testName) {
        ExtentTest classReport;
        if (this.hasClassReport()) {
            classReport = this.currentClassReport;
        } else {
            classReport = this.createClassReport(className, testName);
        }
        ExtentTest report = classReport.createNode(reportName, description);
        this.currentReport = report;
        this.currentReportList.add(report);
        return report;
    }

    public ExtentTest appendChildReport(String parentReportName, String reportName) {
        return this.appendChildReport(parentReportName, reportName, null);
    }

    public ExtentTest appendChildReport(String parentReportName, String reportName, String description) {
        ExtentTest parentReport = this.findReport(parentReportName);
        if (parentReport != null) {
            ExtentTest childReport = parentReport.createNode(reportName, description);
            this.currentReport = childReport;
            return childReport;
        }
        return null;
    }
    
    public void setCurrentReport(String reportName) {
        ExtentTest report = this.findReport(reportName);
        if (report != null) {
            this.currentReport = report;
        }
    }
    
    public ExtentTest findReport(String reportName) {
        ListIterator<ExtentTest> iterator = this.currentReportList.listIterator(this.currentReportList.size());
        while (iterator.hasPrevious()) {
            ExtentTest report = iterator.previous();
            if (report.getModel().getName().equals(reportName)) {
                return report;
            }
        }
        return null;
    }

    public void removeReport(ExtentTest report) {
        extentReports.removeTest(report);
        this.currentReportList.remove(report);
    }

    public void removeReport(String reportName) {
        this.currentReportList.stream()
                              .filter(t -> t.getModel().getName().equals(reportName))
                              .findFirst()
                              .ifPresent(extentReports::removeTest);
    }

    public void removeCurrentReport() {
        if (this.hasCurrentReport()) {
            this.removeReport(this.getCurrentReport());
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

    public ExtentTest log(Status status, String message, Throwable throwable, Media media) {
        ExtentTest currentReport = this.getCurrentReport();
        if (currentReport != null) {
            currentReport.log(status, message, throwable, media);
        }
        return currentReport;
    }
}
