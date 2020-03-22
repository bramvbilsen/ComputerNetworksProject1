package project1;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to help finding embedded objects in HTML files.
 */
class EmbeddedObjectFinder {

    /**
     * Searches the first two "-marks and returns what is between them.
     * 
     * @param str String in which to search.
     * @return String between first two "-marks.
     */
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

    /**
     * Finds the embedded objects in the provided html string and returns a list of
     * their link pointing to where they are stored.
     * 
     * @param html html string to find the embedded object links in.
     * @return list of links of the embedded objects.
     */
    public static List<String> findEmbeddedObjects(String html) {
        String imgHTML = html;
        String stylesHTML = html;
        String scriptsHTML = html;
        List<String> links = new ArrayList<>();
        int embeddedObjectIndex;
        while ((embeddedObjectIndex = imgHTML.indexOf("<img ")) != -1) {
            imgHTML = imgHTML.substring(embeddedObjectIndex + "<img ".length(), imgHTML.length());
            int nextSrcIndex = imgHTML.indexOf("src");
            if (nextSrcIndex != -1) {
                String potentialLink = EmbeddedObjectFinder
                        .combineCharsBetweenNextQuotes(imgHTML.substring(nextSrcIndex, imgHTML.length()));
                if (potentialLink.toLowerCase().endsWith(".png") || potentialLink.toLowerCase().endsWith(".jpg")
                        || potentialLink.toLowerCase().endsWith(".jpeg")
                        || potentialLink.toLowerCase().endsWith(".gif"))
                    links.add(potentialLink);
            }
        }
        while ((embeddedObjectIndex = stylesHTML.indexOf("rel=\"stylesheet\"")) != -1) {
            stylesHTML = stylesHTML.substring(embeddedObjectIndex + "rel=\"stylesheet\"".length(), stylesHTML.length());
            int nextSrcIndex = stylesHTML.indexOf("href");
            if (nextSrcIndex != -1) {
                String potentialLink = EmbeddedObjectFinder
                        .combineCharsBetweenNextQuotes(stylesHTML.substring(nextSrcIndex, stylesHTML.length()));
                if (potentialLink.endsWith(".css")) {
                    links.add(potentialLink);
                }
            }

        }

        while ((embeddedObjectIndex = scriptsHTML.indexOf("<script ")) != -1) {
            scriptsHTML = scriptsHTML.substring(embeddedObjectIndex + "<script ".length(), scriptsHTML.length());
            int nextSrcIndex = scriptsHTML.indexOf("src");
            if (nextSrcIndex != -1) {
                String potentialLink = EmbeddedObjectFinder
                        .combineCharsBetweenNextQuotes(scriptsHTML.substring(nextSrcIndex, scriptsHTML.length()));
                if (potentialLink.endsWith(".js")) {
                    links.add(potentialLink);
                }
            }

        }
        return links;
    }
}