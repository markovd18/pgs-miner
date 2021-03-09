package pgs.worker;

import pgs.HasId;
import pgs.Logger;
import pgs.Simulation;
import pgs.cargo.CargoVehicle;
import pgs.cargo.Lorry;
import pgs.mine.Block;
import pgs.mine.Mine;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Foreman identifies blocks of resources on a map and delegates individual workers to process them.
 *
 * @author <a href="mailto:markovd@students.zcu.cz">David Markov</a>
 * @since 6.3.2021
 */
public class Foreman implements HasId {

    /**
     * Identified of this Foreman
     */
    private final int foremanId;
    /**
     * A mine that the foreman is delegated to.
     */
    private Mine mine;

    /**
     * Constructs new Foreman with given ID.
     * @param foremanId id
     */
    public Foreman(final int foremanId) {
        this.foremanId = foremanId;
    }

    /**
     * Analyzes the mine and identifies all resource blocks in it.
     * @param mine mine to analyze
     */
    public void analyzeMineResources(final Mine mine) {
        if (mine == null) {
            return;
        }

        this.mine = mine;
        Queue<Block> blocks = new LinkedList<>();
        int resourceCount = 0;

        int currentBlockSize = 0;
        for (String line : mine.getMineMap().getLines()) {
            for (int i = 0; i < line.length(); i++) {
                if (line.charAt(i) == 'x') {    // Found resource, incrementing size of the block
                    currentBlockSize++;
                } else if (currentBlockSize > 0) {  // Found free space, if there was block, create it
                    resourceCount += currentBlockSize;
                    blocks.add(new Block(currentBlockSize));
                    currentBlockSize = 0;
                }
            }

            if (currentBlockSize > 0) { // If line ended with block, create it
                resourceCount += currentBlockSize;
                blocks.add(new Block(currentBlockSize));
                currentBlockSize = 0;
            }
        }

        this.mine.setUnprocessedBlocks(blocks);
        Logger.getInstance().logEvent(this,
                "Mine analysis completed. Found " + resourceCount + " resources " +
                        "and " + blocks.size() + " blocks.");
        System.out.println("Mine analysis completed. Found " + resourceCount + " resources " +
                "and " + blocks.size() + " blocks.");
    }

    /**
     * Starts delegating work among available workers. If no available workers are passed, immediately returns.
     * @param availableWorkers workers to delegate
     * @param cargoVehicle cargo vehicle, that all material will be unloaded to
     */
    public void delegateWorkers(final WorkerQueue availableWorkers, final CargoVehicle cargoVehicle) {
        if (availableWorkers == null || availableWorkers.size() == 0 || mine == null) {
            return;
        }

        List<Future<?>> blockProcessings = new ArrayList<>();
        List<Future<?>> lorryReplacements = new ArrayList<>();
        while (mine.hasUnprocessedBlocks()) {
            Worker currentWorker = availableWorkers.getAvailableWorker();
            Block blockToProcess = mine.pollUnprocessedBlock();

            Future<?> result = currentWorker.processBlock(blockToProcess, () -> { // Adding an action what the worker should do when he's done
                for (int i = 0; i < blockToProcess.getLength(); i++) {
                    if (!mine.getSteadyLorry().loadCargo(1)) {
                        i--;    // If we didn't succeed with loading, we try again
                    }

                    if (mine.getSteadyLorry().isFilledUp()) {
                        Future<?> replacementResult = mine.replaceSteadyLorry(new Lorry(Simulation.threadCount++,
                                Lorry.getDefaultCapacity(), Lorry.getDefaultMaxTransportTime()), cargoVehicle);

                        if (replacementResult == null) {
                            System.err.println("Error while replacing Lorry in the mine!");
                        } else {
                            lorryReplacements.add(replacementResult);
                        }
                    }
                }

                availableWorkers.addWorker(currentWorker);
            });

            if (result == null) {
                mine.addResourceBlock(blockToProcess); // If something went wrong, we try to process the block again
            } else {
                blockProcessings.add(result);
            }
        }

        waitForWorksToFinish(blockProcessings, lorryReplacements);

        System.out.println("Všechny bloky zpracovány");
    }

    private void waitForWorksToFinish(final List<Future<?>> blockProcessings, final List<Future<?>> lorryReplacements) {
        for (Future<?> blockProcessing : blockProcessings) {
            try {
                blockProcessing.get();
            } catch (InterruptedException | ExecutionException e) {
                System.err.println("Error while waiting for worker to finish!\n" + e.getMessage());
            }
        }

        for (Future<?> lorryReplacement : lorryReplacements) {
            try {
                lorryReplacement.get();
            } catch (InterruptedException | ExecutionException e) {
                System.err.println("Error while waiting for worker to finish!\n" + e.getMessage());
            }
        }
    }

    @Override
    public int getId() {
        return foremanId;
    }
}
