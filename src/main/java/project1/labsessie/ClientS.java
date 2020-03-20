package project1.labsessie;

// File Name GreetingClient.java
import java.net.*;
import java.io.*;

public class ClientS {

    public ClientS(int port) {
        this.port = port;
    }

    public int port;

    public static void main(String[] args) {
        ClientS client = new ClientS(80);
        // client.head();
        client.get();
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