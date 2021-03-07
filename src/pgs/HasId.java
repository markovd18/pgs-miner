package pgs;

/**
 * This interface is implemented by objects that need to be identified by an ID.
 *
 * @author <a href="mailto:markovd@students.zcu.cz">David Markov</a>
 * @since 7.3.2021
 */
public interface HasId {

    /**
     * Retuns the ID of specific object.
     * @return ID of an object
     */
    int getId();
}
