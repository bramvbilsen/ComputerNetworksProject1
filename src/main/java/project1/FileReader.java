package project1;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class FileReader {

    public static void readFile(String path) throws FileNotFoundException {
        File file = new File("./responseWebPage/response.txt");
        FileInputStream inputStream = new FileInputStream(file);
    }
}