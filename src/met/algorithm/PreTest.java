package met.algorithm;

import met.molecule.Molecule;
import met.molecule.MoleculeProperties;

public class PreTest {



    boolean cannotBeEquivalent(Molecule g1, Molecule g2) {

        // extract the associated met.molecule properties
        MoleculeProperties prop_x = g1.getProperties();
        MoleculeProperties prop_y = g2.getProperties();

        // the molecules cannot be equivalent if their properties differ
        return !prop_x.equals(prop_y);
    }


}
