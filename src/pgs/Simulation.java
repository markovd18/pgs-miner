package pgs;

import pgs.cargo.Ferry;
import pgs.cargo.Lorry;
import pgs.mine.Map;
import pgs.mine.Mine;
import pgs.worker.Foreman;
import pgs.worker.Worker;
import pgs.worker.WorkerQueue;

import java.io.File;
import java.io.IOException;

/**
 * Simulation of the resource mining process.
 *
 * @author <a href="mailto:markovd@students.zcu.cz">David Markov</a>
 * @since 7.3.2021
 */
public class Simulation {
    /**
     * Number of threads that have been created since the start of the simulation
     */
    public static int threadCount = 1;

    /**
     * Starts the simulation with given configuration.
     * @param config configuration of this simulation
     */
    public static void run(final SimulationConfig config) {
        if (config == null) {
            System.err.println("No configuration passed! Simulation will not start.");
            return;
        }

        if (!isConfigValid(config)) {
            System.err.println("Invalid configuration passed! Simulation will not start.");
            return;
        }

        Map mineMap;
        try {
            mineMap = new Map(new File(config.getInputFilePath()));
        } catch (IOException e) {
            System.err.println("Given map input file is invalid!");
            return;
        }

        Logger.createInstance(config.getOutputFilePath());
        Lorry.setDefaultCapacity(config.getLorryCapacity());

        Mine mine = new Mine(mineMap);
        Foreman foreman = new Foreman(threadCount++);
        WorkerQueue workerQueue = new WorkerQueue();
        for (int i = 0; i < config.getWorkerCount(); i++) {
            workerQueue.addWorker(new Worker(threadCount++, config.getMaxWorkerResourceProcessingTime()));
        }

        foreman.analyzeMineResources(mine);
        mine.replaceSteadyLorry(new Lorry(Lorry.getDefaultCapacity()), null); // null, because there is no steady Lorry atm

        foreman.delegateWorkers(workerQueue, new Ferry(config.getFerryCapacity()));   // Starting the entire parallel simulation
    }

    /**
     * Checks if passed configuration is valid.
     * @param config configuration to check
     * @return true, if the configuration is valid, otherwise false
     */
    private static boolean isConfigValid(final SimulationConfig config) {
        if (config == null) {
            return false;
        }

        return (config.getInputFilePath() != null && !config.getInputFilePath().isEmpty() ||
                config.getOutputFilePath() != null && !config.getOutputFilePath().isEmpty() ||
                config.getWorkerCount() > 0 && config.getMaxWorkerResourceProcessingTime() > 0 ||
                config.getLorryCapacity() > 0 && config.getMaxLorryTransportTime() > 0 &&
                config.getFerryCapacity() > 0);
    }
}
