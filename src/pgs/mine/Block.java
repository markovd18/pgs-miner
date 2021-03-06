package pgs.mine;

/**
 * Block of resources that may be mined.
 *
 * @author <a href="mailto:markovd@students.zcu.cz">David Markov</a>
 * @since 6.3.2021
 */
public class Block {

    /**
     * Length of this block of resources
     */
    private final int length;

    public Block(final int length) {
        this.length = length;
    }

    /**
     * Returns the length of this bloc kof resources
     * @return length of this block
     */
    public int getLength() {
        return length;
    }
}
