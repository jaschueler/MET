package met;

import met.algorithm.METDefault;
import met.interfaces.Algorithm;
import met.molecule.Atom;
import met.molecule.Molecule;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

import java.util.HashMap;
import java.util.Map;

/**
 * Main class for testing equivalence of CDK molecules.
 */
public class MoleculeEquivalence {

    // the result of the equivalence test
    private boolean equivalent = false;     // whether the two molecules are equivalent (in 2D)
    private Map<IAtom, IAtom> mapping;      // a mapping from mol1 to mol2 (isomorphism function)

    /**
     * Test whether two CDK molecules are equivalent.
     *
     * @param mol1 CDK container.
     * @param mol2 CDK container.
     */
    public MoleculeEquivalence(IAtomContainer mol1, IAtomContainer mol2) {

        // transform CDK containers into molecule graphs
        Molecule m1 = new Molecule(mol1);
        Molecule m2 = new Molecule(mol2);

        // run an equivalence algorithm
        Algorithm alg = new METDefault(m1, m2);

        // evaluate results
        equivalent = alg.areEquivalent();

        if (equivalent) {

            // create atom mapping
            mapping = new HashMap<>();
            for (Map.Entry<Atom, Atom> kv : alg.getAtomMapping().entrySet()) {
                Atom k = kv.getKey();
                Atom v = kv.getValue();
                mapping.put(k.getIAtom(), v.getIAtom());
            }
        }
    }

    /**
     * Return the result of the equivalence test.
     *
     * @return True, if and only if both molecules are equivalent.
     */
    public boolean areEquivalent() {
        return equivalent;
    }


    /**
     * Return a mapping between the atoms from mol1 to those of mol2, or null if the molecules are not equivalent.
     *
     * @return Mapping of iAtoms from mol1 to mol2
     */
    public Map<IAtom, IAtom> getAtomMapping() {
        return mapping;
    }

}
