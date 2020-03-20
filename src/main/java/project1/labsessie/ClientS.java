package project1.labsessie;

// File Name GreetingClient.java
import java.net.*;
import java.io.*;

public class ClientS {

    final String outputPath = "./output";

    public ClientS(int port) {
        this.port = port;
    }

    public int port;

    public static void main(String[] args) {
        ClientS client = new ClientS(80);
        // client.head();
        client.get();
    }

    private boolean isChunkedBody(String headers) {
        return headers.contains("Transfer-Encoding: chunked");
    }

    private void handleChunckedBody(BufferedReader bodyReader) throws IOException {
        String body = "";
        System.out.println("==========BODY==========");
        // Chunk size ends with CRLF (\r\n). readLine handles this!
        String chunkSizeStr = bodyReader.readLine();
        System.out.println("Reading chunk of length: " + chunkSizeStr);
        for (int chunkSize = Integer.parseInt(chunkSizeStr, 16); chunkSize > 0; chunkSize--) {
            char nextBodyChar = (char) bodyReader.read();
            System.out.print(nextBodyChar);
            body += nextBodyChar;
        }

        String filePath = "test.html";
        File myObj = new File(filePath);
        if (myObj.createNewFile()) {
            System.out.println("File created: " + myObj.getName());
        } else {
            System.out.println("File already exists.");
        }
        try {
            FileWriter myWriter = new FileWriter(filePath);
            myWriter.write(body);
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public void head() {
        String serverName = "toledo.kuleuven.be";
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

    public void get() {
        String serverName = "www.google.com";
        System.out.println("Connecting to " + serverName + " on port " + port);
        try (Socket client = new Socket(serverName, port)) {
            System.out.println("Just connected to " + client.getRemoteSocketAddress());

            try (PrintWriter writer = new PrintWriter(client.getOutputStream(), true)) {

                writer.println("GET / HTTP/1.1");
                writer.println("Host: " + serverName);
                writer.println("User-Agent: Console Http Client");
                // writer.println("Accept: text/html");
                // writer.println("Accept-Language: en-US");
                // Close connection after we are done with it.
                writer.println("Connection: close");
                writer.println();

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()))) {

                    String headers = "";

                    String line;
                    System.out.println("==========HEADERS==========");
                    while ((line = reader.readLine()) != null) {
                        headers += line;
                        // HTTP request ends with a blank line. If the response has a body, it will be
                        // after the blank line.
                        if (line.equals("")) {
                            System.out.println("=========================");
                            System.out.println("=========================");
                            System.out.println("=========================");
                            break;
                        }
                    }

                    System.out.println("Chunked body: " + this.isChunkedBody(headers));
                    this.handleChunckedBody(reader);

                    // System.out.println("==========BODY==========");
                    // while ((line = reader.readLine()) != null) {
                    // System.out.println(line);
                    // // HTTP request ends with a blank line. If the response has a body, it will
                    // be
                    // // after the blank line.
                    // }
                }
            }
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void post() {
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