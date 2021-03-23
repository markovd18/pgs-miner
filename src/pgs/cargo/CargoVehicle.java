package pgs.cargo;

import pgs.PerformsTask;

import java.security.InvalidParameterException;
import java.util.concurrent.Future;

/**
 * A vehicle that can carry some amount of cargo. Some amount of this cargo may be loaded and unloaded to another
 * cargo vehicle.
 *
 * @param <T> type of cargo to be loaded onto the cargo vehicle
 * @author <a href="mailto:markovd@students.zcu.cz">David Markov</a>
 * @since 6.3.2021
 */
public abstract class CargoVehicle<T> implements PerformsTask {
    /**
     * Maximum capacity of this vehicle
     */
    private final int capacity;
    /**
     * ID of this vehicle.
     */
    private final int vehicleId;
    /**
     * Flag indicating whether any task is currently in progress or not;
     */
    protected boolean taskInProgress = false;

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
     * Loads given cargo unit onto the vehicle.
     * @param cargoUnit cargo to load
     */
    public abstract boolean loadCargo(T cargoUnit);

    /**
     * Unloads cargo carried by this vehicle.
     * @return true, if the unloading process successfully began
     */
    public abstract Future<?> unloadCargo();

    /**
     * Returns the maximum capacity of this vehicle.
     * @return capacity of this vehicle
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * Returns the number of currently loaded cargo.
     * @return current load
     */
    public abstract int getCurrentLoad();

    /**
     * Checks if the vehicle is completely filled up.
     * @return true, if the vehicle is filled up, otherwise false
     */
    public abstract boolean isFilledUp();

    @Override
    public int getId() {
        return vehicleId;
    }

    @Override
    public void setTaskDone() {
        taskInProgress = false;
    }
}
