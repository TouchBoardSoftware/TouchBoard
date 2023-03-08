package com.tb.tbUtilities;

import java.awt.*;
import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.*;

public class Use {

    // Current working directory string.
    static final public String workingDirectory
        = System.getProperty("user.dir");

    /**
     * Constructor.
     */
    public Use() {
    }

    /**
     * Returns an ImageIcon, or null if the path was invalid. The path should be relative to the
     * project source directory.
     */
    static public ImageIcon createImageIcon(String path) {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        InputStream stream = classLoader.getResourceAsStream(path);
        if (stream == null) {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
        try {
            ImageIcon icon = new ImageIcon(ImageIO.read(stream));
            return icon;
        } catch (IOException ex) {
            System.err.println("IOException with file: " + path);
            return null;
        }
//        java.net.URL imgURL = getClass().getResource(path);
//        if (imgURL != null) {
//            return new ImageIcon(imgURL, description);
//        } else {
//            System.err.println("Couldn't find file: " + path);
//            return null;
//        }
    }

    static public String safeSubstring(int beginIndex, int afterEndIndex,
        String string) {

        // Ensure that indexes are at least zero.
        if (beginIndex < 0) {
            beginIndex = 0;
        }
        if (afterEndIndex < 0) {
            afterEndIndex = 0;
        }

        // Ensure that end index is no bigger than the string.
        if (afterEndIndex > string.length()) {
            afterEndIndex = string.length();
        }

        // Ensure that begin index is no bigger than end index.
        if (beginIndex > afterEndIndex) {
            beginIndex = afterEndIndex;
        }

        // Return safe substring.
        return string.substring(beginIndex, afterEndIndex);
    }

    /**
     * Get an Integer value from a String. This returns null if the digits are not a parsable
     * integer. This also return null if the number is negative and negativeAllowed is false.
     */
    static public Integer getIntegerFromString(
        boolean negativeAllowed, String digits) {
        int number;
        try {
            number = Integer.parseInt(digits);
        } catch (NumberFormatException e) {
            return null;
        }
        if (negativeAllowed == false && number < 0) {
            return null;
        }
        return number;
    }

    public static boolean toggleBoolean(boolean variable) {
        return !variable;
    }

    public static boolean not(boolean variable) {
        return !variable;
    }

    // Returns a string from a color in the format: "(100,9,255)"
    public static String colorToString(Color color) {
        String string = "(";
        string += color.getRed();
        string += ",";
        string += color.getGreen();
        string += ",";
        string += color.getBlue();
        string += ")";
        return string;
    }

    // Returns a color from a string.
    // Expects a string in the format "(100,9,255)"
    // It will strip any spaces.
    // Returns null if there is any failure.
    public static Color colorFromString(String string) {
        string = string.replace(" ", "");
        string = string.replace("(", "");
        string = string.replace(")", "");
        String[] rgbStrings = string.split(",");
        ArrayList<Integer> rgbNumbers = new ArrayList<Integer>();
        for (int i = 0; i < rgbStrings.length; ++i) {
            rgbNumbers.add(getIntegerFromString(
                false, rgbStrings[i]));
        }
        if (rgbNumbers.size() < 3) {
            return null;
        }
        for (Integer integer : rgbNumbers) {
            if (integer == null) {
                return null;
            }
            if (integer > 255) {
                return null;
            }
        }
        return new Color(
            rgbNumbers.get(0), rgbNumbers.get(1), rgbNumbers.get(2));
    }

    public static int clampInt(int variable, int minimum, int maximum) {
        if (variable < minimum) {
            variable = minimum;
        }
        if (variable > maximum) {
            variable = maximum;
        }
        return variable;
    }

    public static boolean isSystemWindows() {
        String operatingSystem = System.getProperty("os.name");
        return operatingSystem.toLowerCase().contains("windows");
    }

    public static boolean isSystemMac() {
        String operatingSystem = System.getProperty("os.name");
        return operatingSystem.toLowerCase().contains("mac");
    }

    public static void mySleep(int milliseconds) {
        if (milliseconds <= 0) {
            return;
        }
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    public static String prependCWD(String suffix) {
        String prefix = workingDirectory;
        if (!(suffix.startsWith("/") || suffix.startsWith("\\"))) {
            prefix += "/";
        }
        return prefix + suffix;
    }

    public static String prependFileProtocol(String suffix) {
        String prefix = "file://";
        if (!(suffix.startsWith("/") || suffix.startsWith("\\"))) {
            prefix += "/";
        }
        return prefix + suffix;
    }

    public static String[] makeOSXOpenURLCommand(String url) {
        url = url.replace(" ", "%20");
        url = url.replace("~", "%7E");
        url = url.replace("#", "%23");
        return new String[]{"open", url};
    }

    /**
     * Get a FileInputStream. Returns the stream if successful, or null.
     */
    static public FileInputStream getFileInputStream(
        boolean prependCWD, String restOfPath) {
        if (restOfPath == null) {
            System.err.println("Couldn't find file. Path string was null.");
            return null;
        }
        String fullPath = "";
        if (prependCWD) {
            fullPath += workingDirectory;
            if (!(restOfPath.startsWith("/") || restOfPath.startsWith("\\"))) {
                fullPath += "/";
            }
        }
        fullPath += restOfPath;
        try {
            FileInputStream stream = new FileInputStream(fullPath);
            if (stream != null) {
                return stream;
            } else {
                System.err.println("Couldn't find file: " + restOfPath);
                return null;
            }
        } catch (Exception e) {
            System.err.println("Couldn't find file: " + restOfPath);
            return null;
        }
    }

    /**
     * Get a FileOutputStream. Returns the stream if successful, or null.
     */
    static public FileOutputStream getFileOutputStream(
        boolean prependCWD, String restOfPath) {
        if (restOfPath == null) {
            System.err.println("Couldn't find or make file. Path string was null.");
            return null;
        }
        String fullPath = "";
        if (prependCWD) {
            fullPath += workingDirectory;
            if (!(restOfPath.startsWith("/") || restOfPath.startsWith("\\"))) {
                fullPath += "/";
            }
        }
        fullPath += restOfPath;
        try {
            FileOutputStream stream = new FileOutputStream(fullPath);
            if (stream != null) {
                return stream;
            } else {
                System.err.println("Couldn't find or make file: " + restOfPath);
                return null;
            }
        } catch (Exception e) {
            System.err.println("Couldn't find or make file: " + restOfPath);
            return null;
        }
    }

    /**
     * Load a properties instance from a file. Returns the properties if successful, or null.
     */
    public static Properties loadPropertiesFile(
        boolean prependCWD, String restOfPath) {
        FileInputStream stream = getFileInputStream(prependCWD, restOfPath);
        if (stream == null) {
            return null;
        }
        Properties properties = new Properties();
        try {
            properties.load(stream);
            stream.close();
        } catch (Exception e) {
            return null;
        }
        return properties;
    }

    public static boolean savePropertiesFile(
        Properties properties, String comment,
        boolean prependCWD, String restOfPath) {
        FileOutputStream stream = getFileOutputStream(prependCWD, restOfPath);
        if (stream == null) {
            return false;
        }
        try {
            properties.store(stream, comment);
            stream.close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Get an Long value from a String. This returns null if the digits are not a parsable integer.
     */
    public static Long getLongFromString(String longString) {
        long number;
        try {
            number = Long.parseLong(longString);
        } catch (NumberFormatException e) {
            return null;
        }
        return number;
    }
}
