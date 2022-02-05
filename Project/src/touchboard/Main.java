package touchboard;

import java.awt.Image;
import javax.swing.*;
import javax.swing.plaf.*;
import tbUtilities.*;

/**
 * Entry point and initializer for the program. Also stores globally accessible singleton classes.
 */
public class Main {
  // Globally needed singleton classes.

  static private BoardManager boardManager;
  static private MyPopupMenu myPopupMenu;
  static private MouseWatcher mouseWatcher;
  static private CommandCenter commandCenter;
  static private MainPanel mainPanel;
  static public JFrame mainFrame;

  /**
   * Constructor is not used
   */
  private Main() {
  }

  /* Program entry point. */
  public static void main(String[] arguments) {

    // This breaks us out of static mode, and also is the recommended way
    // to start a Java program.
    java.awt.EventQueue.invokeLater(new Runnable() {

      public void run() {

        // Set default label font.
        UIManager.put("Label.font",
            new FontUIResource(Constants.defaultLabelFont));

        // Create main window.
        mainFrame = new JFrame("TB");
        Frames.setMessageParent(mainFrame);
        Image applicationIcon = Constants.applicationIcon.getImage();
        mainFrame.setIconImage(applicationIcon);

        // Create main panel.
        mainPanel = new MainPanel(mainFrame);
        mainFrame.add(getMainPanel());

        // Create board manager.
        boardManager = new BoardManager(
            mainFrame, getMainPanel(), getMainPanel().getContentPanel());

        // Create main menu.
        myPopupMenu = new MyPopupMenu();

        // Set up hover packages.
        MouseWatcher.HoverPackage boardManagerHover
            = new MouseWatcher.HoverPackage(
                getBoardManager().getManagerPanel(), getBoardManager());

        // Set up enter exit packages.
        MouseWatcher.EnterExitPackage menuButtonEnterExit
            = new MouseWatcher.EnterExitPackage(
                getMainPanel().getMenuButton(), getMainPanel());

        // Create bindingFrames array.
        JFrame[] bindingFrames = {mainFrame};

        // Create enterExitPackages array.
        MouseWatcher.EnterExitPackage[] enterExitPackages = {menuButtonEnterExit};

        // Create mouse watcher.
        mouseWatcher = new MouseWatcher(enterExitPackages, bindingFrames);

        // Create the command center.
        commandCenter = new CommandCenter();

        // Register hover listeners.
        getMouseWatcher().addHoverListener(boardManagerHover);

        // Register after hover listeners.
        getMouseWatcher().addAfterHoverListener(getBoardManager());

        // Start mouse watcher.
        getMouseWatcher().start();

        // Load clips board if needed.
        getBoardManager().loadClipsBoardIfNeeded();

        // Load default boards
        getBoardManager().loadDefaultBoards();

        // Arrange once at minimum.
        getBoardManager().arrangeAll();

        // Position the main frame.
        getMainPanel().setParentWindowInitialPosition();

        // Activate welcome dialog if needed.
        boolean showWelcome = Options.getShowWelcome();
        if (showWelcome) {
          WelcomeDialog welcomeDialog = new WelcomeDialog();
          welcomeDialog.setVisible(true);
        }

        // Make the main frame visible.
        mainFrame.setVisible(true);

        // This exists to fix a startup focus bug,
        // that happens under certain circumatances.
        mainFrame.toFront();
      }
    });
  }

  public static BoardManager getBoardManager() {
    return boardManager;
  }

  public static MyPopupMenu getMyPopupMenu() {
    return myPopupMenu;
  }

  public static MouseWatcher getMouseWatcher() {
    return mouseWatcher;
  }

  public static CommandCenter getCommandCenter() {
    return commandCenter;
  }

  public static MainPanel getMainPanel() {
    return mainPanel;
  }
}
