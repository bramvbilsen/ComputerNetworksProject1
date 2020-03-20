package project1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

class Translator {

    final static String translationAPIKey = "trnsl.1.1.20200320T105558Z.9cc4008c27db703b.31aa12119fd731774544f8da1a35002f6a7250d0";

    /**
     * Checks which language this page is in and returns that language abreviation.
     * 
     * @param html
     * @return
     */
    public static String fromLanguage(String html) {
        String lanTagEntry = "lang=\"";
        if (html.indexOf(lanTagEntry) == -1)
            return "en";
        String fromLanguage = html.substring(html.indexOf(lanTagEntry) + lanTagEntry.length(),
                html.indexOf("lang=\"") + lanTagEntry.length() + 2);
        return fromLanguage;
    }

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