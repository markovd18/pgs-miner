package pgs.cargo;

import pgs.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * Ferry is specialized cargo vehicle that can be loaded with Lorries ({@link Lorry}) and transports them
 * across the river.
 *
 * @author <a href="mailto:markovd@students.zcu.cz">David Markov</a>
 * @since 6.3.2021
 */
public class Ferry extends CargoVehicle<CargoVehicle<?>> {
    /**
     * Number of shipped resources by a ferry.
     */
    private int shippedResources = 0;
    /**
     * Time when the ferry has been emptied of created empty.
     */
    private long timeWhenEmptied;
    /**
     * List of vehicles currently loaded on the Ferry.
     */
    private final List<CargoVehicle<?>> loadedVehicles;
    /**
     * Flag indicating whether the ferry is waiting in dock to be loaded or not.
     */
    private boolean waitingInDock = true;

    /**
     * Constructs a Ferry with given capacity
     * @param ferryId identifier of the Ferry
     * @param capacity maximum capacity
     */
    public Ferry(final int ferryId, final int capacity) {
        super(ferryId, capacity);

        loadedVehicles = new ArrayList<>();
        timeWhenEmptied = System.currentTimeMillis();
    }

    @Override
    public boolean isFilledUp() {
        return false;
    }

    /**
     * Loads given cargo vehicle to the Ferry. If the ferry is already full, returns false.
     * @param cargoVehicle cargo vehicle to load
     * @return true if successfully loaded, otherwise false
     */
    @Override
    public synchronized boolean loadCargo(final CargoVehicle<?> cargoVehicle) {
        while (!waitingInDock) {
            try {
                Ferry.this.wait();
            } catch (InterruptedException e) {
                System.err.println("Waiting on ferry to arrive to the dock was interrupted!\n" + e.getMessage());
            }
        }

        loadedVehicles.add(cargoVehicle);
        if (loadedVehicles.size() < getCapacity()) {
            while (waitingInDock) {
                try {
                    Ferry.this.wait();     // Acting as a barrier - everyone who loads has to wait until Ferry is filled
                } catch (InterruptedException e) {
                    System.err.println("Waiting thread was unexpectedly interrupted!\n" + e.getMessage());
                }
            }

        } else {
            long millisToFull = System.currentTimeMillis() - timeWhenEmptied;
            Logger.getInstance().logEvent(this, "Ferry shipped out! Filled in " + millisToFull + " ms.");
            unloadCargo();
            Ferry.this.notifyAll();
        }

        loadedVehicles.remove(cargoVehicle);
        if (loadedVehicles.isEmpty()) {
            waitingInDock = true;   // All vehicles unloaded, going back to the dock
            Ferry.this.notifyAll();
        }

        return true;
    }

    @Override
    public int getCurrentLoad() {
        return loadedVehicles.size();
    }

    /**
     * Ferry unloads carried cargo on the other side of the river. This operation is instant. Cargo is unloaded
     * on the ground, therefore passed parameter is ignored.
     * @return always true
     */
    @Override
    public Future<?> unloadCargo() {
        System.out.println("Ferry shipped out!");
        waitingInDock = false;
        shippedResources += loadedVehicles.stream().mapToInt(CargoVehicle::getCurrentLoad).sum();
        timeWhenEmptied = System.currentTimeMillis();
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
