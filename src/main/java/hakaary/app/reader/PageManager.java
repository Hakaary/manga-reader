package hakaary.app.reader;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.imageio.ImageIO;

public final class PageManager {

    private static int currentChapter;
    private static String currentPageDir;
    private static int currentPageIdx;

    private static HashMap<Integer, ArrayList<String>> images;

    // LRU image cache
    private static final int CACHE_SIZE = 8;
    private static final Map<String, BufferedImage> imageCache =
        Collections.synchronizedMap(new LinkedHashMap<String, BufferedImage>(CACHE_SIZE + 1, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, BufferedImage> eldest) {
                return size() > CACHE_SIZE;
            }
        });

    // Background prefetch executor: 2 daemon threads at minimum priority
    private static final ExecutorService prefetchExecutor =
        Executors.newFixedThreadPool(2, r -> {
            Thread t = new Thread(r, "prefetch");
            t.setDaemon(true);
            t.setPriority(Thread.MIN_PRIORITY);
            return t;
        });

    // Tracks paths currently being fetched to avoid duplicate submissions
    private static final Map<String, Future<?>> pendingLoads = new ConcurrentHashMap<>();

    public static void loadPageManager(File dir) {
        images = Reader.getChaptersPages(dir);

        Object[] chapters = getAllChapters();
        currentChapter = (Integer) chapters[0];
        currentPageIdx = 0;
        currentPageDir = images.get(currentChapter).get(currentPageIdx);
    }

    public static void setPrevPage() {
        currentPageIdx--;

        if (currentPageIdx >= 0) {
            currentPageDir = images.get(currentChapter).get(currentPageIdx);
            return;
        }

        currentChapter--;

        if (images.get(currentChapter) == null) {
            currentChapter++;
            currentPageIdx = 0;
            return;
        }

        currentPageIdx = images.get(currentChapter).size() - 1;
        currentPageDir = images.get(currentChapter).get(currentPageIdx);
    }

    public static void setNextPage() {
        currentPageIdx++;

        if (currentPageIdx < images.get(currentChapter).size()) {
            currentPageDir = images.get(currentChapter).get(currentPageIdx);
            return;
        }

        currentChapter++;

        if (images.get(currentChapter) == null) {
            currentChapter--;
            currentPageIdx = images.get(currentChapter).size() - 1;
            return;
        }

        currentPageIdx = 0;
        currentPageDir = images.get(currentChapter).get(currentPageIdx);
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
        return images.get(currentChapter).size();
    }

    public static void setCurrentChapter(int currentChapt) {
        currentChapter = currentChapt;
        currentPageIdx = 0;
        currentPageDir = images.get(currentChapter).get(currentPageIdx);
    }

    public static int getCurrentChapter() {
        return currentChapter;
    }

    public static void jumpToPage(int pageIdx) {
        currentPageIdx = pageIdx;
        currentPageDir = images.get(currentChapter).get(currentPageIdx);
    }

    public static Image getCurrentPage() {
        String path = getCurrentPageDir();

        BufferedImage cached = imageCache.get(path);
        if (cached != null) {
            return cached;
        }

        // Cache miss: load synchronously as fallback
        try {
            BufferedImage img = ImageIO.read(new File(path));
            if (img == null) throw new IOException();
            imageCache.put(path, img);
            return img;
        } catch (IOException e) {
            System.err.println("Image " + path + " could not be loaded");
            System.exit(1);
            return null;
        }
    }

    // Queues background loading of the 5 next and 3 previous pages
    public static void prefetchAdjacent() {
        for (String path : getAdjacentPaths(5, 3)) {
            if (!imageCache.containsKey(path) && !pendingLoads.containsKey(path)) {
                Future<?> f = prefetchExecutor.submit(() -> {
                    try {
                        BufferedImage img = ImageIO.read(new File(path));
                        if (img != null) {
                            imageCache.put(path, img);
                        }
                    } catch (IOException e) {
                        // Ignore prefetch failures silently
                    } finally {
                        pendingLoads.remove(path);
                    }
                });
                pendingLoads.put(path, f);
            }
        }
    }

    private static List<String> getAdjacentPaths(int nextCount, int prevCount) {
        List<String> result = new ArrayList<>();

        int ch = currentChapter;
        int idx = currentPageIdx;

        for (int i = 0; i < nextCount; i++) {
            idx++;
            if (idx >= images.get(ch).size()) {
                ch++;
                if (images.get(ch) == null) break;
                idx = 0;
            }
            result.add(images.get(ch).get(idx));
        }

        ch = currentChapter;
        idx = currentPageIdx;

        for (int i = 0; i < prevCount; i++) {
            idx--;
            if (idx < 0) {
                ch--;
                if (images.get(ch) == null) break;
                idx = images.get(ch).size() - 1;
            }
            result.add(images.get(ch).get(idx));
        }

        return result;
    }

    public static Object[] getAllChapters() {
        ArrayList<Integer> allChapters = new ArrayList<Integer>(images.keySet());
        allChapters.sort((int1, int2) -> int1.compareTo(int2));
        return allChapters.toArray();
    }

}
