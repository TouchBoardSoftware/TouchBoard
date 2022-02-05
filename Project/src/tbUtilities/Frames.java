package tbUtilities;

import java.awt.*;
import javax.swing.*;
import touchboard.*;

public class Frames {

  static private Component messageParent = null;

  static public interface MessageCustomizer {
  }

  public static void message(String message) {
    message(message, null);
  }

  public static void message(
      String message, MessageCustomizer messageCustomizer) {
    MouseWatcher.stopHoverCheck();
    JOptionPane optionPane = new JOptionPane(
        message, JOptionPane.INFORMATION_MESSAGE,
        JOptionPane.DEFAULT_OPTION);
    JDialog dialog = optionPane.createDialog(messageParent, "Message");
    centerWindow(dialog, null);
    dialog.setModal(true);
    dialog.setAlwaysOnTop(true);
    dialog.setVisible(true);
    MouseWatcher.startHoverCheck();
  }

  /**
   * This displays an option dialog, and returns the chosen option string. If String one == null,
   * the only option available will be the string "OK". If the dialog is closed, this will return
   * the string "DialogClosed". This will never return null.
   */
  public static String showOptions(String message, String title,
      String defaultChoice, int messageType, String... options) {
    MouseWatcher.stopHoverCheck();
    String dialogClosed = "DialogClosed";
    String ok = "OK";
    for (int i = options.length - 1; i > 0; --i) {
      if (options[i] == null) {
        String[] newOptions = new String[i];
        System.arraycopy(options, 0, newOptions, 0, i);
        options = newOptions;
      }
    }
    if (options.length == 0 || options[0] == null) {
      options = new String[]{ok};
    }
    if (defaultChoice == null) {
      defaultChoice = options[0];
    }
    JOptionPane optionPane = new JOptionPane(message, messageType,
        JOptionPane.DEFAULT_OPTION, null, options, defaultChoice);
    optionPane.setInitialValue(defaultChoice);
    JDialog dialog = optionPane.createDialog(messageParent, title);
    centerWindow(dialog, null);
    dialog.setModal(true);
    dialog.setAlwaysOnTop(true);
    optionPane.selectInitialValue();
    dialog.setVisible(true);
    MouseWatcher.startHoverCheck();
    Object selected = optionPane.getValue();
    if (selected == null) {
      return dialogClosed;
    }
    for (String option : options) {
      if (option.equals(selected)) {
        return option;
      }
    }
    return dialogClosed;
  }

  /**
   * This displays an option dialog, and returns the chosen option. If Option one == null, the only
   * option available will be Option.OK. If the dialog is closed, this will return Option.Closed.
   * This will never return null.
   */
  public static Option showOptions(String message, String title,
      Option defaultChoice, int messageType, Option... options) {
    MouseWatcher.stopHoverCheck();
    for (int i = options.length - 1; i > 0; --i) {
      if (options[i] == null) {
        Option[] newOptions = new Option[i];
        System.arraycopy(options, 0, newOptions, 0, i);
        options = newOptions;
      }
    }
    if (options.length == 0 || options[0] == null) {
      options = new Option[]{Option.OK};
    }
    if (defaultChoice == null) {
      defaultChoice = options[0];
    }
    JOptionPane optionPane = new JOptionPane(message, messageType,
        JOptionPane.DEFAULT_OPTION, null, options, defaultChoice);
    optionPane.setInitialValue(defaultChoice);
    JDialog dialog = optionPane.createDialog(messageParent, title);
    centerWindow(dialog, null);
    dialog.setModal(true);
    dialog.setAlwaysOnTop(true);
    optionPane.selectInitialValue();
    dialog.setVisible(true);
    MouseWatcher.startHoverCheck();
    Object selected = optionPane.getValue();
    if (selected == null) {
      return Option.Closed;
    }
    for (Option option : options) {
      if (option == selected) {
        return option;
      }
    }
    return Option.Closed;
  }

  public static void adjustWindowToScreenRatio(Window window) {
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    Rectangle bounds = ge.getMaximumWindowBounds();
    float boundsWidth = bounds.width;
    float boundsHeight = bounds.height;
    int oldWindowWidth = window.getWidth();
    int oldWindowHeight = window.getHeight();
    float ratio = boundsWidth / boundsHeight;
    int ratioWidth = (int) (ratio * oldWindowHeight);
    int ratioHeight = (int) (oldWindowWidth / ratio);
    int newWidth = Math.max(ratioWidth, oldWindowWidth);
    int newHeight = Math.max(ratioHeight, oldWindowHeight);
    window.setBounds(window.getX(), window.getY(), newWidth, newHeight);
    window.validate();
  }

  /**
   * Centers a frame or dialog, or any descendent of java.awt.Window. Pass in null for
   * minimumScreenPercentage to leave the window size unchanged. Pass in a screen percentage from 1
   * to 100 to have this function insure a minimum window size as a percentage of the size returned
   * by GraphicsEnvironment.getMaximumWindowBounds().
   */
  public static void centerWindow(
      Window window, Integer minimumScreenPercentage) {
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    Point center = ge.getCenterPoint();
    Rectangle bounds = ge.getMaximumWindowBounds();
    int w;
    int h;
    if (minimumScreenPercentage != null) {
      minimumScreenPercentage = Use.clampInt(minimumScreenPercentage, 1, 100);
      w = Math.max((bounds.width * minimumScreenPercentage) / 100,
          Math.min(window.getWidth(), bounds.width));
      h = Math.max((bounds.height * minimumScreenPercentage) / 100,
          Math.min(window.getHeight(), bounds.height));
    } else {
      w = window.getWidth();
      h = window.getHeight();
    }
    int x = center.x - w / 2;
    int y = center.y - h / 2;
    window.setBounds(x, y, w, h);
    window.validate();
  }

  public static Rectangle getMaximumWindowBounds() {
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    return ge.getMaximumWindowBounds();
  }

  public static Rectangle getScreenSize() {
    DisplayMode mode = GraphicsEnvironment.getLocalGraphicsEnvironment().
        getDefaultScreenDevice().getDisplayMode();
    return new Rectangle(0, 0, mode.getWidth(), mode.getHeight());
  }

  public static Component getMessageParent() {
    return messageParent;
  }

  public static void setMessageParent(Component aMessageParent) {
    if (aMessageParent != null) {
      messageParent = aMessageParent;
    }
  }
}
