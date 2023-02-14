package touchboard;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import tbUtilities.Frames;
import tbUtilities.Use;
import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * This class runs board keys, and processes commands.
 */
public class CommandCenter {

    // Our java robot class for keystrokes and such.
    private Robot robot;

    // Caps Lock state
    private boolean capslock = false;

    // Shift state, capitalizes a single next character.
    private boolean shift = false;

    // Held key codes for a particular keys contents.
    ArrayList<Integer> heldKeyCodes = new ArrayList<Integer>();

    // This tracks the combined total amount of pause for a single key.
    // All pause amounts are specified in milliseconds.
    private int pauseTotalForKey = 0;
    private int pauseBetweenKeystrokes = 0;
    public int defaultPauseBetweenKeystrokes
            = Options.getDefaultPauseBetweenKeystrokes();
    private final int pauseTotalLimit = 60 * 1000; // 60 seconds.
    private final int pauseInstanceLimit = 10 * 1000; // 10 seconds.

    /**
     * Set this to true if a command contains an error. This boolean is used to
     * stop processing of a key that contains command errors.
     */
    private boolean commandError = false;

    /**
     * Constructor
     */
    public CommandCenter() {
        try {
            robot = new Robot();
        } catch (AWTException ex) {
            /* TODO notify user that his system does now allow applications to send keystokes to other windows. */
            ex.printStackTrace();
        }
    }

    /**
     * This is the function that processes and executes string contents.
     */
    public void runKeyContents(String contents, Key key) {
        // Check for command processing.
        boolean parsingCommands = contents.toLowerCase().contains("[parse]");

        // Reset the pause total at the beginning of every key.
        pauseTotalForKey = 0;
        pauseBetweenKeystrokes = defaultPauseBetweenKeystrokes;

        // Reset the commandError indicator to false at the beginning of every key.
        commandError = false;

        // Loop through every character
        for (int i = 0; i < contents.length(); ++i) {
            if (!parsingCommands) {
                letterToKeyPresses(contents.charAt(i));
            } else {
                // If this character is not the start of a command,
                if (contents.charAt(i) != '[') {
                    letterToKeyPresses(contents.charAt(i));
                } else {
                    // Check for a valid command.
                    String commandWithBrackets = getValidCommandSyntax(
                            i, contents, key.getText());

                    // If the command was valid,
                    if (commandWithBrackets != null) {
                        // Process the command, and increment our position.
                        processCommand(commandWithBrackets, key);
                        i += (commandWithBrackets.length() - 1);
                    }
                }
            }
            // Stop processing this key if a command had an error.
            if (commandError) {
                break;
            }
        } // End: Loop through every character

        // Release any held key codes.
        for (int i = (heldKeyCodes.size() - 1); i >= 0; --i) {
            release(heldKeyCodes.get(i));
        }
        heldKeyCodes.clear();
    }

    /**
     * Type the given string.
     */
    public void type(String string) {
        for (int i = 0; i < string.length(); ++i) {
            letterToKeyPresses(string.charAt(i));
        }
    }

