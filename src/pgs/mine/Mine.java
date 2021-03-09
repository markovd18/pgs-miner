package pgs.mine;

import pgs.cargo.CargoVehicle;
import pgs.cargo.Lorry;

import java.security.InvalidParameterException;
import java.util.Queue;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * Mine contains blocks of resources to be processed by the workers. Mined resources are taken away by
 * a Lorry, waiting at the entrance.
 *
 * @author <a href="mailto:markovd@students.zcu.cz">David Markov</a>
 * @since 7.3.2021
 */
public class Mine {
    /**
     * Lorry, waiting at the mine entrance to be filled up.
     */
    private Lorry steadyLorry;
    /**
     * A map representing this mine's resources.
     */
    private final Map mineMap;
    /**
     * Queue of resource blocks in this mine, that have not been processed yet.
     */
    private Queue<Block> unprocessedBlocks;

    /**
     * Constructs a mine. It's resources will be represented by given map.
     * @param mineMap map representing the resources of this mine
     */
    public Mine(final Map mineMap) {
        if (mineMap == null) {
            throw new InvalidParameterException("A mine map cannot be null!");
        }

        this.mineMap = mineMap;
    }

    /**
     * Returns currently steady Lorry at the mine entrance.
     * @return steady Lorry
     */
    public Lorry getSteadyLorry() {
        return steadyLorry;
    }

    /**
     * Returns the map of this mine.
     * @return map of this mine
     */
    public Map getMineMap() {
        return mineMap;
    }

    /**
     * Sets new set of unprocessed blocks in the mine.
     * @param unprocessedBlocks new set of unprocessed blocks
     */
    public void setUnprocessedBlocks(final Queue<Block> unprocessedBlocks) {
        this.unprocessedBlocks = unprocessedBlocks;
    }

    /**
     * Checks if there are any unprocessed resource blocks in the mine.
     * @return true, if there are any unprocessed blocks, otherwise false
     */
    public boolean hasUnprocessedBlocks() {
        return unprocessedBlocks != null && !unprocessedBlocks.isEmpty();
    }

    /**
     * Removes the first unprocessed block from the queue and returns it.
     * @return first unprocessed block
     */
    public Block pollUnprocessedBlock() {
        return unprocessedBlocks.poll();
    }

    /**
     * Returns number of unprocessed resources in the mine.
     * @return number of unprocessed resources
     */
    public int getUnprocessedResourcesCount() {
        int resourceCount = 0;
        for (Block block : unprocessedBlocks) {
            resourceCount += block.getLength();
        }

        return resourceCount;
    }

    /**
     * Adds new resource block to the queue of unprocessed blocks if there was one found.
     * @param block new resource block
     */
    public void addResourceBlock(final Block block) {
        if (block == null) {
            return;
        }

        unprocessedBlocks.add(block);
    }

    /**
     * Places new steady lorry at the mine entrance. Only one lorry may be waiting at the entrance. If there is
     * one steady lorry at the entrance and is filled up to a maximum load, it will be sent to unload to given
     * cargo vehicle. If the steady Lorry is not filled up, the replacement will not be done.
     * @param newSteadyLorry new steady Lorry
     * @param vehicleToUnloadTo vehicle to unload filled up Lorry to - may be null
     * @return true, if Lorries were successfully replaced, otherwise false
     */
    public synchronized Future<?> replaceSteadyLorry(final Lorry newSteadyLorry, final CargoVehicle vehicleToUnloadTo) {
        if (this.steadyLorry != null) {
            if (this.steadyLorry.isFilledUp()) {
                return this.steadyLorry.unloadCargo(vehicleToUnloadTo);
            } else {
                return new FutureTask<>(() -> null); // Steady Lorry is not filled up yet, we don't approve the replacement
            }
        }

        this.steadyLorry = newSteadyLorry;
        return new FutureTask<>(() -> null);
    }
}
