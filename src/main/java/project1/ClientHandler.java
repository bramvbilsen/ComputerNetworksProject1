package project1;

import java.lang.Runnable;
import java.net.Socket;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

class ClientHandler implements Runnable {
    private Socket clientSocket;
    private String publicPath = "responseWebPage";

    ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void run() {
        try {
            InputStream inputStream = clientSocket.getInputStream();
            OutputStream outputStream = clientSocket.getOutputStream();

            Headers headers;
            do {
                headers = new Headers(inputStream);
                RequestTypes requestType = headers.getRequestType();
                switch (requestType) {
                    case GET:
                        this.get(headers, outputStream);
                        break;
                    case HEAD:
                        this.head(headers, outputStream);
                        break;
                    case POST:
                        this.post(headers, inputStream, outputStream);
                        break;
                    case PUT:
                        this.put(headers, inputStream, outputStream);
                        break;
                    default:
                        break;
                }
            } while (!headers.connectionShouldClose());
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String contentTypeFromPath(String path) throws Exception {
        if (path.endsWith(".html")) {
            return "Content-Type: text/html; charset=UTF-8";
        } else if (path.toLowerCase().endsWith(".jpeg") || path.toLowerCase().endsWith(".jpg")) {
            return "Content-Type: image/jpeg";
        } else if (path.toLowerCase().endsWith(".png")) {
            return "Content-Type: image/png";
        } else if (path.endsWith(".txt")) {
            return "Content-Type: text/plain; charset=UTF-8";
        } else if (path.endsWith(".js")) {
            return "Content-Type: text/javascript; charset=UTF-8";
        } else if (path.endsWith(".css")) {
            return "Content-Type: text/css; charset=UTF-8";
        }
        throw new Exception("File type not supported");
    }

    private void writeToFile(String filePath, String content) throws IOException {
        filePath = this.publicPath + filePath;
        File file = new File(filePath);
        file.getParentFile().mkdirs();
        file.createNewFile();
        FileWriter fileWriter = new FileWriter(filePath);
        fileWriter.write(content);
        fileWriter.close();
        System.out.println("Successfully wrote to the file.");
    }

    private boolean handleProtectedFileChange(String path, PrintWriter writer, OutputStream outputStream)
            throws IOException {
        if (Arrays.equals(path.getBytes(), "/index.html".getBytes())
                || Arrays.equals(path.getBytes(), "/unicorn.png".getBytes())) {
            writer.println("HTTP/1.1 403 Forbidden");
            String html = "<!DOCTYPE html><html><h1>403: Forbidden to overwrite this file.</h1></html>";
            byte[] htmlBytesArray = html.getBytes();
            writer.println("Content-Type: text/html; charset=UTF-8");
            writer.println("Content-Length: " + htmlBytesArray.length);
            writer.println("Content-Disposition: inline;filename=\"error404.html\"");
            writer.println(""); // Mark the end of the headers.
            for (int i = 0; i < htmlBytesArray.length; i++) {
                outputStream.write(htmlBytesArray[i]);
            }
            return true;
        }
        return false;
    }

    private void handleFileWriteError(PrintWriter writer, OutputStream outputStream) throws IOException {
        writer.println("HTTP/1.1 500 Internal Server Error");
        String html = "<!DOCTYPE html><html><h1>500: Internal server error.</h1></html>";
        byte[] htmlBytesArray = html.getBytes();
        writer.println("Content-Type: text/html; charset=UTF-8");
        writer.println("Content-Length: " + htmlBytesArray.length);
        writer.println("Content-Disposition: inline;filename=\"error500.html\"");
        writer.println(""); // Mark the end of the headers.
        for (int i = 0; i < htmlBytesArray.length; i++) {
            outputStream.write(htmlBytesArray[i]);
        }
    }

    /**
     * 
     * @param headers
     * @param bodyOutputStream
     * @return The bytes that were read to figure out the content length.
     * @throws Exception
     */
    public ArrayList<Integer> head(Headers headers, OutputStream outputStream) throws Exception {
        String filePath = headers.getDomainPath();
        if (filePath.length() == 1 && filePath.charAt(0) == '/') {
            filePath = this.publicPath + "/index.html";
        } else {
            filePath = this.publicPath + filePath;
        }

        PrintWriter writer = new PrintWriter(outputStream, true);

        File file = new File(filePath);
        try {
            FileInputStream fileInputStream = new FileInputStream(file);

            ArrayList<Integer> fileBytes = new ArrayList<>();
            int fileByte;
            while ((fileByte = fileInputStream.read()) != -1) {
                fileBytes.add(fileByte);
            }
            fileInputStream.close();
            writer.println("HTTP/1.1 200 OK");
            writer.println("Content-Length: " + fileBytes.size());
            writer.println(this.contentTypeFromPath(filePath));
            writer.println("Content-Disposition: inline;filename=\""
                    + filePath.substring(filePath.lastIndexOf("/") + 1, filePath.length()) + "\"");
            writer.println(""); // Mark the end of the headers.
            return fileBytes;
        } catch (FileNotFoundException e) {
            writer.println("HTTP/1.1 404 Not Found");
            String html = "<!DOCTYPE html><html><h1>404: File not found.</h1></html>";
            byte[] htmlBytesArray = html.getBytes();
            writer.println("Content-Type: text/html; charset=UTF-8");
            writer.println("Content-Length: " + htmlBytesArray.length);
            writer.println("Content-Disposition: inline;filename=\"error404.html\"");
            writer.println(""); // Mark the end of the headers.
            ArrayList<Integer> htmlBytes = new ArrayList<>();
            for (int i = 0; i < htmlBytesArray.length; i++) {
                htmlBytes.add((int) htmlBytesArray[i]);
            }
            return htmlBytes;
        }
    }

    public void get(Headers headers, OutputStream outputStream) throws Exception {
        ArrayList<Integer> htmlBytes = this.head(headers, outputStream);
        for (int i = 0; i < htmlBytes.size(); i++) {
            outputStream.write(htmlBytes.get(i));
        }
    }

    public void put(Headers headers, InputStream inputStream, OutputStream outputStream) throws IOException {
        PrintWriter writer = new PrintWriter(outputStream, true);

        String path = headers.getDomainPath();
        if (handleProtectedFileChange(path, writer, outputStream)) {
            return;
        }

        String receivedString = "";
        for (int i = headers.getContentLength() - 1; i >= 0; i--) {
            receivedString += (char) inputStream.read();
        }

        try {
            this.writeToFile(path, receivedString);
            writer.println("HTTP/1.1 201 Created");
            String html = "<!DOCTYPE html><html><h1>201: Created.</h1></html>";
            byte[] htmlBytesArray = html.getBytes();
            writer.println("Content-Type: text/html; charset=UTF-8");
            writer.println("Content-Length: " + htmlBytesArray.length);
            writer.println("Content-Disposition: inline;filename=\"Created201.html\"");
            writer.println(""); // Mark the end of the headers.
            for (int i = 0; i < htmlBytesArray.length; i++) {
                outputStream.write(htmlBytesArray[i]);
            }
        } catch (IOException e) {
            this.handleFileWriteError(writer, outputStream);
        }
    }

    public void post(Headers headers, InputStream inputStream, OutputStream outputStream) throws IOException {
        String path = headers.getDomainPath();
        File file = new File(this.publicPath + path);
        if (!file.exists()) {
            this.put(headers, inputStream, outputStream);
            return;
        }

        PrintWriter writer = new PrintWriter(outputStream, true);

        if (handleProtectedFileChange(path, writer, outputStream)) {
            return;
        }

        String receivedString = "";
        for (int i = headers.getContentLength() - 1; i >= 0; i--) {
            receivedString += (char) inputStream.read();
        }

        String fileContent = "";
        Scanner myReader = new Scanner(file);
        while (myReader.hasNextLine()) {
            fileContent += myReader.nextLine();
        }
        myReader.close();

        try {
            this.writeToFile(path, fileContent + receivedString);
            writer.println("HTTP/1.1 200 OK");
            String html = "<!DOCTYPE html><html><h1>200: File modified.</h1></html>";
            byte[] htmlBytesArray = html.getBytes();
            writer.println("Content-Type: text/html; charset=UTF-8");
            writer.println("Content-Length: " + htmlBytesArray.length);
            writer.println("Content-Disposition: inline;filename=\"Modified200.html\"");
            writer.println(""); // Mark the end of the headers.
            for (int i = 0; i < htmlBytesArray.length; i++) {
                outputStream.write(htmlBytesArray[i]);
            }
        } catch (IOException e) {
            this.handleFileWriteError(writer, outputStream);
        }
    }
}