    // This takes a character, and generates the java robot key
    // presses required to type that character.
    private void letterToKeyPresses(char c) {

        // This function assumes the following about the integer virtual key
        // codes:
        // A-Z will remain in alphabetical order numerically.
        // 1-9 will remain in order numerically.
        // Obey pauseBetweenKeystrokes
        if ((pauseTotalForKey + pauseBetweenKeystrokes) < pauseTotalLimit) {
            Use.mySleep(pauseBetweenKeystrokes);
            pauseTotalForKey += pauseBetweenKeystrokes;
        }

        // Obey capslock status.
        if (getCapslock()) {
            c = ((Character) c).toUpperCase(c);
        }

        // Obey shift status, capitalizes a single next character.
        // Other way of defining whitespace is:
        // (c != ' ') && (c != '\n') && (c != '\r') && (c != '\t')
        if (shift && (!Character.isWhitespace(c))) {
            c = ((Character) c).toUpperCase(c);
            setShift(false);
        }

        // Press shift key?
        boolean s = false;

        // Virtual key code to press.
        int k = KeyEvent.VK_UNDEFINED;

        // A-Z
        if ((c >= 'A') && (c <= 'Z')) {
            s = true;
            c = Character.toLowerCase(c);
        }

        // a-z
        if ((c >= 'a') && (c <= 'z')) {
            k = KeyEvent.VK_A + (c - 'a');
        }

        // 1-9
        if ((c >= '1') && (c <= '9')) {
            k = KeyEvent.VK_1 + (c - '1');
        }

        // Other printable characters.
        switch (c) {
            case '[':
                k = KeyEvent.VK_OPEN_BRACKET;
                break;
            case '{':
                s = true;
                k = KeyEvent.VK_OPEN_BRACKET;
                break;
            case ']':
                k = KeyEvent.VK_CLOSE_BRACKET;
                break;
            case '}':
                s = true;
                k = KeyEvent.VK_CLOSE_BRACKET;
                break;
            case '=':
                k = KeyEvent.VK_EQUALS;
                break;
            case '+':
                s = true;
                k = KeyEvent.VK_EQUALS;
                break;
            case '!':
                s = true;
                k = KeyEvent.VK_1;
                break;
            case '@':
                s = true;
                k = KeyEvent.VK_2;
                break;
            case '#':
                s = true;
                k = KeyEvent.VK_3;
                break;
            case '$':
                s = true;
                k = KeyEvent.VK_4;
                break;
            case '%':
                s = true;
                k = KeyEvent.VK_5;
                break;
            case '^':
                s = true;
                k = KeyEvent.VK_6;
                break;
            case '&':
                s = true;
                k = KeyEvent.VK_7;
                break;
            case '*':
                s = true;
                k = KeyEvent.VK_8;
                break;
            case '(':
                s = true;
                k = KeyEvent.VK_9;
                break;
            case '0':
                k = KeyEvent.VK_0;
                break;
            case ')':
                s = true;
                k = KeyEvent.VK_0;
                break;
            case '-':
                k = KeyEvent.VK_MINUS;
                break;
            case '_':
                s = true;
                k = KeyEvent.VK_MINUS;
                break;
            case '\\':
                k = KeyEvent.VK_BACK_SLASH;
                break;
            case '|':
                s = true;
                k = KeyEvent.VK_BACK_SLASH;
                break;
            case ';':
                k = KeyEvent.VK_SEMICOLON;
                break;
            case ':':
                s = true;
                k = KeyEvent.VK_SEMICOLON;
                break;
            case '\'':
                k = KeyEvent.VK_QUOTE;
                break;
            case '"':
                s = true;
                k = KeyEvent.VK_QUOTE;
                break;
            case '`':
                k = KeyEvent.VK_BACK_QUOTE;
                break;
            case '~':
                s = true;
                k = KeyEvent.VK_BACK_QUOTE;
                break;
            case ',':
                k = KeyEvent.VK_COMMA;
                break;
            case '<':
                s = true;
                k = KeyEvent.VK_COMMA;
                break;
            case '.':
                k = KeyEvent.VK_PERIOD;
                break;
            case '>':
                s = true;
                k = KeyEvent.VK_PERIOD;
                break;
            case '/':
                k = KeyEvent.VK_SLASH;
                break;
            case '?':
                s = true;
                k = KeyEvent.VK_SLASH;
                break;
            case '\t':
                k = KeyEvent.VK_TAB;
                break;
            case ' ':
                k = KeyEvent.VK_SPACE;
                break;
            case '\n':
                k = KeyEvent.VK_ENTER;
                break;
        }

        // Return if keycode was never set.
        if (k == KeyEvent.VK_UNDEFINED) {
            return;
        }

        // Skip pressing shift if shift is already being held by the hold command.
        for (int i = 0; i < heldKeyCodes.size(); ++i) {
            int code = heldKeyCodes.get(i);
            if (code == KeyEvent.VK_SHIFT) {
                s = false;
            }
        }

        // Press the keys using java robot.
        if (s) {
            robot.keyPress(KeyEvent.VK_SHIFT);
        }
        try {
            robot.keyPress(k);
            robot.keyRelease(k);
        } catch (IllegalArgumentException e) {
            // TODO notify programmer of exceptions here, letterToKeyPresses.
        }
        if (s) {
            robot.keyRelease(KeyEvent.VK_SHIFT);
        }

    }

