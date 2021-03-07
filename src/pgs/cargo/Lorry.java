package pgs.cargo;

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
     * Flag indicating, that there is material already being loaded.
     */
    private boolean loadingInProgress = false;

    /**
     * Constructs new Lorry with given maximum capacity.
     * @param capacity maximum capacity of this Lorry
     */
    public Lorry(final int capacity) {
        super(capacity);
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

    @Override
    public boolean loadCargo(final int cargoAmount) {
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
    public boolean unloadCargo(final CargoVehicle cargoVehicle) {
        return false;   // TODO markovd parallel unload cargo task
    }
}
