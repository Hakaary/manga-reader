package org.gui;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import org.reader.PageManager;

public class AppFrame extends JFrame {

    private final AppImageDisplay imageDisplay;
    private final AppNavbar navbar;
    private final PageManager pageManager;

    public AppFrame(PageManager pageManager) {

        super();
        setSize(400, 500);
        setLayout(new BorderLayout());

        imageDisplay = new AppImageDisplay();
        navbar = new AppNavbar();

        // Close button
        navbar.setButtonCloseFunc(() -> {
            dispose();
            System.exit(0);
        });

        add(imageDisplay.getImageDisplay(), BorderLayout.CENTER);
        add(navbar.getNavbarPanel(), BorderLayout.SOUTH);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.pageManager = pageManager;
        setButtonFuncs();
    }

    public void setCurrentImage() {
        imageDisplay.setCurrentImage(pageManager.getCurrentPage());
    }

    public void setTxtChapter(int chapter) {
        navbar.setTxtChapter(chapter);
    }

    public void setTxtPage(int page, int totalPages) {
        navbar.setTxtPage(page, totalPages);
    }

    private void setButtonFuncs() {
        navbar.setButtonPreviousFunc(() -> {
            pageManager.setPrevPage();
            setCurrentImage();
            setTxtChapter(pageManager.getCurrentChapter());
            setTxtPage(
                pageManager.getCurrentPageIdx() + 1,
                pageManager.getNumPagesCurrentChapter()
            );
        });

        navbar.setButtonNextFunc(() -> {
            pageManager.setNextPage();
            setCurrentImage();
            setTxtChapter(pageManager.getCurrentChapter());
            setTxtPage(
                pageManager.getCurrentPageIdx() + 1,
                pageManager.getNumPagesCurrentChapter()
            );
        });
    }

}
