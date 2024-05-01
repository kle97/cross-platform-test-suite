package cross.platform.test.suite.common;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.HidesKeyboard;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.functions.ExpectedCondition;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.pagefactory.Widget;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.KeyInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
public final class DriverUtil {

    public static final int PAGE_SOURCE_MAX_RETRIES = 3; // time(s)
    public static final int DEFAULT_WAIT_TIME_IN_MS = 1000; // millisecond(s)
    public static final String ELEMENT_NOT_FOUND = "ElementNotFound";
    public static final String PAGE_SOURCE_NOT_FOUND = "Page source is not found!";
    public static final int DEFAULT_PRESS_TIME_IN_MS = 1000; // ms
    public static final int DEFAULT_POLLING_PERIOD_IN_MS = 100; // ms

    private DriverUtil() {
    }

    /**
     * Check if driver is for iOS platform.
     * @param webDriver the WebDriver instance.
     * @return whether driver is an instance of {@link IOSDriver}
     */
    public boolean isIOS(WebDriver webDriver) {
        return webDriver instanceof IOSDriver;
    }

    /**
     * Check if driver is for Android platform.
     * @param webDriver the WebDriver instance.
     * @return whether driver is an instance of {@link AndroidDriver}
     */
    public boolean isAndroid(WebDriver webDriver) {
        return webDriver instanceof AndroidDriver;
    }

    /**
     * Gets current page source from driver.
     * Suppress any {@link WebDriverException} and return {@value #PAGE_SOURCE_NOT_FOUND} instead.
     *
     * @param webDriver the WebDriver instance.
     * @return the current page source
     */
    public static String getPageSource(WebDriver webDriver) {
        for (int attempt = 0; attempt < PAGE_SOURCE_MAX_RETRIES; attempt++) {
            try {
                return webDriver.getPageSource();
            } catch (WebDriverException e) {
                log.trace(e.getMessage());
            }
        }
        log.debug("Failed to get page source after {} attempts.", PAGE_SOURCE_MAX_RETRIES);
        return PAGE_SOURCE_NOT_FOUND;
    }

    /**
     * Performs a double click at middle of the given element.
     * Suppress any {@link WebDriverException}.
     *
     * @param element the element to perform double click on.
     */
    public static void doubleClick(WebElement element) {
        WebDriver webDriver = ((WrapsDriver) element).getWrappedDriver();
        Actions action = new Actions(webDriver);
        action.doubleClick(element).perform();
    }

    /**
     * Allows the execution of long press gestures on an element for {@value #DEFAULT_PRESS_TIME_IN_MS}ms.
     * Suppress any {@link WebDriverException}.
     *
     * @param element the element to be long pressed on.
     */
    public static void longPress(WebElement element) {
        longPress(element, DEFAULT_PRESS_TIME_IN_MS);
    }

    /**
     * Allows the execution of long press gestures on an element for a period of time.
     * Suppress any {@link WebDriverException}.
     *
     * @param element   the element to be long pressed on.
     * @param pressTimeInMs press time in milliseconds.
     */
    public static void longPress(WebElement element, int pressTimeInMs) {
        WebDriver webDriver = ((WrapsDriver) element).getWrappedDriver();
        Actions action = new Actions(webDriver);
        action.clickAndHold(element).pause(pressTimeInMs).release().perform();
    }

    /**
     * Drag an element by offset
     * @param element element to drag
     * @param xOffset horizontal move offset.
     * @param yOffset vertical move offset.
     */
    public static void dragElement(WebElement element, int xOffset, int yOffset) {
        WebDriver webDriver = ((WrapsDriver) element).getWrappedDriver();
        Actions action = new Actions(webDriver);
        action.dragAndDropBy(element, xOffset, yOffset);
    }

