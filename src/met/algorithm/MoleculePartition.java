package met.algorithm;

import met.helper.Partition;
import met.interfaces.Algorithm;
import met.molecule.Molecule;

public class MoleculePartition extends Partition<Molecule> {

    public MoleculePartition() {

        super((x, y) -> {
            Algorithm alg = new METDefault(x, y);
            return alg.areEquivalent();
        }, new MoleculeFingerprint());
    }
}
