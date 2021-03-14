package pgs.cargo;

import pgs.Logger;

import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * Ferry is loaded on the river side of the mine and transports loaded cargo to the other side of the river.
 *
 * @author <a href="mailto:markovd@students.zcu.cz">David Markov</a>
 * @since 6.3.2021
 */
public class Ferry extends CargoVehicle {
    /**
     * Number of shipped resources by a ferry.
     */
    private int shippedResources = 0;
    /**
     * Time when the ferry has been emptied of created empty.
     */
    private long timeWhenEmptied;

    /**
     * Constructs a Ferry with given capacity
     * @param capacity maximum capacity
     */
    public Ferry(final int ferryId, final int capacity) {
        super(ferryId, capacity);

        timeWhenEmptied = System.currentTimeMillis();
    }

    @Override
    public synchronized boolean loadCargo(final int cargoAmount) {
        if ((currentLoad + cargoAmount) > capacity) {
            return false; // Cannot load so many cargo
        }

        currentLoad += cargoAmount;
        if (currentLoad < capacity) {
            try {
                Ferry.this.wait();     // Acting as a barrier - everyone who loads has to wait until Ferry is filled
            } catch (InterruptedException e) {
                System.err.println("Waiting thread was unexpectedly interrupted!\n" + e.getMessage());
            }

        } else {
            unloadCargo(null);  // who loaded something will be notified
            Ferry.this.notifyAll();    // Ferry is full - cargo will be carried to the other side of the river and everyone
        }

        return true;
    }

    /**
     * Ferry unloads carried cargo on the other side of the river. This operation is instant. Cargo is unloaded
     * on the ground, therefore passed parameter is ignored.
     * @param cargoVehicle ignored
     * @return always true
     */
    @Override
    public Future<?> unloadCargo(final CargoVehicle cargoVehicle) {
        // Ferry doesn't unload onto another vehicle - ignoring passed parameter
        long secondsToFull = (System.currentTimeMillis() - timeWhenEmptied) / 1000;
        shippedResources += currentLoad;
        Logger.getInstance().logEvent(this, "Ferry shipped out! Filled in " + secondsToFull + " seconds.");
        System.out.println("Ferry shipped out!");
        currentLoad = 0;
        return new FutureTask<>(() -> null);
    }

    /**
     * Returns the number of shipped resources by this ferry.
     * @return number of shipped resources
     */
    public int getShippedResources() {
        return shippedResources;
    }
}
