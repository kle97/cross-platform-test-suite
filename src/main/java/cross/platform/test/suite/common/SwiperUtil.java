package cross.platform.test.suite.common;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.remote.RemoteWebElement;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

@Slf4j
public final class SwiperUtil {

    public static final int DEFAULT_MAX_SCROLL_SEARCH = 10; // time(s)
    public static final double DEFAULT_SCROLL_PERCENTAGE = 0.97; // xx%
    public static final double DEFAULT_SPEED_PERCENTAGE = 1.0; // xx%
    private static final XPath XPATH = XPathFactory.newInstance().newXPath();
    private static final int DENSITY_DEFAULT = 160;

    private SwiperUtil() {
    }

    /**
     * Scroll until a given element is visible
     *
     * @param scrollableElement the element to be scrolled.
     * @param targetElement     the target element to be found.
     * @param direction         direction of scrolling.
     * @param <T>               the type of the target element.
     * @return the target element.
     * @throws NoSuchElementException throws exception if element is not found.
     */
    public static <T extends SearchContext> T scrollUntilElementVisible(WebElement scrollableElement, T targetElement,
                                                                        Direction direction) throws NoSuchElementException {
        return scrollUntilElementVisible(scrollableElement, targetElement, direction, DEFAULT_SCROLL_PERCENTAGE, DEFAULT_SPEED_PERCENTAGE);
    }

    /**
     * Scroll until a given element is visible
     *
     * @param scrollableElement the element to be scrolled.
     * @param targetElement     the target element to be found.
     * @param direction         direction of scrolling.
     * @param percentage        percentage of the scrollable element for each scroll.
     * @param speed             the relative speed to the default speed (e.g. 1.75 times the default speed).
     * @param <T>               the type of the target element.
     * @return the target element.
     * @throws NoSuchElementException throws exception if element is not found.
     */
    public static <T extends SearchContext> T scrollUntilElementVisible(WebElement scrollableElement, T targetElement,
                                                                        Direction direction, double percentage,
                                                                        double speed) throws NoSuchElementException {
        return findElementInList(scrollableElement, List.of(targetElement), direction,
                                 element -> DriverUtil.isDisplayed(targetElement),
                                 percentage, speed, true);
    }

    /**
     * Finds an element in a scrollable list that contains a specific text.
     *
     * @param scrollableElement   the element to be scrolled.
     * @param elementList         the list of elements in which the target element may be found.
     * @param targetElementText   some unique text which further identifies the target element.
     * @param direction           direction of scrolling.
     * @return the target element.
     * @throws NoSuchElementException throws exception if element is not found.
     */
    public static WebElement findElementInList(WebElement scrollableElement, List<WebElement> elementList,
                                               Direction direction, String targetElementText) throws NoSuchElementException {
        return findElementInList(scrollableElement, elementList, direction,
                                 element -> DriverUtil.getText(element).equals(targetElementText),
                                 DEFAULT_SCROLL_PERCENTAGE);
    }

    /**
     * Finds an element in a scrollable list that satisfies a given predicate.
     * Max scroll search: {@value #DEFAULT_MAX_SCROLL_SEARCH} times.
     *
     * @param scrollableElement the element to be scrolled.
     * @param elementList       the list of elements in which the target element may be found.
     * @param predicate         predicate (boolean-valued function) to determine the target element in the list.
     * @param direction         direction of scrolling.
     * @param <T>               the type of element in the elementList
     * @return the target element.
     * @throws NoSuchElementException throws exception if element is not found.
     */
    public static <T extends SearchContext> T findElementInList(WebElement scrollableElement, List<T> elementList,
                                                                Direction direction, Predicate<T> predicate) throws NoSuchElementException {
        return findElementInList(scrollableElement, elementList, direction, predicate, DEFAULT_SCROLL_PERCENTAGE);
    }

