package com.skillbox.searchengine.services.search;

import com.skillbox.searchengine.dto.search.SearchData;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * Интерфейс для осуществления поиска по сайтам и выдачи результатов.
 * Предоставляет методы для поиска по различным параметрам и источникам.
 */
public interface SearchService {

    /**
     * Выполняет поиск по сайтам с учётом возможных фильтров и ограничений.
     *
     * @param query   Поисковый запрос.
     * @param siteUrl URL сайта для поиска (необязательный параметр).
     * @param offset  Смещение для пагинации (начиная с 0).
     * @param limit   Максимальное количество результатов на странице.
     * @return Объект ResponseEntity, содержащий результаты поиска.
     */
    ResponseEntity<Object> search(String query, String siteUrl, int offset, int limit);

    /**
     * Выполняет поиск по всем известным сайтам.
     *
     * @param query  Поисковый запрос.
     * @return Список объектов SearchData, содержащих результаты поиска.
     */
    List<SearchData> searchAllSites(String query);

    /**
     * Выполняет поиск по единственному сайту.
     *
     * @param query  Поисковый запрос.
     * @param url    URL сайта для поиска.
     * @return Список объектов SearchData, содержащих результаты поиска.
     */
    List<SearchData> oneSiteSearch(String query, String url);

}