    // This checks the specified position for basic validation of the command
    // syntax.
    // Returns the command, including the braces, if the command is valid.
    // Returns null for invalid command syntax.
    private String getValidCommandSyntax(int openBracketIndex,
            String contents, String keyTitle) {

        // Rules:
        // A command must start with a [, and end with a ].
        // A command must not be more than 100 characters between the brackets.
        // A command may not contain any spaces or other whitespace.
        // A command may not itself contain any brackets.
        int characterAfterOpenBracket = openBracketIndex + 1;
        int closeBracketIndex = -1;
        int commandSizeLimit = 100;

        for (int i = characterAfterOpenBracket;
                (i < contents.length()) && (closeBracketIndex == -1);
                ++i) {

            // Check for command exceeding character limit.
            if ((i - openBracketIndex) > (commandSizeLimit + 1)) {
                errorCommandNotClosed(
                        contents.substring(openBracketIndex, i),
                        keyTitle, commandSizeLimit);
                return null;
            }

            char c = contents.charAt(i);
            if (c == ' ' || c == '\t' || c == '\n' || c == '\r') {
                errorCommandContainsWhitespace(
                        contents.substring(openBracketIndex, i), keyTitle);
                return null;
            }
            if (c == '[') {
                errorCommandContainsLeftBracket(
                        contents.substring(openBracketIndex, (i + 1)), keyTitle);
                return null;
            }
            if (c == ']') {
                closeBracketIndex = i;
            }
        }

        // Check for valid close bracket index.
        if (closeBracketIndex == -1) {
            errorCommandNotClosed(
                    contents.substring(openBracketIndex, contents.length()),
                    keyTitle, commandSizeLimit);
            return null;
        }

        // Get command with brackets.
        String commandWithBrackets
                = contents.substring(openBracketIndex, closeBracketIndex + 1);

        // Check for empty command
        if (commandWithBrackets.equals("[]")) {
            errorEmptyCommand(keyTitle);
            return null;
        }

        // Return command with brackets
        return commandWithBrackets;
    }

    private void errorCommandContainsLeftBracket(
            String commandString, String keyTitle) {
        commandError = true;
        Frames.message(
                "Error, command: \"" + commandString + "\" in key \""
                + keyTitle + "\" contains a left bracket '['."
                + getBracketsExplanation());
    }

    private void errorCommandContainsWhitespace(
            String commandString, String keyTitle) {
        commandError = true;
        Frames.message(
                "Error, command: \"" + commandString + "\" in key \""
                + keyTitle + "\" contains a space, tab, or carriage return.\n"
                + "Commands cannot contain any of these characters."
                + getBracketsExplanation());
    }

    private void errorCommandNotClosed(
            String commandString, String keyTitle, int commandSizeLimit) {
        commandError = true;
        Frames.message(
                "Error, command: \"" + commandString + "\" in key \""
                + keyTitle + "\" is not closed.\n"
                + "This is either because the command has no closing bracket ']',\n"
                + "or because the command inside the brackets is longer than "
                + commandSizeLimit + " characters."
                + getBracketsExplanation());
    }

    private void errorEmptyCommand(String keyTitle) {
        commandError = true;
        Frames.message("Error, key \"" + keyTitle
                + "\" contains a pair of empty brackets []."
                + getBracketsExplanation());
    }

    private void errorInvalidKeyCode(
            String commandWithBrackets, String keyCode) {
        commandError = true;
        Frames.message("In command: " + commandWithBrackets
                + "\n\"" + keyCode + "\" is not a valid key code.\n\n"
                + "See the help file for a list of valid key codes.");
    }

    private void errorParsingNumber(
            String commandWithBrackets, String numberSection) {
        commandError = true;
        Frames.message("In command: " + commandWithBrackets
                + "\n\"" + numberSection + "\" is not a valid number.\n"
                + "Please type a positive number after the dot in this command.");
    }

    private void errorParsingPauseNumber(
            String commandWithBrackets, String numberSection) {
        commandError = true;
        Frames.message("In command: " + commandWithBrackets
                + "\n\"" + numberSection + "\" is not a valid number.\n"
                + "Please type a positive number after the dot in this command.\n"
                + "This number represents the amount of time that you wish to pause,\n"
                + "in tenths of a second.");
    }

    private void errorUnsupportedCommand(
            String commandString, String keyTitle) {
        commandError = true;
        Frames.message("Error, command: \"" + commandString
                + "\" in key \"" + keyTitle
                + "\" is not a supported command.\n"
                + "See help for a list of supported commands."
                + getBracketsExplanation());
    }

