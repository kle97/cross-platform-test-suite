package cross.platform.test.suite.utility;

import cross.platform.test.suite.constant.Direction;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;

import java.time.Duration;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

public final class SwiperUtil {

    public static final int DEFAULT_MAX_SCROLL_SEARCH = 3; // times
    public static final Duration DEFAULT_SCROLL_DURATION = Duration.ofMillis(2000); // ms

    private SwiperUtil() {
    }

    /**
     * Find an element within a list, and to scroll that list to find the element automatically.
     *
     * @param appiumDriver      the AppiumDriver instance.
     * @param scrollableElement the container element that can be scrollable.
     * @param elements          the list of elements in which the target element may be found.
     * @param elementText       some unique text which further identifies the target element.
     * @return the found element.
     * @throws NoSuchElementException throws exception if element is not found.
     */
    public static WebElement findElementInScrollableContainerWithText(AppiumDriver appiumDriver,
                                                                      WebElement scrollableElement,
                                                                      List<WebElement> elements,
                                                                      String elementText) throws NoSuchElementException {
        List<Direction> possibleScrollDirections = getPossibleDirections(scrollableElement);
        return findElementAndIndexInScrollableContainerWithText(appiumDriver, scrollableElement, elements, elementText,
                                                                possibleScrollDirections).getKey();
    }

    /**
     * Find an element within a list, and to scroll that list to find the element automatically.
     *
     * @param appiumDriver      the AppiumDriver instance.
     * @param scrollableElement the container element that can be scrollable.
     * @param elements          the list of elements in which the target element may be found.
     * @param elementText       some unique text which further identifies the target element.
     * @param scrollDirection   direction to scroll.
     * @return the found element.
     * @throws NoSuchElementException throws exception if element is not found.
     */
    public static WebElement findElementInScrollableContainerWithText(AppiumDriver appiumDriver,
                                                                      WebElement scrollableElement,
                                                                      List<WebElement> elements,
                                                                      String elementText,
                                                                      Direction scrollDirection) throws NoSuchElementException {
        return findElementAndIndexInScrollableContainerWithText(appiumDriver, scrollableElement, elements, elementText,
                                                                List.of(scrollDirection)).getKey();
    }

    /**
     * Find an element's index within a list, and to scroll that list to find the element automatically.
     *
     * @param appiumDriver      the AppiumDriver instance.
     * @param scrollableElement the container element that can be scrollable.
     * @param elements          the list of elements in which the target element may be found.
     * @param elementText       some unique text which further identifies the target element.
     * @return the found element's index in the elements list.
     * @throws NoSuchElementException throws exception if element is not found.
     */
    public static int findElementIndexInScrollableContainerWithText(AppiumDriver appiumDriver,
                                                                    WebElement scrollableElement,
                                                                    List<WebElement> elements,
                                                                    String elementText) throws NoSuchElementException {
        List<Direction> possibleScrollDirections = getPossibleDirections(scrollableElement);
        return findElementAndIndexInScrollableContainerWithText(appiumDriver, scrollableElement, elements, elementText,
                                                                possibleScrollDirections).getValue();
    }

    /**
     * Find an element's index within a list, and to scroll that list to find the element automatically.
     *
     * @param appiumDriver      the AppiumDriver instance.
     * @param scrollableElement the container element that can be scrollable.
     * @param elements          the list of elements in which the target element may be found.
     * @param elementText       some unique text which further identifies the target element.
     * @param scrollDirection   direction to scroll.
     * @return the found element's index in the elements list.
     * @throws NoSuchElementException throws exception if element is not found.
     */
    public static int findElementIndexInScrollableContainerWithText(AppiumDriver appiumDriver,
                                                                    WebElement scrollableElement,
                                                                    List<WebElement> elements,
                                                                    String elementText,
                                                                    Direction scrollDirection) throws NoSuchElementException {
        return findElementAndIndexInScrollableContainerWithText(appiumDriver, scrollableElement, elements, elementText,
                                                                List.of(scrollDirection)).getValue();
    }

