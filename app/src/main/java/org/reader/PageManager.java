package org.reader;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public final class PageManager {

    private static int currentChapter;
    private static String currentPageDir;
    private static int currentPageIdx;
    private static BufferedImage currentPage;

    private static HashMap<Integer, ArrayList<String>> images;

    public static void loadPageManager(File dir) {
        images = Reader.getChaptersPages(dir);

        Object[] chapters = getAllChapters();
        currentChapter = (Integer) chapters[0];
        currentPageIdx = 0;
        currentPageDir = images.get(
                currentChapter
        ).get(
                currentPageIdx
        );
    }

    public static void setPrevPage() {
        currentPageIdx--;

        // Obvious case, page exists
        if (currentPageIdx >= 0) {
            currentPageDir = images.get(currentChapter).get(
                    currentPageIdx
            );
            return;
        }

        currentChapter--;

        // First downloaded chapter
        if (images.get(currentChapter) == null) {
            currentChapter++;
            currentPageIdx = 0;
            return;
        }

        // Last page of the previous chapter
        currentPageIdx = images.get(currentChapter).size() - 1;
        currentPageDir = images.get(currentChapter).get(
                currentPageIdx
        );
    }

    public static void setNextPage() {
        currentPageIdx++;

        // Obvious case, page exists
        if (currentPageIdx < images.get(currentChapter).size()) {
            currentPageDir = images.get(currentChapter).get(
                    currentPageIdx
            );
            return;
        }

        currentChapter++;

        // Last downloaded chapter
        if (images.get(currentChapter) == null) {
            currentChapter--;
            currentPageIdx = images.get(currentChapter).size() - 1;
            return;
        }

        // First page of the next chapter
        currentPageIdx = 0;
        currentPageDir = images.get(currentChapter).get(
                currentPageIdx
        );
    }

    public static String getCurrentPageDir() {
        return currentPageDir;
    }

    public static void setCurrentPageIdx(int currentPageIndex) {
        currentPageIdx = currentPageIndex;
    }

    public static int getCurrentPageIdx() {
        return currentPageIdx;
    }

    public static int getNumPagesCurrentChapter() {
        return images.get(
                currentChapter
        ).size();
    }

    public static void setCurrentChapter(int currentChapt) {
        currentChapter = currentChapt;
        currentPageIdx = 0;
        currentPageDir = images.get(
                currentChapter
        ).get(
                currentPageIdx
        );
    }

    public static int getCurrentChapter() {
        return currentChapter;
    }

    public static Image getCurrentPage() {
        String imageRoute = "";

        try {
            imageRoute = getCurrentPageDir();
            currentPage = ImageIO.read(new File(imageRoute));
            if (currentPage == null) {
                throw new IOException();
            }
        } catch (IOException e) {
            System.err.println("Image " + imageRoute + " could not be loaded");
            System.exit(1);
        }
        return currentPage;
    }

    public static Object[] getAllChapters() {
        ArrayList<Integer> allChapters = new ArrayList(images.keySet());
        allChapters.sort((int1, int2) -> {
            return int1.compareTo(int2);
        });
        return allChapters.toArray();
    }

}
