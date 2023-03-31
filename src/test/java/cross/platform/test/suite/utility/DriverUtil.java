package cross.platform.test.suite.utility;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.HidesKeyboard;
import io.appium.java_client.functions.ExpectedCondition;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.KeyInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

@Slf4j
public final class DriverUtil {

    public static final int MAX_SEND_KEYS_RETRIES = 3; // times
    public static final int PAGE_SOURCE_MAX_RETRIES = 3; // times
    public static final int DEFAULT_WAIT_INTERVAL = 1000; // ms
    public static final String ELEMENT_NOT_FOUND = "ELEMENT_NOT_FOUND";

    // static method access only
    private DriverUtil() {
    }

    /**
     * Gets current page source from driver
     *
     * @param appiumDriver the AppiumDriver instance.
     * @return the current page source
     */
    public static String getPageSource(AppiumDriver appiumDriver) {
        for (int attempt = 0; attempt < PAGE_SOURCE_MAX_RETRIES; attempt++) {
            try {
                return appiumDriver.getPageSource();
            } catch (WebDriverException ex) {
                log.debug(ex.getMessage());
            }
        }
        log.debug("Failed to get page source after {} attempts.", PAGE_SOURCE_MAX_RETRIES);
        return "Page source not found.";
    }

    /**
     * Allows the execution of long press gestures on an element.
     *
     * @param appiumDriver the AppiumDriver instance.
     * @param element      the element to be long pressed on.
     */
    public static void longPress(AppiumDriver appiumDriver, WebElement element) {
        Actions action = new Actions(appiumDriver);
        action.clickAndHold(element).perform();
        waitForElement(appiumDriver, element);
    }

    /**
     * Wait until element is visible or after one second.
     *
     * @param appiumDriver the AppiumDriver instance.
     * @param element      the expected element.
     * @return the expected element.
     * @throws TimeoutException throws exception when timeout limit is reached.
     */
    public static WebElement waitForElement(AppiumDriver appiumDriver, WebElement element) throws TimeoutException {
        return waitForElement(appiumDriver, element, DEFAULT_WAIT_INTERVAL / 1000);
    }

    /**
     * Wait until element is visible or timeout.
     *
     * @param appiumDriver     the AppiumDriver instance.
     * @param element          the expected element.
     * @param timeOutInSeconds the timeout limit in seconds.
     * @return the expected element.
     * @throws TimeoutException throws exception when timeout limit is reached.
     */
    public static WebElement waitForElement(AppiumDriver appiumDriver, WebElement element,
                                            int timeOutInSeconds) throws TimeoutException {
        WebDriverWait wait = new WebDriverWait(appiumDriver, Duration.ofSeconds(timeOutInSeconds));
        return wait.until(ExpectedConditions.visibilityOf(element));
    }

    /**
     * Wait until all specified elements are visible or after one second.
     *
     * @param appiumDriver the AppiumDriver instance.
     * @param elements     the list of expected elements
     * @return the list of expected elements
     * @throws TimeoutException throws exception when timeout limit is reached.
     */
    public static List<WebElement> waitForAllElements(AppiumDriver appiumDriver,
                                                      List<WebElement> elements) throws TimeoutException {
        return waitForAllElements(appiumDriver, elements, DEFAULT_WAIT_INTERVAL / 1000);
    }

