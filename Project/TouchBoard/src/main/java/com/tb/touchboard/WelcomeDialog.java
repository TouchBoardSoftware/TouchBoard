package com.tb.touchboard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.text.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;
import com.tb.tbUtilities.BrowserLaunch;
import com.tb.tbUtilities.Frames;

public class WelcomeDialog
        extends JDialog {

   public WelcomeDialog() {
      initComponents();

      // Set up buttons
      okButton.setVisible(true);

      //  Set alignment to be centered for all paragraphs
      StyledDocument doc = editorPane.getStyledDocument();
      MutableAttributeSet standard = new SimpleAttributeSet();
      StyleConstants.setAlignment(standard, StyleConstants.ALIGN_CENTER);
      doc.setParagraphAttributes(0, 0, standard, true);

      setUndecorated(true);
      setAlwaysOnTop(true);
      setResizable(true);
      setModal(true);

      setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
      addWindowListener(new WindowAdapter() {

         public void windowClosing(WindowEvent e) {
            Frames.message("Please click one of the buttons below\n"
                    + "to close this window.");
         }
      });

      // Set our welcome dialog text.
      insertEditorText();

      // Add actions to components.
      addActions();

      // Adjust the window size.
      pack();
      Frames.adjustWindowToScreenRatio(this);
      Frames.centerWindow(this, null);
   }

   private void initComponents() {
      // JFormDesigner - Component initialization - DO NOT MODIFY//GEN-BEGIN:initComponents
      panel = new JPanel();
      scrollPane = new JScrollPane();
      editorPane = new JTextPane();
      checkBoxPanel = new JPanel();
      doNotShowCheckBox = new JCheckBox();
      okButton = new JButton();
      CellConstraints cc = new CellConstraints();

      //======== this ========
      setTitle("Welcome");
      Container contentPane = getContentPane();
      contentPane.setLayout(new FormLayout(
              "default:grow",
              "fill:default:grow"));

      //======== panel ========
      {
         panel.setBackground(new Color(0, 204, 255));
         panel.setLayout(new FormLayout(
                 new ColumnSpec[]{
                    FormFactory.UNRELATED_GAP_COLSPEC,
                    new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW),
                    FormFactory.PREF_COLSPEC,
                    FormFactory.RELATED_GAP_COLSPEC,
                    FormFactory.PREF_COLSPEC,
                    FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                    FormFactory.PREF_COLSPEC,
                    new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW),
                    FormFactory.UNRELATED_GAP_COLSPEC
                 },
                 new RowSpec[]{
                    FormFactory.UNRELATED_GAP_ROWSPEC,
                    new RowSpec(RowSpec.FILL, Sizes.PREFERRED, FormSpec.DEFAULT_GROW),
                    FormFactory.RELATED_GAP_ROWSPEC,
                    FormFactory.DEFAULT_ROWSPEC,
                    FormFactory.RELATED_GAP_ROWSPEC,
                    FormFactory.DEFAULT_ROWSPEC,
                    FormFactory.UNRELATED_GAP_ROWSPEC
                 }));
         ((FormLayout) panel.getLayout()).setColumnGroups(new int[][]{{3, 5, 7}});

         //======== scrollPane ========
         {

            //---- editorPane ----
            editorPane.setEditable(false);
            scrollPane.setViewportView(editorPane);
         }
         panel.add(scrollPane, cc.xywh(2, 2, 7, 1));

         //======== checkBoxPanel ========
         {
            checkBoxPanel.setBackground(new Color(0, 204, 255));
            checkBoxPanel.setLayout(new FormLayout(
                    new ColumnSpec[]{
                       new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW),
                       FormFactory.RELATED_GAP_COLSPEC,
                       new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW)
                    },
                    RowSpec.decodeSpecs("default")));

            //---- doNotShowCheckBox ----
            doNotShowCheckBox.setText("Do not show this again");
            doNotShowCheckBox.setBackground(new Color(0, 204, 255));
            checkBoxPanel.add(doNotShowCheckBox, cc.xy(3, 1));
         }
         panel.add(checkBoxPanel, cc.xywh(2, 4, 7, 1));

         //---- okButton ----
         okButton.setText("OK");
         okButton.setVisible(false);
         panel.add(okButton, cc.xy(5, 6));
      }
      contentPane.add(panel, cc.xy(1, 1));
      // JFormDesigner - End of component initialization//GEN-END:initComponents
   }
   // JFormDesigner - Variables declaration - DO NOT MODIFY//GEN-BEGIN:variables
   private JPanel panel;
   private JScrollPane scrollPane;
   private JTextPane editorPane;
   private JPanel checkBoxPanel;
   private JCheckBox doNotShowCheckBox;
   private JButton okButton;
   // JFormDesigner - End of variables declaration//GEN-END:variables

   /**
    * Set our welcome dialog text.
    */
   private void insertEditorText() {
      editorPane.setText(
              "\nWelcome to TouchBoard! \n\n"
              + "TouchBoard allows you to type text without touching your \n"
              + "keyboard or clicking your mouse. Controlling TouchBoard is pretty \n"
              + "easy, but it is done in a very different way from other programs. \n"
              + "If you are a new user, you will want to read the introduction section \n"
              + "of the help file first. TouchBoard needs some explanation to make sense. \n\n"
              + "Enjoy! :) \n");
   }

   /**
    * Add actions to components.
    */
   private void addActions() {

      // okButton
      okButton.addActionListener(new ActionListener() {

         public void actionPerformed(ActionEvent e) {
            okButtonAction();
         }
      });

   }

   private void okButtonAction() {
      exitWelcomeDialog();
      handleCheckBoxes();
   }

   private void handleCheckBoxes() {
      BrowserLaunch.openURL(Constants.helpURL);
      if (doNotShowCheckBox.isSelected()) {
         Options.setShowWelcome(false);
      }
   }

   private void exitWelcomeDialog() {
      dispose();
   }
}
