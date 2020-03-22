// API KEY TRANSLATION TOOL (yandex): trnsl.1.1.20200320T105558Z.9cc4008c27db703b.31aa12119fd731774544f8da1a35002f6a7250d0

package project1;

// File Name GreetingClient.java
import java.net.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import java.io.*;

public class ClientS {

    final String outputPath = "output";

    public ClientS(int port, String uri) {
        this.port = port;
        this.uri = uri;
    }

    public int port;
    public String uri;

    public static void main(String[] args) throws URISyntaxException, UnknownHostException, IOException {

        if (args.length == 4 || args.length == 5) {
            String requestType = args[0].toUpperCase();
            String uri = args[1];
            if (uri.startsWith("https")) {
                System.out.println("Unsupported protocol!");
                return;
            }
            int port;
            try {
                port = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid port!");
                return;
            }
            String language = args[3].toLowerCase();
            String data = "";
            boolean hasData = args.length == 5;
            if (hasData) {
                data = args[4];
            }

            ClientS client = new ClientS(port, uri);

            String domain = client.getDomain(uri);
            String path = client.getPath(uri);

            System.out.println("Request to " + requestType.toString() + ".\nDomain: " + domain + "\nPath: " + path);

            switch (requestType) {
                case "GET":
                    client.get(domain, path, language);
                    break;
                case "HEAD":
                    client.head(domain, path);
                    break;
                case "POST":
                    if (hasData) {
                        client.post(domain, path, data, language);
                    } else {
                        System.out.println("Post requests should have data!");
                    }
                    break;
                case "PUT":
                    if (data != null) {
                        client.put(domain, path, data, language);
                    } else {
                        System.out.println("Post requests should have data!");
                    }
                    break;
                default:
                    System.out.println("Request type not supported!");
            }
        } else {
            System.out.println("Invalid arguments!");
        }

        // String domain = "www.google.com";
        // String domain = "babytree.com";
        // String domain = "www.bizrate.com";
        // String domain = "toledo.kuleuven.be";
        // String domain = "httpbin.org";
        // String domain = "localhost";

        // ClientS client = new ClientS(80, domain);
        // String path = client.getPath(domain);

        // client.head();
        // try {
        // client.get(domain, path, "en");
        // } catch (IOException e) {
        // e.printStackTrace();
        // }
        // try {
        // client.post(domain, "/test2.txt", "Oef", "en");
        // } catch (IOException e) {
        // e.printStackTrace();
        // }
    }

    public String getDomain(String uri) {
        String domain = uri;
        if (uri.startsWith("http://")) {
            domain = domain.substring(domain.indexOf("http://") + "http://".length());
        }
        return domain.substring(0, domain.indexOf("/") == -1 ? domain.length() : domain.indexOf("/"));
    }

