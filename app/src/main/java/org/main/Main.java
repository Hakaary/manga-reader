package org.main;

import java.io.File;

import org.gui.AppFrame;
import org.reader.PageManager;

public class Main {

    private static File dir;
    private static String currentDir = null;

    public static void main(String[] args) {

        if (args.length > 0 && args[0].equals(".")) {
            currentDir = System.getProperty("user.dir");
        }

        try {
            if (currentDir != null) {
                dir = new File(currentDir);
            } else {
                dir = new File(args[0]);
            }
        } catch (NullPointerException e) {
            System.err.println("Error opening route");
            System.exit(1);
        }

        PageManager.loadPageManager(dir);

        final AppFrame appFrame = new AppFrame();

        appFrame.setVisible(
                true);
        for (Object chapter
                : PageManager.getAllChapters()) {
            appFrame.setNewChapter((Integer) chapter);
        }

        appFrame.setCurrentChapterCbBox(
                (Integer) PageManager.getAllChapters()[0], true
        );
        appFrame.setTxtPage(
                PageManager.getCurrentPageIdx() + 1,
                PageManager.getNumPagesCurrentChapter()
        );
        appFrame.setCurrentImage();
    }
}