    /**
     * Finds an element in a scrollable list that satisfies a given predicate.
     * Max scroll search: {@value #DEFAULT_MAX_SCROLL_SEARCH} times.
     *
     * @param scrollableElement the element to be scrolled.
     * @param elementList       the list of elements in which the target element may be found.
     * @param predicate         predicate (boolean-valued function) to determine the target element in the list.
     * @param direction         direction of scrolling.
     * @param exhaustive        whether the function should also look in the opposite direction.
     * @param <T>               the type of element in the elementList
     * @return the target element.
     * @throws NoSuchElementException throws exception if element is not found.
     */
    public static <T extends SearchContext> T findElementInList(WebElement scrollableElement, List<T> elementList,
                                                                Direction direction, Predicate<T> predicate,
                                                                boolean exhaustive) throws NoSuchElementException {
        return findElementInList(scrollableElement, elementList, direction, predicate, DEFAULT_SCROLL_PERCENTAGE, exhaustive);
    }

    /**
     * Finds an element in a scrollable list that satisfies a given predicate.
     * Max scroll search: {@value #DEFAULT_MAX_SCROLL_SEARCH} times.
     *
     * @param scrollableElement the element to be scrolled.
     * @param elementList       the list of elements in which the target element may be found.
     * @param predicate         predicate (boolean-valued function) to determine the target element in the list.
     * @param direction         direction of scrolling.
     * @param percentage        percentage of the scrollable element for each scroll.
     * @param <T>               the type of element in the elementList
     * @return the target element.
     * @throws NoSuchElementException throws exception if element is not found.
     */
    public static <T extends SearchContext> T findElementInList(WebElement scrollableElement, List<T> elementList,
                                                                Direction direction, Predicate<T> predicate,
                                                                double percentage) throws NoSuchElementException {
        return findElementInList(scrollableElement, elementList, direction, predicate, percentage, null, false);
    }

    /**
     * Finds an element in a scrollable list that satisfies a given predicate.
     * Max scroll search: {@value #DEFAULT_MAX_SCROLL_SEARCH} times.
     *
     * @param scrollableElement the element to be scrolled.
     * @param elementList       the list of elements in which the target element may be found.
     * @param predicate         predicate (boolean-valued function) to determine the target element in the list.
     * @param direction         direction of scrolling.
     * @param percentage        percentage of the scrollable element for each scroll.
     * @param speedPercentage   the relative speed to the default speed (e.g. 1.75 times the default speed).
     * @param <T>               the type of element in the elementList
     * @return the target element.
     * @throws NoSuchElementException throws exception if element is not found.
     */
    public static <T extends SearchContext> T findElementInList(WebElement scrollableElement, List<T> elementList,
                                                                Direction direction, Predicate<T> predicate,
                                                                double percentage, Double speedPercentage) throws NoSuchElementException {
        return findElementInList(scrollableElement, elementList, direction, predicate, percentage, speedPercentage, false);
    }

    /**
     * Finds an element in a scrollable list that satisfies a given predicate.
     * Max scroll search: {@value #DEFAULT_MAX_SCROLL_SEARCH} times.
     *
     * @param scrollableElement the element to be scrolled.
     * @param elementList       the list of elements in which the target element may be found.
     * @param predicate         predicate (boolean-valued function) to determine the target element in the list.
     * @param direction         direction of scrolling.
     * @param percentage        percentage of the scrollable element for each scroll.
     * @param exhaustive        whether the function should also look in the opposite direction.
     * @param <T>               the type of element in the elementList.
     * @return the target element.
     * @throws NoSuchElementException throws exception if element is not found.
     */
    public static <T extends SearchContext> T findElementInList(WebElement scrollableElement, List<T> elementList,
                                                                Direction direction, Predicate<T> predicate,
                                                                double percentage, boolean exhaustive) throws NoSuchElementException {
        return findElementInList(scrollableElement, elementList, direction, predicate, percentage, null, exhaustive);
    }

