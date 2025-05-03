package com.skillbox.searchengine.services.indexation.crawling;

import com.skillbox.searchengine.dto.indexing.DtoPage;
import com.skillbox.searchengine.utils.MessageLogs;
import com.skillbox.searchengine.utils.UrlHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.RecursiveAction;

/**
 * Задача параллельного веб-крэлинга (crawling),
 * ответственная за рекурсивный обход ссылок и сохранение собранных данных.
 * <p>
 * Класс реализует рекурсивную стратегию обхода ссылок,
 * позволяя параллельно сканировать веб-страницы.
 */
@RequiredArgsConstructor
@Slf4j
public class WebCrawlerTask extends RecursiveAction {

    /**
     * Задержка между последовательными запросами (в миллисекундах).
     */
    public static final int DELAY_BETWEEN_REQUESTS = 150;
    /**
     * Специальный статус ошибки, используемый при внутренних проблемах (код 500).
     */
    public static final int ERROR_CODE = 500;
    private final Set<String> visitedLinks;
    private final List<DtoPage> pages;
    private final UrlHelper urlHelper;
    private final String startUrl;

    /**
     * Основной метод выполнения задачи.
     * Осуществляет попытку соединения со страницей,
     * получение её контента и последующий обход внутренних ссылок.
     */
    @Override
    protected void compute() {
        visitedLinks.add(startUrl);
        try {
            Thread.sleep(DELAY_BETWEEN_REQUESTS);
            Optional<Document> optionalDoc = urlHelper.getConnection(startUrl);

            if (optionalDoc.isPresent()) {
                Document document = optionalDoc.get();
                Connection.Response response = document.connection().response();
                int statusCode = response.statusCode();
                String htmlContent = document.outerHtml();
                DtoPage successPage = new DtoPage(statusCode, startUrl, htmlContent);
                pages.add(successPage);
                crawlInternalLinks(document);
            } else {
                addErrorPage(startUrl);
            }
        } catch (InterruptedException e) {
            log.error(MessageLogs.LOG_INTERRUPT_EXCEPTION, startUrl, e);
            Thread.currentThread().interrupt();
            addErrorPage(startUrl);
        } catch (Exception e) {
            log.error(MessageLogs.LOG_HTML_PAGE_ERROR, startUrl, e);
            addErrorPage(startUrl);
        }
    }

    /**
     * Сохраняет информацию об ошибочном доступе к странице.
     *
     * @param url URL страницы, для которой зафиксирована ошибка.
     */
    private void addErrorPage(String url) {
        DtoPage errorPage = new DtoPage(ERROR_CODE, url, MessageLogs.INTERNAL_SERVER_ERROR);
        pages.add(errorPage);
    }

    /**
     * Рекурсивно посещает внутренние ссылки страницы и создаёт новые задачи
     * для обработки этих ссылок.
     *
     * @param document Документ текущей страницы.
     * @throws InterruptedException если поток прерван.
     */
    private void crawlInternalLinks(Document document) throws InterruptedException {
        Elements links = document.select("body").select("a");
        List<WebCrawlerTask> subTasks = new ArrayList<>();
        for (Element link : links) {
            String href = link.absUrl("href");
            if (!visitedLinks.contains(href) && LinkValidator.isCorrectLink(href)
                    && href.startsWith(link.baseUri())) {
                visitedLinks.add(href);
                WebCrawlerTask task = new WebCrawlerTask(
                        visitedLinks, pages, urlHelper, href);
                task.fork();
                subTasks.add(task);
            }
        }
        for (WebCrawlerTask task : subTasks) {
            task.join();
        }
    }
}

