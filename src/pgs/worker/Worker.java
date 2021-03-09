package pgs.worker;

import pgs.HasId;
import pgs.cargo.CargoVehicle;
import pgs.mine.Block;
import pgs.task.ProcessBlockTask;

import java.security.InvalidParameterException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Worker, who's responsibility is mining blocks of resources and loading them into the {@link CargoVehicle}
 *
 * @author <a href="markovd@students.zcu.cz>David Markov</a>
 * @since 6.3.2021
 */
public class Worker implements HasId {
    /**
     * ID of a worker
     */
    private final int workerId;
    /**
     * Maximum number of seconds that it takes to process one block
     */
    private final int maxResourceProcessingTime;
    /**
     * The parallel task of processing a block of resources
     */
    private Future<?> blockProcessing;

    /**
     * Constructs a new worker and trains him in a way, that processing of single resource in a resource block
     * will take him maximum of {@code maxResourceProcessingTime} seconds.
     * @param maxResourceProcessingTime maximum number of seconds it will take worker to process one resource
     */
    public Worker(final int workerId, final int maxResourceProcessingTime) {
        if (maxResourceProcessingTime <= 0) {
            throw new InvalidParameterException("Max resource processing time has to be positive!");
        }

        this.maxResourceProcessingTime = maxResourceProcessingTime;
        this.workerId = workerId;
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
    public Future<?> processBlock(final Block block, final Runnable afterBlockProcessed) {
        if (blockProcessing != null && !blockProcessing.isDone()) {
            return null; // We are already busy, cannot process multiple blocks
        }

        blockProcessing = Executors.newSingleThreadExecutor().submit(
                new ProcessBlockTask(this, block, maxResourceProcessingTime, afterBlockProcessed));

        return blockProcessing;
    }

    @Override
    public int getId() {
        return workerId;
    }
}
