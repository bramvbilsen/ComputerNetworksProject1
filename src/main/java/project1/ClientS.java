// API KEY TRANSLATION TOOL (yandex): trnsl.1.1.20200320T105558Z.9cc4008c27db703b.31aa12119fd731774544f8da1a35002f6a7250d0

package project1;

// File Name GreetingClient.java
import java.net.*;
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
        // String domain = "www.bizrate.com";
        // String domain = "toledo.kuleuven.be";
        // String domain = "httpbin.org";
        String domain = "localhost";

        ClientS client = new ClientS(80);
        // client.head();
        // try {
        // client.get(domain);
        // } catch (IOException e) {
        // e.printStackTrace();
        // }
        try {
            client.post(domain, "/test2.txt", "NOICE");
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    private void writeToHTMLFile(String html, String path) throws IOException {
        String filePath;
        if (path.charAt(0) == '/') {
            filePath = this.outputPath + "/index.html";
        } else {
            filePath = this.outputPath + path;
        }
        File file = new File(filePath);
        file.getParentFile().mkdirs();
        if (file.createNewFile()) {
            System.out.println("File created: " + file.getName());
        } else {
            System.out.println("File already exists.");
        }
        try {
            FileWriter myWriter = new FileWriter(filePath);
            myWriter.write(html);
            myWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    private void handleHTMLBody(String domain, String path, BufferedReader reader, int contentLength)
            throws IOException {
        String html;
        if (contentLength == -1) {
            System.out.println("Chunked body");
            html = this.handleChunckedHTMLBody(reader);
        } else {
            System.out.println("Normal body");
            html = this.handleContentLengthHTMLBody(reader, contentLength);
        }

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
            this.get(imgDomain, imgPath);
            System.out.println("Image path: " + imgPath);
            html = html.replace(url, imgPath.substring(1));
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

    private void handleImageBody(String path, InputStream inputStream) throws IOException {
        String fileName = this.outputPath + path;
        System.out.println((new File(fileName)).getParentFile().mkdirs());
        FileOutputStream out = new FileOutputStream(fileName);
        byte[] buffer = new byte[1024];
        int read;
        while ((read = inputStream.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
        out.close();
    }

    private void handlePutAndPost(String domain, String path, PutPost type, String input) throws IOException {
        System.out.println("Connecting to " + domain + " on port " + port);
        Socket client = new Socket(domain, port);
        System.out.println("Just connected to " + client.getRemoteSocketAddress());

        String contentType = "text/plain; charset=UTF-8";

        OutputStream outputStream = client.getOutputStream();
        PrintWriter writer = new PrintWriter(outputStream, true);
        writer.println((type == PutPost.POST ? "POST " : "PUT ") + path + " HTTP/1.1");
        writer.println("Host: " + domain);
        writer.println("Content-Type: " + contentType);
        writer.println("Content-Length: " + input.getBytes().length);
        // writer.println("Connection: close");
        writer.println("");
        writer.println(input);

        InputStream inputStream = client.getInputStream();
        Headers headers = new Headers(inputStream);
        ContentTypes responseContentType = headers.getContentType();
        int contentLength = headers.getContentLength();

        if (responseContentType == ContentTypes.HTML) {
            this.handleHTMLBody(domain, path, new BufferedReader(new InputStreamReader(inputStream)), contentLength);
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
        client.close();
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
            this.handleHTMLBody(domain, path, new BufferedReader(new InputStreamReader(inputStream)), contentLength);
        } else if (contentType == ContentTypes.IMAGE) {
            this.handleImageBody(path, inputStream);
        }
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