package com.skillbox.searchengine.services.search.searchhelpers;

import com.skillbox.searchengine.utils.LemmaExtractor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Компонент, генерирующий отрывок текста (сниппет) с подчеркиванием релевантных слов.
 * Используется для показа релевантных фрагментов документов в результатах поиска.
 */
@Component
@RequiredArgsConstructor
public class SnippetGeneration {

    private final LemmaExtractor lemmaExtractor;
    private static final int CONTEXT_WINDOW_SIZE = 100;
    private static final int MAX_SNIPPET_FRAGMENTS = 2;
    public static final int MAX_WORD_DISTANCE = 5;

    /**
     * Генерация сниппета (текстового фрагмента) с подчеркнутыми релевантными словами.
     *
     * @param content         Исходный текст документа.
     * @param lemmasFromQuery Список релевантных лемм (слов) из поискового запроса.
     * @return Сгенерированный сниппет с подчеркнутыми словами.
     */
    public String getSnippet(String content, List<String> lemmasFromQuery) {

        List<Integer> uniqueSortedIndices = lemmasFromQuery.stream()
                .flatMap(lemma -> lemmaExtractor.findLemmaIndexInText(content, lemma).stream())
                .distinct()
                .sorted()
                .filter(pos -> pos < content.length()
                        && Character.isLetter(content.charAt(pos)))
                .collect(Collectors.toList());
        List<String> wordsList = extractAndHighlightWordsByLemmaIndex(content, uniqueSortedIndices);
        StringBuilder result = new StringBuilder();
        wordsList.stream()
                .limit(MAX_SNIPPET_FRAGMENTS)
                .forEachOrdered(word -> result.append(word).append("..."));

        return result.toString().stripTrailing();
    }

    /**
     * Извлекает и выделяет слова, соответствующие заданным индексам.
     *
     * @param content    Исходный текст документа.
     * @param lemmaIndex Списки индексов лемм в тексте.
     * @return Список выделенных фрагментов текста.
     */
    private List<String> extractAndHighlightWordsByLemmaIndex(String content, List<Integer> lemmaIndex) {
        List<String> result = new ArrayList<>();

        for (int i = 0; i < lemmaIndex.size(); i++) {
            int start = lemmaIndex.get(i);
            int end = findNextSpace(content, start);
            int step = i + 1;

            while (step < lemmaIndex.size() &&
                    lemmaIndex.get(step) - end > 0 &&
                    lemmaIndex.get(step) - end < MAX_WORD_DISTANCE) {
                end = findNextSpace(content, lemmaIndex.get(step));
                step++;
            }
            i = step - 1;
            String text = getWordsFromIndexWithHighlighting(start, end, content);
            result.add(text);
        }
        result.sort(Comparator.comparingInt(String::length).reversed());
        return result;
    }

    /**
     * Находит ближайшее положение пробела после заданной позиции.
     *
     * @param content Исходный текст документа.
     * @param pos     Начальная позиция поиска.
     * @return Индекс следующего пробела или длина строки, если пробел не найден.
     */
    private int findNextSpace(String content, int pos) {
        return content.indexOf(" ", pos);
    }

    /**
     * Форматирует текстовый фрагмент, выделяя заданное слово.
     *
     * @param start   Начало интересующего слова.
     * @param end     Окончание интересующего слова.
     * @param content Исходный текст документа.
     * @return Отформатированный фрагмент текста с выделенным словом.
     */
    private String getWordsFromIndexWithHighlighting(int start, int end, String content) {

        String word = content.substring(start, end);
        int prevSpace = Math.max(content.lastIndexOf(' ', start), 0);
        int nextSpace = Math.min(content.indexOf(' ', end + CONTEXT_WINDOW_SIZE),
                content.length());
        String context = content.substring(prevSpace, nextSpace).trim();

        return context.replace(word, "<b>" + word + "</b>");

    }

}
