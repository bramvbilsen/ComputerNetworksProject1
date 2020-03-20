package project1.labsessie;

import java.io.*;
import java.net.*;

class Oef2 {

    private static int port = 3000;

    public static void main(String[] args) {
        try {
            System.out.println("Running server on port: " + port);
            ServerSocket serverSocket = new ServerSocket(port);
            while (true) {
                Socket socket = serverSocket.accept();

                InputStream input = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));

                OutputStream output = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(output, true);

                System.out.println("==============");
                System.out.println("Bytes available: " + input.available());

                while (input.available() > 0) {
                    System.out.print((char) input.read());
                }

                System.out.println("\n==============");

                writer.println("U MADE IT");

                reader.close();
                socket.close();
            }
        } catch (Exception e) {

        }
    }
}