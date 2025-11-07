package com.example.sportfieldbookingapp.utils;

import java.text.Normalizer;

public final class SearchUtils {
    private SearchUtils() {}

    public static String normalize(String input) {
        if (input == null) return "";
        String lower = input.toLowerCase();
        String normalized = Normalizer.normalize(lower, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return normalized;
    }

    public static boolean containsNormalized(String haystack, String needle) {
        return normalize(haystack).contains(normalize(needle));
    }
}


