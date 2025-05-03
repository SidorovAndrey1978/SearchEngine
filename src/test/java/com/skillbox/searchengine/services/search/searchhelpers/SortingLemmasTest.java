package com.skillbox.searchengine.services.search.searchhelpers;

import com.skillbox.searchengine.model.LemmaEntity;
import com.skillbox.searchengine.model.SiteEntity;
import com.skillbox.searchengine.repository.LemmaRepository;
import com.skillbox.searchengine.repository.PageRepository;
import com.skillbox.searchengine.utils.LemmaExtractor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SortingLemmasTest {
    @Mock
    private LemmaRepository lemmaRepository;

    @Mock
    private PageRepository pageRepository;

    @Mock
    private LemmaExtractor lemmaExtractor;

    @InjectMocks
    private SortingLemmas sortingLemmas;

    private SiteEntity fakeSite;

    @BeforeEach
    public void setup() {
        this.fakeSite = new SiteEntity();
        this.fakeSite.setId(1L);
    }

    @Test
    @DisplayName("Проверка правильного выделения лемм из запроса")
    void testProcessQuery_ReturnsCorrectLemmas() {
        // Arrange
        String query = "красивый автомобиль быстрая дорога";
        SortingLemmas sortingLemmas = new SortingLemmas(
                lemmaRepository, pageRepository, lemmaExtractor);


        when(lemmaExtractor.getLemmaSet(query))
                .thenReturn(Set.of("автомобиль", "красивый", "дорогой", "быстрый"));
        // Act
        List<String> result = sortingLemmas.processQuery(query);

        // Assert
        assertTrue(result.containsAll(
                Set.of("автомобиль", "красивый", "дорогой", "быстрый")));
    }

    @Test
    @DisplayName("Проверка поиска лемм в репозитории")
    void testFindLemmasInRepository_FindsExpectedLemmas() {
        // Arrange
        List<String> lemmas = Arrays.asList("автомобиль", "дорогой");
        Long siteId = fakeSite.getId();

        List<LemmaEntity> expectedLemmas = Arrays.asList(
                new LemmaEntity(1L, fakeSite, "автомобиль", 10,
                        new ArrayList<>()),
                new LemmaEntity(2L, fakeSite, "дорогой", 15,
                        new ArrayList<>())
        );

        when(lemmaRepository.findByLemmasAndSiteIds(lemmas, siteId))
                .thenReturn(expectedLemmas);

        // Act
        List<LemmaEntity> result = sortingLemmas.findLemmasInRepository(lemmas, siteId);

        // Assert
        assertEquals(expectedLemmas.size(), result.size());
        assertTrue(result.containsAll(expectedLemmas));
    }

    @Test
    @DisplayName("Проверка фильтрации и сортировки лемм")
    void testFilterAndSortLemmas_ReturnsSortedFilteredResults() {

        Long siteId = fakeSite.getId();

        // Arrange
        List<LemmaEntity> inputLemmas = Arrays.asList(
                new LemmaEntity(1L, fakeSite, "автомобиль", 10,
                        new ArrayList<>()),
                new LemmaEntity(2L, fakeSite, "дорогой", 15,
                        new ArrayList<>()),
                new LemmaEntity(3L, fakeSite, "красивый", 3,
                        new ArrayList<>())
        );

        when(pageRepository.countPageBySiteId(siteId)).thenReturn(100);

        // Act
        List<LemmaEntity> result = sortingLemmas.filterAndSortLemmas(inputLemmas, siteId);

        // Assert
        assertEquals(3, result.size());
        assertEquals("красивый", result.get(0).getLemma());
    }

    @Test
    @DisplayName("Проверка обработки пустого запроса")
    void testProcessQuery_EmptyQueryReturnsEmptyResult() {
        // Arrange
        String query = "";

        // Act
        List<String> result = sortingLemmas.processQuery(query);

        // Assert
        assertTrue(result.isEmpty());
    }
}