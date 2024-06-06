package com.tb.touchboard;

import java.awt.*;
import java.util.ArrayList;
import javax.swing.JFrame;
import com.tb.tbUtilities.Use;

public class MouseWatcher extends Thread {

  static public class HoverPackage {

    public Component component;
    public HoverListener listener;

    HoverPackage(Component aComponent, HoverListener aListener) {
      component = aComponent;
      listener = aListener;
    }
  }

  static public interface HoverListener {

    public void mouseWatcherHover(
        Point mouseScreenLocation, Point relativeLocation,
        Component relativeTo);
  }

  static public class EnterExitPackage {

    public Component component;
    public EnterExitListener listener;
    public boolean insideLastTime = false;

    EnterExitPackage(Component aComponent, EnterExitListener aListener) {
      component = aComponent;
      listener = aListener;
    }
  }

  static public interface EnterExitListener {

    public void mouseWatcherEntered(Component component);

    public void mouseWatcherExited(Component component);
  }

  static public interface AfterHoverListener {

    public void mouseWatcherAfterHover();
  }

  // This is a list of components that are currently blocking
  // hover events within their boundaries.
  static private ArrayList<Component> hoverBlockers
      = new ArrayList<Component>();

  private ArrayList<HoverPackage> hoverPackages
      = new ArrayList<HoverPackage>();

  private ArrayList<AfterHoverListener> afterHoverListeners
      = new ArrayList<AfterHoverListener>();

  private EnterExitPackage[] enterExitPackages;
  private JFrame[] bindingFrames;

  private int atStartMilliseconds = 0;
  private Point hoverStart = new Point(0, 0);
  private Point mouseScreenLocation = new Point(0, 0);
  static private boolean mouseMovedSinceLastHover = false;
  private static boolean skipHoverCheck = false;
  private static boolean skipHoverBlockersCheck = false;
  static private boolean skipAfterHoverEvents = false;

  // Sleep delay in milliseconds.
  static private final int sleepDelayMilliseconds = 25;
  static private final int hoverDelayMilliseconds = 400;
  static private final int hoverStrayPixels = 2;

  MouseWatcher(
      EnterExitPackage[] someEnterExitPackages,
      JFrame[] someBindingFrames) {
    enterExitPackages = someEnterExitPackages;
    bindingFrames = someBindingFrames;
    setDaemon(true);
  }

  // This will be called when the thread is started.
  public void run() {

    // Don't catch the first hover position.
    getMouseScreenLocation();
    hoverStart.setLocation(mouseScreenLocation);

    // Run forever.
    while (true) {

      // Sleep.
      Use.mySleep(sleepDelayMilliseconds);

      // Get mouse x and y relative to screen.
      getMouseScreenLocation();

      // Check for an enter or exit event.
      checkForEnterExitEvent();

      // Check for a hover event.
      checkForHoverEvent();

      // Give mouseScreenLocation to MainPanel window drag function.
      Main.getMainPanel().useMousePosition(mouseScreenLocation);
    }
  }

  // Check for enter and exit event
  private void checkForEnterExitEvent() {
    for (EnterExitPackage enterExitPackage : enterExitPackages) {

      // Check to see if the mouse is inside of this component.
      if (isMouseInsideComponent(
          mouseScreenLocation, enterExitPackage.component)) {
        if (enterExitPackage.insideLastTime == false) {
          enterExitPackage.listener.mouseWatcherEntered(
              enterExitPackage.component);
        }
        enterExitPackage.insideLastTime = true;
      } else {
        if (enterExitPackage.insideLastTime == true) {
          enterExitPackage.listener.mouseWatcherExited(
              enterExitPackage.component);
        }
        enterExitPackage.insideLastTime = false;
      }
    }
  }

