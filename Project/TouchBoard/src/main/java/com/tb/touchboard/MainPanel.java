package com.tb.touchboard;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import com.tb.tbUtilities.Frames;
import net.miginfocom.swing.*;

public class MainPanel extends JPanel implements
    MouseWatcher.EnterExitListener, MouseListener,
    MouseMotionListener, WindowListener, WindowFocusListener {

  /**
   * Creates a new instance of MainPanel
   */
  @SuppressWarnings("LeakingThisInConstructor")
  public MainPanel(JFrame aParentWindow) {
    parentWindow = aParentWindow;

    initComponents();

    parentWindow.addWindowFocusListener(this);

    menuButton.addMouseListener(this);

    menuButton.addActionListener(
        new java.awt.event.ActionListener() {

      @Override
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        Main.getMyPopupMenu().show(getMenuButton());
      }
    });
  }

  private void initComponents() {
    // JFormDesigner - Component initialization - DO NOT MODIFY//GEN-BEGIN:initComponents
    menuPanel = new JPanel();
    menuButton = new JButton();
    contentPanel = new JPanel();

    //======== this ========
    setLayout(new MigLayout(
        "insets 0,hidemode 3,gap 0 0",
        // columns
        "[grow,fill]",
        // rows
        "[20:n:20,fill]"
        + "[grow,fill]"));

    //======== menuPanel ========
    {
      menuPanel.setLayout(new MigLayout(
          "insets 0,hidemode 3,gap 0 0",
          // columns
          "[fill]"
          + "[grow,fill]",
          // rows
          "[grow,fill]"));

      //---- menuButton ----
      menuButton.setText("<html>&nbsp;Menu&nbsp;</html> ");
      menuButton.setBorder(new EmptyBorder(2, 2, 2, 2));
      menuButton.setFocusable(false);
      menuButton.setVerifyInputWhenFocusTarget(false);
      menuButton.setForeground(Color.darkGray);
      menuButton.setContentAreaFilled(false);
      menuButton.setRolloverEnabled(true);
      menuButton.setFont(new Font("Helvetica Neue", Font.PLAIN, 11));
      menuPanel.add(menuButton, "cell 0 0");
    }
    add(menuPanel, "cell 0 0");

    //======== contentPanel ========
    {
      contentPanel.setBackground(Color.white);
      contentPanel.setLayout(new MigLayout(
          "insets 0,hidemode 3,gap 0 0",
          // columns
          "[grow,fill]",
          // rows
          "[grow,fill]"));
    }
    add(contentPanel, "cell 0 1");
    // JFormDesigner - End of component initialization//GEN-END:initComponents
  }
  // JFormDesigner - Variables declaration - DO NOT MODIFY//GEN-BEGIN:variables
  private JPanel menuPanel;
  private JButton menuButton;
  private JPanel contentPanel;
  // JFormDesigner - End of variables declaration//GEN-END:variables
  private boolean windowDragging = false;
  private Point dragMouseStartPoint;
  private Point dragWindowStartPoint;
  private JFrame parentWindow;
  private static KeyEditPanel keyEditPanel;
  private boolean mainPanelHasFocus;

  // Unused mouse events.
  @Override
  public void mouseClicked(MouseEvent event) {
  }

  @Override
  public void mouseEntered(MouseEvent event) {
  }

  @Override
  public void mouseExited(MouseEvent event) {
  }

  @Override
  public void mouseDragged(MouseEvent event) {
  }

  @Override
  public void mouseMoved(MouseEvent e) {
  }

  // Mouse pressed event.
  @Override
  public void mousePressed(MouseEvent event) {
    if (event.getComponent() == menuButton) {
      menuButton.setBorder(
          BorderFactory.createLoweredBevelBorder());
    }
  }

  // Mouse released event.
  @Override
  public void mouseReleased(MouseEvent event) {
    if (event.getComponent() == menuButton) {
      menuButton.setBorder(
          BorderFactory.createEmptyBorder(2, 2, 2, 2));
    }
  }

  public int getTopControlsHeight() {
    return /* titlePanel.getHeight() + */ menuPanel.getHeight();
  }

  public JPanel getContentPanel() {
    return contentPanel;
  }

  int getLeftRightInsets() {
    return getInsets().left + getInsets().right;
  }

  int getTopBottomInsets() {
    return getInsets().top + getInsets().bottom;
  }

  // Mouse enter event.
  @Override
  public void mouseWatcherEntered(Component component) {
    if (component == menuButton) {
      menuButton.setBorder(BorderFactory.createRaisedBevelBorder());
    }
  }

  // Mouse exit event.
  @Override
  public void mouseWatcherExited(Component component) {
    if (component == menuButton) {
      menuButton.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
    }
  }

  public JButton getMenuButton() {
    return menuButton;
  }

  /**
   * windowOpened, Checks and sets the focus state when the main window first opens. This fixes a
   * previous bug where focus state was not set correctly on startup.
   */
  @Override
  public void windowOpened(WindowEvent e) {
    windowLostFocus(null);
    parentWindow.setVisible(true);
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        Main.getBoardManager().arrangeAll();
      }
    });
  }

  @Override
  public void windowActivated(WindowEvent e) {
  }

  @Override
  public void windowDeactivated(WindowEvent e) {
  }

  @Override
  public void windowIconified(WindowEvent event) {
    MouseWatcher.stopHoverCheck();
  }

  @Override
  public void windowDeiconified(WindowEvent event) {
    MouseWatcher.startHoverCheck();
  }

  @Override
  public void windowClosed(WindowEvent e) {
    exitProgram();
  }

  @Override
  public void windowClosing(WindowEvent e) {
    exitProgram();
  }

  static public void exitProgram() {
    if (keyEditPanel != null) {
      keyEditPanel.windowClosed(null);
    }
    Main.getBoardManager().saveAllBoards();
    System.exit(0);
  }

  @Override
  public void windowGainedFocus(WindowEvent e) {
    Frames.setMessageParent(parentWindow);
    MouseWatcher.setSkipHoverBlockersCheck(true);
    mainPanelHasFocus = true;
    Main.getBoardManager().arrangeAll();
  }

  @Override
  public void windowLostFocus(WindowEvent e) {
    mainPanelHasFocus = false;
//        changeTitleBarColor(new Color(51, 153, 255));
    Main.getBoardManager().setClickTargetLabelShowing(false);
  }

  // Pick parentWindow bounds based on screen and window size.
  void setParentWindowInitialPosition() {
    int startDistanceFromTop = 20;
    int startDistanceFromRight = 34;
    Rectangle screenSize = Frames.getScreenSize();
    Rectangle oldBounds = parentWindow.getBounds();
    int oldWidth = Math.max(oldBounds.width, BoardManager.minimumWindowWidthForOS);
    parentWindow.setBounds(
        screenSize.width - startDistanceFromRight - oldWidth,
        startDistanceFromTop, oldWidth, oldBounds.height);
  }

  public static void setKeyEditPanel(KeyEditPanel aKeyEditPanel) {
    keyEditPanel = aKeyEditPanel;
  }

  void useMousePosition(Point mouseScreenLocation) {
    if (windowDragging) {
      // Get difference between mouse start point and mouse current point.
      int xDifference = mouseScreenLocation.x - dragMouseStartPoint.x;
      int yDifference = mouseScreenLocation.y - dragMouseStartPoint.y;
      int newX = dragWindowStartPoint.x + xDifference;
      int newY = dragWindowStartPoint.y + yDifference;
      parentWindow.setBounds(
          newX, newY, parentWindow.getWidth(), parentWindow.getHeight());
    }
  }

  public boolean mainPanelHasFocus() {
    return mainPanelHasFocus;
  }

  public Color getMenuPanelBackgroundColor() {
    return menuPanel.getBackground();
  }
}
