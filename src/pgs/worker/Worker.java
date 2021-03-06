package pgs.worker;

import pgs.cargo.CargoVehicle;
import pgs.mine.Block;
import pgs.task.ProcessBlockTask;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * Worker, who's responsibility is mining blocks of resources and loading them into the {@link CargoVehicle}
 *
 * @author <a href="markovd@students.zcu.cz>David Markov</a>
 * @since 6.3.2021
 */
public class Worker {
    /**
     * Maximum number of seconds that it takes to process one block
     */
    private final int maxResourceProcessingTime;
    /**
     * The parallel task of processing a block of resources
     */
    private Future<?> blockProcessing;

    public Worker(final int maxResourceProcessingTime) {
        this.maxResourceProcessingTime = maxResourceProcessingTime;
    }

    public int getMaxResourceProcessingTime() {
        return maxResourceProcessingTime;
    }

    /**
     * Commands this worker to process given block of resources. Worker can be given a task, that he should do after
     * the block is processed. If given task is null, worker doesn't do anything afterwards - may be null. Returns
     * true, if the worker successfully started processing given block. If the worker is already processing any block,
     * he can't process two block simultaneously, therefore returns false.
     *
     * @param block block to process
     * @param afterBlockProcessed task to do after processing the block
     * @return true, if block processing has started, otherwise false
     */
    public boolean processBlock(final Block block, final Runnable afterBlockProcessed) {
        if (blockProcessing != null && !blockProcessing.isDone()) {
            return false; // We are already busy, cannot process multiple blocks
        }

        blockProcessing = Executors.newSingleThreadExecutor().submit(new ProcessBlockTask(block, afterBlockProcessed));

        return true;
    }

    private boolean takeAwayMaterial(final CargoVehicle vehicle) {
        if (vehicle == null) {
            return true;    // Nowhere to take away the material - throwing it on the ground
        }

        //TODO markovda take away the material to the vehicle
        return true;
    }
}