    /**
     * Finds an element in a scrollable list that satisfies a given predicate.
     * Max scroll search: {@value #DEFAULT_MAX_SCROLL_SEARCH} times.
     *
     * @param scrollableElement the element to be scrolled.
     * @param elementList       the list of elements in which the target element may be found.
     * @param predicate         predicate (boolean-valued function) to determine the target element in the list.
     * @param direction         direction of scrolling.
     * @param percentage        percentage of the scrollable element for each scroll.
     * @param speedPercentage   the relative speed to the default speed (e.g. 1.75 times the default speed).
     * @param exhaustive        whether the function should also look in the opposite direction.
     * @param <T>               the type of element in the elementList.
     * @return the target element.
     * @throws NoSuchElementException throws exception if element is not found.
     */
    public static <T extends SearchContext> T findElementInList(WebElement scrollableElement, List<T> elementList,
                                                                Direction direction, Predicate<T> predicate,
                                                                double percentage, Double speedPercentage,
                                                                boolean exhaustive) throws NoSuchElementException {
        try {
            // find element in given direction
            return scrollAndFindElementInList(scrollableElement, elementList, direction, predicate,
                                              null, percentage, speedPercentage, exhaustive);
        } catch (NoSuchElementException e) {
            if (exhaustive) {
                // scroll and find in opposite direction if element is not found
                return scrollAndFindElementInList(scrollableElement, elementList, direction.getOpposite(), predicate,
                                                  null, percentage, speedPercentage, false);
            }
            throw e;
        }
    }

    /**
     * Finds an element in a scrollable list that satisfies a given predicate.
     * Max scroll search: {@value #DEFAULT_MAX_SCROLL_SEARCH} times.
     *
     * @param scrollableElement  the element to be scrolled.
     * @param elementList        the list of elements in which the target element may be found.
     * @param predicateWithIndex bi-predicate (boolean-valued function) to determine the target element in the list.
     * @param direction          direction of scrolling.
     * @param percentage         percentage of the scrollable element for each scroll.
     * @param speedPercentage    the relative speed to the default speed (e.g. 1.75 times the default speed).
     * @param exhaustive         whether the function should also look in the opposite direction.
     * @param <T>                the type of element in the elementList.
     * @return the target element.
     * @throws NoSuchElementException throws exception if element is not found.
     */
    public static <T extends SearchContext> T findElementInList(WebElement scrollableElement, List<T> elementList,
                                                                Direction direction, BiPredicate<T, Integer> predicateWithIndex,
                                                                double percentage, Double speedPercentage,
                                                                boolean exhaustive) throws NoSuchElementException {
        try {
            // find element in given direction
            return scrollAndFindElementInList(scrollableElement, elementList, direction, null,
                                              predicateWithIndex, percentage, speedPercentage, exhaustive);
        } catch (NoSuchElementException e) {
            if (exhaustive) {
                // scroll and find in opposite direction if element is not found
                return scrollAndFindElementInList(scrollableElement, elementList, direction.getOpposite(),
                                                  null, predicateWithIndex, percentage, speedPercentage, false);
            }
            throw e;
        }
    }

