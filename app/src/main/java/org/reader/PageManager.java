package org.reader;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public final class PageManager {

    private int currentChapter;
    private String currentPageDir;
    private int currentPageIdx;
    private BufferedImage currentPage;

    private final HashMap<Integer, ArrayList<String>> images;

    public PageManager(File dir) {
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

    public PageManager(HashMap<Integer, ArrayList<String>> images) {
        this.images = images;

        Object[] chapters = getAllChapters();
        currentChapter = (Integer) chapters[0];
        currentPageIdx = 0;
        currentPageDir = images.get(
                currentChapter
        ).get(
                currentPageIdx
        );
    }

    public void setPrevPage() {
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

    public void setNextPage() {
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

    public String getCurrentPageDir() {
        return currentPageDir;
    }

    public void setCurrentPageIdx(int currentPageIdx) {
        this.currentPageIdx = currentPageIdx;
    }

    public int getCurrentPageIdx() {
        return currentPageIdx;
    }

    public int getNumPagesCurrentChapter() {
        return images.get(
                currentChapter
        ).size();
    }

    public void setCurrentChapter(int currentChapter) {
        this.currentChapter = currentChapter;
        currentPageIdx = 0;
        currentPageDir = images.get(
                currentChapter
        ).get(
                currentPageIdx
        );
    }

    public int getCurrentChapter() {
        return currentChapter;
    }

    public Image getCurrentPage() {
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

    public Object[] getAllChapters() {
        ArrayList<Integer> allChapters = new ArrayList(images.keySet());
        allChapters.sort((int1, int2) -> {
            return int1.compareTo(int2);
        });
        return allChapters.toArray();
    }

}
