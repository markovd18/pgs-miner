package pgs.cargo;

import pgs.HasId;

import java.security.InvalidParameterException;
import java.util.concurrent.Future;

/**
 * A vehicle that can carry some amount of cargo. Some amount of this cargo may be loaded and unloaded to another
 * cargo vehicle.
 *
 * @author <a href="mailto:markovd@students.zcu.cz">David Markov</a>
 * @since 6.3.2021
 */
public abstract class CargoVehicle implements HasId {
    /**
     * ID of this vehicle.
     */
    protected final int vehicleId;
    /**
     * Maximum capacity of this vehicle
     */
    protected final int capacity;
    /**
     * Current amount of loaded cargo
     */
    protected int currentLoad;

    /**
     * Constructs new cargo vehicle with given capacity. If given capacity is not positive, throws {@link InvalidParameterException}.
     * @param capacity capacity of a vehicle
     * @throws InvalidParameterException if the capacity is not positive
     */
    public CargoVehicle(final int vehicleId, final int capacity) {
        if (capacity <= 0) {
            throw new InvalidParameterException("Vehicle capacity has to be positive!");
        }

        this.vehicleId = vehicleId;
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
     * @return true, if the unloading process successfully began
     */
    public abstract Future<?> unloadCargo(CargoVehicle cargoVehicle);

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

    /**
     * Chacks if this vehicle is completely filled up.
     * @return true, if the vehicle is filled up, otherwise false
     */
    public boolean isFilledUp() {
        return currentLoad == capacity;
    }

    @Override
    public int getId() {
        return vehicleId;
    }
}
