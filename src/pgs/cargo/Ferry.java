package pgs.cargo;

/**
 * Ferry is loaded on the river side of the mine and transports loaded cargo to the other side of the river.
 *
 * @author <a href="mailto:markovd@students.zcu.cz">David Markov</a>
 * @since 6.3.2021
 */
public class Ferry extends CargoVehicle {

    /**
     * Constructs a Ferry with given capacity
     * @param capacity maximum capacity
     */
    public Ferry(final int capacity) {
        super(capacity);
    }

    @Override
    public synchronized boolean loadCargo(final int cargoAmount) {
        if ((currentLoad + cargoAmount) > capacity) {
            return false; // Cannot load so many cargo
        }

        currentLoad += cargoAmount;
        if (currentLoad < capacity) {
            try {
                wait();     // Acting as a barrier - everyone who loads has to wait until Lorry is filled
            } catch (InterruptedException e) {
                System.err.println("Waiting thread was unexpectedly interrupted!\n" + e.getMessage());
            }

        } else {
            notifyAll();    // Ferry is full - cargo will be carried to the other side of the river and everyone
            unloadCargo(null);  // who loaded something will be notified
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
    public boolean unloadCargo(final CargoVehicle cargoVehicle) {
        // Lorry doesn't unload onto another vehicle - ignoring passed parameter
        currentLoad = 0;
        return true;
    }
}