    private static <T extends SearchContext> T scrollAndFindElementInList(WebElement scrollableElement, List<T> elementList,
                                                                          Direction direction, Predicate<T> predicate,
                                                                          BiPredicate<T, Integer> predicateWithIndex,
                                                                          double percentage, Double speedPercentage,
                                                                          boolean shouldScrollBack) throws NoSuchElementException {
        // scroll and find element in the list that satisfy the predicate.
        int scrollCount = 0;
        WebDriver driver = ((WrapsDriver) scrollableElement).getWrappedDriver();
        Integer speed = null;
        if (speedPercentage != null && driver instanceof AndroidDriver) {
            speed = (int) ((getDensity(driver) * 5000) * speedPercentage);
        }
        Node node = null;

        String xpathExpression = "";
        for (int i = 0; i <= DEFAULT_MAX_SCROLL_SEARCH; i++) {
            int size = elementList.size();
            // using index loop to avoid WebElement list's erroneous
            for (int index = 0; index < size; index++) {
                T element = elementList.get(index);

                // test if the current element is the target element
                if ((predicate != null && predicate.test(element)) || (predicateWithIndex != null && predicateWithIndex.test(element, index))) {
                    if (driver instanceof IOSDriver) {
                        // (on iOS only) scroll to the found element if it's not currently visible
                        scrollToElement(element);
                    }

                    return element;
                }
            }

            // if target element is not found, keep scrolling
            if (scrollCount < DEFAULT_MAX_SCROLL_SEARCH) {
                // extract the container node from page source before scrolling
                if (node == null) {
                    Rectangle rect = scrollableElement.getRect();
                    String bounds = "[" + rect.x + "," + rect.y + "][" + (rect.x + rect.width) + "," + (rect.x + rect.width) + "]";
                    xpathExpression = "//*[@scrollable='true' and @bounds='" + bounds + "']";
                    node = extractNodeFromPageSource(xpathExpression, DriverUtil.getPageSource(driver));
                }
                Node oldNode = node;

                // scroll the element in given direction
                scrollElement(scrollableElement, direction, percentage, speed);
                scrollCount++;

                // extract container node from page source after scrolling
                node = extractNodeFromPageSource(xpathExpression, DriverUtil.getPageSource(driver));
                // compare the previous container node and the new container node to detect unsuccessful scrolling
                if (oldNode != null && node != null && oldNode.isEqualNode(node)) {
                    break;
                }
            }
        }

        if (shouldScrollBack) {
            // scroll back to original position
            for (int i = 1; i <= scrollCount; i++) {
                scrollElement(scrollableElement, direction.getOpposite(), percentage);
            }
        }

        String message = String.format("Couldn't find element after scrolling %d times.", scrollCount);
        throw new NoSuchElementException(message);
    }

    private static Node extractNodeFromPageSource(String xpathExpression, String pageSource) {
        try {
            return (Node) XPATH.evaluate(xpathExpression, new InputSource(new StringReader(pageSource)), XPathConstants.NODE);
        } catch (XPathExpressionException e) {
            log.error(e.getMessage());
        }
        return null;
    }

    private static double getDensity(WebDriver webDriver) {
        if (webDriver instanceof AndroidDriver) {
            Object result = DriverUtil.executeShellScript(webDriver, "getprop", "ro.sf.lcd_density");
            int density = result != null ? (int) result : DENSITY_DEFAULT;
            return (double) ((AndroidDriver) webDriver).getDisplayDensity() / density;
        } else {
            return 0.0;
        }
    }

    /**
     * (works on iOS only) Scrolls the current viewport to the given element.
     *
     * @param targetElement the element to scroll to.
     * @return the target element
     * @param <T> the type of the target element
     */
    public static <T extends SearchContext> T scrollToElement(T targetElement) {
        RemoteWebElement remoteWebElement;
        if (targetElement instanceof RemoteWebElement) {
            remoteWebElement = (RemoteWebElement) targetElement;
        } else if (targetElement instanceof WrapsElement) {
            remoteWebElement = (RemoteWebElement) ((WrapsElement) targetElement).getWrappedElement();
        } else {
            return targetElement;
        }

        WebDriver webDriver = remoteWebElement.getWrappedDriver();
        if (webDriver instanceof IOSDriver && !DriverUtil.isDisplayed(remoteWebElement)) {
            Map<String, Object> params = Map.of("elementId", remoteWebElement.getId());
            DriverUtil.executeScript(webDriver, "mobile: scrollToElement", params);
        }
        return targetElement;
    }

    /**
     * Scrolls the whole screen in a given direction.
     *
     * @param webDriver the WebDriver instance.
     * @param direction direction of scrolling.
     */
    public static void scrollScreen(WebDriver webDriver, Direction direction) {
        scrollElement(webDriver, direction, DEFAULT_SCROLL_PERCENTAGE, null);
    }

