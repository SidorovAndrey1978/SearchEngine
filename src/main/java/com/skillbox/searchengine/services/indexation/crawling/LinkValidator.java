package com.skillbox.searchengine.services.indexation.crawling;

/**
 * Валидатор ссылок, позволяющий проверить корректность URL и установить,
 * является ли ссылка изображением или документом.
 */

public class LinkValidator {

    /**
     * Регулярное выражение для проверки правильности формата URL.
     */
    public static final String REGEX_CORRECT_URL =
            "^https?://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";

    /**
     * Проверяет, является ли ссылка корректной, не изображением и не документом.
     *
     * @param link Проверяемая ссылка.
     * @return True, если ссылка валидная и не является изображением или документом.
     */
    public static boolean isCorrectLink(String link) {
        return isCorrectUrl(link) && !isImageLink(link) && !isDocumentLink(link);
    }

    /**
     * Проверяет, соответствует ли ссылка общему формату URL.
     *
     * @param link Проверяемая ссылка.
     * @return True, если ссылка корректна по формату URL.
     */
    private static boolean isCorrectUrl(String link) {
        return link.matches(REGEX_CORRECT_URL);
    }

    /**
     * Проверяет, является ли ссылка ссылкой на изображение.
     *
     * @param link Проверяемая ссылка.
     * @return True, если ссылка ведет на изображение (форматы JPEG, PNG, GIF и т.д.).
     */
    private static boolean isImageLink(String link) {
        return link.contains(".jpg")
                || link.contains(".jpeg")
                || link.contains(".png")
                || link.contains(".gif")
                || link.contains(".webp");
    }

    /**
     * Проверяет, является ли ссылка ссылкой на документ.
     *
     * @param link Проверяемая ссылка.
     * @return True, если ссылка ведет на документ (PDF, Word, Excel и т.д.).
     */
    private static boolean isDocumentLink(String link) {
        return link.contains(".pdf")
                || link.contains(".eps")
                || link.contains(".xlsx")
                || link.contains(".doc")
                || link.contains(".pptx")
                || link.contains(".docx")
                || link.contains("#")
                || link.contains("?_ga");
    }
}
