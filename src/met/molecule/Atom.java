package met.molecule;

import org.openscience.cdk.interfaces.IAtom;

public class Atom implements Comparable<Atom> {

    private int id;                 // unique id identifying this atom
    private IAtom iAtom;            // associated CDK atom
    private AtomProperties prop;    // a set of properties characterizing this atom

    /**
     * Create an atom associated to the i-th atom from a CDK container.
     *
     * @param index Unique identifier.
     * @param iAtom CDK atom object.
     */
    public Atom(int id, IAtom iAtom) {
        this.id = id;
        this.iAtom = iAtom;
        prop = new AtomProperties(this);
    }

    /**
     * Return the identifier of this atom.
     *
     * @return
     */
    public int getID() {
        return id;
    }

    /**
     * Return the associated CDK object.
     *
     * @return
     */
    public IAtom getIAtom() {
        return iAtom;
    }

    /**
     * Return the set of atom properties.
     *
     * @return
     */
    public AtomProperties getProperties() {
        return prop;
    }

    /**
     * Compare two atoms by their indices.
     *
     * @param atom
     * @return
     */
    @Override
    public int compareTo(Atom atom) {
        return Integer.compare(this.id, atom.id);
    }

    /**
     * Return a string representation of this atom.
     *
     * @return
     */
    @Override
    public String toString() {
        return "" + id;
    }
}
