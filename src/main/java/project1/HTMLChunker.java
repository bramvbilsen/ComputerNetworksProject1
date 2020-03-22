package project1;

import java.util.ArrayList;

/**
 * Class to chunk html string in smaller parts.
 */
class HTMLChunker {
    /**
     * Chunks the given html string in parts with at most `maxCharCount` characters.
     * 
     * @param html         html string to chunk.
     * @param maxCharCount max chars in html chunk.
     * @return List of chunked html strings.
     */
    public static ArrayList<String> chunkHTML(String html, int maxCharCount) {
        ArrayList<String> smallHtmlParts = new ArrayList<>();
        int beginIndex = 0;
        int index = 0;
        int lastCloserIndex = 0;
        while (index < html.length()) {
            if (index - beginIndex > maxCharCount) {
                smallHtmlParts.add(html.substring(beginIndex, lastCloserIndex + 1));
                beginIndex = lastCloserIndex + 1;
            }
            if (html.charAt(index) == '>') {
                lastCloserIndex = index;
            }
            index += 1;
        }
        if (beginIndex <= html.length()) {
            smallHtmlParts.add(html.substring(beginIndex, html.length()));
        }
        return smallHtmlParts;
    }
}