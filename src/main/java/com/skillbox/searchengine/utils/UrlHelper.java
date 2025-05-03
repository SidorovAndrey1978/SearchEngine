package com.skillbox.searchengine.utils;

import com.skillbox.searchengine.config.ConnectionToSite;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

/**
 * Вспомогательный класс для работы с URL и соединений к сайтам.
 * Предоставляет методы для получения HTML-документов,
 * извлечения путей и хостов из URL, а также работы с HTML-контентом.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class UrlHelper {

    private final ConnectionToSite connectionToSite;

    /**
     * Открывает соединение к указанному URL и возвращает HTML-документ.
     *
     * @param url URL сайта.
     * @return Опциональный объект Document, содержащий HTML-документ,
     * или empty, если произошел IO-ошибка.
     */
    public Optional<Document> getConnection(String url) {
        try {
            return Optional.of(Jsoup.connect(url)
                    .userAgent(connectionToSite.getUserAgent())
                    .referrer(connectionToSite.getReferrer())
                    .get());
        } catch (IOException e) {
            log.error(MessageLogs.LOG_CONNECTION_ERROR, url, e);
            return Optional.empty();
        }
    }

    /**
     * Извлекает путь (часть URL после хоста) из заданного URL.
     *
     * @param page URL страницы.
     * @return Путь страницы или пустая строка, если URL неверный.
     */
    public String getPathToPage(String page) {
        try {
            URL url = new URL(page);
            return url.getPath();
        } catch (MalformedURLException e) {
            log.error(MessageLogs.LOG_MALFORMED_URL_EXCEPTION, page);
            return "";
        }
    }

    /**
     * Извлекает имя хоста из заданного URL.
     *
     * @param page URL страницы.
     * @return Хост страницы или пустая строка, если URL неверный.
     */
    public String getHostFromPage(String page) {
        try {
            URL url = new URL(page);
            return url.getHost();
        } catch (MalformedURLException e) {
            log.error(MessageLogs.LOG_MALFORMED_URL_EXCEPTION, page);
            return "";
        }
    }

    /**
     * Извлекает заголовок (title) из HTML-контента.
     *
     * @param content HTML-контент страницы.
     * @return Заголовок страницы.
     */
    public String getTitleFromHtml(String content) {
        Document doc = Jsoup.parse(content);
        return doc.title();
    }

}
