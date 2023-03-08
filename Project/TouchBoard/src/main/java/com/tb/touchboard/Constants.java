package com.tb.touchboard;

import com.tb.tbUtilities.Use;
import java.awt.*;
import javax.swing.*;

public class Constants {

    static final public boolean debug = false;

    // Project Version
    static final public String version = "7-01";

    // URLs
    static String projectSite_ForHuman
        = "github.com/TouchBoardSoftware/TouchBoard";
    static String projectSite_ForBrowser
        = "https://github.com/TouchBoardSoftware/TouchBoard";
    static public String helpURL
        = Use.prependFileProtocol(Use.prependCWD("help/index.html"));

    // The "About" message text. 
    // This text uses html format, but it should not include these tags: <html><body>.
    static public String aboutMessageText
        = "<B>"
        + "TouchBoard<BR>Open Source, Freeware.<BR><BR>"
        + "Version: " + Constants.version + "<BR>"
        + "License: The MIT License.<BR>"
        + "Website: <a href=\"" + Constants.projectSite_ForBrowser
        + "\">" + Constants.projectSite_ForHuman + "</a><BR>"
        + "</B>";

    // Filenames
    static public String clipsBoardFileName = "clips";
    static public String defaultBoardsFileName = "defaults";
    static final public String boardExtension = ".brd";

    // Constant values
    static public int keyRows = 21;
    static public int keyHeight = 20;
    static public int borderThickness = 2;
    static public int defaultKeyWidth = 75;
    static public int firstDefaultsFilePropertyNumber = 1;

    // Minimums and Maximums
    static public int maxColumns = 10;
    static public int maxBoardNameSize = 10;
    static public int maxKeyTitleSize = 100;
    static public int minimumKeyWidth = 10;
    static public int maximumKeyWidth = 500;
    static public int maximumBoardFile = 1000;

    static public Color disabledBackgroundColor
        = new Color(220, 220, 220); // Lighter Gray
    static public Color disabledTextColor
        = new Color(60, 60, 60);   // Very Dark Gray

    // Colors
    static public Color defaultBoardBorderColor
        = new Color(160, 160, 160); // Gray
    static public Color defaultQuickShowBackgroundColor
        = new Color(166, 202, 240); // Light Blue
    static public Color defaultKeyBackgroundColor
        = new Color(192, 220, 192);   // Money Green
    static public Color defaultKeyTextColor
        = new Color(0, 0, 0);       // Black

    // Images
    static final public ImageIcon lockOpen
        = Use.createImageIcon("lockopen.gif");
    static final public ImageIcon lockClosed
        = Use.createImageIcon("lockclosed.gif");
    static final public ImageIcon collapseAllArrow
        = Use.createImageIcon("collapseallarrow.gif");
    static final public ImageIcon minimizeIcon
        = Use.createImageIcon("minimizeicon.gif");
    static final public ImageIcon editButtonIcon
        = Use.createImageIcon("editbuttonicon.gif");
    static final public ImageIcon editButtonIconGreen
        = Use.createImageIcon("editbuttonicongreen.gif");
    static final public ImageIcon capslockOffIcon
        = Use.createImageIcon("capslockofficon.gif");
    static final public ImageIcon capslockOnIcon
        = Use.createImageIcon("capslockonicon.gif");
    static final public ImageIcon applicationIcon
        = Use.createImageIcon("applicationicon.png");

    // Fonts
    static final public Font defaultLabelFont
        = new Font("SansSerif", Font.PLAIN, 11);
    static final public Font biggerLabelFont
        = new Font("SansSerif", Font.PLAIN, 12);

    // Calculated constants.
    static public int getBoardHeight() {
        return borderThickness + (keyRows * (keyHeight + borderThickness));
    }

}
