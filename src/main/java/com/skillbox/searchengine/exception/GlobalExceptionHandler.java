package com.skillbox.searchengine.exception;

import com.skillbox.searchengine.dto.indexing.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Общий обработчик исключений, предназначенный для централизованного управления реакцией приложения на ошибки.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Обрабатывает исключение, возникающее, когда по запросу не найдено результатов.
     *
     * @param e Объект исключения NoResultsFoundException.
     * @return Ответ с кодом NOT FOUND и описанием ошибки.
     */
    @ExceptionHandler(NoResultsFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handleNoResultsFoundException(
            NoResultsFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(e.getMessage()));
    }

    /**
     * Обрабатывает исключение, возникающее, когда поисковый запрос пуст.
     *
     * @param e Объект исключения EmptyQueryException.
     * @return Ответ с кодом BAD REQUEST и описанием ошибки.
     */
    @ExceptionHandler(EmptyQueryException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleEmptyQueryException(
            EmptyQueryException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(e.getMessage()));
    }

    /**
     * Обрабатывает исключение, возникающее, когда запрашиваемая страница находится за пределами разрешенных сайтов.
     *
     * @param e Объект исключения PageOutsideConfigured.
     * @return Ответ с кодом BAD REQUEST и описанием ошибки.
     */
    @ExceptionHandler(PageOutsideConfigured.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handlePageOutsideConfigured(
            PageOutsideConfigured e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(e.getMessage()));
    }
}
