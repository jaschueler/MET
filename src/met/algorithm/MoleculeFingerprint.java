package met.algorithm;

import met.interfaces.Fingerprint;
import met.molecule.Molecule;

/**
 * Create fingerprints for met.molecule graphs.
 */
public class MoleculeFingerprint implements Fingerprint<Molecule> {

    @Override
    public int fingerprint(Molecule x) {
        //System.out.println("MolFing: "+x.getProperties().hashCode());
        return x.getProperties().hashCode();
    }
}
