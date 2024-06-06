package com.tb.touchboard;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import com.jgoodies.forms.layout.*;
import com.tb.tbUtilities.*;

public class BoardManager implements
    MouseWatcher.HoverListener, MouseWatcher.AfterHoverListener {

  public static final int minimumWindowWidthForOS = 80;
  private static final int minimumDesiredWindowWidth = 80;

  // Array of open boards.
  private ArrayList<Board> openBoards = new ArrayList<>();

  // Indicator of whether the clips board is open or not. True if it is open.
  private boolean isClipsOpen = false;

  // The index of the first board after the clips board,
  // 0 if clips is closed, 1 if clips is open.
  private int firstBoardIndexAfterClips = 0;

  // Last edit hover.
  static public int lastEditHover = 100;

  // Storage for objects from other places that this manager needs to access.
  private JFrame parentFrame;
  private MainPanel mainPanel;
  private JPanel contentPanel;

  // Needed labels and panels.
  private JPanel managerPanel = new JPanel();
  private JPanel toolBarPanel = new JPanel();
  private JLabel minimizeLabel = new JLabel();
  private JLabel editButtonLabel = new JLabel();
  private JLabel capslockLabel = new JLabel();
  private JLabel closeAllBoardsLabel = new JLabel();

  // Click target app label.
  private JLabel clickTargetLabel = new JLabel();
  private final int clickTargetLabelHeight = 150;

  // This controls the display if the click target label.
  private boolean showClickTargetLabel = false;

  // The height in pixels of each control row on the board manager.
  private final int controlHeight = 23;

  // The width in pixels of the board quick show labels.
  private final int quickShowWidth = 34;

  // The width in pixels of the lock images.
  private final int lockWidth = 16;

  // The dynamically calculated height and width of the board manager.
  private int managerHeight = 20;
  private int managerWidth = 20;

  // The dynamically calculated width of the content panel.
  private int contentPanelWidth = 0;

  // Current hover quickshow board.
  Board currentQuickShow = null;
  boolean arrangeNeeded = false;

  /**
   * Constructor
   */
  public BoardManager(JFrame aParentFrame, MainPanel aMainPanel,
      JPanel aContentPanel) {

    // Copy our constructor arguments to internal storage.
    parentFrame = aParentFrame;
    mainPanel = aMainPanel;
    contentPanel = aContentPanel;

    // Add click listener to click Target Label
    clickTargetLabel.addMouseListener(new MouseListener() {
      @Override
      public void mouseClicked(MouseEvent e) {
        String ok = "OK";
        String readIntroduction = "Read Introduction";
        String result = Frames.showOptions(
            "Please click on a target application before hovering over a TouchBoard key.\n"
            + "Also be sure to hover over, not click on, TouchBoard keys.\n"
            + "If you would like an introduction to using TouchBoard, click \"Read Introduction\".",
            "", ok, JOptionPane.INFORMATION_MESSAGE,
            ok, readIntroduction, null);
        if (result.equals(readIntroduction)) {
          BrowserLaunch.openURL(Constants.helpURL);
        }
      }

      @Override
      public void mousePressed(MouseEvent e) {
      }

      @Override
      public void mouseReleased(MouseEvent e) {
      }

      @Override
      public void mouseEntered(MouseEvent e) {
      }

      @Override
      public void mouseExited(MouseEvent e) {
      }

    });
  }

  public void arrangeAll() {
    // Save the old window parameters, to use them later.
    int oldWidth = parentFrame.getWidth();
    Point oldTopRightLocation = parentFrame.getLocation();
    oldTopRightLocation.x += oldWidth;

    // Remove all components from all arranged panels.
    contentPanel.removeAll();
    managerPanel.removeAll();
    toolBarPanel.removeAll();

    // Set image icons for all image labels.
    minimizeLabel.setIcon(Constants.minimizeIcon);
    closeAllBoardsLabel.setIcon(Constants.collapseAllArrow);

    // Set text for the focus target application label.
    clickTargetLabel.setOpaque(true);
    clickTargetLabel.setBackground(Color.blue);
    clickTargetLabel.setForeground(Color.white);
    clickTargetLabel.setText(
        "<html>&nbsp;Click the<br>&nbsp;target<br>&nbsp;program,<br>"
        + "&nbsp;or click<br>&nbsp;here for<br>&nbsp;help.</html>");

    if (lastEditHover == 1) {
      editButtonLabel.setIcon(Constants.editButtonIconGreen);
    } else {
      editButtonLabel.setIcon(Constants.editButtonIcon);
    }

    if (Main.getCommandCenter().inCapState()) {
      capslockLabel.setIcon(Constants.capslockOnIcon);
    } else {
      capslockLabel.setIcon(Constants.capslockOffIcon);
    }

    // Create a CellConstraints object for general use.
    CellConstraints cc = new CellConstraints();

    // ARRANGE THE TOOL BAR PANEL
    toolBarPanel.setBackground(Color.gray);
    toolBarPanel.setOpaque(true);

    // Build toolbar column specifications.
    ArrayList<ColumnSpec> toolbarColumns = new ArrayList<>();
    for (int i = 0; i < 5; ++i) {
      if ((i % 2) == 0) {
        toolbarColumns.add(new ColumnSpec(ColumnSpec.FILL,
            Sizes.pixel(16), ColumnSpec.NO_GROW));
      } else {
        toolbarColumns.add(new ColumnSpec(ColumnSpec.FILL,
            Sizes.pixel(1), ColumnSpec.NO_GROW));
      }
    }

    // Build toolbar row specifications.
    ArrayList<RowSpec> toolbarRows = new ArrayList<>();
    toolbarRows.add(new RowSpec(RowSpec.FILL,
        Sizes.pixel(21), RowSpec.NO_GROW));

    // Set the toolbar panel layout manager.
    toolBarPanel.setLayout(new FormLayout(
        toolbarColumns.toArray(new ColumnSpec[0]),
        toolbarRows.toArray(new RowSpec[0])));

    // Set the toolbar border.
    if (areAnyBoardsDisplayed()) {
      toolBarPanel.setBorder(
          BorderFactory.createMatteBorder(
              2, 2, 2, 2, new Color(192, 192, 192)));
    } else {
      toolBarPanel.setBorder(
          BorderFactory.createMatteBorder(
              2, 1, 2, 1, new Color(192, 192, 192)));
    }

    // Add the toolbar buttons
    toolBarPanel.add(minimizeLabel, cc.xy(1, 1));
    toolBarPanel.add(capslockLabel, cc.xy(3, 1));
    toolBarPanel.add(editButtonLabel, cc.xy(5, 1));

    // ARRANGE THE BOARD MANAGER PANEL
    managerPanel.setBackground(new Color(160, 160, 160));
    managerPanel.setOpaque(true);

    // Build board manager column specifications.
    ArrayList<ColumnSpec> managerColumns = new ArrayList<>();
    managerWidth = 0;
    if (areAnyBoardsDisplayed()) {
      managerColumns.add(new ColumnSpec(ColumnSpec.FILL,
          Sizes.pixel(Constants.borderThickness), ColumnSpec.NO_GROW));
      managerWidth += Constants.borderThickness;
    } else {
      managerColumns.add(new ColumnSpec(ColumnSpec.FILL,
          Sizes.pixel(1), ColumnSpec.NO_GROW));
      managerWidth += 1;
    }
    managerColumns.add(new ColumnSpec(ColumnSpec.FILL,
        Sizes.pixel(quickShowWidth), ColumnSpec.NO_GROW));
    managerWidth += quickShowWidth;
    managerColumns.add(new ColumnSpec(ColumnSpec.FILL,
        Sizes.pixel(lockWidth), ColumnSpec.NO_GROW));
    managerWidth += lockWidth;
    if (areAnyBoardsDisplayed()) {
      managerColumns.add(new ColumnSpec(ColumnSpec.FILL,
          Sizes.pixel(Constants.borderThickness), ColumnSpec.NO_GROW));
      managerWidth += Constants.borderThickness;
    } else {
      managerColumns.add(new ColumnSpec(ColumnSpec.FILL,
          Sizes.pixel(1), ColumnSpec.NO_GROW));
      managerWidth += 1;
    }

    // Build board manager row specifications.
    // Also calculate height variable
    ArrayList<RowSpec> managerRows = new ArrayList<>();
    managerHeight = 0;
    int border = Constants.borderThickness;
    int control = controlHeight;

    // Add top border.
    managerRows.add(new RowSpec(RowSpec.FILL, Sizes.pixel(border), RowSpec.NO_GROW));
    managerHeight += border;

    // Add "Toolbar" control
    managerRows.add(new RowSpec(RowSpec.FILL, Sizes.pixel(control), RowSpec.NO_GROW));
    managerHeight += control;
    managerRows.add(new RowSpec(RowSpec.FILL, Sizes.pixel(border), RowSpec.NO_GROW));
    managerHeight += border;

    // Add open boards
    for (int i = 0; i < openBoards.size(); ++i) {
      managerRows.add(new RowSpec(RowSpec.FILL, Sizes.pixel(control), RowSpec.NO_GROW));
      managerHeight += control;
      managerRows.add(new RowSpec(RowSpec.FILL, Sizes.pixel(border), RowSpec.NO_GROW));
      managerHeight += border;
    }

    // Add "Close All Boards" control
    if (areAnyBoardsDisplayed()) {
      managerRows.add(new RowSpec(RowSpec.FILL, Sizes.pixel(control), RowSpec.NO_GROW));
      managerHeight += control;
      managerRows.add(new RowSpec(RowSpec.FILL, Sizes.pixel(border), RowSpec.NO_GROW));
      managerHeight += border;
    }

    if (showClickTargetLabel) {
      int clickTargetLabelGap = 10;
      managerRows.add(new RowSpec(
          RowSpec.FILL, Sizes.pixel(clickTargetLabelGap), RowSpec.NO_GROW));
      managerHeight += clickTargetLabelGap;

      managerRows.add(new RowSpec(
          RowSpec.FILL, Sizes.pixel(clickTargetLabelHeight), RowSpec.NO_GROW));
      managerHeight += clickTargetLabelHeight;

      managerRows.add(
          new RowSpec(RowSpec.FILL, Sizes.pixel(clickTargetLabelGap), RowSpec.NO_GROW));
      managerHeight += clickTargetLabelGap;
    }

    // Set the board manager panel layout manager.
    managerPanel.setLayout(new FormLayout(
        managerColumns.toArray(new ColumnSpec[0]),
        managerRows.toArray(new RowSpec[0])));

    // Place Tool Bar Panel
    managerPanel.add(toolBarPanel, cc.xywh(1, 1, 4, 2));

    // Place Board Controls.
    for (int i = 0; i < openBoards.size(); ++i) {
      int row = (4 + (i * 2));
      managerPanel.add(openBoards.get(i).quickShowLabel, cc.xy(2, row));
      if (openBoards.get(i).lockedOpen) {
        openBoards.get(i).lockStateLabel.setIcon(Constants.lockClosed);
      } else {
        openBoards.get(i).lockStateLabel.setIcon(Constants.lockOpen);
      }
      managerPanel.add(openBoards.get(i).lockStateLabel, cc.xy(3, row));

    }

    // Place Close All Boards Label
    if (areAnyBoardsDisplayed()) {
      managerPanel.add(closeAllBoardsLabel,
          cc.xywh(2, ((openBoards.size() * 2) + 4), 2, 1));
    }

    // Place Click Target Label
    if (showClickTargetLabel) {
      int yPosition;
      if (!areAnyBoardsDisplayed()) {
        yPosition = 5;
      } else {
        yPosition = 7;
      }
      managerPanel.add(clickTargetLabel,
          cc.xywh(2, ((openBoards.size() * 2) + yPosition), 2, 1));
    }

    // ARRANGE THE CONTENT PANEL
    // contentPanel.setBackground(new Color(200, 200, 200));
    Color menuBGColor = Main.getMainPanel().getMenuPanelBackgroundColor();
    int amountDarker = 20;
    Color myContentBGColor = new Color(
        menuBGColor.getRed() - amountDarker,
        menuBGColor.getGreen() - amountDarker,
        menuBGColor.getBlue() - amountDarker);
    contentPanel.setBackground(myContentBGColor);
    contentPanel.setOpaque(true);

    // Build the content panel column specifications.
    ArrayList<ColumnSpec> contentColumns = new ArrayList<>();
    contentPanelWidth = 0;

    // Add a column for each displayed board in open boards.
    for (int i = 0; i < openBoards.size(); ++i) {
      // Arrange the board.
      boolean displayed = openBoards.get(i).isDisplayed();
      openBoards.get(i).arrangeBoard(displayed);
      // If it is displayed, add columns for it.
      if (displayed) {

        // Get the width.
        int width = openBoards.get(i).getWidth();
        contentColumns.add(new ColumnSpec(ColumnSpec.FILL,
            Sizes.pixel(width), ColumnSpec.NO_GROW));
        contentPanelWidth += width;

        // and one for inter-board gap.
        contentColumns.add(new ColumnSpec(ColumnSpec.FILL,
            Sizes.pixel(1), ColumnSpec.NO_GROW));
        contentPanelWidth += 1;
      }
    }

    // Add a column for the board manager.
    contentColumns.add(new ColumnSpec(ColumnSpec.RIGHT,
        Sizes.pixel(getManagerWidth()), ColumnSpec.DEFAULT_GROW));
    contentPanelWidth += getManagerWidth();

    // Build the content panel row specifications.
    ArrayList<RowSpec> contentRows = new ArrayList<>();
    contentRows.add(new RowSpec(RowSpec.FILL,
        Sizes.pixel(getManagerHeight()), RowSpec.NO_GROW));

    contentRows.add(new RowSpec(RowSpec.FILL,
        Sizes.pixel(getContentPanelHeight() - getManagerHeight()),
        RowSpec.NO_GROW));

    // Set the content panel layout manager.
    contentPanel.setLayout(new FormLayout(
        contentColumns.toArray(new ColumnSpec[0]),
        contentRows.toArray(new RowSpec[0])));

    // add managerPanel to the contentPanel
    contentPanel.add(managerPanel, cc.xy(((numberOfDisplayedBoards() * 2) + 1), 1));

    // add displayed boards to the content panel
    int displayedBoardColumn = 1;
    for (int i = 0; i < openBoards.size(); ++i) {
      if (openBoards.get(i).isDisplayed()) {
        contentPanel.add(openBoards.get(i),
            cc.xywh(displayedBoardColumn, 1, 1, 2));
        displayedBoardColumn += 2;
      }
    }

    // Set parent frame height and width.
    int newWidth = getContentPanelWidth() + mainPanel.getLeftRightInsets();
    newWidth = Math.max(newWidth, minimumWindowWidthForOS);
    newWidth = Math.max(newWidth, minimumDesiredWindowWidth);
    int newHeight = getContentPanelHeight() + mainPanel.getTopControlsHeight()
        + mainPanel.getTopBottomInsets();
    int newX = parentFrame.getX() + oldWidth - newWidth;
    int screenWidth = Frames.getScreenSize().width;
    int maximumX = screenWidth - 15;
    if (newX > maximumX) {
      newX = screenWidth - 28 - newWidth;
    }
    parentFrame.setMinimumSize(new Dimension(newWidth, newHeight));
    parentFrame.setBounds(newX, parentFrame.getY(), newWidth, newHeight);
    // Make sure that the upper right location stays the same.
    parentFrame.setLocation((oldTopRightLocation.x - newWidth), oldTopRightLocation.y);
    parentFrame.pack();
    parentFrame.validate();
    parentFrame.repaint();
    // If there are no boards displayed, then hide the window title.
    parentFrame.setTitle((areAnyBoardsDisplayed()) ? "TB" : "");
  }

  int getManagerWidth() {
    return managerWidth;
  }

  int getManagerHeight() {
    return managerHeight;
  }

  private int getContentPanelWidth() {
    return contentPanelWidth;
  }

  private int getContentPanelHeight() {
    if (areAnyBoardsDisplayed()) {
      return Math.max(Constants.getBoardHeight(), getManagerHeight());
    } else {
      return getManagerHeight();
    }
  }

  private boolean areAnyBoardsDisplayed() {
    return numberOfDisplayedBoards() != 0;
  }

  private int numberOfDisplayedBoards() {
    int displayed = 0;
    for (Board board : openBoards) {
      if (board.isDisplayed() == true) {
        ++displayed;
      }
    }
    return displayed;
  }

  // This is called when we receive a hover event on the board manager.
  @Override
  public void mouseWatcherHover(Point mouseScreenLocation,
      Point relativeLocation, Component relativeTo) {

    // Create array of all components in manager panel except for
    // quick show labels and locks.
    Component[] managerPanelComponents
        = {minimizeLabel, editButtonLabel, capslockLabel, closeAllBoardsLabel};

    // Check to see if any of these components was the one with the
    // hover event.
    int managerPanelIndex = MouseWatcher.getIndexOfComponentContainingMouse(
        mouseScreenLocation, managerPanelComponents);

    // If nothing was found, check the quick show and locked state labels.
    if (managerPanelIndex == -1) {
      boolean foundControlLabel = checkQuickShowAndLockStateLabels(
          mouseScreenLocation);
      if (!foundControlLabel) {
        // No valid hover component was found...
        // Tell Mousewatcher to skip after hover events.
        MouseWatcher.skipAfterHoverEvents();
      }
      return;
    }

    // Check for "Minimize application" label activation
    if (managerPanelComponents[managerPanelIndex] == minimizeLabel) {
      parentFrame.setExtendedState(JFrame.ICONIFIED);
      mainPanel.windowIconified(null);
      return;
    }

    // Check for "Edit button" label activation
    if (managerPanelComponents[managerPanelIndex] == editButtonLabel) {
      lastEditHover = 0;
      arrangeNeeded = true;
      return;
    }

    // Check for "Caps Lock" label activation
    if (managerPanelComponents[managerPanelIndex] == capslockLabel) {
      capsLockChange();
      return;
    }

    // Check for "Close all boards" label activation
    if (managerPanelComponents[managerPanelIndex] == closeAllBoardsLabel) {
      for (Board board : openBoards) {
        board.quickShow = false;
        board.lockedOpen = false;
      }
      // This action won't turn off the edit light.
      --lastEditHover;
      arrangeNeeded = true;
      return;
    }
  }

  public JPanel getManagerPanel() {
    return managerPanel;
  }

  private void capsLockChange() {
    Main.getCommandCenter().setCapslock(Use.toggleBoolean(
        Main.getCommandCenter().getCapslock()));
  }

  /**
   * Check quick show labels and lock labels for hover event. Return true if one was found,
   * otherwise false.
   */
  private boolean checkQuickShowAndLockStateLabels(Point mouseScreenLocation) {

    // Loop through all the open boards.
    for (Board board : openBoards) {
      if (MouseWatcher.isMouseInsideComponent(
          mouseScreenLocation, board.quickShowLabel)) {
        if (lastEditHover == 1) {
          BoardNewEditPanel.go(BoardNewEditPanel.PanelType.EDIT_BOARD,
              board);
          return true;
        }
        if (board.quickShow == true) {
          board.quickShow = false;
        } else {
          board.quickShow = true;
          currentQuickShow = board;
        }
        arrangeNeeded = true;
        return true;
      }

      if (MouseWatcher.isMouseInsideComponent(
          mouseScreenLocation, board.lockStateLabel)) {
        if (board.lockedOpen == true) {
          board.lockedOpen = false;
        } else {
          board.lockedOpen = true;
        }
        // This action won't turn off the edit light.
        --lastEditHover;
        arrangeNeeded = true;
        return true;
      }
    } // End board quick show and lock label check loop.
    return false;
  }

  /**
   * Hover listener for this panel.
   */
  @Override
  public void mouseWatcherAfterHover() {

    // Increment last edit hover.
    ++lastEditHover;
    if (lastEditHover == 1 || lastEditHover == 2) {
      arrangeNeeded = true;
    }

    // Clear all quick show states except one that was just set to true.
    for (Board board : openBoards) {
      if (board != currentQuickShow && board.quickShow == true) {
        board.quickShow = false;
        arrangeNeeded = true;
      }
    }
    currentQuickShow = null;
    if (arrangeNeeded) {
      arrangeAll();
      arrangeNeeded = false;
    }
  }

  public void saveAllBoards() {
    for (Board board : openBoards) {
      board.save();
    }
  }

  public ArrayList<Board> getOpenBoards() {
    return openBoards;
  }

  public Board getBoard(int index) {
    return openBoards.get(index);
  }

  public void setOpenBoards(ArrayList<Board> boards) {
    openBoards = boards;
  }

  /**
   * Open a board. Return true for success, or false.
   */
  public boolean openBoard(
      boolean appendCWD, String restOfPath, String fileName) {
    Board board = Board.load(appendCWD, restOfPath, fileName);
    if (board == null) {
      return false;
    }
    addBoard(board, true);
    return true;
  }

  void closeAllBoards() {
    while (openBoards.size() > firstBoardIndexAfterClips) {
      removeBoard(firstBoardIndexAfterClips);
    }
  }

  public String[] getOpenBoardNamesExceptClips() {
    ArrayList<String> array = new ArrayList<>();
    for (int i = firstBoardIndexAfterClips;
        i < openBoards.size(); ++i) {
      array.add(openBoards.get(i).name());
    }
    return array.toArray(new String[]{});
  }

  public String[] getOpenBoardNames() {
    ArrayList<String> array = new ArrayList<>();
    for (int i = 0; i < openBoards.size(); ++i) {
      array.add(openBoards.get(i).name());
    }
    return array.toArray(new String[]{});
  }

  public void setCurrentBoardsAsDefaults() {
    // Make properties instance.
    Properties properties = new Properties();

    // Write default board file names, without any directories.
    Integer propertyNumber = Constants.firstDefaultsFilePropertyNumber;
    for (Integer index = firstBoardIndexAfterClips;
        index < openBoards.size(); ++index) {
      properties.setProperty(propertyNumber.toString(),
          openBoards.get(index).fileName);
      ++propertyNumber;
    }
    Use.savePropertiesFile(properties,
        "Default boards, opened on program start.",
        true, Constants.defaultBoardsFileName);
  }

  void loadDefaultBoards() {
    // Try to load defaults list from the file.
    Properties defaultsList = Use.loadPropertiesFile(
        true, Constants.defaultBoardsFileName);
    if (defaultsList == null) {
      return;
    }
    for (Integer i = Constants.firstDefaultsFilePropertyNumber;
        i <= Constants.maximumBoardFile; ++i) {
      String fileName = defaultsList.getProperty(i.toString());
      if (fileName != null) {
        Board board = Board.load(true, fileName, fileName);
        if (board != null) {
          addBoard(board, false);
        }
      }
    }
    arrangeAll();
  }

  public void loadClipsBoardIfNeeded() {
    // Find out if clips should be opened;
    boolean shouldOpen = Options.getOpenClips();

    // Open clips if indicated.
    if (shouldOpen) {
      openClips();
    }
  }

  /**
   * Add a new clip to the clips board.
   */
  void addClip() {
    // Do nothing if the clips board is not open.
    if (isClipsOpen == false) {
      return;
    }

    // Get clips board
    Board clips = openBoards.get(0);

    // Copy any currently selected text to the clipboard.
    String clipboardOrNull = Main.getCommandCenter().getAnySelectedTextWithClipboard_OrNull();

    // Do nothing for a null or empty clipboard.
    if (clipboardOrNull == null || clipboardOrNull.isEmpty()) {
      Frames.message("There is no text to copy. Please select some text\n"
          + "in your target application before hovering over the\n"
          + "Add Clip key.");
      return;
    }
    String clipboard = clipboardOrNull;

    // Make new clip key.
    moveClipsDownOne(clips);
    clips.keys[0][1] = new Key(clips);
    clips.keys[0][1].contents = clipboard;

    // Create title string.
    String titleString = clipboard.trim();
    titleString = Use.safeSubstring(0, 10, titleString);

    // Handle html tag bug.
    if (titleString.toLowerCase().contains("<html>")) {
      titleString = clipboard.trim();
      titleString = titleString.replace("<", "");
      titleString = titleString.replace(">", "");
      titleString = Use.safeSubstring(0, 10, titleString);
    }
    clips.keys[0][1].setText(titleString);

    // Save clips board
    clips.save();
    arrangeAll();
  }

  /**
   * Move all clip button contents down by one. Leave index 0 alone.
   */
  private void moveClipsDownOne(Board clips) {
    for (int i = (clips.keys[0].length - 2); i >= 1; --i) {
      clips.keys[0][i + 1].copyFrom(clips.keys[0][i], true);
    }
  }

  /**
   * Create or clear the clips board.
   */
  public Board makeOrClearClips() {
    boolean oldQuickShow = false;
    boolean oldLockedOpen = false;
    if ((openBoards != null) && (openBoards.size() >= 1)) {
      oldQuickShow = openBoards.get(0).quickShow;
      oldLockedOpen = openBoards.get(0).lockedOpen;
    }
    Board clips = new Board("Clips", Constants.clipsBoardFileName,
        1, false, 100, Constants.defaultBoardBorderColor,
        Constants.defaultKeyBackgroundColor, Constants.defaultKeyTextColor);
    clips.quickShow = oldQuickShow;
    clips.lockedOpen = oldLockedOpen;
    clips.autoSize = true;
    clips.keys[0][0].setText("Add Clip");
    clips.keys[0][0].contents = "[addclip][parse]";
    clips.keys[0][0].setBackgroundColor(
        Constants.defaultQuickShowBackgroundColor);
    clips.keys[0][0].setHorizontalAlignment(JLabel.CENTER);
    clips.save();
    addOrReplaceClipsBoard(clips);
    return clips;
  }

  private void addOrReplaceClipsBoard(Board clips) {
    // Close clips board if open.
    if (isClipsOpen) {
      closeClips();
    }

    // Add new clips board to openBoards array.
    if (openBoards.isEmpty()) {
      // No boards open, just add to bottom.
      addBoard(clips, false);
    } else {
      // Boards are open, Move all open boards down one.
      openBoards.add(null);
      for (int i = openBoards.size() - 1; i > 0; --i) {
        openBoards.set(i, openBoards.get(i - 1));
      }
      // Set board 0 to clips.
      openBoards.set(0, clips);
    }

    // Add hover listener.
    Main.getMouseWatcher().addHoverListener(
        new MouseWatcher.HoverPackage(clips, clips));

    // Set the clips indicator variables.
    setClipsOpenIndicatorsAndMenuItems(true);

    // Arrange everything.
    arrangeAll();
  }

  /**
   * Sort clips board by usage frequency.
   */
  public void sortClipsByUsage() {
    if ((openBoards == null) || (openBoards.size() < 1)) {
      return;
    }

    // Do nothing if the clips board is not open.
    if (isClipsOpen == false) {
      return;
    }

    Board clips = openBoards.get(0);
    ArrayList<Key> clipsKeys = new ArrayList<>();
    for (int i = 1; i < clips.keys[0].length; ++i) {
      clipsKeys.add(clips.keys[0][i]);
    }
    Comparator<Key> clipsComparator = new Comparator<Key>() {
      @Override
      public int compare(Key key1, Key key2) {
        // Compares its two arguments for order.
        // Returns a negative integer, zero, or a positive integer
        // as the first argument is less than, equal to, or greater than the second.
        // Give the clips a decending order.
        if (key1.uses < key2.uses) {
          return 1;
        } else if (key1.uses == key2.uses) {
          return 0;
        } else {
          return -1;
        }
      }
    };
    Collections.sort(clipsKeys, clipsComparator);
    for (int i = 1; i < clips.keys[0].length; ++i) {
      clips.keys[0][i] = clipsKeys.get(i - 1);
    }
    clips.save();
    arrangeAll();
  }

  public void addBoard(Board board, boolean checkForDisabledBoards) {
    openBoards.add(board);
    // Add hover listener.
    Main.getMouseWatcher().addHoverListener(
        new MouseWatcher.HoverPackage(board, board));

    arrangeAll();
  }

  public void removeBoard(int index) {
    if (index < firstBoardIndexAfterClips
        || index >= openBoards.size()) {
      return;
    }
    openBoards.get(index).save();

    // Remove hover listener.
    Main.getMouseWatcher().removeBoardFromHoverListeners(
        openBoards.get(index));

    openBoards.remove(index);
    arrangeAll();
  }

  int getFirstBoardIndexAfterClips() {
    return firstBoardIndexAfterClips;
  }

  int boardsOpenBesidesClips() {
    return openBoards.size() - firstBoardIndexAfterClips;
  }

  int boardsOpenIncludingClips() {
    return openBoards.size();
  }

  public boolean isClipsOpen() {
    return isClipsOpen;
  }

  @SuppressWarnings("UnusedAssignment")
  public void openClips() {
    String clipsFileName = Constants.clipsBoardFileName;
    Board clips = Board.load(true, clipsFileName, clipsFileName);
    if (clips != null) {
      clips.keys[0][0].setHorizontalAlignment(JLabel.CENTER);
      addOrReplaceClipsBoard(clips);
    } else {
      // Failed to load clips from file, so create a new one.
      if (Constants.debug) {
        Frames.message("Failed to load clips board, creating new one.");
      }
      clips = makeOrClearClips();
    }
  }

  public void closeClips() {
    // continue only if clips board is open.
    if (openBoards.isEmpty() || isClipsOpen == false) {
      return;
    }

    // Save the clips board.
    openBoards.get(0).save();

    // Remove hover listener.
    Main.getMouseWatcher().removeBoardFromHoverListeners(
        openBoards.get(0));

    // Remove the clips board.
    openBoards.remove(0);
    arrangeAll();

    // Set the clips indicator variables.
    setClipsOpenIndicatorsAndMenuItems(false);
  }

  private void setClipsOpenIndicatorsAndMenuItems(
      boolean setIndicateClipsOpen) {
    isClipsOpen = setIndicateClipsOpen;
    firstBoardIndexAfterClips = (isClipsOpen ? 1 : 0);
    Options.setOpenClips(isClipsOpen);
    Main.getMyPopupMenu().setOpenClipsMenuItems();
  }

  public boolean isClickTargetLabelShowing() {
    return showClickTargetLabel;
  }

  public void setClickTargetLabelShowing(boolean show) {
    boolean oldShowState = showClickTargetLabel;
    showClickTargetLabel = show;
    if (oldShowState != showClickTargetLabel) {
      arrangeAll();
    }
  }

}
