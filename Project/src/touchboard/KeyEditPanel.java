package touchboard;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;
import tbUtilities.Frames;
import tbUtilities.Use;

public class KeyEditPanel extends JPanel
    implements WindowFocusListener, WindowListener {

  JFrame parentWindow;
  Key editKey;
  int exampleKeyAutomaticWidth;
  MatteBorder exampleKeyBorder;
  private boolean resetExampleKeyIsInUndoState = false;
  private Color resetExampleKeySavedBackground;
  private Color resetExampleKeySavedForeground;
  private Point keyLocation;
  static private boolean keyEditPanelOpen = false;

  static private enum ClipType {
    Empty, Key, Row, Column
  }
  static private ClipType clipKeysType = ClipType.Empty;
  static private ArrayList<Key> clipKeys = new ArrayList<Key>();
  static private Board clipKeysSourceBoard;
  static private Point clipKeysSourcePoint;

  static private ClipType undoPasteType = ClipType.Empty;
  static private ArrayList<Key> undoPasteKeys = new ArrayList<Key>();

  public KeyEditPanel(JFrame aParentWindow, Key aEditKey, Point aKeyLocation) {
    parentWindow = aParentWindow;
    editKey = aEditKey;
    keyLocation = aKeyLocation;
    MainPanel.setKeyEditPanel(this);

    parentWindow.addWindowFocusListener(this);
    parentWindow.addWindowListener(this);

    // Initialize JFormDesigner components.
    initComponents();

    // Set up example key to automatic width.
    exampleKeyLabel.setText(" Key");
    exampleKeyAutomaticWidth = exampleKeyLabel.getPreferredSize().width;
    setExampleKeyWidth(null);
    reactToAutoSize(editKey.parentBoard.autoSize);
    // TODO React to changes in the key title, by typing it into the example key.

    // Get data from key.
    keyTitleTextField.setText(editKey.getText());
    contentsTextPane.setText(editKey.contents);
    int editKeyColumns = Use.clampInt(editKey.columnsWide, 1, 10);
    columnsWideComboBox.setSelectedIndex(editKeyColumns - 1);
    exampleKeyBorder = BorderFactory.createMatteBorder(
        2, 2, 2, 2, editKey.parentBoard.borderColor);
    exampleKeyLabel.setBorder(exampleKeyBorder);
    exampleKeyLabel.setBackground(editKey.getBackgroundColor());
    exampleKeyLabel.setForeground(editKey.getTextColor());

    // Check for example key color equality, and set reset button enabled
    // or disabled
    setResetColorsButtonState();

    // Arrange the copy buttons.
    arrangeCopyButtons();

    // Add actions to components.
    addActions();
  }

  private void initComponents() {
    // JFormDesigner - Component initialization - DO NOT MODIFY//GEN-BEGIN:initComponents
    keyTitleLabel = new JLabel();
    keyTitleTextField = new JTextField();
    okButton = new JButton();
    columnsWideLabel = new JLabel();
    columnsWideComboBox = new JComboBox();
    undoButtonPanel = new JPanel();
    undoButton = new JButton();
    cancelButton = new JButton();
    stylePanel = new JPanel();
    keyBackgroundLabel = new JLabel();
    keyBackgroundButton = new JButton();
    exampleKeyTitleLabel = new JLabel();
    resetExampleKeyButton = new JButton();
    keyTextColorLabel = new JLabel();
    keyTextColorButton = new JButton();
    exampleKeyPanel = new JPanel();
    exampleKeyLabel = new JLabel();
    contentsLabel = new JLabel();
    contentsScrollPane = new JScrollPane();
    contentsTextPane = new JTextPane();
    buttonPanel = new JPanel();
    copyKeyButton = new JButton();
    pasteKeyButton = new JButton();
    clearKeyButton = new JButton();
    copyRowButton = new JButton();
    pasteRowButton = new JButton();
    copyColumnButton = new JButton();
    pasteColumnButton = new JButton();
    CellConstraints cc = new CellConstraints();

    //======== this ========
    setLayout(new FormLayout(
        new ColumnSpec[]{
          FormFactory.UNRELATED_GAP_COLSPEC,
          FormFactory.DEFAULT_COLSPEC,
          FormFactory.RELATED_GAP_COLSPEC,
          new ColumnSpec("max(default;50dlu)"),
          FormFactory.DEFAULT_COLSPEC,
          new ColumnSpec("max(default;75dlu)"),
          new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW),
          FormFactory.DEFAULT_COLSPEC,
          FormFactory.UNRELATED_GAP_COLSPEC
        },
        new RowSpec[]{
          FormFactory.UNRELATED_GAP_ROWSPEC,
          FormFactory.DEFAULT_ROWSPEC,
          FormFactory.RELATED_GAP_ROWSPEC,
          FormFactory.DEFAULT_ROWSPEC,
          FormFactory.UNRELATED_GAP_ROWSPEC,
          FormFactory.DEFAULT_ROWSPEC,
          FormFactory.UNRELATED_GAP_ROWSPEC,
          FormFactory.DEFAULT_ROWSPEC,
          FormFactory.LINE_GAP_ROWSPEC,
          new RowSpec(RowSpec.FILL, Sizes.bounded(Sizes.DEFAULT, Sizes.dluY(120), Sizes.dluY(150)), FormSpec.DEFAULT_GROW),
          FormFactory.RELATED_GAP_ROWSPEC,
          FormFactory.PREF_ROWSPEC,
          FormFactory.RELATED_GAP_ROWSPEC
        }));

    //---- keyTitleLabel ----
    keyTitleLabel.setText("Key Title");
    add(keyTitleLabel, cc.xy(2, 2));

    //---- keyTitleTextField ----
    keyTitleTextField.setText("Key");
    add(keyTitleTextField, cc.xywh(4, 2, 3, 1));

    //---- okButton ----
    okButton.setText("OK");
    add(okButton, cc.xy(8, 2));

    //---- columnsWideLabel ----
    columnsWideLabel.setText("Columns Wide");
    add(columnsWideLabel, cc.xy(2, 4));

    //---- columnsWideComboBox ----
    columnsWideComboBox.setModel(new DefaultComboBoxModel(new String[]{
      "1",
      "2",
      "3",
      "4",
      "5",
      "6",
      "7",
      "8",
      "9",
      "10"
    }));
    add(columnsWideComboBox, cc.xy(4, 4));

    //======== undoButtonPanel ========
    {
      undoButtonPanel.setLayout(new FormLayout(
          "default:grow, default:grow, default:grow, default:grow, default:grow, default:grow, default:grow",
          "default"));

      //---- undoButton ----
      undoButton.setText("Undo Paste Column");
      undoButton.setFocusable(false);
      undoButton.setFocusPainted(false);
      undoButtonPanel.add(undoButton, new CellConstraints(2, 1, 5, 1, CellConstraints.DEFAULT, CellConstraints.DEFAULT, new Insets(1, 0, 1, 0)));
    }
    add(undoButtonPanel, cc.xywh(6, 4, 2, 1));

    //---- cancelButton ----
    cancelButton.setText("Cancel");
    add(cancelButton, cc.xy(8, 4));

    //======== stylePanel ========
    {
      stylePanel.setBorder(LineBorder.createBlackLineBorder());
      stylePanel.setLayout(new FormLayout(
          new ColumnSpec[]{
            new ColumnSpec("max(default;20dlu)"),
            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
            FormFactory.DEFAULT_COLSPEC,
            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
            FormFactory.DEFAULT_COLSPEC,
            new ColumnSpec("left:max(default;20dlu)"),
            new ColumnSpec("max(pref;20dlu)"),
            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
            new ColumnSpec("max(default;30dlu)"),
            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
            new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW),
            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
            FormFactory.DEFAULT_COLSPEC
          },
          new RowSpec[]{
            FormFactory.LINE_GAP_ROWSPEC,
            FormFactory.LINE_GAP_ROWSPEC,
            FormFactory.DEFAULT_ROWSPEC,
            FormFactory.LINE_GAP_ROWSPEC,
            FormFactory.DEFAULT_ROWSPEC,
            FormFactory.LINE_GAP_ROWSPEC,
            FormFactory.LINE_GAP_ROWSPEC
          }));

      //---- keyBackgroundLabel ----
      keyBackgroundLabel.setText("Key Background:");
      stylePanel.add(keyBackgroundLabel, cc.xy(3, 3));

      //---- keyBackgroundButton ----
      keyBackgroundButton.setText("...");
      keyBackgroundButton.setFocusPainted(false);
      stylePanel.add(keyBackgroundButton, new CellConstraints(5, 3, 1, 1, CellConstraints.DEFAULT, CellConstraints.DEFAULT, new Insets(2, 0, 2, 4)));

      //---- exampleKeyTitleLabel ----
      exampleKeyTitleLabel.setText("Example Key");
      stylePanel.add(exampleKeyTitleLabel, cc.xy(7, 3));

      //---- resetExampleKeyButton ----
      resetExampleKeyButton.setText("<html><small>Reset</small></html>");
      resetExampleKeyButton.setFocusPainted(false);
      stylePanel.add(resetExampleKeyButton, new CellConstraints(9, 3, 1, 1, CellConstraints.DEFAULT, CellConstraints.DEFAULT, new Insets(2, 0, 2, 0)));

      //---- keyTextColorLabel ----
      keyTextColorLabel.setText("Key Text Color:");
      stylePanel.add(keyTextColorLabel, cc.xy(3, 5));

      //---- keyTextColorButton ----
      keyTextColorButton.setText("...");
      keyTextColorButton.setFocusPainted(false);
      stylePanel.add(keyTextColorButton, new CellConstraints(5, 5, 1, 1, CellConstraints.DEFAULT, CellConstraints.DEFAULT, new Insets(2, 0, 2, 4)));

      //======== exampleKeyPanel ========
      {
        exampleKeyPanel.setLayout(new FormLayout(
            "left:default:grow",
            "top:default:grow"));

        //---- exampleKeyLabel ----
        exampleKeyLabel.setText(" Key");
        exampleKeyLabel.setOpaque(true);
        exampleKeyLabel.setBorder(new MatteBorder(2, 2, 2, 2, Color.red));
        exampleKeyLabel.setHorizontalAlignment(SwingConstants.LEFT);
        exampleKeyLabel.setHorizontalTextPosition(SwingConstants.LEFT);
        exampleKeyLabel.setBackground(Color.blue);
        exampleKeyLabel.setForeground(Color.white);
        exampleKeyPanel.add(exampleKeyLabel, new CellConstraints(1, 1, 1, 1, CellConstraints.DEFAULT, CellConstraints.DEFAULT, new Insets(0, 4, 0, 0)));
      }
      stylePanel.add(exampleKeyPanel, cc.xywh(7, 5, 7, 2));
    }
    add(stylePanel, cc.xywh(2, 6, 7, 1));

    //---- contentsLabel ----
    contentsLabel.setText("   Contents:");
    add(contentsLabel, cc.xywh(2, 8, 3, 1));

    //======== contentsScrollPane ========
    {
      contentsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
      contentsScrollPane.setViewportView(contentsTextPane);
    }
    add(contentsScrollPane, cc.xywh(2, 10, 7, 1));

    //======== buttonPanel ========
    {
      buttonPanel.setLayout(new FormLayout(
          new ColumnSpec[]{
            FormFactory.RELATED_GAP_COLSPEC,
            new ColumnSpec(ColumnSpec.FILL, Sizes.dluX(20), FormSpec.DEFAULT_GROW),
            FormFactory.RELATED_GAP_COLSPEC,
            new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW),
            new ColumnSpec(ColumnSpec.LEFT, Sizes.dluX(15), FormSpec.NO_GROW),
            new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW),
            FormFactory.RELATED_GAP_COLSPEC,
            new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW),
            FormFactory.RELATED_GAP_COLSPEC
          },
          RowSpec.decodeSpecs("default, default")));
      ((FormLayout) buttonPanel.getLayout()).setColumnGroups(new int[][]{{2, 4, 6, 8}});

      //---- copyKeyButton ----
      copyKeyButton.setText("Copy Key");
      copyKeyButton.setFocusable(false);
      copyKeyButton.setFocusPainted(false);
      buttonPanel.add(copyKeyButton, new CellConstraints(2, 1, 1, 1, CellConstraints.DEFAULT, CellConstraints.DEFAULT, new Insets(2, 0, 2, 0)));

      //---- pasteKeyButton ----
      pasteKeyButton.setText("Paste Key");
      pasteKeyButton.setFocusable(false);
      pasteKeyButton.setFocusPainted(false);
      buttonPanel.add(pasteKeyButton, new CellConstraints(2, 2, 1, 1, CellConstraints.DEFAULT, CellConstraints.DEFAULT, new Insets(1, 0, 3, 0)));

      //---- clearKeyButton ----
      clearKeyButton.setText("Clear Key");
      clearKeyButton.setFocusable(false);
      clearKeyButton.setFocusPainted(false);
      buttonPanel.add(clearKeyButton, new CellConstraints(4, 1, 1, 1, CellConstraints.DEFAULT, CellConstraints.DEFAULT, new Insets(2, 0, 2, 0)));

      //---- copyRowButton ----
      copyRowButton.setText("Copy Row");
      copyRowButton.setFocusable(false);
      copyRowButton.setFocusPainted(false);
      buttonPanel.add(copyRowButton, new CellConstraints(6, 1, 1, 1, CellConstraints.DEFAULT, CellConstraints.DEFAULT, new Insets(2, 0, 2, 0)));

      //---- pasteRowButton ----
      pasteRowButton.setText("Paste Row");
      pasteRowButton.setFocusable(false);
      pasteRowButton.setFocusPainted(false);
      buttonPanel.add(pasteRowButton, new CellConstraints(6, 2, 1, 1, CellConstraints.DEFAULT, CellConstraints.DEFAULT, new Insets(1, 0, 3, 0)));

      //---- copyColumnButton ----
      copyColumnButton.setText("Copy Column");
      copyColumnButton.setFocusable(false);
      copyColumnButton.setFocusPainted(false);
      buttonPanel.add(copyColumnButton, new CellConstraints(8, 1, 1, 1, CellConstraints.DEFAULT, CellConstraints.DEFAULT, new Insets(2, 0, 2, 0)));

      //---- pasteColumnButton ----
      pasteColumnButton.setText("Paste Column");
      pasteColumnButton.setFocusable(false);
      pasteColumnButton.setFocusPainted(false);
      buttonPanel.add(pasteColumnButton, new CellConstraints(8, 2, 1, 1, CellConstraints.DEFAULT, CellConstraints.DEFAULT, new Insets(1, 0, 3, 0)));
    }
    add(buttonPanel, cc.xywh(2, 12, 7, 1));
    // JFormDesigner - End of component initialization//GEN-END:initComponents
  }

  // JFormDesigner - Variables declaration - DO NOT MODIFY//GEN-BEGIN:variables
  private JLabel keyTitleLabel;
  private JTextField keyTitleTextField;
  private JButton okButton;
  private JLabel columnsWideLabel;
  private JComboBox columnsWideComboBox;
  private JPanel undoButtonPanel;
  private JButton undoButton;
  private JButton cancelButton;
  private JPanel stylePanel;
  private JLabel keyBackgroundLabel;
  private JButton keyBackgroundButton;
  private JLabel exampleKeyTitleLabel;
  private JButton resetExampleKeyButton;
  private JLabel keyTextColorLabel;
  private JButton keyTextColorButton;
  private JPanel exampleKeyPanel;
  private JLabel exampleKeyLabel;
  private JLabel contentsLabel;
  private JScrollPane contentsScrollPane;
  private JTextPane contentsTextPane;
  private JPanel buttonPanel;
  private JButton copyKeyButton;
  private JButton pasteKeyButton;
  private JButton clearKeyButton;
  private JButton copyRowButton;
  private JButton pasteRowButton;
  private JButton copyColumnButton;
  private JButton pasteColumnButton;
  // JFormDesigner - End of variables declaration//GEN-END:variables

  /**
   * This will create and display an instance of this panel
   */
  static public void go(Key editKey, Point keyLocation) {
    if (keyEditPanelOpen == true) {
      return;
    }
    keyEditPanelOpen = true;

    if (editKey == null) {
      Frames.message("Error in KeyEditPanel.go(), null key passed in for editing.");
      return;
    }

    JFrame window = new JFrame();
    MouseWatcher.addHoverBlocker(window);

    window.setTitle("Edit Key");
    window.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    window.setUndecorated(false);
    window.setAlwaysOnTop(true);
    window.setResizable(true);
    KeyEditPanel panel = new KeyEditPanel(
        window, editKey, keyLocation);
    window.add(panel);
    window.pack();
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    Point center = ge.getCenterPoint();
    Rectangle bounds = ge.getMaximumWindowBounds();
    int w = Math.max(bounds.width / 2, Math.min(window.getWidth(), bounds.width));
    int h = Math.max(bounds.height / 2, Math.min(window.getHeight(), bounds.height));
    int x = bounds.x;
    int y = center.y - h / 2;
    window.setBounds(x, y, w, h);
    window.validate();
    window.setVisible(true);

  }

  private void setExampleKeyWidth(Integer width) {
    Dimension newKeySize;
    Dimension newKeyMinimum;
    if (width == null) {
      exampleKeyLabel.setText(" Key");
      newKeySize = new Dimension(exampleKeyAutomaticWidth + 2,
          Constants.keyHeight + 4);
      newKeyMinimum = null;
    } else {
      if (width <= 25) {
        exampleKeyLabel.setText(" K");
      } else {
        exampleKeyLabel.setText(" Key");
      }
      newKeySize = new Dimension(width + 4,
          Constants.keyHeight + 4);
      newKeyMinimum = new Dimension(10, 10);
    }
    exampleKeyLabel.setMinimumSize(newKeyMinimum);
    exampleKeyLabel.setPreferredSize(newKeySize);
    exampleKeyLabel.setMaximumSize(newKeySize);
    exampleKeyPanel.invalidate();
    parentWindow.repaint();
    parentWindow.pack();
  }

  public void reactToAutoSize(boolean autosize) {
    if (autosize) {
      setExampleKeyWidth(null);
    } else {
      setExampleKeyWidth(editKey.parentBoard.fixedKeyWidth);
    }
  }

  public void setResetColorsButtonState() {
    // Find out if the colors are at their defaults or not.
    boolean colorsAtDefaults = false;
    if (exampleKeyLabel.getBackground().equals(
        editKey.parentBoard.newKeysBackgroundColor)
        && exampleKeyLabel.getForeground().equals(
            editKey.parentBoard.newKeysTextColor)) {
      colorsAtDefaults = true;
    }
    // Put the button back at "reset" if someone changes a color
    // after a reset to defaults.
    if (resetExampleKeyIsInUndoState && Use.not(colorsAtDefaults)) {
      resetExampleKeyIsInUndoState = false;
    }
    // Set the reset button label.
    if (resetExampleKeyIsInUndoState) {
      resetExampleKeyButton.setText("<html><small>Undo </small></html>");
    } else {
      resetExampleKeyButton.setText("<html><small>Reset</small></html>");
    }
    // Disable the button if it says reset, but the colors are
    // already at default.
    if (Use.not(resetExampleKeyIsInUndoState) && colorsAtDefaults) {
      resetExampleKeyButton.setEnabled(false);
    } else {
      resetExampleKeyButton.setEnabled(true);
    }
  }

  private void okButtonAction() {
    String title = keyTitleTextField.getText();
    int columnsWide = columnsWideComboBox.getSelectedIndex() + 1;
    String contents = contentsTextPane.getText();
    if (title == null) {
      title = "";
    }
    title = Use.safeSubstring(0, Constants.maxKeyTitleSize, title);
    if (contents == null) {
      contents = "";
    }

    editKey.setText(title);
    editKey.columnsWide = columnsWide;
    editKey.contents = contents;
    editKey.setColors(
        exampleKeyLabel.getBackground(), exampleKeyLabel.getForeground());

    editKey.parentBoard.setKey(keyLocation, editKey);
    editKey.parentBoard.save();
    Main.getBoardManager().arrangeAll();

    // Clear the undo information so that any row or column pastings
    // become permanent.
    undoPasteType = ClipType.Empty;
    undoPasteKeys.clear();

    // Close the window.
    parentWindow.dispose();
  }

  public void windowGainedFocus(WindowEvent e) {
    Frames.setMessageParent(parentWindow);
    MouseWatcher.setSkipHoverBlockersCheck(false);
  }

  public void windowLostFocus(WindowEvent e) {
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
    tryUndoButtonAction();
    undoPasteType = ClipType.Empty;
    undoPasteKeys.clear();
    keyEditPanelOpen = false;
    MouseWatcher.removeHoverBlocker(parentWindow);
    MainPanel.setKeyEditPanel(null);
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
        cancelButtonAction();
      }
    });

    // Key background color Button
    keyBackgroundButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        Color chosenColor = JColorChooser.showDialog(
            parentWindow,
            "Choose key background color",
            exampleKeyLabel.getBackground());
        if (chosenColor != null) {
          exampleKeyLabel.setBackground(chosenColor);
        }
        setResetColorsButtonState();
      }
    });

    // Key text color Button
    keyTextColorButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        Color chosenColor = JColorChooser.showDialog(
            parentWindow,
            "Choose key text color",
            exampleKeyLabel.getForeground());
        if (chosenColor != null) {
          exampleKeyLabel.setForeground(chosenColor);
        }
        setResetColorsButtonState();
      }
    });

    // Reset example key button
    resetExampleKeyButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (resetExampleKeyIsInUndoState == false) {
          resetExampleKeySavedBackground = exampleKeyLabel.getBackground();
          resetExampleKeySavedForeground = exampleKeyLabel.getForeground();
          exampleKeyLabel.setBackground(editKey.parentBoard.newKeysBackgroundColor);
          exampleKeyLabel.setForeground(editKey.parentBoard.newKeysTextColor);
          resetExampleKeyIsInUndoState = true;
        } else {
          exampleKeyLabel.setBackground(resetExampleKeySavedBackground);
          exampleKeyLabel.setForeground(resetExampleKeySavedForeground);
          resetExampleKeySavedBackground = null;
          resetExampleKeySavedForeground = null;
          resetExampleKeyIsInUndoState = false;
        }
        setResetColorsButtonState();
      }
    });

    // Copy Key Button
    copyKeyButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        copyKey();
      }
    });

    // Copy Row Button
    copyRowButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        copyRow();
      }
    });

    // Copy Column Button
    copyColumnButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        copyColumn();
      }
    });

    // Paste Key Button
    pasteKeyButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        pasteKeyToForm(clipKeys.get(0));
      }
    });

    // Paste Row Button
    pasteRowButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        pasteRow();
      }
    });

    // Paste Column Button
    pasteColumnButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        pasteColumn();
      }
    });

    // Clear Key Button
    clearKeyButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        clearForm();
      }
    });

    // Undo Button
    undoButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        tryUndoButtonAction();
      }
    });

  }

  private void copyKey() {
    clipKeysType = ClipType.Key;
    clipKeysSourceBoard = editKey.parentBoard;
    clipKeysSourcePoint = keyLocation;
    clipKeys.clear();

    Key copiedKey = new Key(editKey.parentBoard);
    copiedKey.setText(keyTitleTextField.getText());
    copiedKey.contents = contentsTextPane.getText();
    copiedKey.columnsWide = columnsWideComboBox.getSelectedIndex() + 1;
    copiedKey.setColors(exampleKeyLabel.getBackground(),
        exampleKeyLabel.getForeground());
    clipKeys.add(copiedKey);

    arrangeCopyButtons();
    setResetColorsButtonState();
  }

  private void copyRow() {
    int sourceRowIndex = keyLocation.y;
    clipKeysType = ClipType.Row;
    clipKeysSourceBoard = editKey.parentBoard;
    clipKeysSourcePoint = keyLocation;
    clipKeys.clear();
    for (int i = 0; i < editKey.parentBoard.columns(); ++i) {
      clipKeys.add(
          editKey.parentBoard.keys[i][sourceRowIndex].clone());
    }
    arrangeCopyButtons();
  }

  private void copyColumn() {
    int sourceColumnIndex = keyLocation.x;
    clipKeysType = ClipType.Column;
    clipKeysSourceBoard = editKey.parentBoard;
    clipKeysSourcePoint = keyLocation;
    clipKeys.clear();
    for (int i = 0; i < Constants.keyRows; ++i) {
      clipKeys.add(
          editKey.parentBoard.keys[sourceColumnIndex][i].clone());
    }
    arrangeCopyButtons();
  }

  private void pasteKeyToForm(Key source) {
    keyTitleTextField.setText(source.getText());
    contentsTextPane.setText(source.contents);
    columnsWideComboBox.setSelectedIndex(source.columnsWide - 1);
    exampleKeyLabel.setBackground(source.getBackgroundColor());
    exampleKeyLabel.setForeground(source.getTextColor());
    arrangeCopyButtons();
    setResetColorsButtonState();
  }

  private void pasteRow() {
    // Get our destination row information.
    int destinationRowIndex = keyLocation.y;
    Board destinationBoard = editKey.parentBoard;

    // Return if our destination row is the same as our source row.
    if (destinationBoard == clipKeysSourceBoard
        && destinationRowIndex == clipKeysSourcePoint.y) {
      return;
    }

    // Return if the undo information contains anything.
    if (undoPasteType != ClipType.Empty) {
      return;
    }

    // Create undo information.
    undoPasteType = ClipType.Row;
    undoPasteKeys.clear();
    for (int i = 0; i < editKey.parentBoard.columns(); ++i) {
      undoPasteKeys.add(
          editKey.parentBoard.keys[i][destinationRowIndex].clone());
    }

    // Paste the row to the board.
    for (int i = 0; i < clipKeys.size(); ++i) {
      editKey.parentBoard.setKey(
          new Point(i, destinationRowIndex), clipKeys.get(i));
    }
    Main.getBoardManager().arrangeAll();

    // Paste the right key to the form.
    if (keyLocation.x < clipKeys.size()) {
      pasteKeyToForm(clipKeys.get(keyLocation.x));
    }

    // Arrange the buttons.
    arrangeCopyButtons();
    setResetColorsButtonState();

    // Message the user.
    pasteWarningMessage();
  }

  private void pasteColumn() {
    // Get our editing keys column information.
    int destinationColumnIndex = keyLocation.x;
    Board destinationBoard = editKey.parentBoard;

    // Return if our destination column is the same as our source column.
    if (destinationBoard == clipKeysSourceBoard
        && destinationColumnIndex == clipKeysSourcePoint.x) {
      return;
    }

    // Return if the undo information contains anything.
    if (undoPasteType != ClipType.Empty) {
      return;
    }

    // Create undo information.
    undoPasteType = ClipType.Column;
    undoPasteKeys.clear();
    for (int i = 0; i < Constants.keyRows; ++i) {
      undoPasteKeys.add(
          editKey.parentBoard.keys[destinationColumnIndex][i].clone());
    }

    // Paste the column to the board.
    for (int i = 0; i < clipKeys.size(); ++i) {
      editKey.parentBoard.setKey(
          new Point(destinationColumnIndex, i), clipKeys.get(i));
    }
    Main.getBoardManager().arrangeAll();

    // Paste the right key to the form.
    if (keyLocation.y < clipKeys.size()) {
      pasteKeyToForm(clipKeys.get(keyLocation.y));
    }

    // Arrange the buttons.
    arrangeCopyButtons();
    setResetColorsButtonState();

    // Message the user.
    pasteWarningMessage();
  }

  private void clearForm() {
    keyTitleTextField.setText("");
    contentsTextPane.setText("");
    columnsWideComboBox.setSelectedIndex(0);
    exampleKeyLabel.setBackground(
        editKey.parentBoard.newKeysBackgroundColor);
    exampleKeyLabel.setForeground(
        editKey.parentBoard.newKeysTextColor);
    setResetColorsButtonState();
  }

  private void arrangeCopyButtons() {
    pasteKeyButton.setEnabled(clipKeysType == ClipType.Key);
    pasteRowButton.setEnabled(clipKeysType == ClipType.Row);
    pasteColumnButton.setEnabled(clipKeysType == ClipType.Column);
    undoButton.setVisible(undoPasteType != ClipType.Empty);
    String undoButtonString = undoPasteType == ClipType.Column
        ? "Undo Paste Of Column" : "Undo Paste Of Row";
    undoButton.setText(undoButtonString);
  }

  private void tryUndoButtonAction() {
    // If there is nothing to undo, return.
    if (undoPasteType == ClipType.Empty) {
      return;
    }
    // If we are undoing a row paste,
    // copy undo keys back to the current board row.
    int destinationRowIndex = keyLocation.y;
    if (undoPasteType == ClipType.Row) {
      for (int i = 0; i < undoPasteKeys.size(); ++i) {
        editKey.parentBoard.setKey(
            new Point(i, destinationRowIndex), undoPasteKeys.get(i));
      }
      // Paste the right key to the form.
      if (keyLocation.x < undoPasteKeys.size()) {
        pasteKeyToForm(undoPasteKeys.get(keyLocation.x));
      }
    }

    // If we are undoing a column paste
    // copy undo keys back to the current board column.
    int destinationColumnIndex = keyLocation.x;
    if (undoPasteType == ClipType.Column) {
      for (int i = 0; i < undoPasteKeys.size(); ++i) {
        editKey.parentBoard.setKey(
            new Point(destinationColumnIndex, i), undoPasteKeys.get(i));
      }
      // Paste the right key to the form.
      if (keyLocation.y < undoPasteKeys.size()) {
        pasteKeyToForm(undoPasteKeys.get(keyLocation.y));
      }
    }
    // Arranged the destination board.
    Main.getBoardManager().arrangeAll();

    // Clear the undo information.
    undoPasteType = ClipType.Empty;
    undoPasteKeys.clear();

    // Arrange the buttons.
    arrangeCopyButtons();
  }

  private void pasteWarningMessage() {
    String undoNow = "Undo Paste Now";
    String result = Frames.showOptions(
        "Warning, original keys are DELETED when you paste\n"
        + "a row or column over them.\n\n"
        + "After you click OK on the Edit Key dialog, this paste\n"
        + "becomes -permanent- and cannot be undone.\n"
        + "Before then, you may undo this paste with the\n"
        + "Undo Paste button, or by clicking the Cancel button.",
        "Warning", undoNow, JOptionPane.INFORMATION_MESSAGE,
        undoNow, "Continue With Paste", null);
    if (result.equals(undoNow)) {
      tryUndoButtonAction();
    }
  }

  public void cancelButtonAction() {
    parentWindow.dispose();
  }
}
