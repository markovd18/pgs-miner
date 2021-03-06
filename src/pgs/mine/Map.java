package pgs.mine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Map represents input file with sources to mine. Valid input file consists of "x", "space" and line-separator
 * characters only.
 *
 * @author <a href="mailto:markovd@students.zcu.cz">David Markov</a>
 * @since 6.3.2021
 */
public class Map {

    /**
     * Set of illegal characters that should not be located in the input file
     */
    private static final String ILLEGAL_CHARACTERS = "[^x ]+";
    /**
     * Input map file
     */
    private final File mapFile;

    /**
     * Creates an instance of input map file and validates it's content. Input map file has to contain only characters
     * of "x", " " and line-separators. If the input file contains any different character, throws {@link IOException}.
     *
     * @param mapFile input map file
     * @throws IOException when input map file contains not allowed characters
     */
    public Map(final File mapFile) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(mapFile));
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.matches(ILLEGAL_CHARACTERS)){
                throw new IOException("Passed file is not valid input map file!");
            }
        }

        this.mapFile = mapFile;
    }

    /**
     * Returns all lines in created input map file.
     * @return list of input map file lines
     */
    public List<String> getLines() {
        if (mapFile == null) {
            return null;
        }

        List<String> lines = new ArrayList<>();
        String line;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(mapFile));
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
            return lines;
        } catch (IOException e) {
            System.err.println("Error while reading input map file!\n" + e.getMessage());
            return null;
        }
    }
}
