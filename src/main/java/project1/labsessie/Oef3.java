package project1.labsessie;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Oef3 {
    public static void main(String[] args) {
        try {
            File myObj = new File("./src/main/java/oef1/test.txt");
            Scanner myReader = new Scanner(myObj);
            String textFileString = "";
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                textFileString += data;
            }
            myReader.close();

            Socket socket = new Socket("localhost", 3000);
            OutputStream output = socket.getOutputStream();
            output.write(textFileString.getBytes());
            socket.close();
        } catch (Exception e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}
