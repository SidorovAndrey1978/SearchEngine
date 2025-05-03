package com.skillbox.searchengine.services.indexation.lemmatization;

import com.skillbox.searchengine.dto.indexing.DtoLemma;
import com.skillbox.searchengine.model.PageEntity;
import com.skillbox.searchengine.model.SiteEntity;
import com.skillbox.searchengine.repository.PageRepository;
import com.skillbox.searchengine.utils.LemmaExtractor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Коллектор лемм, ответственный за сбор и агрегирование лемм с
 * нескольких страниц сайта.
 * <p>
 * Получает список страниц, нормализует их содержимое и
 * суммирует частоту повторения каждой леммы.
 */
@Component
@RequiredArgsConstructor
public class LemmasCollector {

    private final PageRepository pageRepository;
    private final LemmaExtractor lemmaExtractor;

    /**
     * Собирает и агрегирует леммы для заданного сайта.
     *
     * @param siteEntity Сайт, для которого собирают леммы.
     * @return Список объектов DtoLemma, содержащих информацию о леммах и их частотах.
     */
    public List<DtoLemma> extractLemmasForSite(SiteEntity siteEntity) {

        Long siteId = siteEntity.getId();
        List<PageEntity> pageEntities = pageRepository.findBySiteId(siteId);
        HashMap<String, Integer> lemmasPerSite = new HashMap<>();

        for (PageEntity page : pageEntities) {

            String clearContent = lemmaExtractor.cleanHtml(page.getContent());
            Map<String, Integer> lemmasOnPage = lemmaExtractor.collectLemmas(clearContent);

            for (Map.Entry<String, Integer> entry : lemmasOnPage.entrySet()) {
                String lemma = entry.getKey();
                lemmasPerSite.merge(lemma, 1, Integer::sum);
            }
        }
        return lemmasPerSite.entrySet().stream()
                .map(entry -> new DtoLemma(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }
}
