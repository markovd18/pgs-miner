package pgs.task;

import pgs.Logger;
import pgs.cargo.CargoVehicle;
import pgs.cargo.Ferry;

import java.util.Random;

/**
 * Parallel task of unloading cargo from the vehicle.
 *
 * @author <a href="mailto:markovd@students.zcu.cz">David Markov</a>
 * @since 7.3.2021
 */
public class UnloadCargoTask implements Runnable {
    /**
     * Object performing the unload task.
     */
    private final CargoVehicle<?> performer;
    /**
     * Maximum number of seconds it will take to transport to the cargo to the destination.
     */
    private final int maxTransportTime;
    /**
     * A ferry on the way to unload the vehicle.
     */
    private final Ferry ferryOnTheWay;

    /**
     * Creates new task to unload a cargo. There will be {@code cargoSize} of cargo unloaded by {@code performer}. The cargo will be
     * transported for maximum of {@code maxTransportTime} milliseconds.
     * @param performer object performing this task
     * @param maxTransportTime maximum transport time
     * @param ferryOnTheWay ferry to load the performer to, to cross the river - may be null, if not unloading across the river
     */
    public UnloadCargoTask(final CargoVehicle<?> performer, final int maxTransportTime, final Ferry ferryOnTheWay) {
        this.performer = performer;
        this.maxTransportTime = maxTransportTime;
        this.ferryOnTheWay = ferryOnTheWay;
    }

    @Override
    public void run() {
        int transportTime = getNextTransportTime();

        try {
            Thread.sleep(transportTime); // Simulating the transportation process
        } catch (InterruptedException e) {
            System.err.println("Cargo transporter " + performer.getId() + " was interrupted during cargo transportation!\n" + e.getMessage());
        }

        if (ferryOnTheWay == null) {
            Logger.getInstance().logEvent(performer, "Vehicle arrived to the unload destination " +
                    "and unloaded cargo. It took " + transportTime + " ms.");
            return;
        } else {
            Logger.getInstance().logEvent(performer, "Vehicle arrived to the " + Ferry.class.getSimpleName() +
                    " to cross the river. It took " + transportTime + " ms.");
        }

        ferryOnTheWay.loadCargo(performer); // The ferry is not null, so the performer has to be loaded onto the ferry - may block

        transportTime = getNextTransportTime(); // Unloaded from the ferry, transporting the cargo
        try {
            Thread.sleep(transportTime); // Simulating the transport to the station
        } catch (InterruptedException e) {
            System.err.println("Cargo transporter " + performer.getId() + " was interrupted during it's return!\n" + e.getMessage());
        }

        Logger.getInstance().logEvent(performer, "Vehicle arrived to it's destination. It took " + transportTime + " ms.");
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
