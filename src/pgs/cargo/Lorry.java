package pgs.cargo;

import pgs.task.UnloadCargoTask;

import java.security.InvalidParameterException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Lorry is a cargo vehicle, that can be loaded up with some amount of material. Loaded material may be unloaded
 * onto another cargo vehicle or onto the ground.
 *
 * @author <a href="mailto:markovd@students.zcu.cz">David Markov</a>
 * @since 7.3.2021
 */
public class Lorry extends CargoVehicle {

    /**
     * Number of milliseconds it takes to load material into the Lotty.
     */
    private static final int RESOURCE_LOADING_TIME_MILLIS = 1000;
    /**
     * Default capacity of lorries.
     */
    private static int defaultCapacity;
    /**
     * Default maximum transport time of lorries.
     */
    private static int defaultMaxTransportTime;
    /**
     * Maximum number of seconds it will take to transport the vehicle.
     */
    private final int maxTransportTime;
    /**
     * Flag indicating, that there is material already being loaded.
     */
    private boolean loadingInProgress = false;

    private Future<?> cargoUnloading;

    /**
     * Constructs new Lorry with given maximum capacity. Transport will take maximum of {@code maxTransportTime} seconds.
     * @param capacity maximum capacity of this Lorry
     * @param maxTransportTime maximum number of seconds it must take to transport somewhere
     */
    public Lorry(final int lorryId, final int capacity, final int maxTransportTime) {
        super(lorryId, capacity);

        if (maxTransportTime <= 0) {
            throw new InvalidParameterException("Maximum transport time has to be positive!");
        }

        this.maxTransportTime = maxTransportTime;
    }

    /**
     * Sets default capacity of every lorry. This capacity may then be used as a constructor parameter.
     * @param defaultCapacity default capacity
     */
    public static void setDefaultCapacity(final int defaultCapacity) {
        if (defaultCapacity <= 0) {
            return;
        }

        Lorry.defaultCapacity = defaultCapacity;
    }

    /**
     * Returns the default capacity of all lorries.
     * @return default lorry capacity
     */
    public static int getDefaultCapacity() {
        return defaultCapacity;
    }

    /**
     * Sets default max transport time of every lorry. This value may then be used as a constructor parameter.
     * @param maxLorryTransportTime default maximum transport time
     */
    public static void setDefaultMaxTransportTime(final int maxLorryTransportTime) {
        if (maxLorryTransportTime <= 0) {
            return;
        }

        Lorry.defaultMaxTransportTime = maxLorryTransportTime;
    }

    /**
     * Returns the default maximum transport time of all lorries.
     * @return default lorry max transport time
     */
    public static int getDefaultMaxTransportTime() {
        return defaultMaxTransportTime;
    }

    @Override
    public synchronized boolean loadCargo(final int cargoAmount) {
        if (cargoUnloading != null && !cargoUnloading.isDone()) {
            return false;   // Unloading cargo, cannot load at the same time
        }

        while (loadingInProgress) {
            try {
                wait();
            } catch (InterruptedException e) {
                System.err.println("Waiting for material loading was interrupted!\n" + e.getMessage());
                return false;   // Returning, so we are still thread safe
            }
        }

        if ((currentLoad + cargoAmount) > capacity) {
            return false; // Cannot load so many cargo
        }

        try {
            Thread.sleep(RESOURCE_LOADING_TIME_MILLIS);
        } catch (InterruptedException e) {
            System.err.println("Loading of material into the lorry was interrupted!\n" + e.getMessage());
        }

        currentLoad += cargoAmount;
        loadingInProgress = false;  // Resetting so others may load
        notify();

        return true;
    }

    @Override
    public Future<?> unloadCargo(final CargoVehicle cargoVehicle) {
        if (cargoUnloading != null && !cargoUnloading.isDone()) {
            return null;   // Already unloading
        }

        cargoUnloading = Executors.newSingleThreadExecutor().submit(
                new UnloadCargoTask(this, this.currentLoad, this.maxTransportTime, cargoVehicle));

        return cargoUnloading;
    }
}
