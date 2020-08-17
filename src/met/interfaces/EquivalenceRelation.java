package met.interfaces;

public interface EquivalenceRelation<T> {

    /**
     * Test whether two items are equivalent.
     *
     * @param x Item.
     * @param y Another item.
     * @return True, if and only if x is equivalent to y.
     */
    boolean equivalent(T x, T y);
}
