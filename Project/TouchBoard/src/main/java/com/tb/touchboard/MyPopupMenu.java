package com.tb.touchboard;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import com.tb.tbUtilities.*;

public class MyPopupMenu implements PopupMenuListener {

    // This is our menu.
    private JPopupMenu popup;
    // Top Level.
    private JMenu board = new JMenu("Board");
    private JMenu clips = new JMenu("Clips");
    private JMenu options = new JMenu("Options");
    private JMenuItem save = new JMenuItem("Save Boards");
    private JMenuItem exit = new JMenuItem("Exit");
    private JMenuItem help = new JMenuItem("Help & Intro");
    private JMenuItem about = new JMenuItem("About");
    // Board level.
    private JMenuItem boardNew = new JMenuItem("New");
    private JMenuItem boardOpen = new JMenuItem("Open");
    private JMenuItem boardEdit = new JMenuItem("Edit");
    private JMenuItem boardReorder = new JMenuItem("Reorder");
    private JMenuItem boardClose = new JMenuItem("Close");
    private JMenuItem boardCloseAll = new JMenuItem("Close All");
    private JMenuItem boardDefaultBoards = new JMenuItem("Set Default Boards");
    // Clips level.
    private JMenuItem clipsOpenClose = new JMenuItem("Menu text not set");
    private JMenuItem clipsClear = new JMenuItem("Clear and Reset clips board");
    private JMenuItem clipsSortByUsage = new JMenuItem("Sort by usage frequency");
    // Options level.
    private JMenu typingSpeed = new JMenu("Default Typing Speed");
    // Typing speed.
    private ButtonGroup typingSpeedGroup = new ButtonGroup();
    private JRadioButtonMenuItem fullSpeed = new JRadioButtonMenuItem("Full Speed");
    private JRadioButtonMenuItem slow = new JRadioButtonMenuItem("Slow               (30 cps)");
    private JRadioButtonMenuItem slower = new JRadioButtonMenuItem("Slower            (15 cps)");
    private JRadioButtonMenuItem slowest = new JRadioButtonMenuItem("Slowest           (5 cps)");

    /**
     * Constructor
     */
    public MyPopupMenu() {
        buildMenu();

        // Add the actions
        addActions();
    }

    /**
     * This will build the menu, from scratch.
     */
    public final void buildMenu() {

        // Clear the old menu elements.
        popup = new JPopupMenu();
        board = new JMenu("Board");
        clips = new JMenu("Clips");
        options = new JMenu("Options");
        typingSpeed = new JMenu("Default Typing Speed");

        // Build top level.
        popup.addPopupMenuListener(this);
        popup.add(board);
        popup.add(clips);
        popup.add(options);
        popup.add(save);
        popup.add(exit);
        popup.addSeparator();
        popup.add(help);
        popup.add(about);

        // Build the board menu.
        board.add(boardNew);
        board.add(boardOpen);
        board.add(boardEdit);
        board.add(boardReorder);
        board.add(boardClose);
        board.add(boardCloseAll);
        board.add(boardDefaultBoards);

        // Build the clips menu.
        clips.add(clipsOpenClose);
        clips.add(clipsClear);
        clips.add(clipsSortByUsage);
        setOpenClipsMenuItems();

        // Build the options menu.
        options.add(typingSpeed);

        // Build the Typing Speed menu.
        typingSpeed.add(fullSpeed);
        typingSpeedGroup.add(fullSpeed);
        typingSpeed.add(slow);
        typingSpeedGroup.add(slow);
        typingSpeed.add(slower);
        typingSpeedGroup.add(slower);
        typingSpeed.add(slowest);
        typingSpeedGroup.add(slowest);
        // Normal values: 0 33 67 200
        int speed = Options.getDefaultPauseBetweenKeystrokes();
        if (speed > 100) {
            slowest.setSelected(true);
        } else if (speed > 50) {
            slower.setSelected(true);
        } else if (speed > 15) {
            slow.setSelected(true);
        } else {
            fullSpeed.setSelected(true);
        }
    }

    public void show(Component invoker) {
        popup.show(invoker, 0, invoker.getHeight() + 2);
    }

