package com.skillbox.searchengine.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Table(name = "`index`")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IndexEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "page_id", referencedColumnName = "id", nullable = false)
    private PageEntity pageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lemma_id", referencedColumnName = "id", nullable = false)
    private LemmaEntity lemmaId;

    @Column(name = "`rank`", nullable = false)
    private float rank;

}
