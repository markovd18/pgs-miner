package pgs.task;

import pgs.Logger;
import pgs.PerformsTask;
import pgs.cargo.CargoVehicle;

import java.util.Random;

/**
 * Parallel task of unloading cargo from the vehicle.
 *
 * @author <a href="mailto:markovd@students.zcu.cz">David Markov</a>
 * @since 7.3.2021
 */
public class UnloadCargoTask implements Runnable {
    /**
     * Number of milliseconds in one second
     */
    private static final int MILLIS_IN_SECOND = 1000;
    /**
     * Object performing the unload task.
     */
    private final PerformsTask performer;
    /**
     * Size of the cargo we are unloading.
     */
    private final int cargoSize;
    /**
     * Maximum number of seconds it will take to transport to the cargo to the destination.
     */
    private final int maxTransportTime;
    /**
     * A vehicle we will be unloading to.
     */
    private final CargoVehicle vehicleToUnloadTo;

    /**
     * Creates new task to unload a cargo. There will be {@code cargoSize} of cargo unloaded by {@code performer}. The cargo will be
     * transported for maximum of {@code maxTransportTime} to the {@code vehicleToUnloadTo}. {@code vehicleToUnloadTo} may be null -
     * the cargo will be thrown on the ground maximum of {@code maxTransportTime} distant from initial position.
     * @param performer object performing this task
     * @param cargoSize size of cargo to unload
     * @param maxTransportTime maximum transport time
     * @param vehicleToUnloadTo vehicle to unload cargo to - may be null
     */
    public UnloadCargoTask(final PerformsTask performer, final int cargoSize, final int maxTransportTime, final CargoVehicle vehicleToUnloadTo) {
        this.performer = performer;
        this.cargoSize = cargoSize;
        this.maxTransportTime = maxTransportTime;
        this.vehicleToUnloadTo = vehicleToUnloadTo;
    }

    @Override
    public void run() {
        int transportTime = getNextTransportTime();

        try {
            Thread.sleep((long)  transportTime* MILLIS_IN_SECOND); // Simulating the transportation process
        } catch (InterruptedException e) {
            System.err.println("Cargo transporter " + performer.getId() + " was interrupted during cargo transportation!\n" + e.getMessage());
        }

        if (vehicleToUnloadTo == null) {
            Logger.getInstance().logEvent(performer, "Vehicle arrived to the unload destination " +
                    "and unloaded cargo. It took " + transportTime + " seconds.");
        } else {
            Logger.getInstance().logEvent(performer, "Vehicle arrived to the " + vehicleToUnloadTo.getClass().getSimpleName() +
                    " to unload cargo. It took " + transportTime + " seconds.");
        }

        if (vehicleToUnloadTo != null && !vehicleToUnloadTo.isFilledUp()) {
            vehicleToUnloadTo.loadCargo(cargoSize); // If there is the vehicle and is not full, we load the cargo there
        }

        // If we cannot load the vehicle, we unload on the ground - do nothing

        transportTime = getNextTransportTime();
        try {
            Thread.sleep((long) transportTime * MILLIS_IN_SECOND); // Simulating the return to the station
        } catch (InterruptedException e) {
            System.err.println("Cargo transporter " + performer.getId() + " was interrupted during it's return!\n" + e.getMessage());
        }

        Logger.getInstance().logEvent(performer, "Vehicle arrived to it's destination. It took " + transportTime + " seconds.");
        performer.setTaskDone();
    }

    /**
     * Returns number of seconds it will take to transport the cargo.
     * @return number of seconds it will take to transport the cargo.
     */
    private int getNextTransportTime() {
        return new Random().nextInt(this.maxTransportTime) + 1;
    }
}
