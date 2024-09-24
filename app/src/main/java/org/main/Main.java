package org.main;

import java.io.File;

import org.gui.AppFrame;
import org.reader.PageManager;

public class Main {

    public static void main(String[] args) {

        File dir = new File("C:\\Users\\luisr\\Documents\\One Piece");

        final PageManager pageManager = new PageManager(dir);
        final AppFrame appFrame = new AppFrame(pageManager);

        appFrame.setVisible(true);
        for (Object chapter : pageManager.getAllChapters()) {
            appFrame.setNewChapter((Integer) chapter);
        }
        appFrame.setCurrentChapter(
            (Integer) pageManager.getAllChapters()[0], true
        );
        appFrame.setTxtPage(
                pageManager.getCurrentPageIdx() + 1,
                pageManager.getNumPagesCurrentChapter()
        );
        appFrame.setCurrentImage();
    }
}