    /**
     * Convenient function for clearing and sending keys to text box field.
     * Combining two methods into one. (element.clear(); element.sendKeys(s)).
     *
     * @param element    the element to set text
     * @param keysToSend character sequence to send to the element
     */
    public static void sendKeys(WebElement element, CharSequence ...keysToSend) {
        element.clear();
        element.sendKeys(keysToSend);
    }

    /**
     * Sometimes, the default element.sendKeys() method doesn't work.
     * This method is a low level workaround for it.
     *
     * @param element    the element to set text
     * @param keysToSend character sequence to send to the element
     */
    public static void sendKeysLowLevel(WebElement element, CharSequence ...keysToSend) {
        WebDriver webDriver = ((WrapsDriver) element).getWrappedDriver();
        if (webDriver instanceof HidesKeyboard) {
            ((HidesKeyboard) webDriver).hideKeyboard();
        }
        element.clear();
        element.click();
        KeyInput keyboard = new KeyInput("keyboard");
        Sequence sequence = new Sequence(keyboard, 0);

        String text = Arrays.toString(keysToSend);
        if (text.length() > 0) {
            for (String character : text.split("(?<=.)")) {
                sequence.addAction(keyboard.createKeyDown(character.codePointAt(0)));
                sequence.addAction(keyboard.createKeyUp(character.codePointAt(0)));
            }
        }

        if (webDriver instanceof AppiumDriver) {
            ((AppiumDriver) webDriver).perform(List.of(sequence));
        }
    }

    /**
     * Get text (or value attribute) from an element.
     * Suppress any {@link WebDriverException} and return {@value #ELEMENT_NOT_FOUND} instead.
     *
     * @param element the element to get text
     * @return text value of the element
     */
    public static String getText(WebElement element) {
        try {
            return element.getText();
        } catch (WebDriverException | NullPointerException e) {
            log.debug(e.getMessage());
            return ELEMENT_NOT_FOUND;
        }
    }

    /**
     * Get the value of the given attribute of the element.
     * Suppress any {@link WebDriverException} and return {@value #ELEMENT_NOT_FOUND} instead.
     *
     * @param element the element to get attribute
     * @param name given attribute name
     * @return value of the given attribute
     */
    public static String getAttribute(WebElement element, String name) {
        try {
            return element.getAttribute(name);
        } catch (WebDriverException | NullPointerException e) {
            log.debug(e.getMessage());
            return ELEMENT_NOT_FOUND;
        }
    }

    /**
     * Check whether the element is displayed.
     * Suppress any {@link WebDriverException} and return a false value instead.
     *
     * @param element the element to check if it's displayed
     * @param <T>     the type of element
     * @return whether the element is displayed
     */
    public static <T extends SearchContext> boolean isDisplayed(T element) {
        try {
            WebElement webElement;
            if (element instanceof WebElement)  {
                webElement = (WebElement) element;
            } else if (element instanceof Widget) {
                webElement = ((Widget) element).getWrappedElement();
            } else {
                return false;
            }
            return webElement.isDisplayed();
        } catch (WebDriverException | NullPointerException e) {
            log.debug(e.getMessage());
            return false;
        }
    }

    /**
     * Wait until the element is displayed or timeout.
     * Suppress any {@link WebDriverException} or {@link TimeoutException} and return a false value instead.
     *
     * @param element     the element to check if it's displayed
     * @param timeOutInMs the timeout limit in milliseconds
     * @param <T>         the type of element
     * @return whether the element is displayed
     */
    public static <T extends SearchContext> boolean isDisplayed(T element, int timeOutInMs) {
        WebElement webElement;
        if (element instanceof WebElement)  {
            webElement = (WebElement) element;
        } else if (element instanceof Widget) {
            webElement = ((Widget) element).getWrappedElement();
        } else {
            return false;
        }

        try {
            return waitForElement(webElement, timeOutInMs) != null;
        } catch (WebDriverException | NullPointerException e) {
            log.debug(e.getMessage());
            return false;
        }
    }

