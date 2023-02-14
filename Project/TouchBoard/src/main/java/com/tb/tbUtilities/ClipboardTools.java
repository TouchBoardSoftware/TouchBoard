package com.tb.tbUtilities;

import java.awt.Toolkit;
import java.awt.datatransfer.*;

public class ClipboardTools {

    public static String getClipboard() {
        try {
            // get the system clipboard.
            Clipboard systemClipboard = Toolkit.getDefaultToolkit()
                    .getSystemClipboard();
            // get the contents on the clipboard in a transferable object.
            Transferable clipboardContents = systemClipboard.getContents(null);
            // check if the clipboard is empty.
            if (clipboardContents == null) {
                return null;
            }
            // see if DataFlavor.stringFlavor is supported.
            if (clipboardContents.isDataFlavorSupported(
                    DataFlavor.stringFlavor)) {
                // return text content
                String text = (String) clipboardContents.getTransferData(
                        DataFlavor.stringFlavor);
                return text;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public static void writeToClipboard(String writeMe) {
        // get the system clipboard
        Clipboard systemClipboard
                = Toolkit
                        .getDefaultToolkit()
                        .getSystemClipboard();
        // set the textual content on the clipboard to our
        // Transferable object
        // we use the
        Transferable transferableText
                = new StringSelection(writeMe);
        systemClipboard.setContents(
                transferableText,
                null);
    }

}
