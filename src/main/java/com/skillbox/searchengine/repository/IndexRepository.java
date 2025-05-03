package com.skillbox.searchengine.repository;

import com.skillbox.searchengine.model.IndexEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Репозиторий для работы с таблицей индексов (IndexEntity).
 */
@Repository
public interface IndexRepository extends JpaRepository<IndexEntity, Long> {

    /**
     * Определяет список индексов, связанных с указанными идентификаторами лемм.
     * Запрос получает список всех индексов, чьи леммы совпадают с входящими ID.
     *
     * @param lemmaIds Список идентификаторов лемм.
     * @return Список сущностей IndexEntity, соответствующих заданным леммам.
     */
    @Query(value = """
            SELECT i.*
            FROM `index` i 
            LEFT JOIN page AS p ON i.page_id = p.id
            WHERE i.lemma_id IN (:lemmaIds)""",
            nativeQuery = true)
    List<IndexEntity> findIndexWithForcedLoadingPages(@Param("lemmaIds") List<Long> lemmaIds);

    /**
     * Определяет список индексов, связанных с указанными идентификаторами страниц.
     * Запрос получает список всех индексов, чьи страницы совпадают с входящими ID.
     *
     * @param pageIds Список идентификаторов страниц.
     * @return Список сущностей IndexEntity, соответствующих заданным страницам.
     */
    @Query(value = """
            SELECT i.*
            FROM `index` i
            WHERE i.page_id IN (:pageIds)""",
            nativeQuery = true)
    List<IndexEntity> findIndexByPageIds(@Param("pageIds") List<Long> pageIds);

}