  // Check for hover events.
  private void checkForHoverEvent() {

    // Are we skipping the hover check?
    if (skipHoverCheck) {
      resetTimerStuff();
      return;
    }

    // Are we inside at least one binding frame?
    boolean notInAFrame = true;
    for (JFrame frame : bindingFrames) {
      if (isMouseInsideComponent(mouseScreenLocation, frame)) {
        notInAFrame = false;
      }
    }
    if (notInAFrame) {
      resetTimerStuff();
      return;
    }

    // Are we inside any hover blocker?
    if (!skipHoverBlockersCheck) {
      boolean inABlocker = false;
      // This line prevents iteration through a changed collection,
      // potentially generating a ConcurrentModificationException.
      ArrayList<Component> copyOfHoverBlockers = new ArrayList<Component>(hoverBlockers);
      for (Component component : copyOfHoverBlockers) {
        if (isMouseInsideComponent(mouseScreenLocation, component)) {
          inABlocker = true;
        }
      }
      if (inABlocker) {
        resetTimerStuff();
        return;
      }
    }

    // Check for hover event.
    // Did the mouse move farther than hoverStrayPixels?
    if ((mouseScreenLocation.x > (hoverStart.x + hoverStrayPixels))
        || (mouseScreenLocation.x < (hoverStart.x - hoverStrayPixels))
        || (mouseScreenLocation.y > (hoverStart.y + hoverStrayPixels))
        || (mouseScreenLocation.y < (hoverStart.y - hoverStrayPixels))) {
      // The mouse moved outside of its stray zone,
      // so reset timer and position, and exit.
      resetTimerStuff();
      return;
    } else {
      // The mouse is in the same area it was in last time,
      // so increment the timer.
      atStartMilliseconds += sleepDelayMilliseconds;
    }

    // Has the timer gone off?
    if (atStartMilliseconds < hoverDelayMilliseconds) {
      return;
    }

    // Has the mouse moved since the last hover event?
    if (!mouseMovedSinceLastHover) {
      return;
    }
    mouseMovedSinceLastHover = false;

    // Loop through the components to see where the event occoured.
    // Copy array to avoid concurrent modification.
    ArrayList<HoverPackage> copyOfHoverPackages
        = new ArrayList<HoverPackage>(hoverPackages);
    for (HoverPackage hoverPackage : copyOfHoverPackages) {
      if (isMouseInsideComponent(mouseScreenLocation,
          hoverPackage.component)) {
        // We found the component.

        // Compute relative mouse coordinates.
        Rectangle componentScreenRectangle = getScreenRectangle(
            hoverPackage.component);
        Point mouseRelativeLocation = new Point(
            mouseScreenLocation.x - componentScreenRectangle.x,
            mouseScreenLocation.y - componentScreenRectangle.y);

        // Notify the hover listener of the hover event.
        hoverPackage.listener.mouseWatcherHover(
            mouseScreenLocation, mouseRelativeLocation,
            hoverPackage.component);

        // Notify all the after hover listeners, if needed.
        if (skipAfterHoverEvents) {
          resetTimerStuff();
          skipAfterHoverEvents = false;
          return;
        }
        // Copy array to avoid concurrent modification.
        ArrayList<AfterHoverListener> copyOfAfterHoverListeners
            = new ArrayList<AfterHoverListener>(afterHoverListeners);
        for (AfterHoverListener listener : copyOfAfterHoverListeners) {
          listener.mouseWatcherAfterHover();
        }
        return;
      }
    }
    // This hover event did not occur inside one of our registered components.
    return;
  }

  private void resetTimerStuff() {
    hoverStart.setLocation(mouseScreenLocation);
    atStartMilliseconds = 0;
    mouseMovedSinceLastHover = true;
  }

  public static boolean isMouseInsideComponent(Point screenLocation,
      Component component) {
    Rectangle rectangle = getScreenRectangle(component);
    if (rectangle.contains(screenLocation)) {
      return true;
    } else {
      return false;
    }
  }

  // This will return the index of the first component in a component
  // array that contains the specified screen mouse coordinates.
  // If no component contains those coordinates, this will return -1.
  public static int getIndexOfComponentContainingMouse(
      Point mouseScreenLocation, Component[] components) {
    Rectangle rectangle;
    for (int i = 0; i < components.length; ++i) {
      rectangle = getScreenRectangle(components[i]);
      if (rectangle.contains(mouseScreenLocation)) {
        return i;
      }
    }
    return -1;
  }

  static public Rectangle getScreenRectangle(Component component) {
    Rectangle rectangle = new Rectangle();
    try {
      rectangle.setLocation(component.getLocationOnScreen());
      rectangle.setSize(component.getSize());
    } catch (java.awt.IllegalComponentStateException e) {
      return new Rectangle(0, 0, 0, 0);
    }
    return rectangle;
  }

  static void skipAfterHoverEvents() {
    skipAfterHoverEvents = true;
  }

  public static void setSkipHoverBlockersCheck(boolean skip) {
    skipHoverBlockersCheck = skip;
  }

  private void getMouseScreenLocation() {
    PointerInfo pointerInfo = MouseInfo.getPointerInfo();
    mouseScreenLocation.setLocation(pointerInfo.getLocation());
  }

  public static void startHoverCheck() {
    skipHoverCheck = false;
    // This prevents hover events immediately after a menu selection.
    // To get a hover event after a menu selection, move the mouse.
    mouseMovedSinceLastHover = false;
  }

  public static void stopHoverCheck() {
    skipHoverCheck = true;
  }

  public void addAfterHoverListener(AfterHoverListener afterHoverListener) {
    afterHoverListeners.add(afterHoverListener);
  }

  public static void addHoverBlocker(Component component) {
    if (Use.not(hoverBlockers.contains(component))) {
      hoverBlockers.add(component);
    }
  }

  public static void removeHoverBlocker(Component component) {
    hoverBlockers.remove(component);
  }

  public void addHoverListener(HoverPackage hoverPackage) {
    hoverPackages.add(hoverPackage);
  }

  void removeBoardFromHoverListeners(Board board) {
    for (int i = (hoverPackages.size() - 1); i >= 0; --i) {
      HoverPackage aPackage = hoverPackages.get(i);
      if (aPackage.listener == board) {
        hoverPackages.remove(i);
      }
    }
  }

}
