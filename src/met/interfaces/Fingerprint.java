package met.interfaces;

/**
 * Interface for fingerprint functions that map items of type T to integers.
 *
 * Two equivalent items need to have the same fingerprint.
 *
 */
public interface Fingerprint<T> {

    /**
     * Create the fingerprint of item x.
     * @param x
     * @return
     */
    int fingerprint(T x);

}
