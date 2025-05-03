package com.skillbox.searchengine.utils;

import lombok.RequiredArgsConstructor;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Компонент, ответственный за извлечение лемм из текста.
 * <p>
 * Включает методы для очистки текста от HTML-разметки,
 * морфологический анализ русских слов, сбор лемм и формирование статистики по словам.
 */
@Component
@RequiredArgsConstructor
public class LemmaExtractor {


    private final RussianLuceneMorphology russianLuceneMorphology;

    public static final int MIN_LENGTH_WORD = 3;
    public static final Pattern ONLY_RUSSIAN_LETTERS = Pattern.compile("[^а-яА-Я]");
    /**
     * Шаблон регулярного выражения для удаления HTML-тегов.
     */
    public static final Pattern TAG_PATTERN = Pattern.compile("<[^>]*>");
    /**
     * Шаблон для распознавания нежелательных символов в словах.
     */
    private static final Pattern WORD_TYPE_REGEX = Pattern.compile("\\W\\w&&[^а-яА-Я\\s]");
    /**
     * Шаблон для разделения текста на отдельные слова.
     */
    public static final String WORD_SPLIT_REGEX = "\\P{L}+";
    /**
     * Массив частиц русского языка, которые игнорируются при анализе.
     */
    private static final String[] PARTICLES_NAMES = new String[]{
            "МЕЖД", "ПРЕДЛ", "СОЮЗ", "МС", "ЧАСТ", "ПРОЧЕЕ", "ВВОДН", "НАРЕЧ"};


    /**
     * Извлекает и подсчитывает леммы из заданного текста.
     *
     * @param text Исходный текст.
     * @return Словарь, где ключ — лемма, а значение — частота её встречаемости.
     */
    public Map<String, Integer> collectLemmas(String text) {
        String[] words = splitIntoWords(text);
        Map<String, Integer> lemmasCount = new HashMap<>();

        for (String word : words) {
            if (!isValidWord(word)) {
                continue;
            }

            List<String> normalForms = russianLuceneMorphology.getNormalForms(word);
            if (normalForms.isEmpty()) {
                continue;
            }

            String lemma = normalForms.get(0);
            lemmasCount.put(lemma, lemmasCount.getOrDefault(lemma, 0) + 1);
        }
        return lemmasCount;
    }

    /**
     * Проверяет, является ли слово валидным для дальнейшего анализа.
     *
     * @param word Анализируемое слово.
     * @return true, если слово валидно, false — если нет.
     */
    private boolean isValidWord(String word) {

        if (word.length() <= MIN_LENGTH_WORD) {
            return false;
        }

        if (ONLY_RUSSIAN_LETTERS.matcher(word).find()) {
            return false;
        }

        List<String> morphInfo = russianLuceneMorphology.getMorphInfo(word);
        if (morphInfo.isEmpty()) {
            return false;
        }

        if (anyWordBaseBelongToParticle(morphInfo)) {
            return false;
        }

        for (String info : morphInfo) {
            if (WORD_TYPE_REGEX.matcher(info).matches()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Возвращает набор уникальных лемм из текста.
     *
     * @param text Исходный текст.
     * @return Набор уникальных лемм.
     */
    public Set<String> getLemmaSet(String text) {
        Map<String, Integer> lemmasCount = collectLemmas(text);
        return lemmasCount.keySet();
    }

    /**
     * Проверяет, принадлежат ли формы слова к частицам русского языка.
     *
     * @param wordBaseForms Список базовых форм слова.
     * @return true, если одна из форм является частицей, false — если нет.
     */
    private boolean anyWordBaseBelongToParticle(List<String> wordBaseForms) {
        return wordBaseForms.stream().anyMatch(this::hasParticleProperty);
    }

    /**
     * Проверяет, содержит ли форма слова признаки частицы.
     *
     * @param wordBase Форма слова.
     * @return true, если форма содержит признак частицы, false — если нет.
     */
    private boolean hasParticleProperty(String wordBase) {
        for (String property : PARTICLES_NAMES) {
            if (wordBase.toUpperCase().contains(property)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Разбивает текст на отдельные слова.
     *
     * @param text Исходный текст.
     * @return Массив слов.
     */
    private String[] splitIntoWords(String text) {
        return text.toLowerCase(Locale.ROOT).split(WORD_SPLIT_REGEX);
    }

    /**
     * Очищает текст от HTML-разметки.
     *
     * @param html HTML-текст.
     * @return Очищенный текст.
     */
    public String cleanHtml(String html) {
        return TAG_PATTERN.matcher(html).replaceAll(" ").trim();
    }

    /**
     * Находит все индексы, где встречается заданная лемма в тексте.
     *
     * @param text  Исходный текст.
     * @param lemma Лемма, позицию которой нужно найти.
     * @return Список индексов, где встречается лемма.
     */
    public List<Integer> findLemmaIndexInText(String text, String lemma) {
        List<Integer> lemmaIndexList = new ArrayList<>();
        String[] elements = text.toLowerCase(Locale.ROOT).split("\\p{Punct}|\\s");
        int index = 0;
        for (String element : elements) {
            Set<String> lemmas = getLemmaSet(element);
            for (String lem : lemmas) {
                if (lem.equals(lemma)) {
                    lemmaIndexList.add(index);
                }
            }
            index += element.length() + 1;
        }
        return lemmaIndexList;
    }
}

