package org.reader;

import java.io.File;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Reader {

    private static HashMap<Integer, ArrayList<String>> images;

    public static HashMap getChaptersPages(File dir) {
        Reader.images = new HashMap<>();
        searchImagesInDirectory(dir);
        orderImages();
        return images;
    }

    private static void searchImagesInDirectory(File dir) {
        if (dir.exists() && dir.isDirectory()) {

            File[] filesList = dir.listFiles();

            if (filesList != null) {

                for (File file : filesList) {
                    if (file.isDirectory()) {
                        searchImagesInDirectory(file);
                    } else if (isImageFile(file)) {
                        addChapterImage(dir, file);
                    }
                }
            }
        }
    }

    private static void addChapterImage(File dir, File file) {
        String[] dirSplit = dir.getAbsolutePath().split("\\\\");
        int dirSplitNumber;
        try {
            dirSplitNumber = Integer.parseInt(dirSplit[dirSplit.length - 1]);
        } catch (NumberFormatException e) {
            System.err.println(
                    "Format not recognized."
                            .concat("Use the manga rust downloader")
                            .concat("to numerate chapters as expected.")
            );
            return;
        }
        if (images.containsKey(dirSplitNumber)) {
            images.get(dirSplitNumber).add(file.getAbsolutePath());
            return;
        }
        ArrayList chapterImagesList = new ArrayList();
        chapterImagesList.add(file.getAbsolutePath());
        images.put(dirSplitNumber, chapterImagesList);
    }

    private static void orderImages() {
        Pattern pattern = Pattern.compile("(\\d+)\\.(jpg|jpeg|png|gif|bmp|tiff)");

        for (ArrayList<String> imagesList : images.values()) {
            imagesList.sort((str1, str2) -> {
                Matcher matcher1 = pattern.matcher(str1);
                Matcher matcher2 = pattern.matcher(str2);

                boolean match1 = matcher1.find();
                boolean match2 = matcher2.find();

                if (!match1 || !match2) {
                    return 0;
                }

                Integer num1 = Integer.valueOf(matcher1.group(1));
                Integer num2 = Integer.valueOf(matcher2.group(1));

                return num1.compareTo(num2);
            });
        }
    }

    private static boolean isImageFile(File file) {
        String[] imageExtensions = {"jpg", "jpeg", "png", "gif", "bmp", "tiff"};

        String fileName = file.getName().toLowerCase();

        for (String ext : imageExtensions) {
            if (fileName.endsWith("." + ext)) {
                return true;
            }
        }

        return false;
    }

}
