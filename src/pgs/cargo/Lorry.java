package pgs.cargo;

import pgs.Logger;
import pgs.task.UnloadCargoTask;

import java.security.InvalidParameterException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Lorry is a cargo vehicle, that can be loaded up with some amount of material. Loaded material may be unloaded
 * onto another cargo vehicle or onto the ground.
 *
 * @author <a href="mailto:markovd@students.zcu.cz">David Markov</a>
 * @since 7.3.2021
 */
public class Lorry extends CargoVehicle<Integer> {
    /**
     * Executor for submitting parallel tasks.
     */
    private static final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
    static {
        executor.allowCoreThreadTimeOut(true);  // Lorry tasks will be executed in new cached threads, which will be created on the run, if needed
        executor.setKeepAliveTime(1, TimeUnit.SECONDS);
    }
    /**
     * Number of milliseconds it takes to load material into the Lorry.
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
     * Number of currently loaded resources.
     */
    private int currentLoad;
    /**
     * Maximum number of milliseconds it will take to transport the vehicle.
     */
    private final int maxTransportTime;
    /**
     * Flag indicating, that there is material already being loaded.
     */
    private boolean loadingInProgress = false;
    /**
     * Time when the lorry has been emptied of created empty.
     */
    private long timeWhenEmptied;
    /**
     * Ferry on the way to unload the cargo.
     */
    private final Ferry ferryOnTheWay;

    /**
     * Constructs new Lorry with given maximum capacity. Transport will take maximum of {@code maxTransportTime} milliseconds.
     * @param capacity maximum capacity of this Lorry
     * @param maxTransportTime maximum number of milliseconds it must take to transport somewhere
     * @param ferry ferry to travel to on the way to unload the cargo
     */
    public Lorry(final int lorryId, final int capacity, final int maxTransportTime, final Ferry ferry) {
        super(lorryId, capacity);

        if (maxTransportTime <= 0) {
            throw new InvalidParameterException("Maximum transport time has to be positive!");
        }

        this.ferryOnTheWay = ferry;
        this.maxTransportTime = maxTransportTime;
        this.timeWhenEmptied = System.currentTimeMillis();
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
    public synchronized boolean loadCargo(final Integer cargoAmount) {
        if (taskInProgress) {
            return false;   // Unloading cargo, cannot load at the same time
        }

        while (loadingInProgress) {
            try {
                wait();
            } catch (InterruptedException e) {
                System.err.println("Waiting for material loading was interrupted!\n" + e.getMessage());
            }
        }

        if ((currentLoad + cargoAmount) > getCapacity()) {
            return false; // Cannot load so many cargo
        }

        loadingInProgress = true;

        try {
            Thread.sleep((long) cargoAmount * RESOURCE_LOADING_TIME_MILLIS);
        } catch (InterruptedException e) {
            System.err.println("Loading of material into the lorry was interrupted!\n" + e.getMessage());
        }

        currentLoad += cargoAmount;
        if (currentLoad == getCapacity()) {
            long millisToFull = System.currentTimeMillis() - timeWhenEmptied;
            Logger.getInstance().logEvent(this, "Lorry is full. Filled in " + millisToFull + " ms.");
        }

        loadingInProgress = false;  // Resetting so others may load
        notify();

        return true;
    }

    @Override
    public int getCurrentLoad() {
        return currentLoad;
    }

    @Override
    public boolean isFilledUp() {
        return currentLoad == getCapacity();
    }

    @Override
    public Future<?> unloadCargo() {
        if (taskInProgress) {
            return null;   // Already unloading
        }

        taskInProgress = true;
        timeWhenEmptied = System.currentTimeMillis();
        return executor.submit(
                new UnloadCargoTask(this, this.maxTransportTime, ferryOnTheWay));
    }
}
