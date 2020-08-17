package met.molecule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MoleculeProperties {

    // sum of the atomic number of all atoms
    private int totalSymbol;

    // non-decreasing list of node degrees
    private List<Integer> degreeSequence;

    // total number of implicit hydrogen atoms
    private int totalHydrogen;

    // total number of single, double, triple bonds
    private int totalSingleBonds;
    private int totalDoubleBonds;
    private int totalTripleBonds;

    // total number of deuterium
    private int totalDeuterium;

    // total formal charge
    private int totalFormalCharge;

    // total number of radicals in this met.molecule
    private int totalSingleElectronCount;

    // sum of all atom descriptors
    private int totalNeighborhoodDescriptors;

    // a string representation of important properties
    private String fingerprint;

    /**
     * Determine a set of statistics that characterize a met.molecule.
     *
     * @param molecule
     */
    public MoleculeProperties(Molecule molecule) {

        /**********************************************************************
         * Initialize the atom descriptors.
         *********************************************************************/

        for (Atom atom : molecule.getAtoms()) {
            atom.getProperties().initialize(molecule);
        }

        /**********************************************************************
         * Based on the atom properties, we assign to each atom an atom
         * descriptor that characterizes the atom and its local neighborhood.
         *
         * Each neighborhood descriptor d(i, k) is an integer that encodes the
         * properties of the associated atom a_i and its local neighborhood up
         * to a depth of k.
         *
         * Two atoms having the same neighborhood descriptor are (likely) to
         * have
         *
         *   a) an equivalent set of atom properties, and
         *   b) an equivalent set of local neighbors.
         *
         * It may however be that two atoms having different structural
         * properties get the same neighborhood descriptor.
         * This is very unlikely and does not harm the correctness of
         * the met.algorithm met.algorithm (but very well harms its efficiency.)
         *********************************************************************/

        List<Integer> neighborhoodDescriptor;

        // todo: experimentally find an optimum parameter
        int maxDepth = 3;

        // stores the descriptors d(a, k)
        neighborhoodDescriptor = new ArrayList<>();

        /**********************************************************************
         * Initially, we set d[a, 0] := hash(atomProperties(a)).
         *********************************************************************/

        for (Atom atom : molecule.getAtoms()) {
            int h_0 = atom.getProperties().hashCode();
            neighborhoodDescriptor.add(h_0);
        }

        /**********************************************************************
         * Calculate d[i, k] := 31 * d[i, k-1] + s,
         * where s is the sum of d[b, k-1] over all adjacent atoms b.
         *********************************************************************/

        for (int k = 0; k < maxDepth; k++) {

            // store d[i,k] in a separate list to avoid overwriting the old values
            List<Integer> newList = new ArrayList<>(molecule.getAtomCount());

            // for each atom
            for (int i = 0; i < molecule.getAtomCount(); i++) {

                Atom atom = molecule.getAtom(i);

                int sum = 0;

                // for each adjacent atom
                for (Atom neighbor : molecule.getAdjacentAtoms(atom)) {
                    int j = neighbor.getID();
                    sum += neighborhoodDescriptor.get(j);
                }

                int oldValue = neighborhoodDescriptor.get(i);
                int newValue = 31 * oldValue + sum;

                // d[i, k] := 31 * d[i, k-1] + sum
                newList.add(newValue);
            }

            // overwrite the old values by the new ones
            for (int i = 0; i < molecule.getAtomCount(); i++) {
                neighborhoodDescriptor.set(i, newList.get(i));
            }
        }


        /**********************************************************************
         * Now the neighborhood descriptors characterize the local neighborhood
         * of each atom up to a depth of maxDepth.
         *********************************************************************/

        // write back the descriptors to the atom properties
        for (Atom atom : molecule.getAtoms()) {
            int i = atom.getID();
            int d_i = neighborhoodDescriptor.get(i);
            atom.getProperties().setNeighborhoodDescriptor(d_i);
        }

        /**********************************************************************
         * Accumulate the atom properties.
         *********************************************************************/

        for (Atom atom : molecule.getAtoms()) {
            AtomProperties prop = atom.getProperties();

            totalSymbol += prop.getSymbol();
            totalHydrogen += prop.getHydrogenCount();
            totalSingleBonds += prop.getNumSingleBonds();
            totalDoubleBonds += prop.getNumDoubleBonds();
            totalTripleBonds += prop.getNumTripleBonds();
            totalDeuterium += prop.getDeuteriumCount();
            totalFormalCharge += prop.getFormalCharge();
            totalSingleElectronCount += prop.getSingleElectronCount();
            totalNeighborhoodDescriptors += prop.getNeighborhoodDescriptor();
        }

        // create degree sequence
        degreeSequence = new ArrayList<>();
        for (Atom atom : molecule.getAtoms()) {
            degreeSequence.add(molecule.getDegree(atom));
        }
        Collections.sort(degreeSequence);

        /**********************************************************************
         * Concatenate all statistics to a single fingerprint string.
         *********************************************************************/

        StringBuilder sb = new StringBuilder();
        sb.append(totalSymbol).append("_");
        sb.append(totalSingleBonds).append("_");
        sb.append(totalDoubleBonds).append("_");
        sb.append(totalTripleBonds).append("_");
        sb.append(totalHydrogen).append("_");
        sb.append(totalDeuterium).append("_");
        sb.append(totalFormalCharge).append("_");
        sb.append(totalSingleElectronCount).append("_");
        sb.append(totalNeighborhoodDescriptors).append("_");
        fingerprint = sb.toString();
    }

    @Override
    public int hashCode() {
        return fingerprint.hashCode();
    }

    /**
     * Test whether two property sets are identical.
     *
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {

        if (!(o instanceof MoleculeProperties))
            return false;

        MoleculeProperties other = (MoleculeProperties) o;

        if (this.totalSymbol != other.totalSymbol) return false;
        if (this.totalSingleBonds != other.totalSingleBonds) return false;
        if (this.totalDoubleBonds != other.totalDoubleBonds) return false;
        if (this.totalTripleBonds != other.totalTripleBonds) return false;
        if (this.totalHydrogen != other.totalHydrogen) return false;
        if (this.totalDeuterium != other.totalDeuterium) return false;
        if (this.totalFormalCharge != other.totalFormalCharge) return false;
        if (this.totalSingleElectronCount != other.totalSingleElectronCount) return false;
        if (this.totalNeighborhoodDescriptors != other.totalNeighborhoodDescriptors) return false;

        // todo: elementwise compare the atom properties

        return true;
    }
}
