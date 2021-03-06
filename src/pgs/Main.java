package pgs;


import pgs.mine.Block;
import pgs.mine.Map;
import pgs.worker.Foreman;
import pgs.worker.Worker;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        try {
            Map map = new Map(new File("testFiles/valid.txt"));
            List<String> lines = map.getLines();
            for (String line : lines) {
                System.out.println(line);
            }

            Foreman foreman = new Foreman();
            foreman.identifyResourceBlocks(map);
        } catch (IOException e) {
            //
        }


    }
}
