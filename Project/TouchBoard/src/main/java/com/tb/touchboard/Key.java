package com.tb.touchboard;

import java.awt.Color;
import javax.swing.*;
import javax.swing.border.*;

/**
 * Note: The the key title is stored in The JLabel superclass, using getText().
 */
public class Key
    extends JLabel implements Cloneable {

  Board parentBoard;
  String contents = "";
  int columnsWide = 1;
  int uses = 0;
  private Color backgroundColor = Constants.defaultKeyBackgroundColor;
  private Color textColor = Constants.defaultKeyTextColor;

  static final private EmptyBorder defaultKeyBorder
      = (EmptyBorder) BorderFactory.createEmptyBorder(1, 2, 1, 2);

  /* Constructor */
  public Key(Board aParentBoard) {
    // Save the parent board.
    parentBoard = aParentBoard;

    // Set up the key default values.
    setBackground(Constants.defaultKeyBackgroundColor);
    setForeground(Constants.defaultKeyTextColor);
    setBorder(defaultKeyBorder);
    setOpaque(true);
  }

  public void copyFrom(Key source, boolean preserveColors) {
    setText(source.getText());
    contents = source.contents;
    columnsWide = source.columnsWide;
    uses = source.uses;
    if (preserveColors) {
      setColors(source.getBackgroundColor(), source.getTextColor());
    }
  }

  public void setColors(Color aBackgroundColor, Color aTextColor) {
    backgroundColor = aBackgroundColor;
    textColor = aTextColor;
  }

  public Color getBackgroundColor() {
    return backgroundColor;
  }

  public Color getTextColor() {
    return textColor;
  }

  public void setBackgroundColor(Color color) {
    backgroundColor = color;
  }

  public void setTextColor(Color color) {
    textColor = color;
  }

  public Key clone() {
    Key newKey = new Key(parentBoard);
    newKey.copyFrom(this, true);
    return newKey;
  }

  boolean hasSmallTitle() {
    String title = getText();
    if (title.length() <= 1) {
      return true;
    }
    if (title.length() <= 3) {
      boolean LettersOrNumbersExist = false;
      for (int i = 0; i < title.length(); ++i) {
        if (Character.isLetterOrDigit(title.charAt(i))) {
          LettersOrNumbersExist = true;
        }
      }
      return !LettersOrNumbersExist;
    }
    return false;
  }
}
