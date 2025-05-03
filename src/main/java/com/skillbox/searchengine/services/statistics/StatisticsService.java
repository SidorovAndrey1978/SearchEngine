package com.skillbox.searchengine.services.statistics;

import com.skillbox.searchengine.dto.statistics.StatisticsResponse;

/**
 * Интерфейс для получения статистических данных по состоянию индекса.
 */
public interface StatisticsService {

    /**
     * Возвращает объект, содержащий статистику по состоянию индекса:
     * количество проиндексированных сайтов, страниц, лемм и другие показатели.
     *
     * @return объект StatisticsResponse с актуальной статистикой.
     */
    StatisticsResponse getStatistics();
}
