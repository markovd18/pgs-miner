package pgs;

import pgs.mine.Map;

import java.io.File;
import java.io.IOException;

public class MainTest {

    public static boolean validImputMapFileTest() {
        return isImputFileValid("testFiles/valid.txt");
    }

    public static boolean invalidImputMapFileTest() {
        return !isImputFileValid("testFiles/invalid.txt");
    }

    private static boolean isImputFileValid(final String filePath) {
        try {
            Map map = new Map(new File(filePath));
            return true;
        } catch (IOException e) {
            return false;
        }
    }
    public static void main(String[] args) {

        System.out.print("Valid imput map file test...\t");
        if (validImputMapFileTest()) {
            System.out.println("OK!");
        } else {
            System.out.println("ERROR!");
        }

        System.out.print("Invalid input map file test...\t");
        if (invalidImputMapFileTest()) {
            System.out.println("OK!");
        } else {
            System.out.println("ERROR!");
        }


        System.out.println("Testing finished.");
    }
}
