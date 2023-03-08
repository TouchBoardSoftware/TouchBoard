package com.tb.touchboard;

import java.awt.Image;
import javax.swing.*;
import javax.swing.plaf.*;
import com.tb.tbUtilities.*;
import java.awt.Desktop;
import java.awt.Font;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import javax.swing.JOptionPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

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
        // Set the application name in the MacOS environment.
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", "TouchBoard");
        System.setProperty("apple.awt.application.name", "TouchBoard");
        // This prevents an extra icon named 'Java' from appearing on the Mac dock.
        System.setProperty("apple.awt.UIElement", "true");

        // This breaks us out of static mode, and also is the recommended way
        // to start a Java program.
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {

                // Enforce java 1.8
                /**
                 * Why: Java 17, at least in the Temurin platform, appears to have a bug where
                 * TouchBoard cannot type capital letters while the user is simultaneously moving
                 * the mouse pointer. The letters are incorrectly typed lowercase if the mouse
                 * pointer is in motion. However, this does not occur when TB is run using Java 1.8
                 * (aka Java 8).
                 */
                String javaVersion = System.getProperty("java.version");
                if (!javaVersion.startsWith("1.8.")) {
                    String versionMessage
                        = "Due to a known bug in later Java versions, (issue: JDK-8196030),\n"
                        + "TouchBoard currently only runs properly in Java 8. (aka 'Java 1.8'.)\n"
                        + "You are using Java version: " + javaVersion
                        + "\n\n"
                        + "Please restart TouchBoard using Java 8.\n"
                        + "This can be done by 1) Installing a Java 8 JDK (if needed).\n"
                        + "2) If on mac, launch TouchBoard with the applescript app that is\n"
                        + "included in the TouchBoard folder. (Not by double clicking the jar.)\n"
                        + "The applescript will look for and use Java 8 on your system.\n\n"
                        + "On windows, you could launch TouchBoard with Java 8 with a batch file\n"
                        + "or a double click. (A double click will only work if Java 8 is your\n"
                        + "default Java version).\n"
                        + "\n"
                        + "Exiting program.";
                    JOptionPane.showMessageDialog(null, versionMessage,
                        "Java Version Requirement", JOptionPane.PLAIN_MESSAGE);
                    System.exit(0);
                }

                // Set default label font.
                UIManager.put("Label.font",
                    new FontUIResource(Constants.defaultLabelFont));

                // Create main window.
                mainFrame = new JFrame("TB");
                Frames.setMessageParent(mainFrame);
                Image applicationIcon = Constants.applicationIcon.getImage();
                mainFrame.setIconImage(applicationIcon);
                mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                mainFrame.setAlwaysOnTop(true);
                mainFrame.setResizable(false);

                // Create main panel.
                mainPanel = new MainPanel(mainFrame);
                mainFrame.addWindowListener(mainPanel);
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

    @SuppressWarnings("StringConcatenationInsideStringBufferAppend")
    public static void showAboutDialog() {

        // for copying style
        JLabel label = new JLabel();
        Font font = label.getFont();

        // create some css from the label's font
        StringBuffer style = new StringBuffer("font-family:" + font.getFamily() + ";");
        style.append("font-weight:" + (font.isBold() ? "bold" : "normal") + ";");
        style.append("font-size:" + font.getSize() + "pt;");

        // html content
        JEditorPane ep = new JEditorPane("text/html", "<html><body style=\"" + style + "\">" //
            + Constants.aboutMessageText + "</body></html>");

        // handle link events
        ep.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
                    URL url = e.getURL();
                    try {
                        Desktop.getDesktop().browse(url.toURI());
                    } catch (IOException | URISyntaxException exception) {
                    }
                }
            }
        });
        ep.setEditable(false);
        ep.setBackground(label.getBackground());

        // show
        JOptionPane.showMessageDialog(null,
            ep,
            "About TouchBoard",
            JOptionPane.INFORMATION_MESSAGE);
    }
}
