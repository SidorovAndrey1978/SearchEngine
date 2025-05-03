package com.skillbox.searchengine.utils;

public class MessageLogs {

    public static final String LOG_MALFORMED_URL_EXCEPTION = "Неверный URL: {}";
    public static final String LOG_CONNECTION_ERROR = "Ошибка подключения к URL: {}";
    public static final String LOG_INTERRUPT_EXCEPTION = "Процесс был прерван: {}";
    public static final String THE_FLOW_WAS_INTERRUPTED = "Поток был прерван.";
    public static final String LOG_HTML_PAGE_ERROR = "Ошибка при обработке страницы: {}";
    public static final String INTERNAL_SERVER_ERROR = "INTERNAL SERVER ERROR.";
    public static final String INDEXING_IS_ALREADY_RUNNING = "Индексация уже запущена.";
    public static final String INDEXING_IS_NOT_RUNNING = "Индексация не запущена.";
    public static final String INDEXATION_STOPPED_BY_USER = "Индексация остановлена пользователем.";
    public static final String ENTER_THE_ADDRESS = "Введите адрес.";
    public static final String NOT_FOUND_ON_REQUEST = "По вашему запросу ничего не найдено. Попробуйте изменить запрос.";
    public static final String LOG_FINISH_AllSITES_SEARCH = "=> Поиск по сайтам завершен.";
    public static final String LOG_START_AllSITES_SEARCH = "=> Запускаем поиск по всем сайтам для запроса: {}";
    public static final String LOG_START_OneSITE_SEARCH = "=> Запускаем поиск по сайту {} для запроса: {}";
    public static final String LOG_FINISH_OneSITES_SEARCH = "=> Поиск по сайту завершен.";
    public static final String PAGE_OUTSIDE_CONFIGURED_SITES = "Данная страница находится " +
            "за пределами сайтов, указанных в конфигурационном файле.";
    public static final String EMPTY_REQUEST = "Задан пустой поисковый запрос.";
}
