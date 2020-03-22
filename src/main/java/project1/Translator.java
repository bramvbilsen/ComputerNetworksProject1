package project1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Class used to translate html files.
 */
class Translator {

    final static String translationAPIKey = "trnsl.1.1.20200322T173021Z.c6375857a50aeb74.334de78295721356b62d50cf9b7119aed9ccf5b0";

    /**
     * Checks which language this page is in and returns that language abreviation.
     * Defaults to "en".
     * 
     * @param html html string to determine language from.
     * @return The language this html string is in.
     */
    public static String fromLanguage(String html) {
        String lanTagEntry = "lang=\"";
        if (html.indexOf(lanTagEntry) == -1)
            return "en";
        String fromLanguage = html.substring(html.indexOf(lanTagEntry) + lanTagEntry.length(),
                html.indexOf("lang=\"") + lanTagEntry.length() + 2);
        return fromLanguage;
    }

    /**
     * Translates the provided html string from and to the given languages.
     * 
     * @param html         html string to translate.
     * @param fromLanguage language in of the html string.
     * @param toLanguage   language to convert the html string to.
     * @return Translated html string.
     * @throws IOException If something went wrong connecting with the Yandex API.
     */
    public static String translateHTML(String html, String fromLanguage, String toLanguage) throws IOException {
        html = html.replaceAll("\n", "");
        html = html.replaceAll("\r", "");
        URL url = new URL("https://translate.yandex.net/api/v1.5/tr.json/translate?key=" + Translator.translationAPIKey
                + "&text=" + URLEncoder.encode(html, "utf-8") + "&lang=" + fromLanguage + "-" + toLanguage
                + "&format=html");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setDoOutput(true);
        urlConnection.setRequestMethod("GET");
        urlConnection.setRequestProperty("Accept", "application/json");

        try (BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            String jsonString = response.toString();
            String jsonTextEntryKey = "\"text\":[\"";
            String jsonTextEnd = "\"]}";
            String translatedHtml = jsonString.substring(
                    jsonString.indexOf(jsonTextEntryKey) + jsonTextEntryKey.length(), jsonString.indexOf(jsonTextEnd));
            return translatedHtml.replaceAll("\\\\", "");
        }
    }
}