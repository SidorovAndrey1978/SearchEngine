package com.skillbox.searchengine.services.indexation.indexing;

import com.skillbox.searchengine.dto.indexing.DtoIndex;
import com.skillbox.searchengine.model.LemmaEntity;
import com.skillbox.searchengine.model.PageEntity;
import com.skillbox.searchengine.model.SiteEntity;
import com.skillbox.searchengine.repository.LemmaRepository;
import com.skillbox.searchengine.repository.PageRepository;
import com.skillbox.searchengine.utils.LemmaExtractor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Компонент, ответственный за построение индекса сайта.
 * <p>
 * Строит список индексов (частот лемм) для каждой страницы сайта,
 * основываясь на содержании страниц и собранных леммах.
 */
@Component
@RequiredArgsConstructor
public class IndexBuilder {

    /**
     * Статус кода, выше которого страницы считаются неисправными и
     * пропускаются при построении индекса.
     */
    public static final int STATUS_CODE = 400;
    private final PageRepository pageRepository;
    private final LemmaRepository lemmaRepository;
    private final LemmaExtractor lemmaExtractor;

    /**
     * Генерирует список индексов для заданного сайта.
     *
     * @param siteEntity Сайт, для которого строится индекс.
     * @return Список индексов (DTOIndex), содержащих информацию о
     * частотах лемм на страницах сайта.
     */
    public List<DtoIndex> buildIndexesForSite(SiteEntity siteEntity) {

        List<DtoIndex> dtoIndexList = new ArrayList<>();
        Long siteId = siteEntity.getId();
        List<PageEntity> pages = pageRepository.findBySiteId(siteId);
        List<LemmaEntity> lemmas = lemmaRepository.findBySiteId(siteId);

        for (PageEntity page : pages) {
            if (page.getCode() >= STATUS_CODE) {
                continue;
            }
            String cleanContent = lemmaExtractor.cleanHtml(page.getContent());
            Map<String, Integer> indexMap = lemmaExtractor.collectLemmas(cleanContent);

            for (LemmaEntity lemmaEntity : lemmas) {
                long lemmaId = lemmaEntity.getId();
                String lemmaWord = lemmaEntity.getLemma();
                if (!indexMap.containsKey(lemmaWord)) {
                    continue;
                }
                float rank = indexMap.get(lemmaWord);
                dtoIndexList.add(new DtoIndex(page.getId(), lemmaId, rank));
            }
        }
        return dtoIndexList;
    }
}
