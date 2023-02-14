package com.tb.touchboard;

import java.util.Properties;
import com.tb.tbUtilities.Use;

public class Options {

    static private String optionsFileName = "options.prop";

    private static class Names {

        static public String defaultPauseBetweenKeystrokes
                = "defaultPauseBetweenKeystrokesInMilliseconds";
        static public String openClips = "openClips";
        static public String showWelcomeDialog = "showWelcomeDialog";
        static public String showClipOpenWarning = "showClipOpenWarning";
    }

    static public final String trueString = "true";
    static public final String falseString = "false";

    /**
     * Constructor is unused.
     */
    private Options() {
    }

    static private String getOptionsPropertyString(
            String propertyName, String defaultValue) {
        // Try to load defaults list from the file.
        Properties optionsList = Use.loadPropertiesFile(true, optionsFileName);
        if (optionsList == null) {
            return defaultValue;
        }
        String propertyValue = optionsList.getProperty(propertyName);
        return (propertyValue != null) ? propertyValue : defaultValue;
    }

    static private void setOptionsPropertyString(
            String propertyName, String value) {
        // Try to load defaults list from the file.
        Properties optionsList = Use.loadPropertiesFile(
                true, optionsFileName);
        if (optionsList == null) {
            // Make a new one if we could not load the file.
            optionsList = new Properties();
        }
        optionsList.setProperty(propertyName, value);
        Use.savePropertiesFile(optionsList, "Options property list.",
                true, optionsFileName);
    }

    static private int getOptionsPropertyInt(
            String propertyName, int defaultValue, int minimum, int maximum) {
        String intString = getOptionsPropertyString(
                propertyName, ((Integer) defaultValue).toString());
        Integer integer = Use.getIntegerFromString(true, intString);
        if (integer != null) {
            return Use.clampInt(integer, minimum, maximum);
        } else {
            return Use.clampInt(defaultValue, minimum, maximum);
        }
    }

    static private void setOptionsPropertyInt(
            String propertyName, int value) {
        setOptionsPropertyString(propertyName, ((Integer) value).toString());
    }

    static private long getOptionsPropertyLong(
            String propertyName, long defaultValue) {
        String longString = getOptionsPropertyString(
                propertyName, ((Long) defaultValue).toString());
        Long longValue = Use.getLongFromString(longString);
        if (longValue != null) {
            return longValue;
        } else {
            return defaultValue;
        }
    }

    static private void setOptionsPropertyLong(
            String propertyName, long value) {
        setOptionsPropertyString(propertyName, ((Long) value).toString());
    }

    private static boolean getOptionsPropertyBoolean(
            String propertyName, boolean defaultValue) {
        String defaultString = (defaultValue ? trueString : falseString);
        String result = getOptionsPropertyString(propertyName, defaultString);
        return result.equalsIgnoreCase(trueString);
    }

    private static void setOptionsPropertyBoolean(
            String propertyName, boolean value) {
        String booleanString = (value ? trueString : falseString);
        setOptionsPropertyString(propertyName, booleanString);
    }

    static public int getDefaultPauseBetweenKeystrokes() {
        return getOptionsPropertyInt(Names.defaultPauseBetweenKeystrokes,
                0, 0, 200);
    }

    static public void setDefaultPauseBetweenKeystrokes(int pauseInMilliseconds) {
        pauseInMilliseconds = Use.clampInt(pauseInMilliseconds, 0, 200);
        setOptionsPropertyInt(
                Names.defaultPauseBetweenKeystrokes, pauseInMilliseconds);
    }

    static public boolean getOpenClips() {
        return getOptionsPropertyBoolean(Names.openClips, false);
    }

    static public void setOpenClips(boolean openClipsValue) {
        setOptionsPropertyBoolean(Names.openClips, openClipsValue);
    }

    static public boolean getShowWelcome() {
        return getOptionsPropertyBoolean(Names.showWelcomeDialog, true);
    }

    static public void setShowWelcome(boolean wantToShowWelcomeDialog) {
        setOptionsPropertyBoolean(
                Names.showWelcomeDialog, wantToShowWelcomeDialog);
    }

    static public boolean getShowClipOpenWarning() {
        return getOptionsPropertyBoolean(Names.showClipOpenWarning, true);
    }

    static public void setShowClipOpenWarning(boolean value) {
        setOptionsPropertyBoolean(Names.showClipOpenWarning, value);
    }

}
