package org.gui;

import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;

class AppNavbar {

    private final JPanel navbarPanel;

    private final JLabel txtChapter;
    private final JLabel txtPage;

    private final JButton buttonClose;
    private final JButton buttonPrevious;
    private final JButton buttonNext;

    public AppNavbar() {
        navbarPanel = new JPanel();
        navbarPanel.setBackground(Color.GRAY);

        txtChapter = new JLabel("Chapter: ");
        txtPage = new JLabel();

        buttonClose = new JButton("Close");
        buttonPrevious = new JButton("< Prev");
        buttonNext = new JButton("Next >");

        navbarPanel.add(txtChapter);
        navbarPanel.add(buttonClose);
        navbarPanel.add(buttonPrevious);
        navbarPanel.add(buttonNext);
        navbarPanel.add(txtPage);
    }

    public JPanel getNavbarPanel() {
        return navbarPanel;
    }

    public void setTxtChapter(int chapter) {
        txtChapter.setText("Chapter: " + chapter);
    }

    public void setTxtPage(int page, int totalPages) {
        txtPage.setText(page + "/" + totalPages);
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
