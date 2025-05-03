package com.skillbox.searchengine.services.indexation;

import com.skillbox.searchengine.exception.PageOutsideConfigured;

/**
 * Интерфейс для предоставления услуг индексации.
 * Определяет набор методов для запуска, остановки процесса индексации и
 * индексирования отдельных страниц.
 */
public interface IndexingService {

    /**
     * Запускает процесс полного переиндексирования всех известных сайтов.
     * Создает пул потоков и распределяет задачи по сайтам.
     *
     * @throws IllegalStateException если процесс индексации уже запущен.
     */
    void startIndexing();

    /**
     * Запускает процесс индексации отдельной страницы.
     *
     * @param page URL страницы, которую нужно проиндексировать.
     * @throws PageOutsideConfigured если страница не доступна
     *                               или не указана в списке разрешенных сайтов.
     */
    void startPageIndexing(String page);

    /**
     * Прерывает текущий процесс индексации.
     * Отмечает все незавершённые сайты как неуспешные и останавливает потоки.
     *
     * @throws IllegalStateException если процесс индексации не активен.
     */
    void stopIndexing();

}
