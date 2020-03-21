package project1;

import java.net.*;
import java.util.ArrayList;
import java.io.*;

public class ServerS extends Thread {

    private ServerSocket serverSocket;
    private String publicPath = "responseWebPage";

    public static void main(String[] args) throws IOException {
        (new ServerS(80)).start();
    }

    public ServerS(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
        serverSocket.setSoTimeout(Integer.MAX_VALUE);
    }

    @Override
    public void run() {
        try {
            while (true) {
                Socket socket = this.serverSocket.accept();
                socket.setKeepAlive(true);

                (new ServerS(0)).start();

                InputStream inputStream = socket.getInputStream();

                Headers headers = new Headers(inputStream);
                RequestTypes requestType = headers.getRequestType();
                switch (requestType) {
                    case GET:
                        this.get(headers, socket.getOutputStream());
                    case HEAD:
                        break;
                    case POST:
                        break;
                    case PUT:
                        break;
                    default:
                        break;
                }
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private String contentTypeFromPath(String path) throws Exception {
        if (path.endsWith(".html")) {
            return "Content-Type: html/plain; charset=UTF-8";
        } else if (path.toLowerCase().endsWith(".jpeg") || path.toLowerCase().endsWith(".jpg")) {
            return "Content-Type: image/jpeg";
        } else if (path.toLowerCase().endsWith(".png")) {
            return "Content-Type: image/png";
        } else if (path.endsWith(".txt")) {
            return "Content-Type: text/plain; charset=UTF-8";
        }
        throw new Exception("File type not supported");
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
        System.out.println(filePath);
        if (filePath.length() == 1 && filePath.charAt(0) == '/') {
            filePath = this.publicPath + "/index.html";
        } else {
            filePath = this.publicPath + filePath;
        }
        System.out.println("GET request at path: " + filePath);

        PrintWriter writer = new PrintWriter(outputStream, true);

        File file = new File(filePath);
        try {
            FileInputStream fileInputStream = new FileInputStream(file);

            writer.println("HTTP/1.1 200 Ok");
            writer.println(this.contentTypeFromPath(filePath));

            ArrayList<Integer> htmlBytes = new ArrayList<>();
            int fileByte;
            while ((fileByte = fileInputStream.read()) != -1) {
                htmlBytes.add(fileByte);
            }
            fileInputStream.close();
            writer.println("Content-Length: " + htmlBytes.size());
            writer.println(""); // Mark the end of the headers.
            return htmlBytes;
        } catch (FileNotFoundException e) {
            writer.println("HTTP/1.1 404 Not Found");
            String html = "<!DOCTYPE html><html><h1>404: File not found</h1></html>";
            byte[] htmlBytesArray = html.getBytes();
            writer.println("Content-Type: html/plain; charset=UTF-8");
            writer.println("Content-Length: " + htmlBytesArray.length);
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
}