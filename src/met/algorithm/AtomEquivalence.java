package met.algorithm;

import met.molecule.Atom;
import met.interfaces.EquivalenceRelation;
import met.molecule.AtomProperties;

/**
 * Two atoms are equivalent iff they have the same atom properties.
 */
public class AtomEquivalence implements EquivalenceRelation<Atom> {

    @Override
    public boolean equivalent(Atom x, Atom y) {

        AtomProperties xp = x.getProperties();
        AtomProperties yp = y.getProperties();

        return xp.equals(yp);
    }
}
