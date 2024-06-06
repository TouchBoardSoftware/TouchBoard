package com.tb.touchboard;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.*;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.*;
import com.tb.tbUtilities.Frames;

public class BoardListPanel
    extends JPanel implements WindowListener, WindowFocusListener {

  static private enum ListType {
    OPEN_BOARDS, FILE_BOARDS
  };
  static private String fileNameResult = null;
  static private Integer openBoardsIndexResult = null;

  private JDialog parentWindow = null;
  private ListType listType = null;
  private boolean reorderOpenBoards = false;
  private String capitalizedVerb = "";
  private BoardsList.FileBoards fileBoardsList = null;
  private BoardsList.OpenBoards openBoardsList = null;

  /**
   * Constructor
   */
  public BoardListPanel(
      JDialog aParentWindow, ListType aListType,
      boolean doReorderOpenBoards, String aCapitalizedVerb,
      boolean filterOpenFileBoards, boolean showClipsInOpenBoards) {
    parentWindow = aParentWindow;
    listType = aListType;
    reorderOpenBoards = doReorderOpenBoards;
    capitalizedVerb = aCapitalizedVerb;

    parentWindow.addWindowFocusListener(this);

    // Clear results.
    fileNameResult = null;
    openBoardsIndexResult = null;

    // Initialize JFormDesigner components.
    initComponents();
    okButton.setText(capitalizedVerb);

    // Add appropriate boards list.
    if (listType == ListType.FILE_BOARDS) {
      fileBoardsList = BoardsList.getFileBoardsList(filterOpenFileBoards);
      boardsList = fileBoardsList;
    }
    if (listType == ListType.OPEN_BOARDS) {
      openBoardsList = BoardsList.getOpenBoardsList(showClipsInOpenBoards);
      boardsList = openBoardsList;
    }
    if (doReorderOpenBoards) {
      moveUpButton.setVisible(true);
      moveDownButton.setVisible(true);
    }
    boardsScrollPane.setViewportView(boardsList);

    // Add actions to components.
    addActions();
  }

  private void initComponents() {
    // JFormDesigner - Component initialization - DO NOT MODIFY//GEN-BEGIN:initComponents
    okButton = new JButton();
    moveUpButton = new JButton();
    moveDownButton = new JButton();
    cancelButton = new JButton();
    boardsScrollPane = new JScrollPane();
    boardsList = new JList();
    CellConstraints cc = new CellConstraints();

    //======== this ========
    setLayout(new FormLayout(
        new ColumnSpec[]{
          FormFactory.UNRELATED_GAP_COLSPEC,
          FormFactory.DEFAULT_COLSPEC,
          new ColumnSpec("max(default;30dlu):grow"),
          FormFactory.DEFAULT_COLSPEC,
          FormFactory.RELATED_GAP_COLSPEC,
          FormFactory.DEFAULT_COLSPEC,
          new ColumnSpec("max(default;40dlu):grow"),
          FormFactory.DEFAULT_COLSPEC,
          FormFactory.UNRELATED_GAP_COLSPEC
        },
        new RowSpec[]{
          FormFactory.UNRELATED_GAP_ROWSPEC,
          FormFactory.DEFAULT_ROWSPEC,
          FormFactory.RELATED_GAP_ROWSPEC,
          FormFactory.DEFAULT_ROWSPEC,
          FormFactory.UNRELATED_GAP_ROWSPEC,
          new RowSpec("fill:max(default;120dlu):grow"),
          FormFactory.UNRELATED_GAP_ROWSPEC
        }));
    ((FormLayout) getLayout()).setColumnGroups(new int[][]{{4, 6}});
    ((FormLayout) getLayout()).setRowGroups(new int[][]{{2, 4}});

    //---- okButton ----
    okButton.setText("OK");
    add(okButton, cc.xy(8, 2));

    //---- moveUpButton ----
    moveUpButton.setText("Move Up");
    moveUpButton.setVisible(false);
    add(moveUpButton, new CellConstraints(4, 4, 1, 1, CellConstraints.DEFAULT, CellConstraints.DEFAULT, new Insets(2, 0, 2, 0)));

    //---- moveDownButton ----
    moveDownButton.setText("Move Down");
    moveDownButton.setVisible(false);
    add(moveDownButton, new CellConstraints(6, 4, 1, 1, CellConstraints.DEFAULT, CellConstraints.DEFAULT, new Insets(2, 0, 2, 0)));

    //---- cancelButton ----
    cancelButton.setText("Cancel");
    add(cancelButton, cc.xy(8, 4));

    //======== boardsScrollPane ========
    {
      boardsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
      boardsScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
      boardsScrollPane.setViewportView(boardsList);
    }
    add(boardsScrollPane, cc.xywh(2, 6, 7, 1));
    // JFormDesigner - End of component initialization//GEN-END:initComponents
  }

  // JFormDesigner - Variables declaration - DO NOT MODIFY//GEN-BEGIN:variables
  private JButton okButton;
  private JButton moveUpButton;
  private JButton moveDownButton;
  private JButton cancelButton;
  private JScrollPane boardsScrollPane;
  private JList boardsList;
  // JFormDesigner - End of variables declaration//GEN-END:variables

  /**
   * This will create and display an instance of this panel. This opens the panel for reordering
   * open boards.
   */
  static public void goOpenBoardsReorder() {
    show("Reorder Boards", "Reorder", ListType.OPEN_BOARDS, true,
        false, false);
  }

  /**
   * This will create and display an instance of this panel. Returns index to first selected open
   * board, or null.
   */
  static public Integer goOpenBoards(String windowTitle,
      String capitalizedVerb, boolean showClips) {
    show(windowTitle, capitalizedVerb, ListType.OPEN_BOARDS, false,
        false, showClips);
    return openBoardsIndexResult;
  }

  /**
   * This will create and display an instance of this panel. Returns path to first selected board
   * file, or null.
   */
  static public String goFileBoards(String windowTitle,
      String capitalizedVerb, boolean filterOpenFileBoards) {
    show(windowTitle, capitalizedVerb, ListType.FILE_BOARDS, false,
        filterOpenFileBoards, false);
    return fileNameResult;
  }

  private static void show(
      String windowTitle, String capitalizedVerb, ListType listType,
      boolean reorderOpenBoards, boolean filterOpenFileBoards,
      boolean showClipsInOpenBoards) {
    MouseWatcher.stopHoverCheck();
    JDialog dialog = new JDialog();
    dialog.setTitle(windowTitle);
    dialog.setModal(true);
    dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    dialog.setUndecorated(false);
    dialog.setAlwaysOnTop(true);
    dialog.setResizable(false);
    BoardListPanel panel = new BoardListPanel(
        dialog, listType, reorderOpenBoards, capitalizedVerb,
        filterOpenFileBoards, showClipsInOpenBoards);
    dialog.addWindowListener(panel);
    dialog.add(panel);
    dialog.pack();
    Frames.centerWindow(dialog, 50);
    dialog.setVisible(true);
  }

  public void windowOpened(WindowEvent e) {
  }

  public void windowClosing(WindowEvent e) {
  }

  public void windowIconified(WindowEvent e) {
  }

  public void windowDeiconified(WindowEvent e) {
  }

  public void windowActivated(WindowEvent e) {
  }

  public void windowDeactivated(WindowEvent e) {
  }

  public void windowClosed(WindowEvent e) {
    MouseWatcher.startHoverCheck();
  }

  // This will add actions for all the needed components
  private void addActions() {

    // OK Button
    okButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        okButtonAction();
      }
    });

    // Cancel Button
    cancelButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        parentWindow.dispose();
      }
    });

    // Up Button
    moveUpButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        moveSelection(true);
      }
    });

    // Down Button
    moveDownButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        moveSelection(false);
      }
    });

  }

  private void moveSelection(boolean up) {
    // Get selected list index and list size.
    int listIndex = openBoardsList.getSelectedIndex();
    int listSize = openBoardsList.listModel.getSize();

    // Display error if nothing is selected.
    if (listIndex < 0) {
      Frames.message("Please select a board to move first.");
      return;
    }

    // Do nothing if they are trying to move something outside the list.
    if (((listIndex == 0) && (up == true))
        || ((listIndex == (listSize - 1)) && (up == false))) {
      return;
    }

    // Move the element.
    int newIndex = listIndex;
    if (up) {
      --newIndex;
    } else {
      ++newIndex;
    }
    // Remove the element at the old position
    Object listEntry = openBoardsList.listModel.remove(listIndex);
    Integer indexEntry = openBoardsList.openBoardIndexes.remove(listIndex);

    // Add the element at the new position;
    openBoardsList.listModel.add(newIndex, listEntry);
    openBoardsList.openBoardIndexes.add(newIndex, indexEntry);

    openBoardsList.setSelectedIndex(newIndex);
    openBoardsList.ensureIndexIsVisible(newIndex);
  }

  private void okButtonAction() {
    if (reorderOpenBoards) {
      okReorderBoards();
      return;
    }
    if (listType == ListType.FILE_BOARDS) {
      okFileBoards();
      return;
    }
    if (listType == ListType.OPEN_BOARDS) {
      okOpenBoards();
      return;
    }
  }

  private void okReorderBoards() {
    ArrayList<Board> oldOpenBoards = Main.getBoardManager().getOpenBoards();
    ArrayList<Board> newOpenBoards = new ArrayList<Board>();
    Vector<Integer> indexMap = openBoardsList.openBoardIndexes;
    for (int i = 0; i < Main.getBoardManager().getFirstBoardIndexAfterClips(); ++i) {
      newOpenBoards.add(oldOpenBoards.get(i));
    }
    for (int i = 0; i < indexMap.size(); ++i) {
      newOpenBoards.add(oldOpenBoards.get(indexMap.get(i)));
    }
    Main.getBoardManager().setOpenBoards(newOpenBoards);
    Main.getBoardManager().arrangeAll();
    parentWindow.dispose();
  }

  private void okFileBoards() {
    int fileIndex = fileBoardsList.getSelectedIndex();
    if (fileIndex < 0) {
      Frames.message("Please select a board file to "
          + capitalizedVerb.toLowerCase() + ".");
      return;
    }
    fileNameResult = fileBoardsList.getFileName(fileIndex);
    parentWindow.dispose();
  }

  private void okOpenBoards() {
    int openIndex = openBoardsList.getSelectedIndex();
    if (openIndex < 0) {
      Frames.message("Please select a board to "
          + capitalizedVerb.toLowerCase() + ".");
      return;
    }
    openBoardsIndexResult = openBoardsList.getOpenBoardsIndex(openIndex);
    parentWindow.dispose();
  }

  public void windowGainedFocus(WindowEvent e) {
    Frames.setMessageParent(parentWindow);
  }

  public void windowLostFocus(WindowEvent e) {
  }

}
