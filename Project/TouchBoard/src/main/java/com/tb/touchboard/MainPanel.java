package com.tb.touchboard;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import com.jgoodies.forms.layout.*;
import com.tb.tbUtilities.Frames;

public class MainPanel extends JPanel implements
        MouseWatcher.EnterExitListener, MouseListener,
        MouseMotionListener, WindowListener, WindowFocusListener {

    /**
     * Creates a new instance of MainPanel
     */
    public MainPanel(JFrame aParentWindow) {
        parentWindow = aParentWindow;

        parentWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        parentWindow.setUndecorated(true);
        parentWindow.setAlwaysOnTop(true);
        parentWindow.setResizable(false);

        initComponents();

        parentWindow.addWindowListener(this);
        parentWindow.addWindowFocusListener(this);

        titleLabel.addMouseListener(this);
        titleLabel.addMouseMotionListener(this);

        menuButton.addMouseListener(this);

        closeButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                closeButtonActionPerformed(e);
            }
        });

        menuButton.addActionListener(
                new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Main.getMyPopupMenu().show(getMenuButton());
            }
        });

        // Debug title color change.
        if (Constants.debug) {
            changeTitleBarColor(Color.magenta);
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY//GEN-BEGIN:initComponents
        titlePanel = new JPanel();
        titleLabel = new JLabel();
        closeToolBar = new JToolBar();
        closeButton = new JButton();
        menuPanel = new JPanel();
        menuButton = new JButton();
        contentPanel = new JPanel();
        CellConstraints cc = new CellConstraints();

        //======== this ========
        setBorder(new BevelBorder(BevelBorder.RAISED, Color.lightGray, null, Color.black, null));
        setLayout(new FormLayout(
                ColumnSpec.decodeSpecs("default:grow"),
                new RowSpec[]{
                    new RowSpec(RowSpec.FILL, Sizes.bounded(Sizes.DEFAULT, Sizes.dluY(12), Sizes.dluY(12)), FormSpec.NO_GROW),
                    new RowSpec(RowSpec.FILL, Sizes.bounded(Sizes.DEFAULT, Sizes.dluY(12), Sizes.dluY(12)), FormSpec.NO_GROW),
                    new RowSpec(RowSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW)
                }));

        //======== titlePanel ========
        {
            titlePanel.setBackground(new Color(51, 153, 255));
            titlePanel.setLayout(new FormLayout(
                    "max(min;0dlu):grow, default",
                    "fill:default:grow"));

            //---- titleLabel ----
            titleLabel.setText("TB");
            titleLabel.setForeground(Color.white);
            titleLabel.setBorder(null);
            titlePanel.add(titleLabel, new CellConstraints(1, 1, 1, 1, CellConstraints.DEFAULT, CellConstraints.DEFAULT, new Insets(0, 3, 0, 0)));

            //======== closeToolBar ========
            {
                closeToolBar.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                closeToolBar.setFloatable(false);
                closeToolBar.setPreferredSize(new Dimension(20, 20));
                closeToolBar.setBorderPainted(false);
                closeToolBar.setMinimumSize(new Dimension(16, 16));
                closeToolBar.setBackground(new Color(51, 153, 255));
                closeToolBar.setMargin(new Insets(1, 1, 1, 1));
                closeToolBar.setBorder(null);
                closeToolBar.setMaximumSize(new Dimension(20, 20));

                //---- closeButton ----
                closeButton.setText("x");
                closeButton.setMinimumSize(new Dimension(16, 16));
                closeButton.setPreferredSize(new Dimension(16, 16));
                closeButton.setMaximumSize(new Dimension(16, 16));
                closeButton.setHorizontalTextPosition(SwingConstants.CENTER);
                closeButton.setFocusable(false);
                closeButton.setBorder(new BevelBorder(BevelBorder.RAISED));
                closeButton.setVerticalAlignment(SwingConstants.BOTTOM);
                closeToolBar.add(closeButton);
            }
            titlePanel.add(closeToolBar, new CellConstraints(2, 1, 1, 1, CellConstraints.DEFAULT, CellConstraints.DEFAULT, new Insets(0, 0, 0, 1)));
        }
        add(titlePanel, cc.xy(1, 1));

        //======== menuPanel ========
        {
            menuPanel.setBorder(null);
            menuPanel.setLayout(new FormLayout(
                    "default, default:grow",
                    "default"));

            //---- menuButton ----
            menuButton.setText("<html><small>&nbsp;Menu&nbsp;</small></html> ");
            menuButton.setBorder(new EmptyBorder(2, 2, 2, 2));
            menuButton.setFocusable(false);
            menuButton.setVerifyInputWhenFocusTarget(false);
            menuButton.setForeground(Color.darkGray);
            menuButton.setContentAreaFilled(false);
            menuButton.setRolloverEnabled(true);
            menuPanel.add(menuButton, cc.xy(1, 1));
        }
        add(menuPanel, cc.xy(1, 2));

        //======== contentPanel ========
        {
            contentPanel.setBackground(Color.white);
            contentPanel.setLayout(new FormLayout(
                    "default, default, default:grow",
                    "fill:default, fill:default:grow, fill:default:grow"));
        }
        add(contentPanel, cc.xy(1, 3));
        // JFormDesigner - End of component initialization//GEN-END:initComponents
    }
    // JFormDesigner - Variables declaration - DO NOT MODIFY//GEN-BEGIN:variables
    private JPanel titlePanel;
    private JLabel titleLabel;
    private JToolBar closeToolBar;
    private JButton closeButton;
    private JPanel menuPanel;
    private JButton menuButton;
    private JPanel contentPanel;
    // JFormDesigner - End of variables declaration//GEN-END:variables
    private boolean windowDragging = false;
    private Point dragMouseStartPoint;
    private Point dragWindowStartPoint;
    private JFrame parentWindow;
    private BoardManager boardManager;
    private static KeyEditPanel keyEditPanel;
    private boolean mainPanelHasFocus;

    private void closeButtonActionPerformed(ActionEvent event) {
        exitProgram();
    }

    // Unused mouse events.
    public void mouseClicked(MouseEvent event) {
    }

    public void mouseEntered(MouseEvent event) {
    }

    public void mouseExited(MouseEvent event) {
    }

    public void mouseDragged(MouseEvent event) {
    }

    public void mouseMoved(MouseEvent e) {
    }

    // Mouse pressed event.
    public void mousePressed(MouseEvent event) {
        if (event.getComponent() == menuButton) {
            menuButton.setBorder(
                    BorderFactory.createLoweredBevelBorder());
        }

        if (event.getComponent() == titleLabel) {
            windowDragging = true;
            dragMouseStartPoint
                    = MouseInfo.getPointerInfo().getLocation();
            dragWindowStartPoint = new Point(
                    parentWindow.getX(), parentWindow.getY());
        }
    }

    // Mouse released event.
    public void mouseReleased(MouseEvent event) {
        if (event.getComponent() == menuButton) {
            menuButton.setBorder(
                    BorderFactory.createEmptyBorder(2, 2, 2, 2));
        }

        if (event.getComponent() == titleLabel) {
            windowDragging = false;
        }
    }

    public int getTopControlsHeight() {
        return titlePanel.getHeight() + menuPanel.getHeight();
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
    public void mouseWatcherEntered(Component component) {
        if (component == menuButton) {
            menuButton.setBorder(BorderFactory.createRaisedBevelBorder());
        }
    }

    // Mouse exit event.
    public void mouseWatcherExited(Component component) {
        if (component == menuButton) {
            menuButton.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        }
    }

    public JButton getMenuButton() {
        return menuButton;
    }

    /**
     * windowOpened, Checks and sets the focus state when the main window first
     * opens. This fixes a previous bug where focus state was not set correctly
     * on startup.
     */
    public void windowOpened(WindowEvent e) {
        windowLostFocus(null);
        /*
      if (parentWindow.isFocusOwner()) {
      windowGainedFocus(null);
      } else {
      windowLostFocus(null);
      }
         */
    }

    public void windowActivated(WindowEvent e) {
    }

    public void windowDeactivated(WindowEvent e) {
    }

    public void windowIconified(WindowEvent event) {
        MouseWatcher.stopHoverCheck();
    }

    public void windowDeiconified(WindowEvent event) {
        MouseWatcher.startHoverCheck();
    }

    public void windowClosed(WindowEvent e) {
        exitProgram();
    }

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

    public void windowGainedFocus(WindowEvent e) {
        Frames.setMessageParent(parentWindow);
        MouseWatcher.setSkipHoverBlockersCheck(true);
        mainPanelHasFocus = true;
        changeTitleBarColor(Color.blue);
    }

    public void windowLostFocus(WindowEvent e) {
        mainPanelHasFocus = false;
        changeTitleBarColor(new Color(51, 153, 255));
        Main.getBoardManager().setClickTargetLabelShowing(false);
    }

    // Pick parentWindow bounds based on screen and window size.
    void setParentWindowInitialPosition() {
        Rectangle screenSize = Frames.getScreenSize();
        Rectangle oldBounds = parentWindow.getBounds();
        parentWindow.setBounds(
                screenSize.width - 28 - oldBounds.width,
                28, oldBounds.width, oldBounds.height);
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

    private void changeTitleBarColor(Color aColor) {
        titlePanel.setBackground(aColor);
        closeToolBar.setBackground(aColor);
    }
}
