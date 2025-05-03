package com.skillbox.searchengine.controllers;

import com.skillbox.searchengine.dto.Response;
import com.skillbox.searchengine.dto.indexing.ErrorResponse;
import com.skillbox.searchengine.dto.indexing.OkResponse;
import com.skillbox.searchengine.dto.statistics.StatisticsResponse;
import com.skillbox.searchengine.services.indexation.IndexingService;
import com.skillbox.searchengine.services.search.SearchService;
import com.skillbox.searchengine.services.statistics.StatisticsService;
import com.skillbox.searchengine.utils.MessageLogs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ApiController {

    private final StatisticsService statisticsService;
    private final IndexingService indexingService;
    private final SearchService searchService;

    /**
     * Предоставляет статистику по индексе страниц.
     *
     * @return JSON-представление статистики по страницам и индексу.
     */
    @GetMapping("/statistics")
    public ResponseEntity<StatisticsResponse> statistics() {
        return ResponseEntity.ok(statisticsService.getStatistics());
    }

    /**
     * Запускает процесс полного переиндексирования всех страниц.
     *
     * @return OK, если процесс запущен успешно, или BAD REQUEST, если возникла ошибка.
     */
    @GetMapping("/startIndexing")
    public ResponseEntity<Response> startIndexing() {
        try {
            indexingService.startIndexing();
            return ResponseEntity.ok(new OkResponse());
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Останавливает текущий процесс переиндексирования.
     *
     * @return OK, если остановка прошла успешно, или BAD REQUEST, если возникли проблемы.
     */
    @GetMapping("/stopIndexing")
    public ResponseEntity<Response> stopIndexing() {
        try {
            indexingService.stopIndexing();
            return ResponseEntity.ok(new OkResponse());
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Индексирует отдельную страницу по указанному URL.
     *
     * @param url URL страницы для индексации.
     * @return OK, если страница успешно проиндексирована, или BAD REQUEST, если URL пуст или произошла ошибка.
     */
    @PostMapping("/indexPage")
    public ResponseEntity<Response> indexPage(@RequestParam(name = "url") String url) {
        if (url.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(MessageLogs.ENTER_THE_ADDRESS));
        }
        try {
            indexingService.startPageIndexing(url);
            return ResponseEntity.ok(new OkResponse());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Выполняет поиск по запросу с возможностью фильтрации по сайту и пагинацией.
     *
     * @param query   Поисковый запрос.
     * @param site    Адрес сайта, на котором искать (необязательный параметр).
     * @param offset  Смещение для пагинации (по умолчанию 0).
     * @param limit   Количество записей на странице (по умолчанию 10).
     * @return Результаты поиска или сообщение об ошибке.
     */
    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam(required = false) String query,
                                         @RequestParam(required = false) String site,
                                         @RequestParam(defaultValue = "0") int offset,
                                         @RequestParam(defaultValue = "10") int limit) {

        return searchService.search(query, site, offset, limit);
    }
}
