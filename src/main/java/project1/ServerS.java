package project1;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Server class that handles multiple threads.
 */
public class ServerS {

    private static final int PORT_NUMBER = 80;
    private static ServerSocket serverSocket;
    private static ClientHandler clientHandler;
    private static Thread thread;

    public static void main(String[] args) throws IOException {
        serverSocket = new ServerSocket(PORT_NUMBER);

        while (true) {
            clientHandler = new ClientHandler(serverSocket.accept());
            thread = new Thread(clientHandler);
            thread.start();
        }
    }

    @Override
    protected void finalize() throws IOException {
        serverSocket.close();
    }
}