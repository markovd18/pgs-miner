package pgs.worker;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Queue of workers, waiting to be assigned to process blocks of resources.
 *
 * @author <a href="mailto:markovd@students.zcu.cz">David Markov</a>
 * @since 6.3.2021
 */
public class WorkerQueue {
    /**
     * Queue of waiting workers
     */
    private final Queue<Worker> workers;

    public WorkerQueue() {
        workers = new LinkedList<>();
    }

    /**
     * Adds given worker to the queue. If the queue is empty and someone is waiting for an available worker,
     * he will be notified.
     * @param worker worker to add to the queue
     */
    public synchronized void addWorker(final Worker worker) {
        workers.add(worker);
        notify();   // If there is anyone waiting for any worker, we notify him
    }

    /**
     * Retrieves the first worker from the queue of available workers. This operation is blocking - if there are
     * none available workers, this thread will be paused, until some worker is available.
     * @return available worker
     */
    public synchronized Worker getAvailableWorker() {
        while (workers.isEmpty()) {
            try {
                wait();     // We need to wait, until there is any worker available
            } catch (InterruptedException e) {
                System.err.println("Thread was unexpectedly woken up!\n" + e.getMessage());
            }
        }

        return workers.poll();
    }

    /**
     * Returns the number of queued workers.
     * @return number of available workers
     */
    public int size() {
        return workers.size();
    }
}
