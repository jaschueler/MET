package met.molecule;

/**
 * Model a bond between two atoms.
 */
public class Bond {

    private Atom v;
    private Atom w;

    /**
     * Create a bond between two atoms.
     * @param v
     * @param w
     */
    public Bond(Atom v, Atom w) {
        this.v = v;
        this.w = w;
    }

    /**
     * Return the first atom.
     * @return
     */
    public Atom getOne() {
        return v;
    }

    /**
     * Return the second atom.
     * @return
     */
    public Atom getOther() {
        return w;
    }
}