    /**
     * Scrolls the whole screen in a given direction.
     *
     * @param webDriver  the WebDriver instance.
     * @param direction  direction of scrolling.
     * @param percentage percentage of the scrolling.
     */
    public static void scrollScreen(WebDriver webDriver, Direction direction, double percentage) {
        scrollElement(webDriver, direction, percentage, null);
    }

    /**
     * Scrolls the scrollable element in a given direction.
     *
     * @param scrollableElement the element to be scrolled.
     * @param direction         direction of scrolling.
     */
    public static void scrollElement(WebElement scrollableElement, Direction direction) {
        scrollElement(scrollableElement, direction, DEFAULT_SCROLL_PERCENTAGE, null);
    }

    /**
     * Scrolls the scrollable element in a given direction.
     *
     * @param scrollableElement the element to be scrolled.
     * @param direction         direction of scrolling.
     * @param percentage        percentage of the scrolling.
     */
    public static void scrollElement(WebElement scrollableElement, Direction direction, double percentage) {
        scrollElement(scrollableElement, direction, percentage, null);
    }

    /**
     * Scrolls the scrollable element in a given direction.
     *
     * @param scrollableElement the element to be scrolled.
     * @param direction         direction of scrolling.
     * @param speed             the speed at which to perform this gesture in pixels/s. Default value: 5000 * displayDensity.
     */
    public static void scrollElement(WebElement scrollableElement, Direction direction, Integer speed) {
        scrollElement(scrollableElement, direction, DEFAULT_SCROLL_PERCENTAGE, speed);
    }

    /**
     * Scrolls the scrollable element in a given direction.
     *
     * @param scrollableElement the element to be scrolled.
     * @param direction         direction of scrolling.
     * @param percentage        percentage of the scrolling.
     * @param speed             the speed at which to perform this gesture in pixels/s. Default value: 5000 * displayDensity.
     * @param <T>               the type of element.
     */
    public static <T extends SearchContext> void scrollElement(T scrollableElement, Direction direction, double percentage, Integer speed) {
        if (scrollableElement instanceof RemoteWebElement) {
            RemoteWebElement remoteWebElement = (RemoteWebElement) scrollableElement;
            WebDriver webDriver = remoteWebElement.getWrappedDriver();
            if (webDriver instanceof IOSDriver) {
                swipeElement(scrollableElement, direction.getOpposite(), percentage, speed);
            } else {
                Map<String, Object> params = Map.ofEntries(
                        Map.entry("elementId", remoteWebElement.getId()),
                        Map.entry("direction", direction.label),
                        Map.entry("percent", percentage),
                        Map.entry("speed", speed)
                                                          );
                DriverUtil.executeScript(webDriver, "mobile: scrollGesture", params);
            }
        } else if (scrollableElement instanceof WebDriver) {
            WebDriver webDriver = (WebDriver) scrollableElement;
            if (webDriver instanceof IOSDriver) {
                swipeElement(webDriver, direction.getOpposite(), percentage, speed);
            } else {
                Point position = webDriver.manage().window().getPosition();
                Dimension dimension = webDriver.manage().window().getSize();
                Map<String, Object> params = Map.ofEntries(
                        Map.entry("left", position.getX()),
                        Map.entry("top", position.getY()),
                        Map.entry("width", dimension.getWidth() - 5),
                        Map.entry("height", dimension.getHeight() - 5),
                        Map.entry("direction", direction.label),
                        Map.entry("percent", percentage),
                        Map.entry("speed", speed)
                                                          );
                DriverUtil.executeScript(webDriver, "mobile: scrollGesture", params);
            }
        }
    }

    /**
     * Swipes the whole screen in a given direction.
     *
     * @param webDriver the WebDriver instance.
     * @param direction direction of swiping.
     */
    public static void swipeScreen(WebDriver webDriver, Direction direction) {
        swipeElement(webDriver, direction, DEFAULT_SCROLL_PERCENTAGE, null);
    }

