package pgs;

/**
 * Interface representing an object performing any task.
 *
 * @author <a href="markovd@students.zcu.cz">David Markov</a>
 * @since 14. 3. 2021
 */
public interface PerformsTask extends HasId {
    /**
     * Sets the performed task to a finished state.
     */
    void setTaskDone();
}