    private static AbstractMap.SimpleImmutableEntry<WebElement, Integer> findElementAndIndexInScrollableContainerWithText(
            AppiumDriver appiumDriver, WebElement scrollableElement, List<WebElement> elements,
            String elementText, List<Direction> possibleScrollDirections) throws NoSuchElementException {
        double scrollPercentage = 0.93;
        List<Direction> scrollDirections = new ArrayList<>();
        for (Direction direction : possibleScrollDirections) {
            scrollDirections.add(direction);
            scrollDirections.add(direction.getOpposite());
        }

        // scroll and find element with specified text in the list.
        for (Direction direction : scrollDirections) {
            int scrollTurn = 0;
            for (int i = 0; i <= DEFAULT_MAX_SCROLL_SEARCH; i++) {
                int length = elements.size();
                // looping through elements using index
                for (int index = 0; index < length; index++) {
                    WebElement element = elements.get(index);
                    if (DriverUtil.getText(element).equals(elementText)) {
                        return new AbstractMap.SimpleImmutableEntry<>(element, index);
                    }
                }
                // if target element is not found, keep scrolling
                if (scrollTurn < DEFAULT_MAX_SCROLL_SEARCH) {
                    String beforeScrollingPageSource = DriverUtil.getPageSource(appiumDriver);
                    scrollByElementSizePercentage(appiumDriver, scrollableElement, direction, scrollPercentage);
                    if (DriverUtil.getPageSource(appiumDriver).equals(
                            beforeScrollingPageSource)) { // if the scrolling is not successful, skip
                        break;
                    }
                    scrollTurn++;
                }
            }
            // scroll back to original position
            for (int i = 1; i <= scrollTurn; i++) {
                scrollByElementSizePercentage(appiumDriver, scrollableElement, direction.getOpposite(),
                                              scrollPercentage);
            }
        }

        throw new NoSuchElementException(String.format("Couldn't find element with text '%s' after scrolling.", elementText));
    }

    private static List<Direction> getPossibleDirections(WebElement scrollableElement) {
        // get the possible scrolling directions based on the scrollable element's dimension.
        Rectangle scrollableElementRectangle = scrollableElement.getRect();
        int containerWidth = scrollableElementRectangle.getWidth();
        int containerHeight = scrollableElementRectangle.getHeight();
        List<Direction> listOfDirections;
        if (containerWidth > containerHeight) {
            listOfDirections = List.of(Direction.RIGHT, Direction.DOWN);
        } else {
            listOfDirections = List.of(Direction.DOWN, Direction.RIGHT);
        }
        return listOfDirections;
    }

    /**
     * Scroll screen by its size percentage.
     *
     * @param appiumDriver the appiumDriver instance.
     * @param direction    direction of scrolling.
     * @param percentage   screen size percentage to scroll.
     * @throws IllegalArgumentException throws exception for invalid direction or size percentage.
     */
    public static void scrollByScreenSizePercentage(AppiumDriver appiumDriver, Direction direction,
                                                    double percentage) throws IllegalArgumentException {
        scrollByScreenSizePercentage(appiumDriver, direction, percentage, DEFAULT_SCROLL_DURATION);
    }

    /**
     * Scroll screen by its size percentage (with scroll duration option).
     *
     * @param appiumDriver the AppiumDriver instance.
     * @param direction    direction of scrolling.
     * @param percentage   screen size percentage to scroll.
     * @param duration     duration of scrolling.
     * @throws IllegalArgumentException throws exception for invalid direction or size percentage.
     */
    public static void scrollByScreenSizePercentage(AppiumDriver appiumDriver, Direction direction,
                                                    double percentage, Duration duration) throws IllegalArgumentException {
        Dimension screenDimension = appiumDriver.manage().window().getSize();
        int screenWidth = screenDimension.getWidth();
        int screenHeight = screenDimension.getHeight();
        Point leftUpperCornerPoint = new Point(0, 0);
        scrollBySizePercentage(appiumDriver, leftUpperCornerPoint, screenWidth,
                               screenHeight, direction, percentage, duration);
    }

    /**
     * Scroll element by its size percentage.
     *
     * @param appiumDriver      the AppiumDriver instance.
     * @param scrollableElement the element to apply scrolling.
     * @param direction         direction of scrolling.
     * @param percentage        element size percentage to scroll.
     * @throws IllegalArgumentException throws exception for invalid direction or size percentage.
     */
    public static void scrollByElementSizePercentage(AppiumDriver appiumDriver, WebElement scrollableElement,
                                                     Direction direction, double percentage) throws IllegalArgumentException {
        scrollByElementSizePercentage(appiumDriver, scrollableElement, direction, percentage, DEFAULT_SCROLL_DURATION);
    }

