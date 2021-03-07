package pgs.task;

import pgs.cargo.CargoVehicle;

/**
 * Parallel task of unloading cargo from the vehicle.
 *
 * @author <a href="mailto:markovd@students.zcu.cz">David Markov</a>
 * @since 7.3.2021
 */
public class UnloadCargoTask implements Runnable {

    /**
     * A vehicle we will be unloading to.
     */
    private final CargoVehicle vehicleToUnloadTo;

    public UnloadCargoTask(final CargoVehicle vehicleToUnloadTo) {
        this.vehicleToUnloadTo = vehicleToUnloadTo;
    }

    @Override
    public void run() {

    }
}