    public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
        MouseWatcher.stopHoverCheck();
    }

    public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
        MouseWatcher.startHoverCheck();
    }

    public void popupMenuCanceled(PopupMenuEvent e) {
        MouseWatcher.startHoverCheck();
    }

    public JPopupMenu getPopup() {
        return popup;
    }

    public void setOpenClipsMenuItems() {
        if (Main.getBoardManager().isClipsOpen()) {
            clipsOpenClose.setText("Close the clips board");
            clipsClear.setEnabled(true);
            clipsSortByUsage.setEnabled(true);
        } else {
            clipsOpenClose.setText("Open the clips board");
            clipsClear.setEnabled(false);
            clipsSortByUsage.setEnabled(false);
        }
    }

    private void addActions() {

        // Board > New
        boardNew.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                BoardNewEditPanel.go(BoardNewEditPanel.PanelType.NEW_BOARD, null);
            }
        });

        // Board > Open
        boardOpen.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                String name = BoardListPanel.goFileBoards(
                        "Open Board", "Open", true);
                if (name == null) {
                    return;
                }
                boolean success = Main.getBoardManager().openBoard(
                        true, name, name);
                if (!success) {
                    Frames.message("Could not open board: \n" + name);
                }
            }
        });

        // Board > Edit
        boardEdit.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (Main.getBoardManager().boardsOpenIncludingClips() == 0) {
                    Frames.message("There are no boards open to edit.");
                    return;
                }
                Integer index = BoardListPanel.goOpenBoards("Edit Board", "Edit", true);
                if (index == null) {
                    return;
                }
                BoardNewEditPanel.go(BoardNewEditPanel.PanelType.EDIT_BOARD,
                        Main.getBoardManager().getBoard(index));
            }
        });

        // Board > Reorder
        boardReorder.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (Main.getBoardManager().boardsOpenBesidesClips() < 2) {
                    Frames.message("Not enough boards are open to reorder them.");
                    return;
                }
                BoardListPanel.goOpenBoardsReorder();
            }
        });

        // Board > Close
        boardClose.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (Main.getBoardManager().boardsOpenBesidesClips() == 0) {
                    Frames.message("No boards are open to close.");
                    return;
                }
                Integer index = BoardListPanel.goOpenBoards(
                        "Close Board", "Close", false);
                if (index == null) {
                    return;
                }
                Main.getBoardManager().removeBoard(index);
            }
        });

        // Board > CloseAll
        boardCloseAll.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (Main.getBoardManager().boardsOpenBesidesClips() == 0) {
                    Frames.message("No boards are open to close.");
                    return;
                }
                Option sure = Frames.showOptions("Close all boards?",
                        "", Option.No, JOptionPane.QUESTION_MESSAGE,
                        Option.Yes, Option.No, null);
                if (sure != Option.Yes) {
                    return;
                }
                Main.getBoardManager().closeAllBoards();
            }
        });

        // Board > DefaultBoards
        boardDefaultBoards.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                Option sure = Frames.showOptions(
                        "Set the current boards as the defaults?\n\n"
                        + "The default boards are opened every time the program starts.",
                        "", Option.No, JOptionPane.QUESTION_MESSAGE,
                        Option.Yes, Option.No, null);
                if (sure != Option.Yes) {
                    return;
                }
                Main.getBoardManager().setCurrentBoardsAsDefaults();
            }
        });

        // Clips > Open Close
        clipsOpenClose.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (Main.getBoardManager().isClipsOpen()) {
                    Main.getBoardManager().closeClips();
                } else {
                    boolean backedOut = warnExperimentalAndLetUserBackOut();
                    if (backedOut) {
                        return;
                    } else {
                        Main.getBoardManager().openClips();
                    }
                }

            }

            private boolean warnExperimentalAndLetUserBackOut() {
                boolean backedOut = true;

                // Check warning bypass.
                boolean showWarning = Options.getShowClipOpenWarning();
                if (showWarning == false) {
                    backedOut = false;
                    return backedOut;
                }

                // Show Warning.
                String no = "No";
                String yes = "Yes";
                String yesDontShow = "<html>Yes and<br>Don't Show<br>This Again</html>";
                String cancel = "Cancel";
                String continueClips = Frames.showOptions(
                        "The clips board is an experimental feature. It does not currently\n"
                        + "work on all operating systems. Specifically, the \"add clip\" key\n"
                        + "does not add a new clip to the board on some computers.\n\n"
                        + "There is a good chance that the clips board will work fine for you.\n"
                        + "It was decided to keep this feature available in its experimental state,\n"
                        + "since a lot of people could benefit from it.\n\n"
                        + "Be sure to read the help file for instructions on how to use the \"add clip\" key.\n\n"
                        + "Do you still wish to open the clips board?",
                        "Experimental Feature", no, JOptionPane.QUESTION_MESSAGE,
                        no, yes, yesDontShow, cancel);
                if (continueClips.equals(yes) || continueClips.equals(yesDontShow)) {
                    backedOut = false;
                }
                if (continueClips.equals(yesDontShow)) {
                    Options.setShowClipOpenWarning(false);
                }
                return backedOut;
            }
        });

        // Clips > Clear
        clipsClear.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                Option sure = Frames.showOptions(
                        "Clear and Reset the clips board?\n\n"
                        + "All current clips will be DELETED.",
                        "", Option.No, JOptionPane.QUESTION_MESSAGE,
                        Option.Yes, Option.No, null);
                if (sure != Option.Yes) {
                    return;
                }
                Main.getBoardManager().makeOrClearClips();
            }
        });

        // Clips > Sort By Usage
        clipsSortByUsage.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                Main.getBoardManager().sortClipsByUsage();
            }
        });

        // Options > Speed > Full Speed
        fullSpeed.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                int pause = 0;
                Options.setDefaultPauseBetweenKeystrokes(pause);
                Main.getCommandCenter().defaultPauseBetweenKeystrokes = pause;
            }
        });

        // Options > Speed > Slow, 30 kps
        slow.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                int pause = 33; // 30 kps
                Options.setDefaultPauseBetweenKeystrokes(pause);
                Main.getCommandCenter().defaultPauseBetweenKeystrokes = pause;
            }
        });

        // Options > Speed > Slower, 15 kps
        slower.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                int pause = 67; // 15 kps
                Options.setDefaultPauseBetweenKeystrokes(pause);
                Main.getCommandCenter().defaultPauseBetweenKeystrokes = pause;
            }
        });

        // Options > Speed > Slowest, 5 kps
        slowest.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                int pause = 200; // 5 kps
                Options.setDefaultPauseBetweenKeystrokes(pause);
                Main.getCommandCenter().defaultPauseBetweenKeystrokes = pause;
            }
        });

        // Save
        save.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                Main.getBoardManager().saveAllBoards();
                Frames.message("The boards have been saved.\n\n"
                        + "You can also set the default boards if desired, using the "
                        + "\"Board\" menu\n ");
            }
        });

        // Exit
        exit.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                MainPanel.exitProgram();
            }
        });

        // Help
        help.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                BrowserLaunch.openURL(Constants.helpURL);
            }
        });

        // About
        about.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

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
        });

    }
}
