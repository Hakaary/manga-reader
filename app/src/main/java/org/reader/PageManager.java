package org.reader;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class PageManager {

    private int currentChapter;
    private String currentPageDir;
    private int currentPageIdx;
    private BufferedImage currentPage;

    private final HashMap<Integer, ArrayList<String>> images;

    public PageManager(File dir) {
        images = Reader.getChaptersPages(dir);

        ArrayList<Integer> keys = new ArrayList(images.keySet());
        keys.sort((int1, int2) -> {
            return int1.compareTo(int2);
        });
        currentChapter = keys.get(0);
        currentPageIdx = 0;
        currentPageDir = this.images.get(
                currentChapter
        ).get(
                currentPageIdx
        );
    }

    public PageManager(HashMap<Integer, ArrayList<String>> images) {
        this.images = images;

        ArrayList<Integer> keys = new ArrayList(images.keySet());
        keys.sort((int1, int2) -> {
            return int1.compareTo(int2);
        });
        currentChapter = keys.get(0);
        currentPageIdx = 0;
        currentPageDir = this.images.get(
                currentChapter
        ).get(
                currentPageIdx
        );
    }

    public void setPrevPage() {
        try {
            currentPageIdx--;
            currentPageDir = this.images.get(currentChapter).get(
                    currentPageIdx
            );
        } catch (IndexOutOfBoundsException e) {
            currentChapter--;
            try {
                currentPageIdx = this.images.get(
                        this.currentChapter
                ).size() - 1;
            } catch (NullPointerException d) {
                currentChapter++;
                currentPageIdx = 0;
            }
            currentPageDir = this.images.get(
                    currentChapter
            ).get(
                    currentPageIdx
            );
        }
    }

    public void setNextPage() {
        try {
            currentPageIdx++;
            currentPageDir = this.images.get(currentChapter).get(
                    currentPageIdx
            );
        } catch (IndexOutOfBoundsException e) {
            currentChapter++;
            currentPageIdx = 0;
            try {
                currentPageDir = images.get(
                        currentChapter
                ).get(
                        currentPageIdx
                );
            } catch (NullPointerException d) {
                currentChapter--;
                currentPageIdx = images.get(
                        currentChapter
                ).size() - 1;
                currentPageDir = images.get(
                        currentChapter
                ).get(
                        currentPageIdx
                );
            }
        }
    }

    public String getCurrentPageDir() {
        return currentPageDir;
    }

    public int getCurrentPageIdx() {
        return currentPageIdx;
    }

    public int getNumPagesCurrentChapter() {
        return this.images.get(
                currentChapter
        ).size();
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

}
