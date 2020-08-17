package met.interfaces;

import met.molecule.Atom;

import java.util.Map;

public interface Algorithm {


    /**
     * Test whether two met.molecule graphs are equivalent (with respect to their labels).
     */
    boolean areEquivalent();

    /**
     * If the two graphs are isomorphic, return the met.algorithm function that maps nodes from g1 to g2.
     * If the two graphs are non-isomorphic, return null.
     *
     * @return A mapping between nodes from g1 and g2, or null if g1 and g2 are non-isomorphic.
     */
    Map<Atom, Atom> getAtomMapping();

}
