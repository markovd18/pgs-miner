package pgs;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Logger logs important information about the mining process into the log file.
 *
 * @author <a href="markovd@students.zcu.cz">David Markov</a>
 * @since 7.3.2021
 */
public class Logger {
    /**
     * Default name of the log file
     */
    private static final String LOG_FILE_PATH = "pgs-mine.log";
    /**
     * Logger instance - acting as a singleton
     */
    private static Logger INSTANCE;
    /**
     * File that will be logged into
     */
    private final File logFile;
    /**
     * Flag indicating, that some thread is already writing into the file
     */
    private boolean fileOpen = false;

    private Logger(String logFilePath) { // Acting as a singleton
        if (logFilePath == null) {
            logFilePath = LOG_FILE_PATH;
            System.out.println("Logger output path not set. Falling back to default path... (" + LOG_FILE_PATH + ")");
        }

        this.logFile = new File(logFilePath);
    }

    /**
     * Creates new instance of logger. If the instance already exists, returns false.
     * @param logFilePath output file of the logger
     * @return true, if the instance was successfully created, otherwise false
     */
    public static boolean createInstance(final String logFilePath) {
        if (INSTANCE != null) {
            return false;
        }

        INSTANCE = new Logger(logFilePath);
        return true;
    }

    /**
     * Returns the only existing instance of Logger. If {@code createInstance} was not called previously,
     * always will return NULL.
     * @return Logger instance
     */
    public static Logger getInstance() {
        return INSTANCE;
    }

    /**
     * Logs an event with given description into the log file. Resulting log is in following format:
     * "timestamp with millisecond precision" "object name" "object ID" "event description"
     * @param object object with ID that logs an event
     * @param description description of logged event
     */
    public synchronized void logEvent(final HasId object, final String description) {
        try {
            while (fileOpen) {  // We need to be thread safe
                wait();
            }
            BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true));
            writer.write(constructLog(object, description));
            writer.close();

            fileOpen = false;    // When we wrote what we needed, we close file and notify others waiting
            notify();
        } catch (IOException e) {
            System.err.println("Error while opening the log file!\n" + e.getMessage());
        } catch (InterruptedException e) {
            System.err.println("Logger was unexpectedly interrupted while waiting for file to be open!\n" + e.getMessage());
        }
    }

    /**
     * Creates a timestamp of the moment of calling.
     * @return timestamp
     */
    private String getTimestamp() {
        return LocalDateTime.now().toString();
    }

    /**
     * Returns the name of given object.
     * @param object object to get name of
     * @return name of the object
     */
    private String getObjectName(final HasId object) {
        return object.getClass().getSimpleName();
    }

    /**
     * Constructs a log message from given parameters.
     * @param object object logging an event
     * @param description description of the event
     * @return message to be logged
     */
    private String constructLog(final HasId object, final String description) {
        return getTimestamp() + " " + getObjectName(object) + " " + object.getId() + " " + description + "\n";
    }
}
