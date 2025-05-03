package com.skillbox.searchengine.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "lemma")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class LemmaEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id", referencedColumnName = "id", nullable = false)
    private SiteEntity siteId;

    @Column(name = "lemma", columnDefinition = "VARCHAR(255)", nullable = false)
    private String lemma;

    @Column(name = "frequency", nullable = false)
    private int frequency;

    @OneToMany(mappedBy = "lemmaId", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    protected List<IndexEntity> indexSearch = new ArrayList<>();

}
