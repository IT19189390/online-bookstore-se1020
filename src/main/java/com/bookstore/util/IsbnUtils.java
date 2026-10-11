package com.bookstore.util;

import java.util.Locale;

public final class IsbnUtils {
    private IsbnUtils() {
    }

    public static String normalize(String isbn) {
        return isbn == null ? "" : isbn.replace("-", "").replace(" ", "").toUpperCase(Locale.ROOT);
    }

    public static boolean hasValidLength(String isbn) {
        int length = normalize(isbn).length();
        return length == 10 || length == 13;
    }
}
