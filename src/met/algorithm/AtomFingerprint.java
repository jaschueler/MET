package met.algorithm;

import met.molecule.Atom;
import met.interfaces.Fingerprint;

/**
 * Create a fingerprint from the properties of an atom.
 */
public class AtomFingerprint implements Fingerprint<Atom> {

    @Override
    public int fingerprint(Atom x) {
    //   System.out.println("AtomFing: " + x.getID() + " " + x.getProperties().toString());
        return x.getProperties().hashCode();
    }
}
