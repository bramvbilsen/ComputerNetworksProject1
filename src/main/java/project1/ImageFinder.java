package project1;

import java.util.ArrayList;
import java.util.List;

class ImageFinder {

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

    public static List<String> findImageTagSources(String html) {
        List<String> links = new ArrayList<>();
        int currentHtmlTagIndex;
        while ((currentHtmlTagIndex = html.indexOf("<img ")) != -1) {
            html = html.substring(currentHtmlTagIndex + "<img ".length(), html.length());
            int nextSrcIndex = html.indexOf("src");
            if (nextSrcIndex != -1) {
                String potentialLink = ImageFinder
                        .combineCharsBetweenNextQuotes(html.substring(nextSrcIndex, html.length()));
                if (potentialLink.toLowerCase().endsWith(".png") || potentialLink.toLowerCase().endsWith(".jpg")
                        || potentialLink.toLowerCase().endsWith(".jpeg")
                        || potentialLink.toLowerCase().endsWith(".gif"))
                    links.add(ImageFinder.combineCharsBetweenNextQuotes(html.substring(nextSrcIndex, html.length())));
            }
        }
        return links;
    }
}