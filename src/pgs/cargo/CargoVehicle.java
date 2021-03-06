package pgs.cargo;

/**
 * A vehicle that can carry some amount of cargo. Some amount of this cargo may be loaded and unloaded to another
 * cargo vehicle.
 *
 * @author <a href="mailto:markovd@students.zcu.cz">David Markov</a>
 * @since 6.3.2021
 */
public abstract class CargoVehicle {
    /**
     * Maximum capacity of this vehicle
     */
    protected final int capacity;
    /**
     * Current amount of loaded cargo
     */
    protected int currentLoad;

    public CargoVehicle(final int capacity) {
        this.capacity = capacity;
    }

    /**
     * Loads given amount of cargo onto the vehicle. The amount has to be positive.
     * @param cargoAmount amount of cargo to load
     */
    public abstract boolean loadCargo(int cargoAmount);

    /**
     * Unloads cargo carried by this vehicle. If given cargo vehicle is not null, cargo is loaded onto that vehicle,
     * otherwise it is unloaded elsewhere.
     * @param cargoVehicle cargo vehicle to unload cargo onto - may be null
     * @return true, if successfully unloaded, otherwise false
     */
    public abstract boolean unloadCargo(CargoVehicle cargoVehicle);

    /**
     * Returns the maximum capacity of this vehicle.
     * @return capacity of this vehicle
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * Returns the amount of currently loaded cargo.
     * @return current amount of loaded cargo
     */
    public int getCurrentLoad() {
        return currentLoad;
    }
}
