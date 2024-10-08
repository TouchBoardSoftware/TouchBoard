package com.tb.touchboard;

import com.tb.tbUtilities.ClipboardTools;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import com.tb.tbUtilities.Frames;
import com.tb.tbUtilities.Use;
import java.awt.*;
import java.awt.event.KeyEvent;
import javax.swing.JOptionPane;

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
    ArrayList<Integer> heldKeyCodes = new ArrayList<>();

    // This tracks the combined total amount of pause for a single key.
    // All pause amounts are specified in milliseconds.
    private int pauseTotalForKey = 0;
    private int pauseBetweenKeystrokes = 0;
    public int defaultPauseBetweenKeystrokes
        = Options.getDefaultPauseBetweenKeystrokes();
    private final int pauseTotalLimit = 60 * 1000; // 60 seconds.
    private final int pauseInstanceLimit = 10 * 1000; // 10 seconds.

    /**
     * Set this to true if a command contains an error. This boolean is used to stop processing of a
     * key that contains command errors.
     */
    private boolean commandError = false;

    /**
     * Constructor
     */
    public CommandCenter() {
        try {
            robot = new Robot();
        } catch (AWTException ex) {
            // The system is not allowing this application to control the keyboard.
            JOptionPane.showMessageDialog(null,
                "TouchBoard does not currently have permission to control the keyboard.\n"
                + "TouchBoard is a virtual keyboard application, and will need this permission to\n"
                + "function properly.\n\n"
                + "On Mac systems, permission to control the keyboard will need to be given\n"
                + "to any used launcher program, and not just to the TouchBoard jar file."
                + "For any questions about setting up the keyboard permissions, the TouchBoard\n"
                + "documentation and issue forums are available at:\n"
                + "https://github.com/TouchBoardSoftware/TouchBoard \n", "",
                JOptionPane.INFORMATION_MESSAGE);
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
            c = Character.toUpperCase(c);
        }

        // Obey shift status, capitalizes a single next character.
        // Other way of defining whitespace is:
        // (c != ' ') && (c != '\n') && (c != '\r') && (c != '\t')
        if (shift && (!Character.isWhitespace(c))) {
            c = Character.toUpperCase(c);
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
     * This takes a string virtualKeyCode, representing one of the static public final int virtual
     * key code fields from the KeyEvent class. This string can be in any case, and can optionally
     * omit the "VK_" prefix.
     *
     * Example valid virtualKeyCode values: "VK_A", "vk_f3", "f3", "CONTROL", "vk_Control".
     *
     * This function will also accept the special cross platform code "MYCONTROL". This code
     * represents VK_META when running on a Mac, or VK_CONTROL when running on any other OS.
     *
     * Returns: the integer value of the virtual key code or null. A null return value probably
     * means that the specified virtual key code does not exist.
     */
    @SuppressWarnings("UseSpecificCatch")
    public Integer getVirtualKeyValueFromString(String keyCode) {
        keyCode = keyCode.toUpperCase();
        if ("MYCONTROL".equals(keyCode)) {
            return getOsSpecificControlKey();
        }
        if (!keyCode.startsWith("VK_")) {
            keyCode = "VK_" + keyCode;
        }
        try {
            return KeyEvent.class.getField(keyCode).getInt(null);
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
            heldKeyCodes.remove(Integer.valueOf(keyCode));
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

        // [uppercase_first_letter]
        // Capitalize the first letter in the selected string, otherwise leave the string unchanged.
        if (command.equals("uppercase_first_letter")) {
            String clipboardOrNull = getAnySelectedTextWithClipboard_OrNull();
            // Do nothing for a null or empty clipboard.
            if (clipboardOrNull == null || clipboardOrNull.isEmpty()) {
                Frames.message("There is no selected text to modify. Please select some text\n"
                    + "in your target application before using the 'uppercase_first_letter' command.");
                return;
            }
            String clipboard = clipboardOrNull;
            String result = capitalizeFirstCharacterThatIsLetter(clipboard);
            type(result);
            return;
        }

        // [lowercase_first_letter]
        // Lowercase the first letter in the selected string, otherwise leave the string unchanged.
        if (command.equals("lowercase_first_letter")) {
            String clipboardOrNull = getAnySelectedTextWithClipboard_OrNull();
            // Do nothing for a null or empty clipboard.
            if (clipboardOrNull == null || clipboardOrNull.isEmpty()) {
                Frames.message("There is no selected text to modify. Please select some text\n"
                    + "in your target application before using the 'lowercase_first_letter' command.");
                return;
            }
            String clipboard = clipboardOrNull;
            String result = lowercaseFirstCharacterThatIsLetter(clipboard);
            type(result);
            return;
        }

        // [all_uppercase_that]
        // Convert the entire string to uppercase.
        if (command.equals("all_uppercase_that")) {
            String clipboardOrNull = getAnySelectedTextWithClipboard_OrNull();
            // Do nothing for a null or empty clipboard.
            if (clipboardOrNull == null || clipboardOrNull.isEmpty()) {
                Frames.message("There is no selected text to modify. Please select some text\n"
                    + "in your target application before using the 'all_uppercase_that' command.");
                return;
            }
            String clipboard = clipboardOrNull;
            String result = clipboard.toUpperCase();
            type(result);
            return;
        }

        // [all_lowercase_that]
        // Convert the entire string to lowercase.
        if (command.equals("all_lowercase_that")) {
            String clipboardOrNull = getAnySelectedTextWithClipboard_OrNull();
            // Do nothing for a null or empty clipboard.
            if (clipboardOrNull == null || clipboardOrNull.isEmpty()) {
                Frames.message("There is no selected text to modify. Please select some text\n"
                    + "in your target application before using the 'all_lowercase_that' command.");
                return;
            }
            String clipboard = clipboardOrNull;
            String result = clipboard.toLowerCase();
            type(result);
            return;
        }

        // [camelcase_that]
        // Convert selected string to camel case. Tokens start with spaces, dots, returns, or tabs.
        if (command.equals("camelcase_that")) {
            String clipboardOrNull = getAnySelectedTextWithClipboard_OrNull();
            // Do nothing for a null or empty clipboard.
            if (clipboardOrNull == null || clipboardOrNull.trim().isEmpty()) {
                Frames.message("There is no selected text to modify. Please select some text\n"
                    + "in your target application before using the 'camelcase_that' command.");
                return;
            }
            String clipboard = clipboardOrNull;
            String result = convertToCamelCase(clipboard);
            type(result);
            return;
        }

        // [pascalcase_that]
        // Convert selected string to pascal case. Tokens start with spaces, dots, returns, or tabs.
        if (command.equals("pascalcase_that")) {
            String clipboardOrNull = getAnySelectedTextWithClipboard_OrNull();
            // Do nothing for a null or empty clipboard.
            if (clipboardOrNull == null || clipboardOrNull.trim().isEmpty()) {
                Frames.message("There is no selected text to modify. Please select some text\n"
                    + "in your target application before using the 'pascalcase_that' command.");
                return;
            }
            String clipboard = clipboardOrNull;
            String result = convertToPascalCase(clipboard);
            type(result);
            return;
        }

        // [titlecase_that]
        // Convert selected string to title case. Spaces are preserved. "This Is Title Case".
        if (command.equals("titlecase_that")) {
            String clipboardOrNull = getAnySelectedTextWithClipboard_OrNull();
            // Do nothing for a null or empty clipboard.
            if (clipboardOrNull == null || clipboardOrNull.trim().isEmpty()) {
                Frames.message("There is no selected text to modify. Please select some text\n"
                    + "in your target application before using the 'titlecase_that' command.");
                return;
            }
            String clipboard = clipboardOrNull;
            String result = convertToTitleCase(clipboard);
            type(result);
            return;
        }

        // [underscore_that]
        // Convert any spaces in the selected string to underscores. Other characters are unchanged.
        if (command.equals("underscore_that")) {
            String clipboardOrNull = getAnySelectedTextWithClipboard_OrNull();
            // Do nothing for a null or empty clipboard.
            if (clipboardOrNull == null || clipboardOrNull.trim().isEmpty()) {
                Frames.message("There is no selected text to modify. Please select some text\n"
                    + "in your target application before using the 'underscore_that' command.");
                return;
            }
            String clipboard = clipboardOrNull;
            String result = convertSpacesToUnderscores(clipboard);
            type(result);
            return;
        }

        // [underscore_with_titlecase_that]
        // Convert selected string to title case. Spaces are converted to underscores. 
        // "This_Is_Underscore_With_Title_Case".
        if (command.equals("underscore_with_titlecase_that")) {
            String clipboardOrNull = getAnySelectedTextWithClipboard_OrNull();
            // Do nothing for a null or empty clipboard.
            if (clipboardOrNull == null || clipboardOrNull.trim().isEmpty()) {
                Frames.message(
                    "There is no selected text to modify. Please select some text in your\n"
                    + "target application before using the 'underscore_with_titlecase_that' command.");
                return;
            }
            String clipboard = clipboardOrNull;
            String result = convertSpacesToUnderscores(clipboard);
            result = convertToTitleCase(result);
            type(result);
            return;
        }

        // [underscore_with_all_uppercase_that]
        // Convert selected string to all uppercase. Spaces are converted to underscores.
        // "THIS_IS_UNDERSCORE_WITH_ALL_UPPERCASE".
        if (command.equals("underscore_with_all_uppercase_that")) {
            String clipboardOrNull = getAnySelectedTextWithClipboard_OrNull();
            // Do nothing for a null or empty clipboard.
            if (clipboardOrNull == null || clipboardOrNull.trim().isEmpty()) {
                Frames.message(
                    "There is no selected text to modify. Please select some text in your\n"
                    + "target application before using the 'underscore_with_all_uppercase_that' command.");
                return;
            }
            String clipboard = clipboardOrNull;
            String result = convertSpacesToUnderscores(clipboard);
            result = result.toUpperCase();
            type(result);
            return;
        }

        // [underscore_with_all_lowercase_that]
        // Convert selected string to all lowercase. Spaces are converted to underscores.
        // "this_is_underscore_with_all_lowercase".
        if (command.equals("underscore_with_all_lowercase_that")) {
            String clipboardOrNull = getAnySelectedTextWithClipboard_OrNull();
            // Do nothing for a null or empty clipboard.
            if (clipboardOrNull == null || clipboardOrNull.trim().isEmpty()) {
                Frames.message(
                    "There is no selected text to modify. Please select some text in your\n"
                    + "target application before using the 'underscore_with_all_lowercase_that' command.");
                return;
            }
            String clipboard = clipboardOrNull;
            String result = convertSpacesToUnderscores(clipboard);
            result = result.toLowerCase();
            type(result);
            return;
        }

        // [unwrap_that]
        // This will remove all newline (aka return) characters from the selected string.
        // Each newline character that is found will be replaced with a single space.
        if (command.equals("unwrap_that")) {
            String clipboardOrNull = getAnySelectedTextWithClipboard_OrNull();
            // Do nothing for a null or empty clipboard.
            if (clipboardOrNull == null || clipboardOrNull.trim().isEmpty()) {
                Frames.message("There is no selected text to modify. Please select some text\n"
                    + "in your target application before using the 'unwrap_that' command.");
                return;
            }
            String clipboard = clipboardOrNull;
            String result = clipboard.replace("\n", " ");
            type(result);
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
            ArrayList<Integer> integerArray = new ArrayList<>();
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

    public final int getOsSpecificControlKey() {
        return (Use.isSystemMac) ? KeyEvent.VK_META : KeyEvent.VK_CONTROL;
    }

    public final void pressOsSpecificControlCombo(int secondKey) {
        pressNestedKeyCombination(new int[]{getOsSpecificControlKey(), secondKey});
    }

    public final String getAnySelectedTextWithClipboard_OrNull() {
        // Empty the clipboard.
        String emptyClipboard = "";
        ClipboardTools.writeToClipboard(emptyClipboard);
        Use.mySleep(50);
        // Copy any selected text to the clipboard.
        // Press Meta-C on mac, or Ctrl-C on windows, linux, and everything else.
        pressOsSpecificControlCombo(KeyEvent.VK_C);
        // Wait for a clipboard change for up to 1000 miliseconds.
        String newClipboardOrNull = ClipboardTools.getClipboard_OrNull();
        int waited = 0;
        while ((emptyClipboard.equals(newClipboardOrNull)) && (waited < 1000)) {
            Use.mySleep(50);
            waited += 50;
            newClipboardOrNull = ClipboardTools.getClipboard_OrNull();
        }
        // Return the retrieved clipboard text or null.
        return newClipboardOrNull;
    }

    private String convertToCamelCase(String text) {
        if (text == null) {
            return null;
        }
        if (text.trim().isEmpty()) {
            return null;
        }
        int startingSpaces = countStartingSpaces(text);
        int endingSpaces = countEndingSpaces(text);
        text = text.replace("\r", " ");
        text = text.replace("\n", " ");
        text = text.replace("\t", " ");
        text = text.trim();
        String result = "";
        for (int i = 0; i < startingSpaces; ++i) {
            result += ' ';
        }
        boolean didTextEndWithDot = text.endsWith(".");
        String[] dotGroupArray = text.split("\\.");
        int lastDotGroupIndex = (dotGroupArray.length - 1);
        dotGroupLoop:
        for (int dIdx = 0; dIdx < dotGroupArray.length; ++dIdx) {
            String dotGroupText = dotGroupArray[dIdx].trim();
            String[] tokenArray = dotGroupText.split(" ");
            boolean isFirstPopulatedTokenInDotGroup = true;
            tokenLoop:
            for (int tIdx = 0; tIdx < tokenArray.length; ++tIdx) {
                String token = tokenArray[tIdx].trim();
                if (token.isEmpty()) {
                    continue tokenLoop;
                }
                if (isFirstPopulatedTokenInDotGroup) {
                    result += token.toLowerCase();
                } else {
                    result += String.valueOf(token.charAt(0)).toUpperCase()
                        + Use.safeSubstring(1, token.length(), token).toLowerCase();
                }
                isFirstPopulatedTokenInDotGroup = false;
            }
            if (dIdx < lastDotGroupIndex) {
                result += ".";
            }
        }
        if (didTextEndWithDot && (!(result.endsWith(".")))) {
            result += ".";
        }
        for (int i = 0; i < endingSpaces; ++i) {
            result += ' ';
        }
        return result;
    }

    private String convertToPascalCase(String text) {
        if (text == null) {
            return null;
        }
        if (text.trim().isEmpty()) {
            return null;
        }
        int startingSpaces = countStartingSpaces(text);
        int endingSpaces = countEndingSpaces(text);
        text = text.replace("\r", " ");
        text = text.replace("\n", " ");
        text = text.replace("\t", " ");
        text = text.trim();
        String result = "";
        for (int i = 0; i < startingSpaces; ++i) {
            result += ' ';
        }
        boolean didTextEndWithDot = text.endsWith(".");
        String[] dotGroupArray = text.split("\\.");
        int lastDotGroupIndex = (dotGroupArray.length - 1);
        dotGroupLoop:
        for (int dIdx = 0; dIdx < dotGroupArray.length; ++dIdx) {
            String dotGroupText = dotGroupArray[dIdx].trim();
            String[] tokenArray = dotGroupText.split(" ");
            tokenLoop:
            for (int tIdx = 0; tIdx < tokenArray.length; ++tIdx) {
                String token = tokenArray[tIdx].trim();
                if (token.isEmpty()) {
                    continue tokenLoop;
                }
                result += String.valueOf(token.charAt(0)).toUpperCase()
                    + Use.safeSubstring(1, token.length(), token).toLowerCase();
            }
            if (dIdx < lastDotGroupIndex) {
                result += ".";
            }
        }
        if (didTextEndWithDot && (!(result.endsWith(".")))) {
            result += ".";
        }
        for (int i = 0; i < endingSpaces; ++i) {
            result += ' ';
        }
        return result;
    }

    private String capitalizeFirstCharacterThatIsLetter(String text) {
        if (text == null) {
            return null;
        }
        String result = "";
        boolean isFirstLetterDone = false;
        letterLoop:
        for (int i = 0; i < text.length(); ++i) {
            char c = text.charAt(i);
            if (Character.isLetter(c)) {
                result += isFirstLetterDone ? c : Character.toUpperCase(c);
                isFirstLetterDone = true;
            } else {
                result += c;
            }
        }
        return result;
    }

    private String lowercaseFirstCharacterThatIsLetter(String text) {
        if (text == null) {
            return null;
        }
        String result = "";
        boolean isFirstLetterDone = false;
        letterLoop:
        for (int i = 0; i < text.length(); ++i) {
            char c = text.charAt(i);
            if (Character.isLetter(c)) {
                result += isFirstLetterDone ? c : Character.toLowerCase(c);
                isFirstLetterDone = true;
            } else {
                result += c;
            }
        }
        return result;
    }

    private int countStartingSpaces(String text) {
        int result = 0;
        for (int i = 0; i < text.length(); ++i) {
            char c = text.charAt(i);
            if (c == ' ') {
                ++result;
            } else {
                break;
            }
        }
        return result;
    }

    private int countEndingSpaces(String text) {
        int result = 0;
        for (int i = (text.length() - 1); i >= 0; --i) {
            char c = text.charAt(i);
            if (c == ' ') {
                ++result;
            } else {
                break;
            }
        }
        return result;
    }

    private String convertToTitleCase(String text) {
        if (text == null) {
            return null;
        }
        if (text.trim().isEmpty()) {
            return null;
        }
        String result = "";
        boolean shouldCaptalizeNextLetter = true;
        for (int i = 0; i < text.length(); ++i) {
            char c = text.charAt(i);
            result += (shouldCaptalizeNextLetter) ? Character.toUpperCase(c) : Character.
                toLowerCase(c);
            shouldCaptalizeNextLetter = (!(Character.isLetter(c)));
        }
        return result;
    }

    private String convertSpacesToUnderscores(String text) {
        if (text == null) {
            return null;
        }
        if (text.trim().isEmpty()) {
            return null;
        }
        String result = "";
        for (int i = 0; i < text.length(); ++i) {
            char c = text.charAt(i);
            result += (c == ' ') ? '_' : c;
        }
        return result;
    }

    private String convertToTitleCaseWithUnderscores(String text) {
        if (text == null) {
            return null;
        }
        if (text.trim().isEmpty()) {
            return null;
        }
        String result = "";
        boolean shouldCaptalizeNextLetter = true;
        for (int i = 0; i < text.length(); ++i) {
            char c = text.charAt(i);
            c = (c == ' ') ? '_' : c;
            result += (shouldCaptalizeNextLetter) ? Character.toUpperCase(c) : Character.
                toLowerCase(c);
            shouldCaptalizeNextLetter = (!(Character.isLetter(c)));
        }
        return result;
    }

}
