package com.skillbox.searchengine.services.search.searchhelpers;

import com.skillbox.searchengine.model.IndexEntity;
import com.skillbox.searchengine.model.LemmaEntity;
import com.skillbox.searchengine.model.PageEntity;
import com.skillbox.searchengine.model.SiteEntity;
import com.skillbox.searchengine.repository.IndexRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PageFinderTest {

    @Mock
    private IndexRepository indexRepository;

    @InjectMocks
    private PageFinder pageFinder;

    private PageEntity fakePage;
    private LemmaEntity fakeLemmaAuto;
    private LemmaEntity fakeLemmaRoad;

    @BeforeEach
    public void setup() {
        SiteEntity fakeSite = new SiteEntity();
        fakeSite.setId(1L);

        fakePage = new PageEntity(1L, fakeSite, "/auto.html",
                200, "content", new ArrayList<>());

        fakeLemmaAuto = new LemmaEntity(1L, fakeSite, "автомобиль",
                10, new ArrayList<>());
        fakeLemmaRoad = new LemmaEntity(2L, fakeSite, "дорога",
                15, new ArrayList<>());
    }


    @Test
    @DisplayName("""
            Поиск страницы по единственной лемме 
            должен возвращать соответствующую страницу
            """)
    void testFindPagesWithSingleLemmaShouldReturnCorrectPage() {
        // Arrange
        List<LemmaEntity> lemmas = List.of(fakeLemmaAuto);
        List<PageEntity> expectedPages = List.of(fakePage);


        when(indexRepository.findIndexWithForcedLoadingPages(any())).thenReturn(
                List.of(new IndexEntity(1L, fakePage, fakeLemmaAuto, 1f)));

        // Act
        List<PageEntity> result = pageFinder.findPagesContainingAllLemmas(lemmas);

        // Assert
        assertEquals(expectedPages, result);
    }

    @Test
    @DisplayName("""
            Поиск страницы по двум леммам 
            должен возвращать соответствующую страницу
            """)
    void testFindPagesWithTwoLemmasShouldReturnCorrectPage() {
        // Arrange
        List<LemmaEntity> lemmas = List.of(fakeLemmaAuto, fakeLemmaRoad);
        List<PageEntity> expectedPages = List.of(fakePage);

        when(indexRepository.findIndexWithForcedLoadingPages(any())).thenReturn(
                List.of(new IndexEntity(1L, fakePage, fakeLemmaAuto, 1f),
                        new IndexEntity(2L, fakePage, fakeLemmaRoad, 1f)
                ));

        // Act
        List<PageEntity> result = pageFinder.findPagesContainingAllLemmas(lemmas);

        // Assert
        assertEquals(expectedPages, result);
    }

    @Test
    @DisplayName("""
            Поиск страницы по леммам, которых нет на странице, 
            должен возвращать пустой результат
            """)
    void testFindPagesWithoutAnyLemmasReturnsEmptyResult() {
        // Arrange
        List<LemmaEntity> lemmas = List.of(fakeLemmaAuto, fakeLemmaRoad);

        List<PageEntity> expectedPages = List.of();

        when(indexRepository.findIndexWithForcedLoadingPages(any())).thenReturn(List.of());

        // Act
        List<PageEntity> result = pageFinder.findPagesContainingAllLemmas(lemmas);

        // Assert
        assertEquals(expectedPages, result);
    }
}