package project1;

import java.util.ArrayList;
import java.util.List;

class ScriptFinder {

    private static String combineCharsBetweenNextQuotes(String str) {
        String combined = "";

        boolean foundFirstQuote = false;
        for (int i = 0; i < str.length(); i++) {
            if (!foundFirstQuote) {
                if (str.charAt(i) == '\"') {
                    foundFirstQuote = true;
                }
            } else {
                if (str.charAt(i) == '\"') {
                    break;
                }
                combined += str.charAt(i);
            }
        }
        return combined;
    }

    public static List<String> findJSTagSources(String html) {
        List<String> links = new ArrayList<>();
        int currentHtmlTagIndex;
        while ((currentHtmlTagIndex = html.indexOf("<script ")) != -1) {
            html = html.substring(currentHtmlTagIndex + "<script ".length(), html.length());
            int nextSrcIndex = html.indexOf("src");
            if (nextSrcIndex != -1) {
                String potentialLink = ScriptFinder
                        .combineCharsBetweenNextQuotes(html.substring(nextSrcIndex, html.length()));
                if (potentialLink.endsWith(".js")) {
                    links.add(potentialLink);
                }
            }

        }
        return links;
    }
}