    private String getBracketsExplanation() {
        return "\n\n"
                + "When [parse] is specified, brackets[] in key contents\n"
                + "are reserved for use with commands only. If you wish to type a\n"
                + "bracket character, please use the commands\n"
                + "[leftbracket] or [rightbracket].";
    }

    // Same as pressKey(int keyCode), but press multiple times.
    private void pressKey(int keyCode, int count) {
        for (int i = 0; i < count; ++i) {
            pressKey(keyCode);
        }
    }

    // This function is designed to press and release a single key.
    // It uses KeyEvent key codes.
    private void pressKey(int keyCode) {
        try {
            robot.keyPress(keyCode);
            robot.keyRelease(keyCode);
        } catch (Exception e) {
            // TODO notify programmer of exceptions here, pressKey.
        }
    }

    private void pressNestedKeyCombination(ArrayList<Integer> integerArray) {
        int size = integerArray.size();
        int[] codes = new int[size];
        for (int i = 0; i < size; ++i) {
            codes[i] = integerArray.get(i);
        }
        pressNestedKeyCombination(codes);
    }

    // This function is designed to press a single key combination like
    // ctrl + c. Pass in the keys in the order that they should be pressed.
    // It uses KeyEvent key codes.
    public void pressNestedKeyCombination(int[] keyCodes) {

        // Press all the keys in order.
        for (int i = 0; i < keyCodes.length; ++i) {
            try {
                robot.keyPress(keyCodes[i]);
            } // For an exception, try to release the previously pressed keys.
            catch (IllegalArgumentException e) {
                // TODO notify programmer of exceptions here, pressNestedKeyCombination.
                --i;
                while (i >= 0) {
                    robot.keyRelease(keyCodes[i]);
                    --i;
                }
                // Return after an exception.
                return;
            }
        }

        // Release all the keys in reverse order.
        for (int i = (keyCodes.length - 1); i >= 0; --i) {
            try {
                robot.keyRelease(keyCodes[i]);
            } catch (IllegalArgumentException e) {
                // TODO notify programmer of exceptions here, pressNestedKeyCombination.
            }
        }
    }

    private void typeDateTimeStamp(
            String formatString, boolean lowercase, String keyTitle) {
        formatString = formatString.replace('_', ' ');
        SimpleDateFormat formatInstance;
        try {
            formatInstance = new SimpleDateFormat(formatString);
        } catch (IllegalArgumentException e) {
            Frames.message("Error, date_time_stamp format: \"" + formatString
                    + "\" in key \"" + keyTitle + "\" is not valid.\n"
                    + "See help for a list of supported date_time_stamp formats.");
            return;
        }
        DateFormatSymbols symbols = formatInstance.getDateFormatSymbols();
        symbols.setAmPmStrings(new String[]{"a", "p"});
        formatInstance.setDateFormatSymbols(symbols);
        String stamp = formatInstance.format(new Date());
        if (lowercase) {
            stamp = stamp.toLowerCase();
        }
        type(stamp);
    }

    public boolean getCapslock() {
        return capslock;
    }

    public void setCapslock(boolean aCapslock) {
        boolean before = inCapState();
        capslock = aCapslock;
        boolean after = inCapState();
        if (before != after) {
            Main.getBoardManager().arrangeAll();
        }
    }

    public boolean getShift() {
        return shift;
    }

    public void setShift(boolean aShift) {
        boolean before = inCapState();
        shift = aShift;
        boolean after = inCapState();
        if (before != after) {
            Main.getBoardManager().arrangeAll();
        }
    }

    public boolean inCapState() {
        return capslock || shift;
    }

    /**
     * Returns the integer value of one of the virtual key code fields from the
     * java.awt.event.KeyEvent class.
     *
     * This takes a string virtualKeyCode, representing one of the static public
     * final int virtual key code fields from the KeyEvent class. This string
     * can be in any case, and can optionally omit the "VK_" prefix.
     *
     * Example valid virtualKeyCode values: "VK_A", "vk_f3", "f3", "CONTROL",
     * "vk_Control".
     *
     * Returns: the integer value of the virtual key code or null. A null return
     * value probably means that the specified virtual key code does not exist.
     */
    static public Integer getVirtualKeyValueFromString(String virtualKeyCode) {
        virtualKeyCode = virtualKeyCode.toUpperCase();
        if (!virtualKeyCode.startsWith("VK_")) {
            virtualKeyCode = "VK_" + virtualKeyCode;
        }
        try {
            return KeyEvent.class.getField(virtualKeyCode).getInt(null);
        } catch (Exception e) {
            return null;
        }
    }

