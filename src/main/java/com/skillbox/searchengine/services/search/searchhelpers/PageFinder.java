package com.skillbox.searchengine.services.search.searchhelpers;

import com.skillbox.searchengine.model.IndexEntity;
import com.skillbox.searchengine.model.LemmaEntity;
import com.skillbox.searchengine.model.PageEntity;
import com.skillbox.searchengine.repository.IndexRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Компонент, ответственный за поиск страниц, содержащих указанные леммы.
 * Использует индексированную структуру данных для эффективного поиска страниц.
 */
@Component
@RequiredArgsConstructor
public class PageFinder {

    private final IndexRepository indexRepository;

    /**
     * Находит страницы, содержащие все указанные леммы.
     *
     * @param sortedLemmas Список упорядоченных лемм.
     * @return Список страниц, каждая из которых содержит все указанные леммы.
     */
    @Transactional
    public List<PageEntity> findPagesContainingAllLemmas(List<LemmaEntity> sortedLemmas) {

        Map<Long, List<PageEntity>> pageEntitiesMap = findPagesByLemmas(sortedLemmas);

        List<PageEntity> resultPages = new ArrayList<>();

        for (LemmaEntity lemma : sortedLemmas) {

            List<PageEntity> currentPages = pageEntitiesMap.get(lemma.getId());

            if (currentPages == null || currentPages.isEmpty()) {
                return new ArrayList<>();
            }

            if (resultPages.isEmpty()) {
                resultPages.addAll(currentPages);

            } else {
                List<PageEntity> intersection = intersect(resultPages, currentPages);
                if (!intersection.isEmpty()) {
                    resultPages = intersection;
                }
            }
        }
        return resultPages;
    }

    /**
     * Находит пересечения двух списков страниц.
     *
     * @param firstList  Первый список страниц.
     * @param secondList Второй список страниц.
     * @return Список страниц, присутствующих одновременно в обоих списках.
     */
    private List<PageEntity> intersect(List<PageEntity> firstList, List<PageEntity> secondList) {

        return firstList.stream()
                .filter(secondList::contains)
                .collect(Collectors.toList());
    }

    /**
     * Находит страницы, содержащие хотя бы одну из указанных лемм.
     *
     * @param lemmas Список лемм.
     * @return Карта, связывающая идентификатор леммы с соответствующими ей страницами.
     */
    public Map<Long, List<PageEntity>> findPagesByLemmas(List<LemmaEntity> lemmas) {

        List<IndexEntity> indexEntities = getIndexEntities(lemmas);

        return indexEntities.stream()
                .collect(Collectors.groupingBy(
                        indexEntity -> indexEntity.getLemmaId().getId(),
                        Collectors.mapping(IndexEntity::getPageId, Collectors.toList())
                ));
    }

    /**
     * Получает индексированные записи, соответствующие указанным леммам.
     *
     * @param lemmas Список лемм.
     * @return Список индексированных записей, соответствующих указанным леммам.
     */
    public List<IndexEntity> getIndexEntities(List<LemmaEntity> lemmas) {

        List<Long> lemmaIds = lemmas.stream()
                .map(LemmaEntity::getId)
                .toList();

        return indexRepository.findIndexWithForcedLoadingPages(lemmaIds);
    }
}