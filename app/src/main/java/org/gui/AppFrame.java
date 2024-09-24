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
        setFuncs();
    }

    public void setNewChapter(int chapter) {
        navbar.setNewChapter(chapter);
    }

    public void setChapter(int chapter) {
        pageManager.setCurrentChapter(chapter);
    }

    public Integer getCurrentSelectedChapter() {
        return navbar.getCurrentSelectedChapter();
    }

    public void setCurrentImage() {
        imageDisplay.setCurrentImage(pageManager.getCurrentPage());
    }

    public void setTxtPage(int page, int totalPages) {
        navbar.setTxtPage(page, totalPages);
    }

    public void setCurrentChapterCbBox(int chapter, boolean triggerFunc) {
        navbar.setCurrentChapterCbBox(chapter, triggerFunc);
    }

    private void setFuncs() {
        navbar.setButtonPreviousFunc(() -> {
            pageManager.setPrevPage();
            setCurrentChapterCbBox(pageManager.getCurrentChapter(), false);
            setTxtPage(
                    pageManager.getCurrentPageIdx() + 1,
                    pageManager.getNumPagesCurrentChapter()
            );
            setCurrentImage();
        });

        navbar.setButtonNextFunc(() -> {
            pageManager.setNextPage();
            setCurrentChapterCbBox(pageManager.getCurrentChapter(), false);
            setTxtPage(
                    pageManager.getCurrentPageIdx() + 1,
                    pageManager.getNumPagesCurrentChapter()
            );
            setCurrentImage();
        });

        navbar.setCbChapterFunc(() -> {
            pageManager.setCurrentChapter(
                    getCurrentSelectedChapter()
            );
            pageManager.setCurrentPageIdx(0);
            setTxtPage(
                    pageManager.getCurrentPageIdx() + 1,
                    pageManager.getNumPagesCurrentChapter()
            );
            setCurrentImage();
        });
    }

}
