package com.skillbox.searchengine.exception;

import com.skillbox.searchengine.dto.indexing.ErrorResponse;
import com.skillbox.searchengine.utils.MessageLogs;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler handler;
    
    @Test
    @DisplayName("Проверка обработки исключения NoResultsFoundException")
    void testHandleNoResultsFoundException() {
        // Arrange
        NoResultsFoundException exception = new NoResultsFoundException();

        // Act
        ResponseEntity<ErrorResponse> response = handler.handleNoResultsFoundException(exception);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(MessageLogs.NOT_FOUND_ON_REQUEST, response.getBody().getError());
    }

    @Test
    @DisplayName("Проверка обработки исключения EmptyQueryException")
    void testHandleEmptyQueryException() {
        // Arrange
        EmptyQueryException exception = new EmptyQueryException();

        // Act
        ResponseEntity<ErrorResponse> response = handler.handleEmptyQueryException(exception);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(MessageLogs.EMPTY_REQUEST, response.getBody().getError());
    }

    @Test
    @DisplayName("Проверка обработки исключения PageOutsideConfigured")
    void testHandlePageOutsideConfigured() {
        // Arrange
        PageOutsideConfigured exception = new PageOutsideConfigured();

        // Act
        ResponseEntity<ErrorResponse> response = handler.handlePageOutsideConfigured(exception);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(MessageLogs.PAGE_OUTSIDE_CONFIGURED_SITES, response.getBody().getError());
    }
}