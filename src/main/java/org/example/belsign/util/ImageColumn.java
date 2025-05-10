package org.example.belsign.util;

public class ImageColumn {
    public static final int MAX_ADDITIONAL_IMAGES = 20;

    public static String getAdditionalColumnName(int index) {
        if (index < 1 || index > MAX_ADDITIONAL_IMAGES)
            throw new IllegalArgumentException("Index must be 1–20.");
        return "Additional_" + index;
    }

    public static boolean isAdditionalColumn(String label) {
        return label.startsWith("Additional");
    }

    public static String getDefaultColumnName(String label) {
        return "Image_" + label.toUpperCase();
    }
}