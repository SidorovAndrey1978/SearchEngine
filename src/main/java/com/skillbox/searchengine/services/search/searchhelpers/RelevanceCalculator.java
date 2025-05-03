package com.skillbox.searchengine.services.search.searchhelpers;

import com.skillbox.searchengine.model.IndexEntity;
import com.skillbox.searchengine.model.PageEntity;
import com.skillbox.searchengine.repository.IndexRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Компонент, ответственный за расчет релевантности страниц.
 * Рассчитывает релевантность страниц на основании рангов лемм и
 * нормализацией полученных значений.
 */
@Component
@RequiredArgsConstructor
public class RelevanceCalculator {

    private final IndexRepository indexRepository;

    /**
     * Рассчитывает релевантность страниц, учитывая сумму рангов лемм,
     * содержащихся на страницах.
     *
     * @param pagesWithLemmas Список страниц, каждая из которых содержит леммы.
     * @return Отсортированная карта, содержащая страницы и их относительные
     * коэффициенты релевантности.
     */
    public LinkedHashMap<PageEntity, Float> calculateRelevance(List<PageEntity> pagesWithLemmas) {

        List<Long> pageIds = pagesWithLemmas.stream()
                .map(PageEntity::getId)
                .toList();

        List<IndexEntity> result = indexRepository.findIndexByPageIds(pageIds);

        Map<Long, List<IndexEntity>> indexRecordsByPage = result.stream()
                .collect(Collectors.groupingBy(
                        indexEntity -> indexEntity.getPageId().getId()));

        LinkedHashMap<PageEntity, Float> relevanceMap = new LinkedHashMap<>();
        for (PageEntity page : pagesWithLemmas) {

            List<IndexEntity> recordsForPage = indexRecordsByPage.get(page.getId());

            float sumOfRanks = (float) recordsForPage.stream()
                    .mapToDouble(IndexEntity::getRank)
                    .sum();
            relevanceMap.put(page, sumOfRanks);
        }

        float maxRelevance = relevanceMap.values().stream()
                .max(Float::compareTo)
                .orElse(0.0f);
        relevanceMap.replaceAll((page, relevance) -> relevance / maxRelevance);

        return relevanceMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));
    }
}