    /**
     * Swipes the whole screen in a given direction.
     *
     * @param webDriver  the WebDriver instance.
     * @param direction  direction of swiping.
     * @param percentage percentage of the swiping.
     */
    public static void swipeScreen(WebDriver webDriver, Direction direction, double percentage) {
        swipeElement(webDriver, direction, percentage, null);
    }

    /**
     * Swipes the scrollable element in a given direction.
     *
     * @param elementToSwipe the element to be swiped.
     * @param direction      direction of swiping.
     */
    public static void swipeElement(WebElement elementToSwipe, Direction direction) {
        swipeElement(elementToSwipe, direction, DEFAULT_SCROLL_PERCENTAGE, null);
    }

    /**
     * Swipes the scrollable element in a given direction.
     *
     * @param elementToSwipe the element to be swiped.
     * @param direction      direction of swiping.
     * @param speed          the speed at which to perform this gesture in pixels/s. Default value: 5000 * displayDensity.
     */
    public static void swipeElement(WebElement elementToSwipe, Direction direction, Integer speed) {
        swipeElement(elementToSwipe, direction, DEFAULT_SCROLL_PERCENTAGE, speed);
    }

    /**
     * Swipes the scrollable element in a given direction.
     *
     * @param elementToSwipe the element to be swiped.
     * @param direction      direction of swiping.
     * @param percentage     percentage of the swiping.
     * @param speed          the speed at which to perform this gesture in pixels/s. Default value: 5000 * displayDensity.
     * @param <T>            the type of element.
     */
    public static <T extends SearchContext> void swipeElement(T elementToSwipe, Direction direction, double percentage, Integer speed) {
        if (elementToSwipe instanceof RemoteWebElement) {
            RemoteWebElement remoteWebElement = (RemoteWebElement) elementToSwipe;
            WebDriver webDriver = remoteWebElement.getWrappedDriver();
            if (webDriver instanceof IOSDriver) {
                Map<String, Object> params = Map.ofEntries(
                        Map.entry("elementId", remoteWebElement.getId()),
                        Map.entry("direction", direction.label)
                                                          );
                DriverUtil.executeScript(webDriver, "mobile: swipe", params);
            } else {
                Map<String, Object> params = Map.ofEntries(
                        Map.entry("elementId", remoteWebElement.getId()),
                        Map.entry("direction", direction.label),
                        Map.entry("percent", percentage),
                        Map.entry("speed", speed)
                                                          );
                DriverUtil.executeScript(webDriver, "mobile: swipeGesture", params);
            }
        } else if (elementToSwipe instanceof WebDriver) {
            WebDriver webDriver = (WebDriver) elementToSwipe;
            if (webDriver instanceof IOSDriver) {
                Map<String, Object> params = Map.of("direction", direction.label);
                DriverUtil.executeScript(webDriver, "mobile: swipe", params);
            } else {
                Point position = webDriver.manage().window().getPosition();
                Dimension dimension = webDriver.manage().window().getSize();

                Map<String, Object> params = Map.ofEntries(
                        Map.entry("left", position.getX()),
                        Map.entry("top", position.getY()),
                        Map.entry("width", dimension.getWidth() - 5),
                        Map.entry("height", dimension.getHeight()- 5),
                        Map.entry("direction", direction.label),
                        Map.entry("percent", percentage),
                        Map.entry("speed", speed)
                                                          );
                DriverUtil.executeScript(webDriver, "mobile: swipeGesture", params);
            }
        }
    }

