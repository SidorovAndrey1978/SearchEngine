package com.skillbox.searchengine.integration;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class SearchServiceIntegrationTest {

    @LocalServerPort
    private int port;

    private final RestTemplate restTemplate = new RestTemplate();

    @Test
    @DisplayName("Проверка поиска по запросу")
    void testSearchExistingQuery() {
        //Arrange
        String query = "метро";
        String site = "https://www.playback.ru";
        int offset = 0;
        int limit = 10;

        //Act
        ResponseEntity<Object> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/api/search?" +
                        "query=" + query +
                        "&site=" + site +
                        "&offset=" + offset +
                        "&limit=" + limit,
                Object.class
        );

        //Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

}
