package org.gui;

import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;

class AppNavbar {

    private final JPanel navbarPanel;

    private final JComboBox cbChapter;
    private boolean runCbChapterFunc;

    private final JLabel txtChapter;
    private final JLabel txtPage;

    private final JButton buttonClose;
    private final JButton buttonPrevious;
    private final JButton buttonNext;

    public AppNavbar() {
        navbarPanel = new JPanel();
        navbarPanel.setBackground(Color.GRAY);

        txtChapter = new JLabel("Chapter:");
        cbChapter = new JComboBox();
        runCbChapterFunc = true;
        txtPage = new JLabel();

        buttonClose = new JButton("Close");
        buttonPrevious = new JButton("< Prev");
        buttonNext = new JButton("Next >");

        navbarPanel.add(txtChapter);
        navbarPanel.add(cbChapter);
        navbarPanel.add(buttonClose);
        navbarPanel.add(buttonPrevious);
        navbarPanel.add(buttonNext);
        navbarPanel.add(txtPage);
    }

    public JPanel getNavbarPanel() {
        return navbarPanel;
    }

    public void setNewChapter(int chapter) {
        cbChapter.addItem(chapter);
    }

    public void setTxtPage(int page, int totalPages) {
        txtPage.setText(page + "/" + totalPages);
    }

    public void setCurrentChapterCbBox(int chapter, boolean triggerFunc) {
        runCbChapterFunc = triggerFunc;
        cbChapter.setSelectedItem(chapter);
        runCbChapterFunc = true;
    }

    public Integer getCurrentSelectedChapter() {
        return (Integer) cbChapter.getItemAt(cbChapter.getSelectedIndex());
    }

    public void setCbChapterFunc(Runnable function) {
        cbChapter.addItemListener(
                _ -> {
                    if (runCbChapterFunc) {
                        function.run();
                    }
                }
        );
    }

    public void setButtonCloseFunc(Runnable function) {
        buttonClose.addActionListener(
                _ -> function.run()
        );
    }

    public void setButtonPreviousFunc(Runnable function) {
        buttonPrevious.addActionListener(
                _ -> function.run()
        );
    }

    public void setButtonNextFunc(Runnable function) {
        buttonNext.addActionListener(
                _ -> function.run()
        );
    }

}