    /**
     * Drag an element in a given direction with offset.
     *
     * @param draggableElement the element to perform drag gesture on
     * @param direction        the direction to drag
     * @param offSet           the distance from the edge of the element to drag
     * @param <T>              type of the element
     */
    public static <T extends SearchContext> void dragElement(T draggableElement, Direction direction, int offSet) {
        RemoteWebElement webElement;
        try {
            webElement = (RemoteWebElement) DriverUtil.getWebElement(draggableElement);
        } catch (NoSuchElementException e) {
            return;
        }

        Rectangle rectangle = webElement.getRect();
        int x = rectangle.getX();
        int y = rectangle.getY();
        int width = rectangle.getWidth() - 5;
        int height = rectangle.getHeight() - 5;
        int middleX = (x + width) / 2;
        int middleY = (y + height) / 2;
        switch (direction) {
            case UP:
                dragElement(draggableElement, middleX, y - offSet);
                break;
            case DOWN:
                dragElement(draggableElement, middleX, y + height + offSet);
                break;
            case LEFT:
                dragElement(draggableElement, x - offSet, middleY);
                break;
            case RIGHT:
                dragElement(draggableElement, x + width + offSet, middleY);
            default:
        }
    }

    /**
     * Drag an element to a target element.
     *
     * @param draggableElement the element to perform drag gesture on
     * @param targetElement    the target element to drag to
     * @param <T>              type of the elements
     */
    public static <T extends SearchContext> void dragElement(T draggableElement, T targetElement) {
        RemoteWebElement targetWebElement;
        try {
            targetWebElement = (RemoteWebElement) DriverUtil.getWebElement(targetElement);
        } catch (NoSuchElementException e) {
            return;
        }

        Rectangle rectangle = targetWebElement.getRect();
        dragElement(draggableElement,
                    rectangle.getX() + rectangle.getWidth() / 2,
                    rectangle.getY() + rectangle.getHeight() / 2);
    }

    /**
     * Drag an element to a target location.
     *
     * @param draggableElement the element to perform drag gesture on
     * @param endX             the x coordinate of the destination
     * @param endY             the y coordinate of the destination
     * @param <T>              type of the element
     */
    public static <T extends SearchContext> void dragElement(T draggableElement, int endX, int endY) {
        RemoteWebElement webElement;
        try {
            webElement = (RemoteWebElement) DriverUtil.getWebElement(draggableElement);
        } catch (NoSuchElementException e) {
            return;
        }

        WebDriver webDriver = webElement.getWrappedDriver();
        Map<String, Object> params = Map.ofEntries(
                Map.entry("elementId", webElement.getId()),
                Map.entry("endX", endX),
                Map.entry("endY", endY)
                                                  );

        DriverUtil.executeScript(webDriver, "mobile: dragGesture", params);
    }

    /**
     * Swipe gesture by coordinates using W3C Actions API.
     * This is an alternative way for deprecated TouchActions API approach.
     * See also https://appium.io/docs/en/commands/interactions/actions/
     *
     * @param webDriver  the WebDriver instance.
     * @param startPoint start point of swipe gesture.
     * @param endPoint   end point of swipe gesture.
     * @param duration   duration of swipe gesture, the more time added to the gesture, the more accurate and reliable it is.
     */
    public static void swipeByCoordinates(WebDriver webDriver, Point startPoint, Point endPoint, Duration duration) {
        // prepare input for swiping gesture
        PointerInput input = new PointerInput(PointerInput.Kind.TOUCH, "fingerSwipe"); // input source with id string
        Sequence swipe = new Sequence(input, 0);

        // add pressing gesture at start point to sequence
        swipe.addAction(input.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(),
                                                startPoint.x, startPoint.y));
        swipe.addAction(input.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        swipe.addAction(new Pause(input, Duration.ofMillis(100)));

        // add move pointer to end point to sequence
        swipe.addAction(input.createPointerMove(duration, PointerInput.Origin.viewport(), endPoint.x, endPoint.y));
        // add release gesture to sequence
        swipe.addAction(input.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        // perform the swipe gesture sequence
        if (webDriver instanceof AppiumDriver) {
            ((AppiumDriver) webDriver).perform(List.of(swipe));
        }
    }
}
