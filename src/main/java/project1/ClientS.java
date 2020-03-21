// API KEY TRANSLATION TOOL (yandex): trnsl.1.1.20200320T105558Z.9cc4008c27db703b.31aa12119fd731774544f8da1a35002f6a7250d0

package project1;

// File Name GreetingClient.java
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.io.*;

public class ClientS {

    final String outputPath = "./output";

    public ClientS(int port) {
        this.port = port;
    }

    public int port;

    public static void main(String[] args) {
        // String domain = "www.google.com";
        String domain = "www.bizrate.com";
        // String domain = "toledo.kuleuven.be";

        ClientS client = new ClientS(80);
        // client.head();
        try {
            client.get(domain);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Return the body
     * 
     * @param bodyReader
     * @return
     * @throws IOException
     */
    private String handleChunckedHTMLBody(BufferedReader bodyReader) throws IOException {
        String body = "";
        // Chunk size ends with CRLF (\r\n). readLine handles this!
        String chunkSizeStr = bodyReader.readLine();
        while (chunkSizeStr != null) {
            System.out.println("Reading chunk of length: " + chunkSizeStr);
            for (int chunkSize = Integer.parseInt(chunkSizeStr, 16); chunkSize > 0; chunkSize--) {
                char nextBodyChar = (char) bodyReader.read();
                // System.out.print(nextBodyChar);
                body += nextBodyChar;
            }
            chunkSizeStr = bodyReader.readLine();
            if (chunkSizeStr.length() == 0) {
                break;
            }
        }
        return body;
    }

    private String handleContentLengthHTMLBody(BufferedReader bodyReader, int length) throws IOException {
        String body = "";
        for (int i = length; i > 0; i--) {
            char nextBodyChar = (char) bodyReader.read();
            System.out.print(nextBodyChar);
            body += nextBodyChar;
        }

        return body;
    }

    private void writeToHTMLFile(String html) throws IOException {
        String filePath = "test.html";
        File myObj = new File(filePath);
        if (myObj.createNewFile()) {
            System.out.println("File created: " + myObj.getName());
        } else {
            System.out.println("File already exists.");
        }
        try {
            FileWriter myWriter = new FileWriter(filePath);
            myWriter.write(html);
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public void head(String domain) throws UnknownHostException, IOException {
        System.out.println("Connecting to " + domain + " on port " + port);
        Socket client = new Socket(domain, port);
        System.out.println("Just connected to " + client.getRemoteSocketAddress());

        PrintWriter writer = new PrintWriter(client.getOutputStream(), true);

        writer.println("GET / HTTP/1.1");
        writer.println("Host: " + domain);
        writer.println("User-Agent: Computer Networks Client");
        writer.println("Connection: close"); // Close connection after we are done with it.
        writer.println();

        InputStream inputStream = client.getInputStream();

        new Headers(inputStream);
        client.close();
    }

    private void handleHTMLGetRequest(String domain, BufferedReader reader, int contentLength) throws IOException {
        String html;
        if (contentLength == -1) {
            System.out.println("Chunked body");
            html = this.handleChunckedHTMLBody(reader);
        } else {
            System.out.println("Normal body");
            html = this.handleContentLengthHTMLBody(reader, contentLength);
        }

        // Chunks the html in sizable parts to translate.
        List<String> smallHtmlParts = HTMLChunker.chunkHTML(html, 8000);

        // Translate
        // String originalLan = Translator.fromLanguage(html);
        // String translatedHtml = "";
        // for (String htmlPart : smallHtmlParts) {
        // translatedHtml += Translator.translateHTML(htmlPart, originalLan, "ru");
        // }
        // this.writeToHTMLFile(translatedHtml);
        // \\
        this.writeToHTMLFile(html);

        for (String url : ImageFinder.findImageTagSources(html)) {
            String newDomain;
            String path;
            if (url.startsWith("//")) {
                newDomain = url.substring(2, url.length());
                int domainPathSeperatorIndex = newDomain.indexOf("/");
                path = newDomain.substring(domainPathSeperatorIndex, newDomain.length());
                newDomain = newDomain.substring(0, domainPathSeperatorIndex);
            } else if (url.startsWith("/")) {
                newDomain = domain;
                path = url;
            } else if (url.startsWith("http://")) {
                newDomain = url.substring("http://".length(), url.length());
                int domainPathSeperatorIndex = newDomain.indexOf("/");
                path = newDomain.substring(domainPathSeperatorIndex, newDomain.length());
                newDomain = newDomain.substring(0, domainPathSeperatorIndex);
            } else {
                continue; // If for example the url starts with https, skip!
            }
            this.get(newDomain, path);
        }
    }

    private void handleImageGetRequest(String path, InputStream inputStream) throws IOException {
        String fileName = path.substring(path.lastIndexOf("/") + 1);
        FileOutputStream out = new FileOutputStream(fileName);
        byte[] buffer = new byte[1024];
        int read;
        while ((read = inputStream.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
        out.close();
    }

    public void get(String domain) throws UnknownHostException, IOException {
        this.get(domain, "/");
    }

    public void get(String domain, String path) throws UnknownHostException, IOException {
        System.out.println("Connecting to " + domain + " on port " + port);
        Socket client = new Socket(domain, port);
        System.out.println("Just connected to " + client.getRemoteSocketAddress());

        PrintWriter writer = new PrintWriter(client.getOutputStream(), true);

        writer.println("GET " + path + " HTTP/1.1");
        writer.println("Host: " + domain);
        writer.println("User-Agent: Computer Networks Client");
        writer.println("Connection: close"); // Close connection after we are done with it.
        writer.println();

        InputStream inputStream = client.getInputStream();

        Headers headers = new Headers(inputStream);
        ContentTypes contentType = headers.getContentType();
        int contentLength = headers.getContentLength();

        if (contentType == ContentTypes.HTML) {
            this.handleHTMLGetRequest(domain, new BufferedReader(new InputStreamReader(inputStream)), contentLength);
        } else if (contentType == ContentTypes.IMAGE) {
            this.handleImageGetRequest(path, inputStream);
        }
        client.close();
    }

    public void post() {
        String serverName = "www.google.com";
        System.out.println("Connecting to " + serverName + " on port " + port);
        try (Socket client = new Socket(serverName, port)) {
            System.out.println("Just connected to " + client.getRemoteSocketAddress());

            try (PrintWriter writer = new PrintWriter(client.getOutputStream(), true)) {

                writer.println("POST / HTTP/1.1");
                writer.println("Host: " + serverName);

                String contentType = "nog implementeren";
                int contentLength = 0;

                writer.println("Content-Length: " + contentLength);
                writer.println("Content-Type: " + contentType); // application/x-www-form-urlencoded
                // writer.println("Accept: text/html");
                // writer.println("Accept-Language: en-US");
                // Close connection after we are done with it.
                writer.println("Connection: close");
                writer.println();

                // try (BufferedReader reader = new BufferedReader(new
                // InputStreamReader(client.getInputStream()))) {
                // String line;
                // while ((line = reader.readLine()) != null) {
                // System.out.println(line);
                // }
                // }
            }
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void put() {
        String serverName = "www.google.com";
        System.out.println("Connecting to " + serverName + " on port " + port);
        try (Socket client = new Socket(serverName, port)) {
            System.out.println("Just connected to " + client.getRemoteSocketAddress());

            try (PrintWriter writer = new PrintWriter(client.getOutputStream(), true)) {

                writer.println("HEAD / HTTP/1.1");
                writer.println("Host: " + serverName);
                writer.println("User-Agent: Console Http Client");
                // writer.println("Accept: text/html");
                // writer.println("Accept-Language: en-US");
                // Close connection after we are done with it.
                writer.println("Connection: close");
                writer.println();

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println(line);
                    }
                }
            }
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}