    /**
     * Wait until all specified elements are visible or timeout.
     *
     * @param webDriver        the WebDriver instance.
     * @param elements         the list of expected elements.
     * @param timeOutInSeconds the timeout limit in seconds.
     * @return the list of expected elements.
     * @throws TimeoutException throws exception when timeout limit is reached.
     */
    public static List<WebElement> waitForAllElements(WebDriver webDriver, List<WebElement> elements,
                                                      int timeOutInSeconds) throws TimeoutException {
        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(timeOutInSeconds));
        return wait.until(ExpectedConditions.visibilityOfAllElements(elements));
    }

    /**
     * Wait until element disappears or timeout.
     *
     * @param webDriver        the WebDriver instance.
     * @param element          the expected element.
     * @param timeOutInSeconds the timeout limit in seconds.
     * @return whether the element disappears or not.
     */
    public static boolean waitForElementDisappearance(WebDriver webDriver, WebElement element, int timeOutInSeconds) {
        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(timeOutInSeconds));
        try {
            return wait.until(invisibilityOf(element));
        } catch (TimeoutException ex) {
            log.debug(ex.getMessage());
            return false;
        }
    }

    /**
     * Wait until all elements disappear or timeout.
     *
     * @param webDriver        the WebDriver instance to pass to the expected conditions
     * @param elements         the list of all expected elements
     * @param timeOutInSeconds the timeout in seconds when an expectation is called
     * @return whether all the elements disappear or not
     */
    public static boolean waitForAllElementsDisappearance(WebDriver webDriver, List<WebElement> elements,
                                                          int timeOutInSeconds) {
        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(timeOutInSeconds));
        try {
            return wait.until(invisibilityOfAllElements(elements));
        } catch (TimeoutException ex) {
            log.debug(ex.getMessage());
            return false;
        }
    }

    private static ExpectedCondition<Boolean> invisibilityOf(final WebElement element) {
        return new ExpectedCondition<>() {
            @Override
            public Boolean apply(WebDriver webDriver) {
                try {
                    return !element.isDisplayed();
                } catch (WebDriverException ex) {
                    log.debug(ex.getMessage());
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
        } catch (WebDriverException | NullPointerException ex) {
            log.debug(ex.getMessage());
            return true;
        }
    }

    /**
     * Send text to an element with retries.
     * This method uses a soft assertion for {@link WebDriverException}.
     *
     * @param element the element to set text
     * @param text    the text to send
     */
    public static void setTextWithRetry(WebElement element, String text) {
        for (int index = 0; index < MAX_SEND_KEYS_RETRIES; index++) {
            try {
                element.sendKeys(text);
                if (getText(element).equals(text)) {
                    return;
                }
            } catch (WebDriverException | NullPointerException ex) {
                log.debug(ex.getMessage());

            }
        }

        log.debug(String.format("Couldn't set text to element after %d tries", MAX_SEND_KEYS_RETRIES));
    }

    /**
     * Sometimes, the default element.sendKeys() method doesn't work.
     * This method is a low level workaround for it.
     *
     * @param appiumDriver the AppiumDriver instance.
     * @param element      the element to set text
     * @param text         the text to send
     */
    public static void sendKeys(AppiumDriver appiumDriver, WebElement element, String text) {
        if (appiumDriver instanceof HidesKeyboard) {
            ((HidesKeyboard) appiumDriver).hideKeyboard();
        }
        element.clear();
        element.click();
        KeyInput keyboard = new KeyInput("keyboard");
        Sequence sequence = new Sequence(keyboard, 0);

        if (text.length() > 0) {
            for (String character : text.split("(?<=.)")) {
                sequence.addAction(keyboard.createKeyDown(character.codePointAt(0)));
                sequence.addAction(keyboard.createKeyUp(character.codePointAt(0)));
            }
        }

        appiumDriver.perform(List.of(sequence));
    }

    /**
     * Get text (or value attribute) from an element.
     * This method uses a soft assertion for {@link WebDriverException}
     * and return ELEMENT_NOT_FOUND instead.
     *
     * @param element the element to get text
     * @return text value of the element
     */
    public static String getText(WebElement element) {
        try {
            return element.getText();
        } catch (WebDriverException | NullPointerException ex) {
            log.debug(ex.getMessage());
            return ELEMENT_NOT_FOUND;
        }
    }

    /**
     * Check whether the element is displayed.
     * This method uses a soft assertion for {@link WebDriverException} and
     * return a false value.
     *
     * @param element the element to check if it's display
     * @return whether the element is displayed
     */
    public static boolean isDisplayed(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (WebDriverException | NullPointerException ex) {
            log.debug(ex.getMessage());
            return false;
        }
    }

    /**
     * Check whether the element is selected.
     * This method uses a soft assertion for {@link WebDriverException} and
     * return a false value.
     *
     * @param element the element to check if it's selected
     * @return whether the element is selected
     */
    public static boolean isSelected(WebElement element) {
        try {
            return element.isSelected();
        } catch (WebDriverException | NullPointerException ex) {
            log.debug(ex.getMessage());
            return false;
        }
    }

    /**
     * Check whether the element is checked.
     * This method uses a soft assertion for {@link WebDriverException} and
     * return a false value.
     *
     * @param element the element to check if it's selected
     * @return whether the element is selected
     */
    public static boolean isChecked(WebElement element) {
        try {
            return parseBoolean(element.getAttribute("checked"));
        } catch (WebDriverException | NullPointerException ex) {
            log.debug(ex.getMessage());
            return false;
        }
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
}