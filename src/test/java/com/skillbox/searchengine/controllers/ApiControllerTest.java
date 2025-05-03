package com.skillbox.searchengine.controllers;

import com.skillbox.searchengine.dto.Response;
import com.skillbox.searchengine.dto.indexing.OkResponse;
import com.skillbox.searchengine.dto.statistics.DetailedStatisticsItem;
import com.skillbox.searchengine.dto.statistics.StatisticsData;
import com.skillbox.searchengine.dto.statistics.StatisticsResponse;
import com.skillbox.searchengine.dto.statistics.TotalStatistics;
import com.skillbox.searchengine.services.indexation.IndexingService;
import com.skillbox.searchengine.services.search.SearchService;
import com.skillbox.searchengine.services.statistics.StatisticsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApiControllerTest {

    @Mock
    private StatisticsService statisticsService;

    @Mock
    private IndexingService indexingService;

    @Mock
    private SearchService searchService;

    @InjectMocks
    private ApiController apiController;

    @Test
    @DisplayName("Проверка работы метода получения статистики")
    void testGetStatistics() {
        // Arrange
        TotalStatistics totalStats = new TotalStatistics(
                10, 20, 30, true);
        DetailedStatisticsItem item = new DetailedStatisticsItem(
                "https://example.com",
                "Example Site",
                "INDEXED",
                LocalDateTime.now(),
                " ",
                100,
                500
        );
        List<DetailedStatisticsItem> detailedItems = List.of(item);
        StatisticsData data = new StatisticsData(totalStats, detailedItems);
        boolean indexing = true;
        StatisticsResponse stats = new StatisticsResponse(indexing, data);
        when(statisticsService.getStatistics()).thenReturn(stats);

        // Act
        ResponseEntity<StatisticsResponse> response = apiController.statistics();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(stats, response.getBody());
    }

    @Test
    @DisplayName("Проверка старта индексации")
    void testStartIndexing() {
        // Arrange
        doNothing().when(indexingService).startIndexing();

        // Act
        ResponseEntity<Response> response = apiController.startIndexing();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertInstanceOf(OkResponse.class, response.getBody());
    }

    @Test
    @DisplayName("Проверка остановки индексации")
    void testStopIndexing() {
        // Arrange
        doNothing().when(indexingService).stopIndexing();

        // Act
        ResponseEntity<Response> response = apiController.stopIndexing();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertInstanceOf(OkResponse.class, response.getBody());
    }

    @Test
    @DisplayName("Проверка индексации страницы по заданному URL")
    void testIndexPage() {
        // Arrange
        String url = "https://example.com";
        doNothing().when(indexingService).startPageIndexing(url);

        // Act
        ResponseEntity<Response> response = apiController.indexPage(url);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertInstanceOf(OkResponse.class, response.getBody());
    }

}