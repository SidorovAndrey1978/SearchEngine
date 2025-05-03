package com.skillbox.searchengine.services.indexation.crawling;

import com.skillbox.searchengine.config.Site;
import com.skillbox.searchengine.config.SitesList;
import com.skillbox.searchengine.repository.PageRepository;
import com.skillbox.searchengine.repository.SiteRepository;
import com.skillbox.searchengine.utils.MessageLogs;
import com.skillbox.searchengine.utils.UrlHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class PageIndexerTest {

    @Mock
    private SiteRepository siteRepository;
    @Mock
    private PageRepository pageRepository;
    @Mock
    private SitesList sitesList;
    @Mock
    private UrlHelper urlHelper;
    @InjectMocks
    private PageIndexer pageIndexer;

    @Test
    void testStart_WhenInvalidURLProvided_ShouldThrowIllegalArgumentException() {

        // Arrange
        String invalidPage = "https://invalid-url.ru";
        List<Site> mockSites = new ArrayList<>();
        mockSites.add(new Site("example.com", "Пример сайта"));
        Mockito.lenient().when(sitesList.getSites()).thenReturn(mockSites);
        Mockito.lenient().when(urlHelper.getHostFromPage(invalidPage)).thenReturn("invalid-url.ru");

        // Act
        Throwable exception = assertThrows(
                IllegalArgumentException.class,
                () -> pageIndexer.start(invalidPage),
                "Ожидается исключение IllegalArgumentException"
        );
        // Assert
        assertEquals(MessageLogs.PAGE_OUTSIDE_CONFIGURED_SITES, exception.getMessage());
    }


}