package com.tb.touchboard;

import java.io.File;
import java.util.ArrayList;
import java.util.*;
import javax.swing.*;
import com.tb.tbUtilities.Use;

public class BoardsList {

    static public class OpenBoards extends JList {

        public DefaultListModel listModel = new DefaultListModel();
        public Vector<Integer> openBoardIndexes = new Vector<Integer>();

        public OpenBoards(boolean showClips) {
            String[] openBoards;
            int i;
            if (showClips) {
                openBoards = Main.getBoardManager().getOpenBoardNames();
                i = 0;
            } else {
                openBoards = Main.getBoardManager().getOpenBoardNamesExceptClips();
                i = Main.getBoardManager().getFirstBoardIndexAfterClips();
            }
            for (String boardName : openBoards) {
                listModel.addElement(boardName);
                openBoardIndexes.addElement(i);
                ++i;
            }
            setModel(listModel);
            setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        }

        public int getOpenBoardsIndex(int listIndex) {
            return openBoardIndexes.get(listIndex);
        }
    }

    static public class FileBoards extends JList {

        private ArrayList<String> fileNames = new ArrayList<String>();

        public FileBoards(boolean filterOpenBoards) {
            super(getFileBoardStrings(filterOpenBoards));
            fileNames = tempFileNames;
            tempFileNames = null;
            setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        }

        public String getFileName(int listIndex) {
            return fileNames.get(listIndex);
        }
    }

    /**
     * Use the factory methods to get board lists.
     */
    private BoardsList() {
    }

    static private ArrayList<String> tempFileNames = null;

    static private String[] getFileBoardStrings(boolean filterOpenBoards) {
        tempFileNames = new ArrayList<String>();
        ArrayList<String> tempBoardNames = new ArrayList<String>();
        File file;
        int max = Constants.maximumBoardFile;
        for (Integer i = 1; i <= max; ++i) {
            String fullPath = Use.workingDirectory + "/"
                    + i.toString() + Constants.boardExtension;
            file = new File(fullPath);
            if (file.exists()) {
                String fileName = i.toString() + Constants.boardExtension;
                Board board = Board.load(false, fullPath, fileName);
                if (board != null) {
                    if (filterOpenBoards && boardFileIsOpen(board.fileName)) {
                        continue;
                    }
                    tempFileNames.add(fileName);
                    tempBoardNames.add(
                            "(" + i.toString() + Constants.boardExtension
                            + ")  " + board.name());
                }
            }
        }
        return tempBoardNames.toArray(new String[]{});
    }

    static public BoardsList.FileBoards getFileBoardsList(
            boolean filterOpenBoards) {
        return new BoardsList.FileBoards(filterOpenBoards);
    }

    static public BoardsList.OpenBoards getOpenBoardsList(
            boolean showClips) {
        return new BoardsList.OpenBoards(showClips);
    }

    private static boolean boardFileIsOpen(String fileName) {
        boolean open = false;
        ArrayList<Board> openBoards = Main.getBoardManager().getOpenBoards();
        for (Board board : openBoards) {
            if (board.fileName.equalsIgnoreCase(fileName)) {
                open = true;
            }
        }
        return open;
    }

}