    public String getPath(String uri) {
        String path = uri;
        if (uri.startsWith("http://")) {
            path = path.substring(path.indexOf("http://") + "http://".length());
        }
        if (path.indexOf("/") != -1) {
            return path.substring(path.indexOf("/"), path.length());
        } else {
            return "/";
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
            FileWriter myWriter = new FileWriter(filePath, Charset.forName("UTF-8"));
            myWriter.write(html);
            myWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    private void handleHTMLBody(String domain, String path, Socket client, int contentLength, String language)
            throws IOException {
        String html;

        InputStream inputStream = client.getInputStream();
        if (contentLength == -1) {
            System.out.println("Chunked body");
            html = this.handleChunckedHTMLBody(inputStream);
        } else {
            System.out.println("Normal body");
            html = this.handleContentLengthHTMLBody(inputStream, contentLength);
        }

        System.out.println("\n\n\nHTML READ, SCANNING FOR EMBEDDED OBJECTS");

        ArrayList<String> embeddedObjectDomains = new ArrayList<>();
        ArrayList<String> embeddedObjectPaths = new ArrayList<>();
        for (String url : EmbeddedObjectFinder.findEmbeddedObjects(html)) {
            String embeddedObjectDomain;
            String embeddedObjectPath;
            if (url.startsWith("//")) {
                embeddedObjectDomain = url.substring(2, url.length());
                int domainPathSeperatorIndex = embeddedObjectDomain.indexOf("/");
                embeddedObjectPath = embeddedObjectDomain.substring(domainPathSeperatorIndex,
                        embeddedObjectDomain.length());
                embeddedObjectDomain = embeddedObjectDomain.substring(0, domainPathSeperatorIndex);
            } else if (url.startsWith("/")) {
                embeddedObjectDomain = domain;
                embeddedObjectPath = url;
            } else if (url.startsWith("http://")) {
                embeddedObjectDomain = url.substring("http://".length(), url.length());
                int domainPathSeperatorIndex = embeddedObjectDomain.indexOf("/");
                embeddedObjectPath = embeddedObjectDomain.substring(domainPathSeperatorIndex,
                        embeddedObjectDomain.length());
                embeddedObjectDomain = embeddedObjectDomain.substring(0, domainPathSeperatorIndex);
            } else if (url.startsWith("https://")) {
                continue;
            } else {
                embeddedObjectDomain = domain;
                embeddedObjectPath = "/" + url;
            }
            embeddedObjectDomains.add(embeddedObjectDomain);
            embeddedObjectPaths.add(embeddedObjectPath);
            html = html.replace(url, embeddedObjectPath.substring(1));
        }
        for (int i = 0; i < embeddedObjectDomains.size(); i++) {
            if (embeddedObjectDomains.get(i) != this.getDomain(this.uri)) {
                System.out.println("File on other server, setting up connection...");
                this.get(embeddedObjectDomains.get(i), embeddedObjectPaths.get(i), language);
            } else {
                System.out.println("Can re-use connection!");
                this.handleGeneralGet(embeddedObjectDomains.get(i), embeddedObjectPaths.get(i), client, language);
            }
        }

        // Chunks the html in sizable parts to translate.
        List<String> smallHtmlParts = HTMLChunker.chunkHTML(html, 8000);

        // Translate
        // String originalLan = Translator.fromLanguage(html);
        // String translatedHtml = "";
        // for (String htmlPart : smallHtmlParts) {
        // translatedHtml += Translator.translateHTML(htmlPart, originalLan, language);
        // }
        // this.writeToHTMLFile(translatedHtml);
        // \\
        this.writeToHTMLFile(html, path);
    }

    private void handleFileBody(String path, Socket client, int contentLength) throws IOException {
        InputStream inputStream = client.getInputStream();
        String fileName = this.outputPath + path;
        (new File(fileName)).getParentFile().mkdirs();
        FileOutputStream out = new FileOutputStream(fileName);

        byte[] buffer = new byte[8 * 1024];
        int bytesRead;
        int downloaded = 0;
        while (downloaded < contentLength) {
            bytesRead = inputStream.read(buffer);
            out.write(buffer, 0, bytesRead);
            downloaded += bytesRead;
            System.out.println("Downloaded: " + downloaded + "/" + contentLength);
        }
        out.close();
    }

    private void handleUnknownBody(Socket client, int contentLength) throws IOException {
        InputStream inputStream = client.getInputStream();
        byte[] buffer = new byte[8 * 1024];
        int bytesRead;
        int downloaded = 0;
        while (downloaded < contentLength) {
            bytesRead = inputStream.read(buffer);
            downloaded += bytesRead;
        }
    }

    private void handlePutAndPost(String domain, String path, PutPost type, String input, String language)
            throws IOException {
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
            this.handleHTMLBody(domain, path, client, contentLength, language);
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
        client.close();
    }

    private void handleGeneralGet(String domain, String path, Socket client, String language)
            throws UnknownHostException, IOException {
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
            this.handleHTMLBody(domain, path, client, contentLength, language);
        } else if (contentType == ContentTypes.IMAGE || contentType == ContentTypes.SCRIPT
                || contentType == ContentTypes.STYLES) {
            this.handleFileBody(path, client, contentLength);
        } else {
            this.handleUnknownBody(client, contentLength);
        }
    }

    private void handleGeneralHead(String domain, String path, boolean shouldClose, Socket client) throws IOException {
        PrintWriter writer = new PrintWriter(client.getOutputStream(), true);

        writer.println("HEAD " + path + " HTTP/1.1");
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
        this.head(domain, "/");
    }

    public void head(String domain, String path) throws UnknownHostException, IOException {
        System.out.println("Connecting to " + domain + " on port " + port);
        Socket client = new Socket(domain, port);
        this.handleGeneralHead(domain, path, true, client);
        client.close();
    }

    public void get(String domain, String language) throws UnknownHostException, IOException {
        this.get(domain, "/", language);
    }

    public void get(String domain, String path, String language) throws UnknownHostException, IOException {
        System.out.println("Connecting to " + domain + " on port " + port);
        Socket client = new Socket(domain, port);
        this.handleGeneralGet(domain, path, client, language);
        this.handleGeneralHead(domain, path, true, client);
        client.close();
    }

    public void put(String domain, String path, String input, String language)
            throws UnknownHostException, IOException {
        this.handlePutAndPost(domain, path, PutPost.PUT, input, language);
    }

    public void post(String domain, String path, String input, String language)
            throws UnknownHostException, IOException {
        this.handlePutAndPost(domain, path, PutPost.POST, input, language);
    }
}

enum PutPost {
    PUT, POST
}