    private void hold(int keyCode) {
        try {
            robot.keyPress(keyCode);
            heldKeyCodes.add(keyCode);
        } catch (Exception e) {
        }
    }

    private void release(int keyCode) {
        try {
            robot.keyRelease(keyCode);
            heldKeyCodes.remove(new Integer(keyCode));
        } catch (Exception e) {
        }
    }

    // This executes the specified command.
    private void processCommand(String commandWithBrackets, Key key) {

        // This string is our command without any brackets.
        String command = commandWithBrackets.substring(
                1, (commandWithBrackets.length() - 1));

        // Save original case string without brackets, for
        // case sensitive commands.
        String commandOriginalCase = command;

        // Commands are most often case insensitive, change
        // command string to lower case.
        command = command.toLowerCase();

        // COMMAND TYPES
        // [parse]
        if (command.equals("parse")) {
            return;
        }

        // [leftbracket]
        if (command.equals("leftbracket")) {
            letterToKeyPresses('[');
            return;
        }

        // [rightbracket]
        if (command.equals("rightbracket")) {
            letterToKeyPresses(']');
            return;
        }

        // [up.x]
        if (command.startsWith("up.")) {
            String numberString = Use.safeSubstring(
                    "up.".length(), command.length(), command);
            Integer count = Use.getIntegerFromString(false, numberString);
            if (count != null) {
                pressKey(KeyEvent.VK_UP, count);
            } else {
                errorParsingNumber(commandWithBrackets, numberString);
            }
            return;
        }

        // [down.x]
        if (command.startsWith("down.")) {
            String numberString = Use.safeSubstring(
                    "down.".length(), command.length(), command);
            Integer count = Use.getIntegerFromString(false, numberString);
            if (count != null) {
                pressKey(KeyEvent.VK_DOWN, count);
            } else {
                errorParsingNumber(commandWithBrackets, numberString);
            }
            return;
        }

        // [left.x]
        if (command.startsWith("left.")) {
            String numberString = Use.safeSubstring(
                    "left.".length(), command.length(), command);
            Integer count = Use.getIntegerFromString(false, numberString);
            if (count != null) {
                pressKey(KeyEvent.VK_LEFT, count);
            } else {
                errorParsingNumber(commandWithBrackets, numberString);
            }
            return;
        }

        // [right.x]
        if (command.startsWith("right.")) {
            String numberString = Use.safeSubstring(
                    "right.".length(), command.length(), command);
            Integer count = Use.getIntegerFromString(false, numberString);
            if (count != null) {
                pressKey(KeyEvent.VK_RIGHT, count);
            } else {
                errorParsingNumber(commandWithBrackets, numberString);
            }
            return;
        }

        // [backspace.x]
        if (command.startsWith("backspace.")) {
            String numberString = Use.safeSubstring(
                    "backspace.".length(), command.length(), command);
            Integer count = Use.getIntegerFromString(false, numberString);
            if (count != null) {
                pressKey(KeyEvent.VK_BACK_SPACE, count);
            } else {
                errorParsingNumber(commandWithBrackets, numberString);
            }
            return;
        }

        // [delete.x]
        if (command.startsWith("delete.")) {
            String numberString = Use.safeSubstring(
                    "delete.".length(), command.length(), command);
            Integer count = Use.getIntegerFromString(false, numberString);
            if (count != null) {
                pressKey(KeyEvent.VK_DELETE, count);
            } else {
                errorParsingNumber(commandWithBrackets, numberString);
            }
            return;
        }

        // [capslock_toggle]
        if (command.equals("capslock_toggle")) {
            setCapslock(Use.toggleBoolean(getCapslock()));
            return;
        }

        // [capslock_on]
        if (command.equals("capslock_on")) {
            setCapslock(true);
            return;
        }

        // [capslock_off]
        if (command.equals("capslock_off")) {
            setCapslock(false);
            return;
        }

        // [shift]
        if (command.equals("shift")) {
            setShift(true);
            return;
        }

        // [date_time_stamp.format]
        if (command.startsWith("date_time_stamp.")) {
            // Get format string in original case.
            String formatString = Use.safeSubstring(
                    "date_time_stamp.".length(),
                    commandOriginalCase.length(),
                    commandOriginalCase);
            typeDateTimeStamp(formatString, false, key.getText());
            return;
        }

        // [date_time_stamp_lowercase.format]
        if (command.startsWith("date_time_stamp_lowercase.")) {
            // Get format string in original case.
            String formatString = Use.safeSubstring(
                    "date_time_stamp_lowercase.".length(),
                    commandOriginalCase.length(),
                    commandOriginalCase);
            typeDateTimeStamp(formatString, true, key.getText());
            return;
        }

        // [addclip]
        if (command.equals("addclip")) {
            Main.getBoardManager().addClip();
            return;
        }

        // [pause.x]
        // Pause for the specified time, in tenths of a second.
        // Maximum value is pauseInstanceLimit milliseconds.
        // Maximum total for a key is pauseTotalLimit milliseconds.
        if (command.startsWith("pause.")) {
            String numberString = Use.safeSubstring(
                    "pause.".length(), command.length(), command);
            Integer amount = Use.getIntegerFromString(false, numberString);
            if (amount != null) {
                int amountInMilliseconds = amount * 100;
                amountInMilliseconds = Use.clampInt(amountInMilliseconds,
                        0, pauseInstanceLimit);
                if ((pauseTotalForKey + amountInMilliseconds) <= pauseTotalLimit) {
                    Use.mySleep(amountInMilliseconds);
                    pauseTotalForKey += amountInMilliseconds;
                }
            } else {
                errorParsingPauseNumber(commandWithBrackets, numberString);
            }
            return;
        }

        // [type_full_speed]
        if (command.equals("type_full_speed")) {
            pauseBetweenKeystrokes = 0;
            return;
        }

        // [type_slow]
        if (command.equals("type_slow")) {
            pauseBetweenKeystrokes = 33; // 30 kps
            return;
        }

        // [type_slower]
        if (command.equals("type_slower")) {
            pauseBetweenKeystrokes = 67; // 15 kps
            return;
        }

        // [type_slowest]
        if (command.equals("type_slowest")) {
            pauseBetweenKeystrokes = 200; // 5 kps
            return;
        }

        // [press.key.key]
        if (command.startsWith("press.")) {
            String keyCodesString = Use.safeSubstring(
                    "press.".length(), command.length(), command);
            String[] keyCodeArray = keyCodesString.split("\\Q.\\E");
            for (String keyCode : keyCodeArray) {
                Integer keyValue = getVirtualKeyValueFromString(keyCode);
                if (keyValue != null) {
                    pressKey(keyValue, 1);
                } else {
                    errorInvalidKeyCode(commandWithBrackets, keyCode);
                    return;
                }
            }
            return;
        }

        // [press_combination.key.key]
        if (command.startsWith("press_combination.")) {
            String keyCodesString = Use.safeSubstring(
                    "press_combination.".length(), command.length(), command);
            String[] keyCodeArray = keyCodesString.split("\\Q.\\E");
            ArrayList<Integer> integerArray = new ArrayList<Integer>();
            for (String keyCode : keyCodeArray) {
                Integer keyValue = getVirtualKeyValueFromString(keyCode);
                if (keyValue != null) {
                    integerArray.add(keyValue);
                } else {
                    errorInvalidKeyCode(commandWithBrackets, keyCode);
                    return;
                }
            }
            pressNestedKeyCombination(integerArray);
            return;
        }

        // [hold.onekey]
        if (command.startsWith("hold.")) {
            String keyCode = Use.safeSubstring(
                    "hold.".length(), command.length(), command);
            Integer keyValue = getVirtualKeyValueFromString(keyCode);
            if (keyValue != null) {
                hold(keyValue);
            } else {
                errorInvalidKeyCode(commandWithBrackets, keyCode);
            }
            return;
        }

        // [release.onekey]
        if (command.startsWith("release.")) {
            String keyCode = Use.safeSubstring(
                    "release.".length(), command.length(), command);
            Integer keyValue = getVirtualKeyValueFromString(keyCode);
            if (keyValue != null) {
                release(keyValue);
            } else {
                errorInvalidKeyCode(commandWithBrackets, keyCode);
            }
            return;
        }

        // If none of these cases caught the command, then it is not supported.
        errorUnsupportedCommand(commandWithBrackets, key.getText());
    }

}
