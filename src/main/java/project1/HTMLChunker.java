package project1;

import java.util.ArrayList;

class HTMLChunker {
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