package pgs;

public class PgsMineApp {

    /**
     * Number of parameters that need to be passed from command line
     */
    private static final int REQUIRED_PARAMS_COUNT = 14;

    private static final String INPUT_FILE_FLAG = "-i";

    private static final String OUTPUT_FILE_FLAG = "-o";

    private static final String WORKER_COUNT_FLAG = "-cWorker";

    private static final String WORKER_TIME_FLAG = "-tWorker";

    private static final String LORRY_CAP_FLAG = "-cLorry";

    private static final String FERRY_CAP_FLAG = "-cFerry";

    private static final String LORRY_TIME_FLAG = "-tLorry";

    public static void main(String[] args) {
        if (args.length != REQUIRED_PARAMS_COUNT) {
            System.out.println("Invalid parameter count passed!");
            printHelp();
            return;
        }

        SimulationConfig config = new SimulationConfig();
        for (int i = 0; i < args.length; i += 2) {  // Incrementing two, cause processing two in each iteration
            switch (args[i]) {
                case INPUT_FILE_FLAG:
                    config.setInputFilePath(args[i + 1]);
                    break;
                case OUTPUT_FILE_FLAG:
                    config.setOutputFilePath(args[i + 1]);
                    break;
                case WORKER_COUNT_FLAG:
                    try {
                        config.setWorkerCount(Integer.parseInt(args[i + 1]));
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid parameter passed as worker count: " + args[i + 1]);
                        System.out.println("Worker count has to be an integer.");
                        printHelp();
                        return;
                    }
                case WORKER_TIME_FLAG:
                    try {
                        config.setMaxWorkerResourceProcessingTime(Integer.parseInt(args[i + 1]));
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid parameter passed as max worker resource processing time: " + args[i + 1]);
                        System.out.println("Max worker resource processing time has to be an integer.");
                        printHelp();
                        return;
                    }
                case LORRY_CAP_FLAG:
                    try {
                        config.setLorryCapacity(Integer.parseInt(args[i + 1]));
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid parameter passed as lorry capacity: " + args[i + 1]);
                        System.out.println("Lorry capacity has to be an integer.");
                        printHelp();
                        return;
                    }
                case LORRY_TIME_FLAG:
                    try {
                        config.setMaxLorryTransportTime(Integer.parseInt(args[i + 1]));
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid parameter passed as max lorry transport time: " + args[i + 1]);
                        System.out.println("Max lorry transport time has to be an integer.");
                        printHelp();
                        return;
                    }
                case FERRY_CAP_FLAG:
                    try {
                        config.setFerryCapacity(Integer.parseInt(args[i + 1]));
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid parameter passed as ferry capacity: " + args[i + 1]);
                        System.out.println("Ferry capacity has to be an integer.");
                        printHelp();
                        return;
                    }
                default:
                    System.out.println("Unknown switch passed!");
                    printHelp();
                    return;
            }
        }

        printPassedParameters(config);
        Simulation.run(config);
    }

    /**
     * Prints a little help with the program usage.
     */
    private static void printHelp() {
        System.out.println("Required parameters:\n" +
                INPUT_FILE_FLAG + " <input file path>\n" +
                OUTPUT_FILE_FLAG + " <output file path>\n" +
                WORKER_COUNT_FLAG + " <worker count>\n" +
                WORKER_TIME_FLAG + " <max worker resource processing time (sec)>\n" +
                LORRY_CAP_FLAG + " <Lorry capacity>\n" +
                LORRY_TIME_FLAG + " <may Lorry transport time>\n" +
                FERRY_CAP_FLAG + " <Ferry capacity>\n" +
                "Parameters do not need to be in this order.");
    }

    /**
     * Prints the configuration that was passed from the command line
     * @param config passed configuration
     */
    private static void printPassedParameters(final SimulationConfig config) {
        System.out.println("Passed configuration:\n" +
                INPUT_FILE_FLAG + " " + config.getInputFilePath() + "\n" +
                OUTPUT_FILE_FLAG + " " + config.getOutputFilePath() +"\n" +
                WORKER_COUNT_FLAG + " " + config.getWorkerCount() + "\n" +
                WORKER_TIME_FLAG + " " + config.getMaxWorkerResourceProcessingTime() + "(sec)\n" +
                LORRY_CAP_FLAG + " " + config.getLorryCapacity() + "\n" +
                LORRY_TIME_FLAG + " " + config.getLorryCapacity() + "\n" +
                FERRY_CAP_FLAG + " " + config.getFerryCapacity());
    }
}
