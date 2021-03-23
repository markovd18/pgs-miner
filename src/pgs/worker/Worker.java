package pgs.worker;

import pgs.PerformsTask;
import pgs.cargo.CargoVehicle;
import pgs.mine.Block;
import pgs.task.ProcessBlockTask;

import java.security.InvalidParameterException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Worker, who's responsibility is mining blocks of resources and loading them into the {@link CargoVehicle}
 *
 * @author <a href="markovd@students.zcu.cz>David Markov</a>
 * @since 6.3.2021
 */
public class Worker implements PerformsTask {
    /**
     * Executor for submitting parallel tasks.
     */
    private static final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
    static {
//        executor.allowCoreThreadTimeOut(true);
        executor.setKeepAliveTime(0L, TimeUnit.MILLISECONDS);
    }
    /**
     * ID of a worker
     */
    private final int workerId;
    /**
     * Maximum number of milliseconds that it takes to process one block
     */
    private final int maxResourceProcessingTime;
    /**
     * Number of resources blocks by a worker.
     */
    private int processedResources = 0;
    /**
     * Flag indicating whether any task is currently in progress.
     */
    private boolean taskInProgress = false;

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

    /**
     * Returns the number of processed resources by this worker.
     * @return number of processed resources
     */
    public int getProcessedResources() {
        return this.processedResources;
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
        if (taskInProgress) {   // We are already busy
            return null;
        }

        taskInProgress = true;
        processedResources += block.getLength();
        return executor.submit(
                new ProcessBlockTask(this, block, maxResourceProcessingTime, afterBlockProcessed));
    }

    /**
     * Sets the core expected number of workers, that will perform asynchronous tasks, to given value.
     * @param workerCount expected number of workers to perform an asynchronous task
     */
    public static void setWorkerCount(final int workerCount) {
        executor.setCorePoolSize(workerCount);
    }

    /**
     * All workers will be sent home and all their actions will be ended.
     */
    public static void sendWorkersHome() {
        executor.shutdown();
    }

    @Override
    public int getId() {
        return workerId;
    }

    @Override
    public void setTaskDone() {
        taskInProgress = false;
    }
}
