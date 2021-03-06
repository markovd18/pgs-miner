package pgs.worker;

import pgs.mine.Block;
import pgs.mine.Map;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Foreman identifies blocks of resources on a map and delegates individual workers to process them.
 *
 * @author <a href="mailto:markovd@students.zcu.cz">David Markov</a>
 * @since 6.3.2021
 */
public class Foreman {

    private Queue<Block> unprocessedBlocks;

    public Foreman() {
    }

    /**
     * Identifies all resource blocks in given map
     * @param map map to parse
     */
    public void identifyResourceBlocks(final Map map) {
        Queue<Block> blocks = new LinkedList<>();

        int currentBlockSize = 0;
        for (String line : map.getLines()) {
            for (int i = 0; i < line.length(); i++) {
                if (line.charAt(i) == 'x') {    // Found resource, incrementing size of the block
                    currentBlockSize++;
                } else if (currentBlockSize > 0) {  // Found free space, if there was block, create it
                    blocks.add(new Block(currentBlockSize));
                    currentBlockSize = 0;
                }
            }

            if (currentBlockSize > 0) { // If line ended with block, create it
                blocks.add(new Block(currentBlockSize));
                currentBlockSize = 0;
            }
        }

        this.unprocessedBlocks = blocks;
    }

    /**
     * Starts delegating work among available workers. If no available workers are passed, immediately returns.
     * @param availableWorkers workers to delegate
     */
    public void delegateWorkers(final WorkerQueue availableWorkers) {
        if (availableWorkers == null || availableWorkers.size() == 0) {
            return;
        }

        while (!unprocessedBlocks.isEmpty()) {
            Worker currentWorker = availableWorkers.getAvailableWorker();
            Block blockToProcess = unprocessedBlocks.poll();
            boolean result = currentWorker.processBlock(blockToProcess, () -> availableWorkers.addWorker(currentWorker));
            if (!result) {
                unprocessedBlocks.add(blockToProcess); // If something went wrong, we try to process the block again
            }
        }
    }
}
