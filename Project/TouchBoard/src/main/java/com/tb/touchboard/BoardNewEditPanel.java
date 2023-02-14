package com.tb.touchboard;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;
import com.tb.tbUtilities.Frames;
import com.tb.tbUtilities.Option;
import com.tb.tbUtilities.Use;

public class BoardNewEditPanel
        extends JPanel implements WindowListener, WindowFocusListener {

    static public enum PanelType {
        NEW_BOARD, EDIT_BOARD
    };
    private JDialog parentWindow;
    private Board editBoard;
    private MatteBorder exampleKeyBorder;
    private SpinnerNumberModel spinnerModel;
    private PanelType panelType;
    private ItemListener clearAllExistingKeysCheckBoxListener;
    private BoardsList.FileBoards fileBoardsList;
    private final int exampleKeyAutomaticWidth;
    private boolean resetExampleKeyIsInUndoState = false;
    private Color resetExampleKeySavedBorder;
    private Color resetExampleKeySavedBackground;
    private Color resetExampleKeySavedForeground;

    public BoardNewEditPanel(JDialog aParentWindow,
            PanelType aPanelType, Board optionalEditBoard) {
        parentWindow = aParentWindow;
        panelType = aPanelType;
        editBoard = optionalEditBoard;

        parentWindow.addWindowFocusListener(this);

        // Initialize JFormDesigner components.
        initComponents();

        // Create file boards list.
        fileBoardsList = BoardsList.getFileBoardsList(false);
        existingBoardsScrollPane.setViewportView(fileBoardsList);

        // Set up example key to new board defaults.
        exampleKeyLabel.setText(" Key");
        exampleKeyAutomaticWidth = exampleKeyLabel.getPreferredSize().width;
        exampleKeyBorder = BorderFactory.createMatteBorder(
                2, 2, 2, 2, Constants.defaultBoardBorderColor);
        exampleKeyLabel.setBorder(exampleKeyBorder);
        exampleKeyLabel.setBackground(Constants.defaultKeyBackgroundColor);
        exampleKeyLabel.setForeground(Constants.defaultKeyTextColor);
        setExampleKeyWidth(null);

        // Set up other new board values.
        columnsComboBox.setSelectedIndex(1);
        spinnerModel = (SpinnerNumberModel) buttonWidthSpinner.getModel();
        spinnerModel.setValue(Constants.defaultKeyWidth);

        // Set up edit panel specific things.
        if (panelType == PanelType.EDIT_BOARD) {
            // Change visible components.
            parentWindow.setTitle("Edit Board");
            blankNewBoardRadioButton.setVisible(false);
            copyKeysFromExistingBoardRadioButton.setVisible(false);
            resetAllKeyColorsCheckBox.setVisible(true);
            clearAllExistingKeysCheckBox.setVisible(true);
            copyAllKeysFromExistingBoardCheckBox.setVisible(true);

            // Get data from board.
            boardNameTextField.setText(editBoard.name());
            int editOriginalColumns = Use.clampInt(editBoard.columns(), 1, 10);
            columnsComboBox.setSelectedIndex(editOriginalColumns - 1);
            exampleKeyBorder = BorderFactory.createMatteBorder(
                    2, 2, 2, 2, editBoard.borderColor);
            exampleKeyLabel.setBorder(exampleKeyBorder);
            exampleKeyLabel.setBackground(editBoard.newKeysBackgroundColor);
            exampleKeyLabel.setForeground(editBoard.newKeysTextColor);
            autoSizeButtonsCheckBox.setSelected(editBoard.autoSize);
            int keyWidthTemp = Use.clampInt(editBoard.fixedKeyWidth,
                    Constants.minimumKeyWidth, Constants.maximumKeyWidth);
            spinnerModel.setValue(keyWidthTemp);
            reactToAutoSize(editBoard.autoSize);
        }
        // Check for example key color equality, and set reset button enabled
        // or disabled
        setResetColorsButtonState();

        // Add actions to components.
        addActions();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY//GEN-BEGIN:initComponents
        boardNameLabel = new JLabel();
        boardNameTextField = new JTextField();
        okButton = new JButton();
        columnsLabel = new JLabel();
        columnsComboBox = new JComboBox();
        copyAllKeysFromExistingBoardCheckBox = new JCheckBox();
        cancelButton = new JButton();
        stylePanel = new JPanel();
        boardBorderColorLabel = new JLabel();
        boardBorderColorButton = new JButton();
        exampleKeyTitleLabel = new JLabel();
        resetExampleKeyButton = new JButton();
        newKeysBackgroundLabel = new JLabel();
        newKeysBackgroundButton = new JButton();
        exampleKeyPanel = new JPanel();
        exampleKeyLabel = new JLabel();
        newKeysTextColorLabel = new JLabel();
        newKeysTextColorButton = new JButton();
        resetAllKeyColorsCheckBox = new JCheckBox();
        autoSizeButtonsCheckBox = new JCheckBox();
        buttonWidthLabel = new JLabel();
        buttonWidthSpinner = new JSpinner();
        buttonWidthInactiveTextField = new JTextField();
        clearAllExistingKeysCheckBox = new JCheckBox();
        blankNewBoardRadioButton = new JRadioButton();
        copyKeysFromExistingBoardRadioButton = new JRadioButton();
        existingBoardsScrollPane = new JScrollPane();
        preserveKeyColorsFromExistingBoardCheckBox = new JCheckBox();
        CellConstraints cc = new CellConstraints();

        //======== this ========
        setLayout(new FormLayout(
                new ColumnSpec[]{
                    FormFactory.UNRELATED_GAP_COLSPEC,
                    FormFactory.DEFAULT_COLSPEC,
                    FormFactory.RELATED_GAP_COLSPEC,
                    new ColumnSpec("max(default;50dlu)"),
                    FormFactory.DEFAULT_COLSPEC,
                    new ColumnSpec("max(default;80dlu)"),
                    FormFactory.DEFAULT_COLSPEC,
                    new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW),
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
                    FormFactory.RELATED_GAP_ROWSPEC,
                    new RowSpec(Sizes.dluY(50)),
                    FormFactory.RELATED_GAP_ROWSPEC,
                    new RowSpec("max(default;15dlu)"),
                    FormFactory.LINE_GAP_ROWSPEC,
                    FormFactory.DEFAULT_ROWSPEC,
                    FormFactory.UNRELATED_GAP_ROWSPEC
                }));

        //---- boardNameLabel ----
        boardNameLabel.setText("Board Name");
        add(boardNameLabel, cc.xy(2, 2));

        //---- boardNameTextField ----
        boardNameTextField.setText("New");
        add(boardNameTextField, cc.xy(4, 2));

        //---- okButton ----
        okButton.setText("OK");
        add(okButton, cc.xy(7, 2));

        //---- columnsLabel ----
        columnsLabel.setText("Columns");
        add(columnsLabel, cc.xy(2, 4));

        //---- columnsComboBox ----
        columnsComboBox.setModel(new DefaultComboBoxModel(new String[]{
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
        add(columnsComboBox, cc.xy(4, 4));

        //---- copyAllKeysFromExistingBoardCheckBox ----
        copyAllKeysFromExistingBoardCheckBox.setText("Copy All Keys From Other Board");
        copyAllKeysFromExistingBoardCheckBox.setVisible(false);
        add(copyAllKeysFromExistingBoardCheckBox, cc.xywh(6, 8, 2, 1));

        //---- cancelButton ----
        cancelButton.setText("Cancel");
        add(cancelButton, cc.xy(7, 4));

        //======== stylePanel ========
        {
            stylePanel.setBorder(LineBorder.createBlackLineBorder());
            stylePanel.setLayout(new FormLayout(
                    new ColumnSpec[]{
                        FormFactory.DEFAULT_COLSPEC,
                        FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                        FormFactory.DEFAULT_COLSPEC,
                        FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                        FormFactory.DEFAULT_COLSPEC,
                        FormFactory.UNRELATED_GAP_COLSPEC,
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
                        FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.LINE_GAP_ROWSPEC,
                        FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.LINE_GAP_ROWSPEC,
                        FormFactory.LINE_GAP_ROWSPEC
                    }));

            //---- boardBorderColorLabel ----
            boardBorderColorLabel.setText("Board Border Color:");
            stylePanel.add(boardBorderColorLabel, cc.xy(3, 3));

            //---- boardBorderColorButton ----
            boardBorderColorButton.setText("...");
            boardBorderColorButton.setFocusPainted(false);
            stylePanel.add(boardBorderColorButton, new CellConstraints(5, 3, 1, 1, CellConstraints.DEFAULT, CellConstraints.DEFAULT, new Insets(2, 0, 2, 4)));

            //---- exampleKeyTitleLabel ----
            exampleKeyTitleLabel.setText("Example Key");
            stylePanel.add(exampleKeyTitleLabel, cc.xy(7, 3));

            //---- resetExampleKeyButton ----
            resetExampleKeyButton.setText("<html><small>Reset</small></html>");
            resetExampleKeyButton.setFocusPainted(false);
            stylePanel.add(resetExampleKeyButton, new CellConstraints(9, 3, 1, 1, CellConstraints.DEFAULT, CellConstraints.DEFAULT, new Insets(2, 0, 2, 0)));

            //---- newKeysBackgroundLabel ----
            newKeysBackgroundLabel.setText("New Keys Background:");
            stylePanel.add(newKeysBackgroundLabel, cc.xy(3, 5));

            //---- newKeysBackgroundButton ----
            newKeysBackgroundButton.setText("...");
            newKeysBackgroundButton.setFocusPainted(false);
            stylePanel.add(newKeysBackgroundButton, new CellConstraints(5, 5, 1, 1, CellConstraints.DEFAULT, CellConstraints.DEFAULT, new Insets(2, 0, 2, 4)));

            //======== exampleKeyPanel ========
            {
                exampleKeyPanel.setLayout(new FormLayout(
                        "left:default:grow",
                        "top:default:grow"));

                //---- exampleKeyLabel ----
                exampleKeyLabel.setText(" Key");
                exampleKeyLabel.setOpaque(true);
                exampleKeyLabel.setBorder(new MatteBorder(2, 2, 2, 2, Color.red));
                exampleKeyLabel.setBackground(Color.blue);
                exampleKeyLabel.setHorizontalAlignment(SwingConstants.LEFT);
                exampleKeyLabel.setHorizontalTextPosition(SwingConstants.LEFT);
                exampleKeyLabel.setForeground(Color.white);
                exampleKeyPanel.add(exampleKeyLabel, new CellConstraints(1, 1, 1, 1, CellConstraints.DEFAULT, CellConstraints.DEFAULT, new Insets(0, 4, 0, 0)));
            }
            stylePanel.add(exampleKeyPanel, cc.xywh(7, 5, 7, 2));

            //---- newKeysTextColorLabel ----
            newKeysTextColorLabel.setText("New Keys Text Color:");
            stylePanel.add(newKeysTextColorLabel, cc.xy(3, 7));

            //---- newKeysTextColorButton ----
            newKeysTextColorButton.setText("...");
            newKeysTextColorButton.setFocusPainted(false);
            stylePanel.add(newKeysTextColorButton, new CellConstraints(5, 7, 1, 1, CellConstraints.DEFAULT, CellConstraints.DEFAULT, new Insets(2, 0, 2, 4)));

            //---- resetAllKeyColorsCheckBox ----
            resetAllKeyColorsCheckBox.setText("Reset All Keys To These Colors");
            resetAllKeyColorsCheckBox.setVisible(false);
            stylePanel.add(resetAllKeyColorsCheckBox, cc.xywh(7, 7, 5, 1));

            //---- autoSizeButtonsCheckBox ----
            autoSizeButtonsCheckBox.setText("Auto Size Keys");
            autoSizeButtonsCheckBox.setSelected(true);
            stylePanel.add(autoSizeButtonsCheckBox, cc.xy(3, 9));

            //---- buttonWidthLabel ----
            buttonWidthLabel.setText("Key Width");
            buttonWidthLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
            buttonWidthLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            buttonWidthLabel.setEnabled(false);
            stylePanel.add(buttonWidthLabel, cc.xywh(5, 9, 3, 1));

            //---- buttonWidthSpinner ----
            buttonWidthSpinner.setModel(new SpinnerNumberModel(50, 10, 500, 1));
            buttonWidthSpinner.setVisible(false);
            stylePanel.add(buttonWidthSpinner, cc.xy(9, 9));

            //---- buttonWidthInactiveTextField ----
            buttonWidthInactiveTextField.setEnabled(false);
            buttonWidthInactiveTextField.setBackground(Color.lightGray);
            stylePanel.add(buttonWidthInactiveTextField, cc.xy(9, 9));
        }
        add(stylePanel, cc.xywh(2, 6, 7, 1));

        //---- clearAllExistingKeysCheckBox ----
        clearAllExistingKeysCheckBox.setText("Clear All Keys");
        clearAllExistingKeysCheckBox.setVisible(false);
        add(clearAllExistingKeysCheckBox, cc.xywh(2, 8, 4, 1));

        //---- blankNewBoardRadioButton ----
        blankNewBoardRadioButton.setText("Blank New Board");
        blankNewBoardRadioButton.setSelected(true);
        add(blankNewBoardRadioButton, cc.xywh(2, 8, 3, 1));

        //---- copyKeysFromExistingBoardRadioButton ----
        copyKeysFromExistingBoardRadioButton.setText("Copy Keys From Existing Board");
        add(copyKeysFromExistingBoardRadioButton, cc.xywh(5, 8, 4, 1));

        //======== existingBoardsScrollPane ========
        {
            existingBoardsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            existingBoardsScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            existingBoardsScrollPane.setVisible(false);
        }
        add(existingBoardsScrollPane, cc.xywh(2, 10, 6, 1));

        //---- preserveKeyColorsFromExistingBoardCheckBox ----
        preserveKeyColorsFromExistingBoardCheckBox.setText("Preserve Key Colors From The Other Board");
        preserveKeyColorsFromExistingBoardCheckBox.setVisible(false);
        add(preserveKeyColorsFromExistingBoardCheckBox, cc.xywh(2, 12, 6, 1));

        //---- radioButtonGroup1 ----
        ButtonGroup radioButtonGroup1 = new ButtonGroup();
        radioButtonGroup1.add(blankNewBoardRadioButton);
        radioButtonGroup1.add(copyKeysFromExistingBoardRadioButton);
        // JFormDesigner - End of component initialization//GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY//GEN-BEGIN:variables
    private JLabel boardNameLabel;
    private JTextField boardNameTextField;
    private JButton okButton;
    private JLabel columnsLabel;
    private JComboBox columnsComboBox;
    private JCheckBox copyAllKeysFromExistingBoardCheckBox;
    private JButton cancelButton;
    private JPanel stylePanel;
    private JLabel boardBorderColorLabel;
    private JButton boardBorderColorButton;
    private JLabel exampleKeyTitleLabel;
    private JButton resetExampleKeyButton;
    private JLabel newKeysBackgroundLabel;
    private JButton newKeysBackgroundButton;
    private JPanel exampleKeyPanel;
    private JLabel exampleKeyLabel;
    private JLabel newKeysTextColorLabel;
    private JButton newKeysTextColorButton;
    private JCheckBox resetAllKeyColorsCheckBox;
    private JCheckBox autoSizeButtonsCheckBox;
    private JLabel buttonWidthLabel;
    private JSpinner buttonWidthSpinner;
    private JTextField buttonWidthInactiveTextField;
    private JCheckBox clearAllExistingKeysCheckBox;
    private JRadioButton blankNewBoardRadioButton;
    private JRadioButton copyKeysFromExistingBoardRadioButton;
    private JScrollPane existingBoardsScrollPane;
    private JCheckBox preserveKeyColorsFromExistingBoardCheckBox;
    // JFormDesigner - End of variables declaration//GEN-END:variables

    /**
     * This will create and display an instance of this panel
     */
    static public void go(PanelType panelType, Board optionalEditBoard) {
        if ((panelType == PanelType.EDIT_BOARD) && (optionalEditBoard == null)) {
            Frames.message("Error in BoardPanel.go(), null board passed in for editing.");
            return;
        }

        MouseWatcher.stopHoverCheck();

        JDialog dialog = new JDialog();
        dialog.setTitle("New Board");
        dialog.setModal(true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setUndecorated(false);
        dialog.setAlwaysOnTop(true);
        dialog.setResizable(true);
        BoardNewEditPanel panel = new BoardNewEditPanel(
                dialog, panelType, optionalEditBoard);
        dialog.addWindowListener(panel);
        dialog.add(panel);
        dialog.pack();
        Frames.centerWindow(dialog, 50);
        dialog.setVisible(true);

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

        // Auto Size Buttons Checkbox
        autoSizeButtonsCheckBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                boolean selected = (e.getStateChange() == ItemEvent.SELECTED);
                reactToAutoSize(selected);
            }
        });

        // Button width spinner change
        buttonWidthSpinner.addChangeListener(
                new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                setExampleKeyWidth(spinnerModel.getNumber().intValue());
            }
        });

        // Existing board radio button
        copyKeysFromExistingBoardRadioButton.addItemListener(
                new ItemListener() {

            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    existingBoardsScrollPane.setVisible(true);
                    preserveKeyColorsFromExistingBoardCheckBox.setVisible(true);
                    parentWindow.pack();
                } else {
                    existingBoardsScrollPane.setVisible(false);
                    preserveKeyColorsFromExistingBoardCheckBox.setVisible(false);
                    parentWindow.pack();
                }
            }
        });

        // Board border color Button
        boardBorderColorButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Color chosenColor = JColorChooser.showDialog(
                        parentWindow,
                        "Choose board border color",
                        exampleKeyBorder.getMatteColor());
                if (chosenColor != null) {
                    exampleKeyBorder = BorderFactory.createMatteBorder(
                            2, 2, 2, 2, chosenColor);
                    exampleKeyLabel.setBorder(exampleKeyBorder);
                }
                setResetColorsButtonState();
            }
        });

        // New keys background color Button
        newKeysBackgroundButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Color chosenColor = JColorChooser.showDialog(
                        parentWindow,
                        "Choose new keys background color",
                        exampleKeyLabel.getBackground());
                if (chosenColor != null) {
                    exampleKeyLabel.setBackground(chosenColor);
                }
                setResetColorsButtonState();
            }
        });

        // New keys text color Button
        newKeysTextColorButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Color chosenColor = JColorChooser.showDialog(
                        parentWindow,
                        "Choose new keys text color",
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
                    resetExampleKeySavedBorder = exampleKeyBorder.getMatteColor();
                    resetExampleKeySavedBackground = exampleKeyLabel.getBackground();
                    resetExampleKeySavedForeground = exampleKeyLabel.getForeground();
                    exampleKeyBorder = BorderFactory.createMatteBorder(
                            2, 2, 2, 2, Constants.defaultBoardBorderColor);
                    exampleKeyLabel.setBorder(exampleKeyBorder);
                    exampleKeyLabel.setBackground(Constants.defaultKeyBackgroundColor);
                    exampleKeyLabel.setForeground(Constants.defaultKeyTextColor);
                    resetExampleKeyIsInUndoState = true;
                } else {
                    exampleKeyBorder = BorderFactory.createMatteBorder(
                            2, 2, 2, 2, resetExampleKeySavedBorder);
                    exampleKeyLabel.setBorder(exampleKeyBorder);
                    exampleKeyLabel.setBackground(resetExampleKeySavedBackground);
                    exampleKeyLabel.setForeground(resetExampleKeySavedForeground);
                    resetExampleKeySavedBorder = null;
                    resetExampleKeySavedBackground = null;
                    resetExampleKeySavedForeground = null;
                    resetExampleKeyIsInUndoState = false;
                }
                setResetColorsButtonState();
            }
        });

        // clearAllExistingKeysCheckBox
        clearAllExistingKeysCheckBoxListener = new ItemListener() {

            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    Option sure = Frames.showOptions(
                            "This checkbox will DELETE and clear all keys "
                            + "on your board.\n\nAre you sure you want to do this?",
                            "Warning", Option.No, JOptionPane.WARNING_MESSAGE,
                            Option.No, Option.Yes, Option.Cancel);
                    if (sure != Option.Yes) {
                        clearAllExistingKeysCheckBox.setSelected(false);
                        return;
                    }
                } else {
                    copyAllKeysFromExistingBoardCheckBox.setSelected(false);
                }
            }
        };
        clearAllExistingKeysCheckBox.addItemListener(
                clearAllExistingKeysCheckBoxListener);

        // copyAllKeysFromExistingBoardCheckBox
        copyAllKeysFromExistingBoardCheckBox.addItemListener(
                new ItemListener() {

            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    Option sure = Frames.showOptions(
                            "This checkbox will DELETE all keys "
                            + "on your board. \n\n"
                            + "Then the keys from the selected other board "
                            + "will be copied on to this board."
                            + "\nAre you sure you want to do this?",
                            "Warning", Option.No, JOptionPane.WARNING_MESSAGE,
                            Option.No, Option.Yes, Option.Cancel);

                    if (sure != Option.Yes) {
                        copyAllKeysFromExistingBoardCheckBox.setSelected(false);
                        return;
                    } else {
                        setClearKeysCheckboxWithoutEvent(true);
                        existingBoardsScrollPane.setVisible(true);
                        preserveKeyColorsFromExistingBoardCheckBox.setVisible(true);
                        parentWindow.pack();
                    }
                } else {
                    setClearKeysCheckboxWithoutEvent(false);
                    existingBoardsScrollPane.setVisible(false);
                    preserveKeyColorsFromExistingBoardCheckBox.setVisible(false);
                    parentWindow.pack();
                }
            }
        });

        // resetAllKeyColorsCheckBox
        resetAllKeyColorsCheckBox.addItemListener(
                new ItemListener() {

            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    preserveKeyColorsFromExistingBoardCheckBox.setSelected(
                            false);
                }
            }
        });

        // preserveKeyColorsFromExistingBoardCheckBox
        preserveKeyColorsFromExistingBoardCheckBox.addItemListener(
                new ItemListener() {

            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    resetAllKeyColorsCheckBox.setSelected(false);
                }
            }
        });

    }

    public void reactToAutoSize(boolean selected) {
        buttonWidthLabel.setEnabled(!selected);
        buttonWidthSpinner.setVisible(!selected);
        buttonWidthInactiveTextField.setVisible(selected);
        if (selected) {
            setExampleKeyWidth(null);
        } else {
            setExampleKeyWidth(spinnerModel.getNumber().intValue());
        }
    }

    public void setResetColorsButtonState() {
        // Find out if the colors are at their defaults or not.
        boolean colorsAtDefaults = false;
        if (exampleKeyBorder.getMatteColor().equals(
                Constants.defaultBoardBorderColor)
                && exampleKeyLabel.getBackground().equals(
                        Constants.defaultKeyBackgroundColor)
                && exampleKeyLabel.getForeground().equals(
                        Constants.defaultKeyTextColor)) {
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

    private void setClearKeysCheckboxWithoutEvent(boolean selected) {
        clearAllExistingKeysCheckBox.removeItemListener(
                clearAllExistingKeysCheckBoxListener);
        clearAllExistingKeysCheckBox.setSelected(selected);
        clearAllExistingKeysCheckBox.addItemListener(
                clearAllExistingKeysCheckBoxListener);
    }

    private void okButtonAction() {
        if (panelType == PanelType.NEW_BOARD) {
            okButtonNewBoard();
        }
        if (panelType == PanelType.EDIT_BOARD) {
            okButtonEditBoard();
        }
    }

    private void okButtonNewBoard() {
        String name = boardNameTextField.getText();
        if ((name == null) || (name.equals(""))) {
            Frames.message("Please type a board name.");
            return;
        }
        name = Use.safeSubstring(0, Constants.maxBoardNameSize, name);

        int columns = columnsComboBox.getSelectedIndex() + 1;

        String fileName = Board.getFreeFileName();
        if (fileName == null) {
            return;
        }

        Board board = new Board(name, fileName, columns);

        board.borderColor = exampleKeyBorder.getMatteColor();
        board.newKeysBackgroundColor = exampleKeyLabel.getBackground();
        board.newKeysTextColor = exampleKeyLabel.getForeground();

        Board.KeysIterator it = new Board.KeysIterator(board.keys);
        while (it.hasNext()) {
            board.keys[it.x][it.y].setColors(
                    board.newKeysBackgroundColor, board.newKeysTextColor);
        }

        boolean autoSizeChecked = autoSizeButtonsCheckBox.isSelected();
        int buttonWidthSpinnerValue = spinnerModel.getNumber().intValue();
        board.autoSize = autoSizeChecked;
        board.fixedKeyWidth = buttonWidthSpinnerValue;

        boolean copyKeys = copyKeysFromExistingBoardRadioButton.isSelected();
        boolean preserveCopyColors
                = preserveKeyColorsFromExistingBoardCheckBox.isSelected();
        Board doneBoard;
        if (copyKeys) {
            int fileBoardsIndex = fileBoardsList.getSelectedIndex();
            if (fileBoardsIndex < 0) {
                Frames.message("Please select a board to copy, or turn off the\n"
                        + "\"copy keys from other board\" option.");
                return;
            }
            String sourceFileName = fileBoardsList.getFileName(fileBoardsIndex);
            Board sourceBoard = Board.load(
                    true, sourceFileName, sourceFileName);
            if (sourceBoard == null) {
                Frames.message("Could not read from selected source board for copy operation.");
                return;
            }
            doneBoard = Board.copyBoardKeys(sourceBoard, board,
                    preserveCopyColors);
        } else {
            doneBoard = board;
        }
        Main.getBoardManager().addBoard(doneBoard, true);
        doneBoard.save();
        parentWindow.dispose();
    }

    private void okButtonEditBoard() {
        // Perform all validity checks first.
        String name = boardNameTextField.getText();
        int columns = columnsComboBox.getSelectedIndex() + 1;
        boolean autoSizeChecked = autoSizeButtonsCheckBox.isSelected();
        boolean resetColors = resetAllKeyColorsCheckBox.isSelected();
        int buttonWidthSpinnerValue = spinnerModel.getNumber().intValue();
        boolean clearKeys = clearAllExistingKeysCheckBox.isSelected();
        boolean copyKeys = copyAllKeysFromExistingBoardCheckBox.isSelected();
        boolean preserveCopyColors
                = preserveKeyColorsFromExistingBoardCheckBox.isSelected();

        if ((name == null) || (name.equals(""))) {
            Frames.message("Please type a board name.");
            return;
        }
        name = Use.safeSubstring(0, Constants.maxBoardNameSize, name);

        if (columns < editBoard.columns()) {
            Option sure = Frames.showOptions(
                    "You have chosen to reduce the number of columns in this board\n"
                    + "from " + editBoard.columns() + " to " + columns + ".\n\n"
                    + "Keys in the removed columns will be DELETED. This cannot be undone.\n"
                    + "Are you sure you want to do this?",
                    "Warning", Option.No, JOptionPane.WARNING_MESSAGE,
                    Option.No, Option.Yes, Option.Cancel);
            if (sure != Option.Yes) {
                int editOriginalColumns = Use.clampInt(editBoard.columns(), 1, 10);
                Frames.message("OK, Changing columns back to " + editOriginalColumns
                        + ", instead of " + columns + ".");
                columnsComboBox.setSelectedIndex(editOriginalColumns - 1);
                return;
            }
        }

        Board sourceBoard = null;
        if (copyKeys) {
            int fileBoardsIndex = fileBoardsList.getSelectedIndex();
            if (fileBoardsIndex < 0) {
                Frames.message("Please select a board to copy, or turn off the\n"
                        + "\"copy keys from other board\" option.");
                return;
            }
            String sourceFileName = fileBoardsList.getFileName(fileBoardsIndex);
            sourceBoard = Board.load(
                    true, sourceFileName, sourceFileName);
            if (sourceBoard == null) {
                Frames.message("Could not read from selected source board for copy operation.");
                return;
            }
        }

        // Copy attributes to editBoard second.
        editBoard.setName(name);
        editBoard.borderColor = exampleKeyBorder.getMatteColor();
        editBoard.newKeysBackgroundColor = exampleKeyLabel.getBackground();
        editBoard.newKeysTextColor = exampleKeyLabel.getForeground();
        editBoard.setColumns(columns);
        editBoard.autoSize = autoSizeChecked;
        editBoard.fixedKeyWidth = buttonWidthSpinnerValue;
        if (resetColors) {
            editBoard.resetAllKeyColors();
        }
        if (clearKeys) {
            editBoard.clearAllKeys();
        }
        if (copyKeys) {
            editBoard = Board.copyBoardKeys(sourceBoard, editBoard,
                    preserveCopyColors);
        }

        editBoard.save();
        parentWindow.dispose();
        Main.getBoardManager().arrangeAll();
    }

    public void windowGainedFocus(WindowEvent e) {
        Frames.setMessageParent(parentWindow);
    }

    public void windowLostFocus(WindowEvent e) {
    }
}
