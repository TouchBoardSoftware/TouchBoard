package com.tb.touchboard;

import java.awt.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import com.jgoodies.forms.layout.*;
import com.tb.tbUtilities.Frames;
import com.tb.tbUtilities.Use;

public class Board extends JPanel implements
    MouseWatcher.HoverListener {

    /**
     * Iterator used for looping through a set of board keys.
     */
    public static class KeysIterator {

        private int rows;
        private int columns;
        private int private_x = -1;
        private int private_y = -1;
        /**
         * Current value of x after call to hasNext. Read only.
         */
        public int x = -1;
        /**
         * Current value of y after call to hasNext. Read only.
         */
        public int y = -1;

        /**
         * Constructor, pass in the keys array.
         */
        public KeysIterator(Key keys[][]) {
            if (keys == null || keys.length == 0 || keys[0].length == 0) {
                throw new RuntimeException("Invalid key array passed in to KeysIterator.");
            }
            rows = Math.min(Constants.keyRows, keys[0].length);
            columns = keys.length;
        }

        /**
         * Call this once every time you wish to increment the loop. Returns true if there is a next
         * item, false if not. Suggested use: while(it.hasNext} do stuff.
         */
        boolean hasNext() {
            // Catch the first call;
            if (private_x == -1 && private_y == -1) {
                private_x = 0;
                private_y = 0;
                copyToPublic(private_x, private_y);
                return true;
            }

            // increment x
            ++private_x;
            // bounds check x
            if (private_x >= columns) {
                // increment y, reset x.
                ++private_y;
                private_x = 0;
                // bounds check y
                if (private_y >= rows) {
                    // we have reached the end.
                    copyToPublic(private_x, private_y);
                    return false;
                }
            }
            // return success.
            copyToPublic(private_x, private_y);
            return true;
        }

        private void copyToPublic(int private_x, int private_y) {
            x = private_x;
            y = private_y;
        }
    }

    // Board keys.
    public Key[][] keys;

    // Board name is stored in:
    // quickShowLabel.getText() or name()
    // File name with extension, this does -not- include a directory.
    public String fileName;

    // Number of columns is:
    // keys.length, or columns().
    public boolean autoSize;
    private int autoKeyWidth = Constants.minimumKeyWidth;
    public int fixedKeyWidth;
    public Color borderColor;
    public Color newKeysBackgroundColor;
    public Color newKeysTextColor;

    // Board current show state.
    public boolean quickShow = false;
    public boolean lockedOpen = false;

    // Labels for quickshow and lock state.
    public JLabel quickShowLabel = new JLabel();
    public JLabel lockStateLabel = new JLabel();

    // Strings for saving and loading board files.
    private static class Names {

        static String boardName = "boardName";
        static String columns = "columns";
        static String autoSize = "autoSize";
        static String fixedKeyWidth = "fixedKeyWidth";
        static String newKeysBackgroundColor = "newKeysBackgroundColor";
        static String newKeysTextColor = "newKeysTextColor";
        static String boardBorderColor = "boardBorderColor";
        static String title = "title";
        static String background = "background";
        static String foreground = "foreground";
        static String columnsWide = "columnsWide";
        static String contents = "contents";
        static String uses = "uses";
    }

    /**
     * Little Constructor
     */
    public Board(String name, String aFileName, int columns) {
        this(name, aFileName, columns,
            true, 75, Constants.defaultBoardBorderColor,
            Constants.defaultKeyBackgroundColor,
            Constants.defaultKeyTextColor);
    }

    /**
     * Big Constructor
     */
    public Board(String name, String aFileName, int columns,
        boolean aAutoSize, int aKeysWidth, Color aBorderColor,
        Color aNewKeysBackgroundColor, Color aNewKeysTextColor) {
        quickShowLabel.setText(name);
        fileName = aFileName;
        autoSize = aAutoSize;
        fixedKeyWidth = aKeysWidth;
        borderColor = aBorderColor;
        newKeysBackgroundColor = aNewKeysBackgroundColor;
        newKeysTextColor = aNewKeysTextColor;

        // Range clamp columns.
        columns = Use.clampInt(columns, 1, Constants.maxColumns);

        // Initialize keys.
        keys = new Key[columns][Constants.keyRows];
        KeysIterator it = new KeysIterator(keys);
        while (it.hasNext()) {
            keys[it.x][it.y] = new Key(this);
        }

    }

    public void arrangeBoard(boolean isDisplayed) {

        // Remove all components from the panel.
        removeAll();

        // Set panel and board border color.
        setBackground(borderColor);

        // Initialize lock state icon.
        lockStateLabel.setOpaque(true);

        // Initialize quick show icon.
        quickShowLabel.setBackground(Constants.defaultQuickShowBackgroundColor);
        quickShowLabel.setForeground(Constants.defaultKeyTextColor);
        quickShowLabel.setOpaque(true);
        quickShowLabel.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 0));

        // No need for more unless we are displayed.
        if (!isDisplayed) {
            return;
        }

        // Calculate automatic key width if needed.
        if (autoSize) {
            calculateAutoKeyWidth();
        }

        // Build column specifications.
        ArrayList<ColumnSpec> columnsSpecs = new ArrayList<ColumnSpec>();
        for (int i = 0; i < ((columns() * 2) + 1); ++i) {
            // If even,
            if (i % 2 == 0) {
                // We are on a border.
                columnsSpecs.add(new ColumnSpec(ColumnSpec.FILL,
                    Sizes.pixel(Constants.borderThickness), ColumnSpec.NO_GROW));
            } else {
                // We are on a button.
                columnsSpecs.add(new ColumnSpec(ColumnSpec.FILL,
                    Sizes.pixel(getKeyWidth()), ColumnSpec.NO_GROW));
            }
        }

        // Build row specifications.
        ArrayList<RowSpec> rowsSpecs = new ArrayList<RowSpec>();
        for (int i = 0; i < ((Constants.keyRows * 2) + 1); ++i) {
            // If even,
            if (i % 2 == 0) {
                // We are on a border.
                rowsSpecs.add(new RowSpec(RowSpec.FILL,
                    Sizes.pixel(Constants.borderThickness), RowSpec.NO_GROW));
            } else {
                // We are on a button.
                rowsSpecs.add(new RowSpec(RowSpec.FILL,
                    Sizes.pixel(Constants.keyHeight), RowSpec.NO_GROW));
            }
        }

        // Set the layout manager.
        setLayout(new FormLayout(columnsSpecs.toArray(new ColumnSpec[0]), rowsSpecs.toArray(new RowSpec[0])));

        // Create and place keys.
        // Looping is left to right, then top to bottom.
        CellConstraints cc = new CellConstraints();
        KeysIterator it = new KeysIterator(keys);
        int skipKeys = 0;
        while (it.hasNext()) {
            if (skipKeys > 0) {
                // Do not place a key, and decrement skipKeys.
                --skipKeys;
            } else {
                // Place this key.
                // Calculate limitedColumnWidth from the keys columnsWide value,
                // and the number of columns in the board.
                int columnsAvailableForThisKey = columns() - it.x;
                int limitedColumnWidth = Use.clampInt(
                    keys[it.x][it.y].columnsWide, 1, columnsAvailableForThisKey);

                // Make sure the appropriate number of future keys are skipped.
                skipKeys = limitedColumnWidth - 1;

                // Give the Key the correct font.
                if (keys[it.x][it.y].hasSmallTitle()) {
                    keys[it.x][it.y].setFont(Constants.biggerLabelFont);
                } else {
                    keys[it.x][it.y].setFont(Constants.defaultLabelFont);
                }

                // Place the key.
                int layoutCellsWide = (limitedColumnWidth * 2) - 1;
                add(keys[it.x][it.y], cc.xywh(((it.x * 2) + 2), ((it.y * 2) + 2),
                    layoutCellsWide, 1));
            }
        }
    }

    public boolean isDisplayed() {
        return quickShow || lockedOpen;
    }

    public int getWidth() {
        return Constants.borderThickness
            + (columns() * (getKeyWidth() + Constants.borderThickness));
    }

    public int getKeyWidth() {
        return autoSize ? autoKeyWidth : fixedKeyWidth;
    }

    @Override
    public int getHeight() {
        return Constants.getBoardHeight();
    }

    // Save the board to its file.
    public void save() {

        // Make properties instance.
        Properties properties = new Properties();

        // Write board information.
        properties.setProperty(Names.boardName,
            quickShowLabel.getText());
        properties.setProperty(Names.columns,
            ((Integer) columns()).toString());
        properties.setProperty(Names.autoSize,
            ((Boolean) autoSize).toString());
        properties.setProperty(Names.fixedKeyWidth,
            ((Integer) fixedKeyWidth).toString());
        properties.setProperty(Names.newKeysBackgroundColor,
            Use.colorToString(newKeysBackgroundColor));
        properties.setProperty(Names.newKeysTextColor,
            Use.colorToString(newKeysTextColor));
        properties.setProperty(Names.boardBorderColor,
            Use.colorToString(borderColor));

        // Write keys information.
        KeysIterator it = new KeysIterator(keys);
        while (it.hasNext()) {
            String keyPrefix = ((Integer) it.x).toString() + ","
                + ((Integer) it.y).toString() + ".";

            properties.setProperty(keyPrefix + Names.title,
                keys[it.x][it.y].getText());
            properties.setProperty(keyPrefix + Names.background,
                Use.colorToString(keys[it.x][it.y].getBackgroundColor()));
            properties.setProperty(keyPrefix + Names.foreground,
                Use.colorToString(keys[it.x][it.y].getTextColor()));
            properties.setProperty(keyPrefix + Names.columnsWide,
                ((Integer) keys[it.x][it.y].columnsWide).toString());
            properties.setProperty(keyPrefix + Names.uses,
                ((Integer) keys[it.x][it.y].uses).toString());
            properties.setProperty(keyPrefix + Names.contents,
                keys[it.x][it.y].contents);
        }

        try {
            String fullpath = Use.workingDirectory + "/" + fileName;
            FileOutputStream outStream = new FileOutputStream(fullpath);
            properties.store(outStream, "Board File");
            outStream.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // Load the board from a file.
    // Return board for success, otherwise null.
    public static Board load(
        boolean prependCWD, String restOfPath, String fileName) {

        // Load properties from file.
        Properties properties = Use.loadPropertiesFile(prependCWD, restOfPath);
        if (properties == null) {
            return null;
        }

        // Get board name.
        String boardName = properties.getProperty(Names.boardName);
        if (boardName == null) {
            return null;
        }

        // Get columns.
        Integer columnsInteger = Use.getIntegerFromString(
            false, properties.getProperty(Names.columns));
        if (columnsInteger == null) {
            return null;
        }
        columnsInteger = Use.clampInt(columnsInteger, 1, Constants.maxColumns);

        // Make board instance
        Board board = new Board(boardName, fileName, columnsInteger);

        // Get autoSize.
        boolean autoSizeFalse = properties.getProperty(Names.autoSize).
            toLowerCase().contains("false");
        board.autoSize = Use.toggleBoolean(autoSizeFalse);

        // Get fixedKeyWidth
        Integer keyWidthInteger = Use.getIntegerFromString(
            false, properties.getProperty(Names.fixedKeyWidth));
        if (keyWidthInteger == null) {
            keyWidthInteger = Constants.defaultKeyWidth;
        }
        board.fixedKeyWidth = Use.clampInt(keyWidthInteger,
            Constants.minimumKeyWidth, Constants.maximumKeyWidth);

        // Get newKeysBackgroundColor.
        Color tempcolor;
        tempcolor = Use.colorFromString(properties.getProperty(
            Names.newKeysBackgroundColor));
        if (tempcolor == null) {
            board.newKeysBackgroundColor = Constants.defaultKeyBackgroundColor;
        } else {
            board.newKeysBackgroundColor = tempcolor;
        }

        // Get newKeysTextColor.
        tempcolor = Use.colorFromString(properties.getProperty(
            Names.newKeysTextColor));
        if (tempcolor == null) {
            board.newKeysTextColor = Constants.defaultKeyTextColor;
        } else {
            board.newKeysTextColor = tempcolor;
        }

        // Get boardBorderColor.
        tempcolor = Use.colorFromString(properties.getProperty(
            Names.boardBorderColor));
        if (tempcolor == null) {
            board.borderColor = Constants.defaultBoardBorderColor;
        } else {
            board.borderColor = tempcolor;
        }

        // Make keys array.
        board.keys = new Key[board.columns()][Constants.keyRows];

        // Get keys information.
        KeysIterator it = new KeysIterator(board.keys);
        while (it.hasNext()) {
            String keyPrefix = ((Integer) it.x).toString() + ","
                + ((Integer) it.y).toString() + ".";

            // Make new Key
            board.keys[it.x][it.y] = new Key(board);

            // setText
            board.keys[it.x][it.y].setText(properties.getProperty(
                keyPrefix + Names.title, ""));

            // setBackground
            tempcolor = Use.colorFromString(properties.getProperty(
                keyPrefix + Names.background));
            if (tempcolor == null) {
                board.keys[it.x][it.y].setBackgroundColor(board.newKeysBackgroundColor);
            } else {
                board.keys[it.x][it.y].setBackgroundColor(tempcolor);
            }

            // setForeground
            tempcolor = Use.colorFromString(properties.getProperty(
                keyPrefix + Names.foreground));
            if (tempcolor == null) {
                board.keys[it.x][it.y].setTextColor(board.newKeysTextColor);
            } else {
                board.keys[it.x][it.y].setTextColor(tempcolor);
            }

            // set columnsWide
            Integer columnsWideInteger = Use.getIntegerFromString(
                false, properties.getProperty(
                    keyPrefix + Names.columnsWide, "1"));
            if (columnsWideInteger == null
                || columnsWideInteger < 1
                || columnsWideInteger > Constants.maxColumns) {
                board.keys[it.x][it.y].columnsWide = 1;
            } else {
                board.keys[it.x][it.y].columnsWide = columnsWideInteger;
            }

            // set uses
            Integer usesInteger = Use.getIntegerFromString(
                false, properties.getProperty(keyPrefix + Names.uses, "0"));
            if (usesInteger == null || usesInteger < 0) {
                board.keys[it.x][it.y].uses = 0;
            } else {
                board.keys[it.x][it.y].uses = usesInteger;
            }

            // set contents
            board.keys[it.x][it.y].contents = properties.getProperty(
                keyPrefix + Names.contents, "");
        }
        return board;
    }

    public String name() {
        return quickShowLabel.getText();
    }

    public void setName(String name) {
        quickShowLabel.setText(name);
    }

    static public String getFreeFileName() {
        File file;
        int max = Constants.maximumBoardFile;
        for (Integer i = 1; i <= max; ++i) {
            String fullPath = Use.workingDirectory + "/"
                + i.toString() + Constants.boardExtension;
            file = new File(fullPath);
            if (!file.exists()) {
                return i.toString() + Constants.boardExtension;
            }
        }
        Frames.message("Out of available file numbers. You have "
            + ((Integer) Constants.maximumBoardFile).toString() + " board files?");
        return null;
    }

    static public Board copyBoardKeys(Board sourceBoard, Board destinationBoard,
        boolean preserveCopyColors) {

        KeysIterator it = new KeysIterator(sourceBoard.keys);
        while (it.hasNext()) {
            if (keyExists(destinationBoard, it.x, it.y)) {
                destinationBoard.keys[it.x][it.y].copyFrom(
                    sourceBoard.keys[it.x][it.y], preserveCopyColors);
            }
        }
        return destinationBoard;
    }

    private static boolean keyExists(Board board, int x, int y) {
        if ((board == null) || (board.keys == null)) {
            return false;
        }
        if ((x < 0) || (x >= board.keys.length)) {
            return false;
        }
        if ((y < 0) || (y >= board.keys[0].length)) {
            return false;
        }
        if (board.keys[x][y] == null) {
            return false;
        }
        return true;
    }

    public int columns() {
        return keys.length;
    }

    public void setColumns(int columns) {
        // Return if the value has not changed.
        if (columns == this.columns()) {
            return;
        }

        // Save old keys array.
        Key[][] oldKeys = keys;

        // Make new keys array of the correct size, save as current keys.
        keys = new Key[columns][Constants.keyRows];

        // Initialize new array.
        KeysIterator itNew = new KeysIterator(keys);
        while (itNew.hasNext()) {
            keys[itNew.x][itNew.y] = new Key(this);
            keys[itNew.x][itNew.y].setColors(
                newKeysBackgroundColor, newKeysTextColor);
        }

        // Copy all keys possible, from old array.
        KeysIterator itOld = new KeysIterator(oldKeys);
        while (itOld.hasNext()) {
            if (keyExists(this, itOld.x, itOld.y)) {
                keys[itOld.x][itOld.y].copyFrom(
                    oldKeys[itOld.x][itOld.y], true);
            }
        }
    }

    void resetAllKeyColors() {
        KeysIterator it = new KeysIterator(keys);
        while (it.hasNext()) {
            keys[it.x][it.y].setColors(
                newKeysBackgroundColor, newKeysTextColor);
        }
    }

    void clearAllKeys() {
        KeysIterator it = new KeysIterator(keys);
        Key blank = new Key(this);
        blank.setColors(newKeysBackgroundColor, newKeysTextColor);
        while (it.hasNext()) {
            keys[it.x][it.y].copyFrom(blank, true);
        }
    }

    public void mouseWatcherHover(Point mouseScreenLocation,
        Point relativeLocation, Component relativeTo) {

        // Loop through all the keys, checking for hover inside.
        KeysIterator it = new KeysIterator(keys);
        boolean foundOne = false;
        while (it.hasNext()) {
            if (MouseWatcher.isMouseInsideComponent(
                mouseScreenLocation, keys[it.x][it.y])) {
                if (Main.getBoardManager().lastEditHover != 1) {
                    // Check for unfocused main panel
                    if (Main.getMainPanel().mainPanelHasFocus()) {
                        // Main panel has focus, display message and skip after hover events.
                        Main.getBoardManager().setClickTargetLabelShowing(true);
                        MouseWatcher.skipAfterHoverEvents();
                    } else {
                        // Main panel does not have focus, run key.
                        // Increment the uses.
                        keys[it.x][it.y].uses += 1;
                        // Run the key contents.
                        Main.getCommandCenter().runKeyContents(
                            keys[it.x][it.y].contents, keys[it.x][it.y]);
                    }
                } else {
                    KeyEditPanel.go(keys[it.x][it.y],
                        new Point(it.x, it.y));
                }
                foundOne = true;
            }
        }
        if (!foundOne) {
            // No valid hover component was found...
            // Tell Mousewatcher to skip after hover events.
            MouseWatcher.skipAfterHoverEvents();
        }

    }

    void setKey(Point keyLocation, Key key) {
        if (keyExists(this, keyLocation.x, keyLocation.y)) {
            keys[keyLocation.x][keyLocation.y].copyFrom(key, true);
        }
    }

    public void calculateAutoKeyWidth() {
        int result = Constants.minimumKeyWidth;
        Key key = new Key(null);
        key.setOpaque(true);
        int skipKeys = 0;
        KeysIterator it = new KeysIterator(keys);
        while (it.hasNext()) {
            // Do not calculate a key, and decrement skipKeys if needed.
            if (skipKeys > 0) {
                --skipKeys;
                continue;
            }
            // Calculate limitedColumnWidth from the keys columnsWide value,
            // and the number of columns in the board.
            int columnsAvailableForThisKey = columns() - it.x;
            int limitedColumnWidth = Use.clampInt(
                keys[it.x][it.y].columnsWide, 1, columnsAvailableForThisKey);

            // Make sure the appropriate number of future keys are skipped.
            skipKeys = limitedColumnWidth - 1;

            // Get the needed key total size for this title.
            key.setText(keys[it.x][it.y].getText());
            key.validate();
            int neededKeySize = key.getPreferredSize().width;

            // Calculate the needed single column width for this key.
            int totalBorderPixels = (limitedColumnWidth - 1)
                * Constants.borderThickness;
            int neededTotalColumnPixels = neededKeySize - totalBorderPixels;
            int neededSingleColumnWidth
                = neededTotalColumnPixels / limitedColumnWidth;
            if (neededTotalColumnPixels % limitedColumnWidth != 0) {
                neededSingleColumnWidth += 1;
            }
            result = Math.max(neededSingleColumnWidth, result);
        }
        autoKeyWidth = result;
    }

}