    /**
     * Check whether the element is selected.
     * Suppress any {@link WebDriverException} and return a false value instead.
     *
     * @param element the element to check if it's selected
     * @return whether the element is selected
     */
    public static boolean isSelected(WebElement element) {
        try {
            return element.isSelected();
        } catch (WebDriverException | NullPointerException e) {
            log.debug(e.getMessage());
            return false;
        }
    }

    /**
     * Check whether the element is checked.
     * Suppress any {@link WebDriverException} and return a false value instead.
     *
     * @param element the element to check if it's selected
     * @return whether the element is selected
     */
    public static boolean isChecked(WebElement element) {
        try {
            return parseBoolean(element.getAttribute("checked"));
        } catch (WebDriverException | NullPointerException e) {
            log.debug(e.getMessage());
            return false;
        }
    }

    /**
     * get WebElement from the generic element that extends {@link SearchContext}
     * @param element the generic element to extract from
     * @return WebElement instance
     * @param <T> type of element
     * @throws NoSuchElementException if WebElement can't be retrieved
     */
    public static <T extends SearchContext> WebElement getWebElement(T element) {
        if (element instanceof WebElement) {
            return (WebElement) element;
        } else if (element instanceof WrapsElement) {
            return ((WrapsElement) element).getWrappedElement();
        }
        throw new NoSuchElementException("Failed to extract WebElement!");
    }

    private static boolean parseBoolean(String value) {
        if (value == null) {
            return false;
        }
        boolean isOne = "1".equalsIgnoreCase(value);
        boolean isTrue = "true".equalsIgnoreCase(value);
        boolean isOn = "on".equalsIgnoreCase(value);
        boolean isChecked = value.contains("-checked");
        boolean isIosToggleOn = "Toggled".equalsIgnoreCase(value);
        boolean isIosPasswordToggleOn = value.contains("-hide");
        boolean isIosRadioButtonSelected = value.contains("-selected");
        return (isOne || isTrue || isOn || isChecked || isIosToggleOn || isIosPasswordToggleOn || isIosRadioButtonSelected);
    }

    /**
     * Return the current window dimension, not just the view port.
     *
     * @param webDriver the WebDriver instance
     * @return current window size
     */
    public static Dimension getWindowSize(WebDriver webDriver) {
        return webDriver.manage().window().getSize();
    }

    /**
     * Wait until element is visible or timeout.
     * Timeout after {@value #DEFAULT_WAIT_TIME_IN_MS} millisecond(s).
     * Suppress any {@link WebDriverException}.
     *
     * @param element   the expected element.
     * @return the expected element.
     * @throws TimeoutException throws exception when timeout limit is reached.
     */
    public static WebElement waitForElement(WebElement element) throws TimeoutException {
        return waitForElement(element, DEFAULT_WAIT_TIME_IN_MS);
    }

