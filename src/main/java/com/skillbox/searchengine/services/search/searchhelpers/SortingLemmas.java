package com.skillbox.searchengine.services.search.searchhelpers;

import com.skillbox.searchengine.model.LemmaEntity;
import com.skillbox.searchengine.repository.LemmaRepository;
import com.skillbox.searchengine.repository.PageRepository;
import com.skillbox.searchengine.utils.LemmaExtractor;
import com.skillbox.searchengine.utils.MessageLogs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Компонент, ответственный за фильтрацию и сортировку лемм.
 * Обеспечивает подготовку набора лемм для дальнейшего использования в поиске.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SortingLemmas {

    private final LemmaRepository lemmaRepository;
    private final PageRepository pageRepository;
    private final LemmaExtractor lemmaExtractor;
    private static final double THRESHOLD_PERCENT = 1.0;

    /**
     * Извлекает набор лемм из поискового запроса.
     *
     * @param query Входящий поисковый запрос.
     * @return Набор уникальных лемм, извлеченных из запроса.
     */
    public List<String> processQuery(String query) {
        return lemmaExtractor.getLemmaSet(query).stream().toList();
    }

    /**
     * Находит леммы в репозитории, соответствующие указанным условиям.
     *
     * @param lemmas Список лемм для поиска.
     * @param siteId Идентификатор сайта, для которого ищутся леммы.
     * @return Список лемм, найденных в репозитории.
     */
    public List<LemmaEntity> findLemmasInRepository(List<String> lemmas, Long siteId) {

        List<LemmaEntity> byLemmasAndSiteIds = lemmaRepository
                .findByLemmasAndSiteIds(lemmas, siteId);
        if (byLemmasAndSiteIds.isEmpty()) {
            log.warn(MessageLogs.NOT_FOUND_ON_REQUEST);
        }

        return byLemmasAndSiteIds;
    }

    /**
     * Фильтрует и сортирует леммы по частоте их встречаемости.
     *
     * @param lemmas Список лемм для фильтрации и сортировки.
     * @param siteId Идентификатор сайта, для которого применяются критерии фильтрации.
     * @return Отфильтрованный и отсортированный список лемм.
     */
    public List<LemmaEntity> filterAndSortLemmas(List<LemmaEntity> lemmas, Long siteId) {

        int totalPages = pageRepository.countPageBySiteId(siteId);

        int threshold = (int) (totalPages * THRESHOLD_PERCENT);

        return lemmas.stream()
                .filter(lemma -> lemma.getFrequency() <= threshold)
                .sorted(Comparator.comparingInt(LemmaEntity::getFrequency))
                .collect(Collectors.toList());
    }
}