    /**
     * Scroll element by its size percentage (with scroll duration option).
     *
     * @param appiumDriver      the AppiumDriver instance
     * @param scrollableElement the element to apply scrolling.
     * @param direction         direction of scrolling.
     * @param percentage        element size percentage to scroll.
     * @param duration          duration of scrolling.
     * @throws IllegalArgumentException throws exception for invalid direction or size percentage.
     */
    public static void scrollByElementSizePercentage(AppiumDriver appiumDriver, WebElement scrollableElement,
                                                     Direction direction, double percentage, Duration duration) throws IllegalArgumentException {
        Rectangle elementRectangle = scrollableElement.getRect();
        int elementWidth = elementRectangle.getWidth();
        int elementHeight = elementRectangle.getHeight();
        Point leftUpperCornerPoint = new Point(elementRectangle.x, elementRectangle.y);
        scrollBySizePercentage(appiumDriver, leftUpperCornerPoint, elementWidth,
                               elementHeight, direction, percentage, duration);
    }

    /**
     * Scroll element/screen by its size percentage.
     *
     * @param appiumDriver       the AppiumDriver instance.
     * @param topLeftCornerPoint origin or top left corner coordinates of element/screen.
     * @param elementWidth       element/screen width.
     * @param elementHeight      element/screen height.
     * @param direction          direction of scrolling.
     * @param percentage         element/screen size percentage to scroll.
     * @param duration           duration of scrolling.
     * @throws IllegalArgumentException throws exception for invalid direction or size percentage.
     */
    public static void scrollBySizePercentage(AppiumDriver appiumDriver, Point topLeftCornerPoint, int elementWidth,
                                              int elementHeight, Direction direction, double percentage,
                                              Duration duration) throws IllegalArgumentException {
        if (percentage < 0 || percentage > 1) {
            throw new IllegalArgumentException("Swipe percentage must be between 0.00 and 1.00");
        }

        Point centerPoint = new Point((int) (topLeftCornerPoint.x + elementWidth * 0.5),
                                      (int) (topLeftCornerPoint.y + elementHeight * 0.5));
        int top = centerPoint.y - (int) (elementHeight * percentage * 0.5);
        int bottom = centerPoint.y + (int) (elementHeight * percentage * 0.5);
        int left = centerPoint.x - (int) (elementWidth * percentage * 0.5);
        int right = centerPoint.x + (int) (elementWidth * percentage * 0.5);
        switch (direction) {
            case UP:
                swipeByCoordinates(appiumDriver, new Point(centerPoint.x, top),
                                   new Point(centerPoint.x, bottom), duration);
                break;
            case DOWN:
                swipeByCoordinates(appiumDriver, new Point(centerPoint.x, bottom),
                                   new Point(centerPoint.x, top), duration);
                break;
            case LEFT:
                swipeByCoordinates(appiumDriver, new Point(left, centerPoint.y),
                                   new Point(right, centerPoint.y), duration);
                break;
            case RIGHT:
                swipeByCoordinates(appiumDriver, new Point(right, centerPoint.y),
                                   new Point(left, centerPoint.y), duration);
                break;
            default:
                throw new IllegalArgumentException(String.format("Invalid direction: %s", direction.toString()));
        }
    }

    /**
     * Swipe gesture by coordinates using W3C Actions API.
     * This is an alternative way for deprecated TouchActions API approach.
     * See also https://appium.io/docs/en/commands/interactions/actions/
     *
     * @param appiumDriver the AppiumDriver instance.
     * @param startPoint   start point of swipe gesture.
     * @param endPoint     end point of swipe gesture.
     * @param duration     duration of swipe gesture, the more time added to the gesture, the more accurate and reliable it is.
     */
    public static void swipeByCoordinates(AppiumDriver appiumDriver, Point startPoint, Point endPoint, Duration duration) {
        // prepare input for swiping gesture
        PointerInput input = new PointerInput(PointerInput.Kind.TOUCH, "fingerSwipe"); // input source with id string
        Sequence swipe = new Sequence(input, 0);

        // add pressing gesture at start point to sequence
        swipe.addAction(
                input.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startPoint.x, startPoint.y));
        swipe.addAction(input.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        swipe.addAction(new Pause(input, Duration.ofMillis(100)));

        // add move pointer to end point to sequence
        swipe.addAction(input.createPointerMove(duration, PointerInput.Origin.viewport(), endPoint.x, endPoint.y));
        // add release gesture to sequence
        swipe.addAction(input.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        // perform the swipe gesture sequence
        appiumDriver.perform(List.of(swipe));
    }
}