    /**
     * Wait until element is visible or timeout.
     * Suppress any {@link WebDriverException}.
     *
     * @param element          the expected element.
     * @param timeOutInMs the timeout limit in milliseconds.
     * @return the expected element.
     * @throws TimeoutException throws exception when timeout limit is reached.
     */
    public static WebElement waitForElement(WebElement element, int timeOutInMs) throws TimeoutException {
        WebDriver webDriver = ((WrapsDriver) element).getWrappedDriver();
        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofMillis(timeOutInMs));
        return wait.pollingEvery(Duration.ofMillis(DEFAULT_POLLING_PERIOD_IN_MS))
                   .until(ExpectedConditions.visibilityOf(element));
    }

    /**
     * Wait until all specified elements are visible.
     * Timeout after {@value #DEFAULT_WAIT_TIME_IN_MS} millisecond(s).
     * Suppress any {@link WebDriverException}.
     *
     * @param webDriver the WebDriver instance.
     * @param elements  the list of expected elements
     * @return the list of expected elements
     * @throws TimeoutException throws exception when timeout limit is reached.
     */
    public static List<WebElement> waitForAllElements(WebDriver webDriver,
                                                      List<WebElement> elements) throws TimeoutException {
        return waitForAllElements(webDriver, elements, DEFAULT_WAIT_TIME_IN_MS);
    }

    /**
     * Wait until all specified elements are visible or timeout.
     * Suppress any {@link WebDriverException}.
     *
     * @param webDriver        the WebDriver instance.
     * @param elements         the list of expected elements.
     * @param timeOutInMs the timeout limit in milliseconds.
     * @return the list of expected elements.
     * @throws TimeoutException throws exception when timeout limit is reached.
     */
    public static List<WebElement> waitForAllElements(WebDriver webDriver, List<WebElement> elements,
                                                      int timeOutInMs) throws TimeoutException {
        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofMillis(timeOutInMs));
        return wait.pollingEvery(Duration.ofMillis(DEFAULT_POLLING_PERIOD_IN_MS))
                   .until(ExpectedConditions.visibilityOfAllElements(elements));
    }

    /**
     * Wait until element disappears or timeout.
     * Suppress any {@link WebDriverException}.
     *
     * @param element          the expected element.
     * @param timeOutInMs the timeout limit in milliseconds.
     * @return whether the element disappears or not.
     */
    public static boolean waitForElementDisappearance(WebElement element, long timeOutInMs) {
        WebDriver webDriver = ((WrapsDriver) element).getWrappedDriver();
        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofMillis(timeOutInMs));
        try {
            return wait.pollingEvery(Duration.ofMillis(DEFAULT_POLLING_PERIOD_IN_MS))
                       .until(invisibilityOf(element));
        } catch (TimeoutException e) {
            log.debug(e.getMessage());
            return false;
        }
    }

    /**
     * Wait until all elements disappear or timeout.
     * Suppress any {@link WebDriverException}.
     *
     * @param webDriver        the WebDriver instance to pass to the expected conditions
     * @param elements         the list of all expected elements
     * @param timeOutInMs the timeout in milliseconds when an expectation is called
     * @return whether all the elements disappear or not
     */
    public static boolean waitForAllElementsDisappearance(WebDriver webDriver, List<WebElement> elements,
                                                          int timeOutInMs) {
        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofMillis(timeOutInMs));
        try {
            return wait.pollingEvery(Duration.ofMillis(DEFAULT_POLLING_PERIOD_IN_MS))
                       .until(invisibilityOfAllElements(elements));
        } catch (TimeoutException e) {
            log.debug(e.getMessage());
            return false;
        }
    }

    private static ExpectedCondition<Boolean> invisibilityOf(final WebElement element) {
        return new ExpectedCondition<>() {
            @Override
            public Boolean apply(WebDriver webDriver) {
                try {
                    return !element.isDisplayed();
                } catch (WebDriverException e) {
                    log.debug(e.getMessage());
                    return true;
                }
            }

            @Override
            public String toString() {
                return "invisibility of " + element;
            }
        };
    }

    private static ExpectedCondition<Boolean> invisibilityOfAllElements(final List<WebElement> elements) {
        return new ExpectedCondition<>() {

            @Override
            public Boolean apply(WebDriver webDriver) {
                return elements.stream().allMatch(DriverUtil::isInvisible);
            }

            @Override
            public String toString() {
                return "invisibility of all elements " + elements;
            }
        };
    }

    private static boolean isInvisible(final WebElement element) {
        try {
            return !element.isDisplayed();
        } catch (WebDriverException | NullPointerException e) {
            log.debug(e.getMessage());
            return true;
        }
    }

    /**
     * Executes driver script.
     * Suppress any thrown exception.
     *
     * @param webDriver  the WebDriver instance.
     * @param script     the script to execute.
     * @param parameters the parameters of the script.
     * @return result of execution if available, otherwise null.
     */
    public static Object executeScript(WebDriver webDriver, String script, Object... parameters) {
        if (webDriver instanceof JavascriptExecutor) {
            try {
                return ((JavascriptExecutor) webDriver).executeScript(script, parameters);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        return null;
    }

    /**
     * Executes driver shell script.
     * Suppress any thrown exception.
     *
     * @param webDriver the WebDriver instance.
     * @param command   the shell script's command to execute.
     * @param args      the arguments of the shell script.
     * @return result of execution if available, otherwise null.
     */
    public static Object executeShellScript(WebDriver webDriver, String command, String args) {
        if (webDriver instanceof JavascriptExecutor) {
            try {
                return ((JavascriptExecutor) webDriver).executeScript("mobile: shell", 
                                                                      Map.ofEntries(Map.entry("command", command), 
                                                                                    Map.entry("args", args)));
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        return null;
    }

    /**
     * Retrieve app version from app package or bundle ID
     * Suppress any thrown exception.
     *
     * @param webDriver  the WebDriver instance.
     * @param appPackage app package or bundle id to retrieve version from.
     * @return version of app if available, otherwise null.
     */
    public static String getAppVersion(WebDriver webDriver, String appPackage) {
        try {
            if (webDriver instanceof AndroidDriver) {
                return (String) executeShellScript(webDriver,
                                                   "dumpsys",
                                                   "package " + appPackage + " | grep versionCode | grep -o -E '[0-9.]+' | head -1 | tr -d '\n'");
            } else {
                Map<String, Map<String, String>> listApps = (Map<String, Map<String, String>>) executeScript(webDriver, "listApps", Map.of());
                return listApps.get(appPackage).get("CFBundleVersion");
            }
        } catch (Exception e) {
            log.warn("App version not found for bundleId/appPackage {}!", appPackage);
            return "APP_VERSION_NOT_FOUND";
        }
    }

    /**
     * Alternative way to accept iOS system popup dialog.
     * Suppress any thrown exception.
     *
     * @param webDriver the WebDriver instance.
     */
    public void acceptIOSPopupDialog(WebDriver webDriver) {
        acceptIOSPopupDialog(webDriver, "Allow");
    }

    /**
     * Alternative way to accept iOS system popup dialog.
     * Suppress any thrown exception.
     *
     * @param webDriver   the WebDriver instance.
     * @param buttonLabel button label to interact.
     */
    public void acceptIOSPopupDialog(WebDriver webDriver, String buttonLabel) {
        if (isIOS(webDriver)) {
            executeScript(webDriver, "mobile: alert",
                          Map.of("action", "accept", "buttonLabel", buttonLabel));
        }
    }

    /**
     * Alternative way to dismiss iOS system popup dialog.
     * Suppress any thrown exception.
     *
     * @param webDriver the WebDriver instance.
     */
    public void dismissIOSPopupDialog(WebDriver webDriver) {
        dismissIOSPopupDialog(webDriver, "Don’t Allow");
    }

    /**
     * Alternative way to dismiss iOS system popup dialog.
     * Suppress any thrown exception.
     *
     * @param webDriver   the WebDriver instance.
     * @param buttonLabel button label to interact.
     */
    public void dismissIOSPopupDialog(WebDriver webDriver, String buttonLabel) {
        if (isIOS(webDriver)) {
            executeScript(webDriver, "mobile: alert",
                          Map.of("action", "dismiss", "buttonLabel", buttonLabel));
        }
    }

    /**
     * Alternative way to get iOS system popup dialog message.
     * Suppress any thrown exception.
     *
     * @param webDriver the WebDriver instance.
     */
    public String getIOSPopupMessage(WebDriver webDriver) {
        if (isIOS(webDriver)) {
            try {
                String message = (String) executeScript(webDriver, "mobile: alert",
                                                        Map.of("action", "accept", "buttonLabel", "NA"));
                return message.substring(message.indexOf("for alert: \"") + 12, message.indexOf("\" Alert\""));
            } catch (Exception ignored) {
            }
        }
        return ELEMENT_NOT_FOUND;
    }
}
