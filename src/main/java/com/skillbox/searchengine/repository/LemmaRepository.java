package com.skillbox.searchengine.repository;

import com.skillbox.searchengine.model.LemmaEntity;
import com.skillbox.searchengine.model.SiteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Репозиторий для работы с таблицей индексов (LemmaEntity).
 */
@Repository
public interface LemmaRepository extends JpaRepository<LemmaEntity, Long> {

    /**
     * Найти все леммы, принадлежащие сайту с заданным идентификатором.
     *
     * @param siteId Идентификатор сайта.
     * @return Список лемм, относящихся к данному сайту.
     */
    @Query(value = """
            SELECT l.* FROM lemma l 
            WHERE l.site_id = :siteId""",
            nativeQuery = true)
    List<LemmaEntity> findBySiteId(@Param("siteId") Long siteId);

    /**
     * Подсчитать количество лемм, принадлежащих сайту.
     *
     * @param siteEntity Энтити сайта, для которого считаем леммы.
     * @return Количество лемм для указанного сайта.
     */
    int countBySiteId(SiteEntity siteEntity);

    /**
     * Найти леммы по списку заданных лемм и сайту.
     * <p>
     * Если siteId не задан (NULL), поиск ведется по всему списку лемм без привязки к сайту.
     *
     * @param lemmas Список лемм для поиска.
     * @param siteId Идентификатор сайта (может быть NULL).
     * @return Список лемм, удовлетворяющих критериям поиска.
     */
    @Query(value = """
            SELECT l.* FROM lemma l 
            WHERE l.lemma IN (:lemmas) 
            AND ((l.site_id = :siteId) OR (:siteId IS NULL))""", nativeQuery = true)
    List<LemmaEntity> findByLemmasAndSiteIds(@Param("lemmas") List<String> lemmas,
                                             @Param("siteId") Long siteId);
}
