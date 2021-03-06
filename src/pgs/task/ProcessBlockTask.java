package pgs.task;

import pgs.mine.Block;

public class ProcessBlockTask implements Runnable {

    private static int workerId = 0;
    private final Block processedBlock;
    private final Runnable afterBlockProcessed;

    public ProcessBlockTask(final Block processedBlock, final Runnable afterBlockProcessed) {
        this.processedBlock = processedBlock;
        this.afterBlockProcessed = afterBlockProcessed;
        workerId++;
    }

    @Override
    public void run() {

    }
}
