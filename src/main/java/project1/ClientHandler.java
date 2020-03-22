package project1;

import java.lang.Runnable;
import java.net.Socket;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Used by `ServerS` to handle client connections.
 */
class ClientHandler implements Runnable {
    /**
     * Socket used to communicate to the client.
     */
    private Socket clientSocket;
    /**
     * Server path available to the public.
     */
    private String publicPath = "responseWebPage";

    ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            InputStream inputStream = clientSocket.getInputStream();
            OutputStream outputStream = clientSocket.getOutputStream();

            Headers headers;
            do {
                headers = new Headers(inputStream);
                RequestTypes requestType = headers.getRequestType();
                if (requestType == null) {
                    clientSocket.close();
                    return;
                }
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

    /**
     * Gets the content type header for the file at the provided path by looking at
     * the file extension.
     * 
     * @param path Path of file
     * @return Content type header for the file at the given path.
     * @throws Exception If the file type is not supported.
     */
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

    /**
     * Writes the provided content to the provided file path.
     * 
     * @param filePath Path to the file.
     * @param content  Text to be put into the file.
     * @throws IOException if the named file exists but is a directory rather than a
     *                     regular file, does not exist but cannot be created, or
     *                     cannot be opened for any other reason
     */
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

    /**
     * Checks and handles a client trying to change a file that is protected. Will
     * return a 404 to the client if a protected file was at the path. Otherwise
     * won't talk to client.
     * 
     * @param path         Path to the file
     * @param writer       PrintWriter to write the headers to the client's
     *                     inputtream.
     * @param outputStream Stream to write the html response to the client's
     *                     inputstream.
     * @return false if the path is not to a protected file, otherwise true.
     * @throws IOException if an I/O error occurs. In particular, an IOException may
     *                     be thrown if the output stream has been closed.
     */
    private boolean handleProtectedFileChange(String path, PrintWriter writer, OutputStream outputStream)
            throws IOException {
        if (Arrays.equals(path.getBytes(), "/index.html".getBytes())
                || Arrays.equals(path.getBytes(), "/unicorn.png".getBytes())
                || Arrays.equals(path.getBytes(), "/index.css".getBytes())
                || Arrays.equals(path.getBytes(), "/index.js".getBytes())) {
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

    /**
     * Handles an error while writing file and notifies client.
     * 
     * @param writer       writer to communicate with the client.
     * @param outputStream outputstream to communicate with the client.
     * @throws IOException
     */
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
     * Handles head request from client.
     * 
     * @param headers      Headers from the client's request.
     * @param outputStream Outputstream used to communicate with client.
     * @return The bytes that were read to figure out the content length.
     * @throws Exception If the file type is not supported or if an I/O error
     *                   occurs.
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

    /**
     * Handles get request from client.
     * 
     * @param headers      Headers from the client's request.
     * @param outputStream Outputstream used to communicate with client.
     * @return The bytes that were read to figure out the content length.
     * @throws Exception If the file type is not supported or if an I/O error
     *                   occurs.
     */
    public void get(Headers headers, OutputStream outputStream) throws Exception {
        ArrayList<Integer> htmlBytes = this.head(headers, outputStream);
        for (int i = 0; i < htmlBytes.size(); i++) {
            outputStream.write(htmlBytes.get(i));
        }
    }

    /**
     * Handles put request from client.
     * 
     * @param headers      Headers from the client's request.
     * @param inputStream  Inputstream to read body of client's request.
     * @param outputStream Outputstream used to communicate with client.
     * @return The bytes that were read to figure out the content length.
     * @throws IOException if the named file exists but is a directory rather than a
     *                     regular file, does not exist but cannot be created, or
     *                     cannot be opened for any other reason or in general when
     *                     an I/O error occurs.
     */
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

    /**
     * Handles post request from client.
     * 
     * @param headers      Headers from the client's request.
     * @param inputStream  Inputstream to read body of client's request.
     * @param outputStream Outputstream used to communicate with client.
     * @return The bytes that were read to figure out the content length.
     * @throws IOException if the named file exists but is a directory rather than a
     *                     regular file, does not exist but cannot be created, or
     *                     cannot be opened for any other reason or in general when
     *                     an I/O error occurs.
     */
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