package pgs.task;

import pgs.HasId;
import pgs.Logger;
import pgs.mine.Block;

import java.util.Random;

/**
 * Parallel task of processing of given resource block.
 *
 * @author <a href="mailto:markovd@students.zcu.cz">David Markov</a>
 * @since 7.3.2021
 */
public class ProcessBlockTask implements Runnable {
    /**
     * Number of milliseconds in one second
     */
    private static final int MILLIS_IN_SECOND = 1000;
    /**
     * Processor performing this task
     */
    private final HasId processor;
    /**
     * Block to be processed
     */
    private final Block processedBlock;
    /**
     * Maximum time in seconds it may take to process a single resource in a block
     */
    private final int maxResourceProcessingTime;
    /**
     * Number of seconds it took to process the entire block of resources
     */
    private int blockProcessingTime;
    /**
     * Action to perform, after the block is processed
     */
    private final Runnable afterBlockProcessed;

    /**
     * Creates new tast to process a block of resources. Each resource in given block will be processed by a worker with ID {@code workerId}
     * for maximum of {@code maxResourceProcessingTime} seconds. After the block is processed, worker will preform {@code afterBlockProcessed}
     * action. If this action is null, worker will just finish the task.
     * @param processor object performing this task
     * @param processedBlock block of resources to process
     * @param maxResourceProcessingTime maximum number of seconds it will take to process one resource in given block
     * @param afterBlockProcessed task to perform after the block is processed - may be null
     */
    public ProcessBlockTask(final HasId processor, final Block processedBlock, final int maxResourceProcessingTime, final Runnable afterBlockProcessed) {
        this.processor = processor;
        this.processedBlock = processedBlock;
        this.maxResourceProcessingTime = maxResourceProcessingTime;
        this.afterBlockProcessed = afterBlockProcessed;
    }

    @Override
    public void run() {
        for (int i = 0; i < processedBlock.getLength(); i++) {
            int resourceProcessingTime = getNextResourceProcessingTime();

            try {
                Thread.sleep((long)  resourceProcessingTime * MILLIS_IN_SECOND); // Simulating processing of the resource
            } catch (InterruptedException e) {
                System.err.println("Resource processor " + processor.getId() + " was interrupted during resource processing!\n" + e.getMessage());
            }

            this.blockProcessingTime += resourceProcessingTime;
            Logger.getInstance().logEvent(
                    processor, "Resource processing finished (took " + resourceProcessingTime + " seconds)");
        }

        Logger.getInstance().logEvent(
                processor, "Block processing finished (took " + blockProcessingTime + " seconds)");

        if (afterBlockProcessed != null) { // If we were supposed to do something after processing the block, we do it
            afterBlockProcessed.run();
        }
    }

    /**
     * Returns number of seconds it will take to process next resource
     * @return number of seconds it will take to process next resource
     */
    private int getNextResourceProcessingTime() {
        return new Random().nextInt(this.maxResourceProcessingTime) + 1;
    }
}
