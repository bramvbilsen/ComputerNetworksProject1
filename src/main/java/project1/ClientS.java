// API KEY TRANSLATION TOOL (yandex): trnsl.1.1.20200320T105558Z.9cc4008c27db703b.31aa12119fd731774544f8da1a35002f6a7250d0

package project1;

// File Name GreetingClient.java
import java.net.*;
import java.util.ArrayList;
import java.util.List;

import java.io.*;

public class ClientS {

    final String outputPath = "output";

    public ClientS(int port) {
        this.port = port;
    }

    public int port;

    public static void main(String[] args) {
        // String domain = "www.google.com";
        // String domain = "babytree.com";
        // String domain = "diptera.myspecies.info";
        // String domain = "www.bizrate.com";
        // String domain = "toledo.kuleuven.be";
        // String domain = "techofires.com";
        // String domain = "httpbin.org";
        String domain = "localhost";

        ClientS client = new ClientS(80);
        // client.head();
        try {
            client.get(domain);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // try {
        // client.post(domain, "/test2.txt", "Oef");
        // } catch (IOException e) {
        // e.printStackTrace();
        // }
    }

    public void execute(String input) throws UnknownHostException, IOException {
        String[] splittedInput = input.split(" ");
        if (splittedInput.length == 4 || splittedInput.length == 5) {
            String command = splittedInput[0];
            String uri = splittedInput[1];
            this.port = Integer.parseInt(splittedInput[2]);
            String language = splittedInput[3];
            String output = "";
            if (splittedInput.length == 5) {
                output = splittedInput[4];
            }
            switch (command) {
                case "GET":
                    this.get(uri);
                    break;
                case "HEAD":
                    this.head(uri);
                    break;
                case "POST":
                    this.post(uri, "/", output);
                    break;
                case "PUT":
                    this.put(uri, "/", output);
                    break;
            }
        }
    }

    private int readChunkSize(InputStream inputStream) throws IOException {
        String chunkSizeStr = "";
        // Chunk size ends with CRLF (\r\n). readLine handles this!
        boolean lastWasLineEnd = false;
        int inputByte;
        while ((inputByte = inputStream.read()) != -1) {
            if ((char) inputByte == '\r') {
                lastWasLineEnd = true;
            } else if (lastWasLineEnd && (char) inputByte == '\n') {
                break;
            } else {
                chunkSizeStr += (char) inputByte;
            }
        }
        try {
            return Integer.parseInt(chunkSizeStr, 16);
        } catch (Exception _) {
            while (inputStream.available() > 0) { // empty the stream. We read all chunks.
                inputStream.read();
            }
            return -1;
        }
    }

    /**
     * Return the body
     * 
     * @param bodyReader
     * @return
     * @throws IOException
     */
    private String handleChunckedHTMLBody(InputStream inputStream) throws IOException {
        String body = "";

        int chunkSize;
        while ((chunkSize = this.readChunkSize(inputStream)) != -1) {
            System.out.println("Reading chunk of length: " + chunkSize);
            for (int i = chunkSize; i > 0; i--) {
                char nextBodyChar = (char) inputStream.read();
                body += nextBodyChar;
            }
        }
        return body;
    }

    private String handleContentLengthHTMLBody(InputStream inputStream, int contentLength) throws IOException {
        String body = "";
        for (int i = contentLength; i > 0; i--) {
            char nextBodyChar = (char) inputStream.read();
            body += nextBodyChar;
        }

        return body;
    }

    private void writeToHTMLFile(String html, String path) throws IOException {
        String filePath;
        if (path.charAt(0) == '/') {
            filePath = this.outputPath + "/index.html";
        } else {
            filePath = this.outputPath + path;
        }
        File file = new File(filePath);
        file.getParentFile().mkdirs();
        file.createNewFile();
        try {
            FileWriter myWriter = new FileWriter(filePath);
            myWriter.write(html);
            myWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    private void handleHTMLBody(String domain, String path, Socket client, int contentLength) throws IOException {
        String html;

        InputStream inputStream = client.getInputStream();
        if (contentLength == -1) {
            System.out.println("Chunked body");
            html = this.handleChunckedHTMLBody(inputStream);
        } else {
            System.out.println("Normal body");
            html = this.handleContentLengthHTMLBody(inputStream, contentLength);
        }

        ArrayList<String> imgDomains = new ArrayList<>();
        ArrayList<String> imgPaths = new ArrayList<>();
        for (String url : ImageFinder.findImageTagSources(html)) {
            String imgDomain;
            String imgPath;
            if (url.startsWith("//")) {
                imgDomain = url.substring(2, url.length());
                int domainPathSeperatorIndex = imgDomain.indexOf("/");
                imgPath = imgDomain.substring(domainPathSeperatorIndex, imgDomain.length());
                imgDomain = imgDomain.substring(0, domainPathSeperatorIndex);
            } else if (url.startsWith("/")) {
                imgDomain = domain;
                imgPath = url;
            } else if (url.startsWith("http://")) {
                imgDomain = url.substring("http://".length(), url.length());
                int domainPathSeperatorIndex = imgDomain.indexOf("/");
                imgPath = imgDomain.substring(domainPathSeperatorIndex, imgDomain.length());
                imgDomain = imgDomain.substring(0, domainPathSeperatorIndex);
            } else if (url.startsWith("https://")) {
                continue;
            } else {
                imgDomain = domain;
                imgPath = "/" + url;
            }
            imgDomains.add(imgDomain);
            imgPaths.add(imgPath);
            System.out.println("Image path: " + imgPath);
            html = html.replace(url, imgPath.substring(1));
        }
        for (int i = 0; i < imgDomains.size(); i++) {
            this.handleGeneralGet(imgDomains.get(i), imgPaths.get(i), client);
        }

        ArrayList<String> styleDomains = new ArrayList<>();
        ArrayList<String> stylePaths = new ArrayList<>();
        for (String url : StylesFinder.findStylesTagSources(html)) {
            String styleDomain;
            String stylePath;
            if (url.startsWith("//")) {
                styleDomain = url.substring(2, url.length());
                int domainPathSeperatorIndex = styleDomain.indexOf("/");
                stylePath = styleDomain.substring(domainPathSeperatorIndex, styleDomain.length());
                styleDomain = styleDomain.substring(0, domainPathSeperatorIndex);
            } else if (url.startsWith("/")) {
                styleDomain = domain;
                stylePath = url;
            } else if (url.startsWith("http://")) {
                styleDomain = url.substring("http://".length(), url.length());
                int domainPathSeperatorIndex = styleDomain.indexOf("/");
                stylePath = styleDomain.substring(domainPathSeperatorIndex, styleDomain.length());
                styleDomain = styleDomain.substring(0, domainPathSeperatorIndex);
            } else if (url.startsWith("https://")) {
                continue;
            } else {
                styleDomain = domain;
                stylePath = "/" + url;
            }
            styleDomains.add(styleDomain);
            stylePaths.add(stylePath);
            System.out.println("Script path: " + stylePath);
            html = html.replace(url, stylePath.substring(1));
        }
        for (int i = 0; i < styleDomains.size(); i++) {
            this.handleGeneralGet(styleDomains.get(i), stylePaths.get(i), client);
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
        this.writeToHTMLFile(html, path);
    }

    private void handleFileBody(String path, InputStream inputStream, int contentLength) throws IOException {
        String fileName = this.outputPath + path;
        (new File(fileName)).getParentFile().mkdirs();
        while (inputStream.available() != contentLength) {
            System.out.println("Downloading image:  "
                    + Math.round((((double) inputStream.available()) / ((double) contentLength)) * 100) + "%");
        }
        System.out.println("Download completed!");
        FileOutputStream out = new FileOutputStream(fileName);
        byte[] buffer = new byte[contentLength];
        out.write(buffer, 0, inputStream.read(buffer));
        out.close();
    }

    private void handlePutAndPost(String domain, String path, PutPost type, String input) throws IOException {
        System.out.println("Connecting to " + domain + " on port " + port);
        Socket client = new Socket(domain, port);

        String contentType = "text/plain; charset=UTF-8";

        OutputStream outputStream = client.getOutputStream();
        PrintWriter writer = new PrintWriter(outputStream, true);
        writer.println((type == PutPost.POST ? "POST " : "PUT ") + path + " HTTP/1.1");
        writer.println("Host: " + domain);
        writer.println("Content-Type: " + contentType);
        writer.println("Content-Length: " + input.getBytes().length);
        writer.println("Connection: close");
        writer.println("");
        writer.println(input);

        InputStream inputStream = client.getInputStream();
        Headers headers = new Headers(inputStream);
        ContentTypes responseContentType = headers.getContentType();
        int contentLength = headers.getContentLength();

        if (responseContentType == ContentTypes.HTML) {
            this.handleHTMLBody(domain, path, client, contentLength);
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
        client.close();
    }

    private void handleGeneralGet(String domain, String path, Socket client) throws UnknownHostException, IOException {
        PrintWriter writer = new PrintWriter(client.getOutputStream(), true);

        writer.println("GET " + path + " HTTP/1.1");
        writer.println("Host: " + domain);
        writer.println("User-Agent: Computer Networks Client");
        writer.println("Connection: keep-alive");
        writer.println("");

        InputStream inputStream = client.getInputStream();

        Headers headers = new Headers(inputStream);
        ContentTypes contentType = headers.getContentType();
        int contentLength = headers.getContentLength();

        if (contentType == ContentTypes.HTML) {
            this.handleHTMLBody(domain, path, client, contentLength);
        } else if (contentType == ContentTypes.IMAGE || contentType == ContentTypes.SCRIPT) {
            this.handleFileBody(path, inputStream, contentLength);
        }
    }

    private void handleGeneralHead(String domain, boolean shouldClose, Socket client) throws IOException {
        PrintWriter writer = new PrintWriter(client.getOutputStream(), true);

        writer.println("HEAD / HTTP/1.1");
        writer.println("Host: " + domain);
        writer.println("User-Agent: Computer Networks Client");
        if (shouldClose) {
            writer.println("Connection: close");
        }
        writer.println("");

        InputStream inputStream = client.getInputStream();

        new Headers(inputStream);
    }

    public void head(String domain) throws UnknownHostException, IOException {
        System.out.println("Connecting to " + domain + " on port " + port);
        Socket client = new Socket(domain, port);
        this.handleGeneralHead(domain, true, client);
        client.close();
    }

    public void get(String domain) throws UnknownHostException, IOException {
        this.get(domain, "/");
    }

    public void get(String domain, String path) throws UnknownHostException, IOException {
        System.out.println("Connecting to " + domain + " on port " + port);
        Socket client = new Socket(domain, port);
        this.handleGeneralGet(domain, path, client);
        this.handleGeneralHead(domain, true, client);
        client.close();
    }

    public void put(String domain, String path, String input) throws UnknownHostException, IOException {
        this.handlePutAndPost(domain, path, PutPost.PUT, input);
    }

    public void post(String domain, String path, String input) throws UnknownHostException, IOException {
        this.handlePutAndPost(domain, path, PutPost.POST, input);
    }
}

enum PutPost {
    PUT, POST
}