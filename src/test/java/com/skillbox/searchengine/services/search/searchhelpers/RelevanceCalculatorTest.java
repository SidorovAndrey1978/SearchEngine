package com.skillbox.searchengine.services.search.searchhelpers;

import com.skillbox.searchengine.model.IndexEntity;
import com.skillbox.searchengine.model.LemmaEntity;
import com.skillbox.searchengine.model.PageEntity;
import com.skillbox.searchengine.repository.IndexRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RelevanceCalculatorTest {
    @Mock
    private IndexRepository indexRepository;

    @InjectMocks
    private RelevanceCalculator relevanceCalculator;

    private PageEntity fakePage1;
    private PageEntity fakePage2;
    private IndexEntity fakeIndex1;
    private IndexEntity fakeIndex2;
    private IndexEntity fakeIndex3;


    @BeforeEach
    public void setup() {
        fakePage1 = new PageEntity();
        fakePage1.setId(1L);
        fakePage2 = new PageEntity();
        fakePage2.setId(2L);

        LemmaEntity fakeLemma = new LemmaEntity();
        fakeLemma.setId(1L);

        fakeIndex1 = new IndexEntity(1L, fakePage1, fakeLemma, 2.0f);
        fakeIndex2 = new IndexEntity(2L, fakePage1, fakeLemma, 3.0f);
        fakeIndex3 = new IndexEntity(3L, fakePage2, fakeLemma, 8.0f);
    }

    @Test
    @DisplayName("""
            Правильность расчета релевантности 
            при наличии одной леммы и двух страниц
            """)
    public void testCalculateRelevance_BasicScenario() {
        // Arrange
        List<PageEntity> pages = List.of(fakePage1, fakePage2);
        List<IndexEntity> indices = List.of(fakeIndex1, fakeIndex3);

        when(indexRepository.findIndexByPageIds(any())).thenReturn(indices);

        // Act
        LinkedHashMap<PageEntity, Float> result =
                relevanceCalculator.calculateRelevance(pages);

        // Assert
        assertEquals(2, result.size());
        assertEquals(0.25f, result.get(fakePage1), 0.01);
        assertEquals(1.0f, result.get(fakePage2), 0.01);
    }

    @Test
    @DisplayName("""
            Правильность расчета релевантности 
            при наличии двух лемм на двух страниц
            """)
    void testCalculateRelevance_MultipleLemmas() {
        // Arrange
        List<PageEntity> pages = List.of(fakePage1, fakePage2);
        List<IndexEntity> indices = List.of(fakeIndex1, fakeIndex2, fakeIndex3);

        when(indexRepository.findIndexByPageIds(any())).thenReturn(indices);

        // Act
        LinkedHashMap<PageEntity, Float> result =
                relevanceCalculator.calculateRelevance(pages);

        // Assert
        assertEquals(2, result.size());
        assertEquals(0.625f, result.get(fakePage1), 0.01);
        assertEquals(1.0f, result.get(fakePage2), 0.01);
    }
}