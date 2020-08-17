package met.molecule;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Molecule {

    // reference to the CDK atom container
    private IAtomContainer atomContainer;

    // list of atoms
    private List<Atom> atoms;

    // list of bonds
    private List<Bond> bonds;

    // adjacency list representation
    private List<List<Atom>> adjacentAtoms;

    // properties and statistics characterizing this met.molecule
    private MoleculeProperties moleculeProperties;

    /**
     * Create an undirected graph from a CDK container.
     */
    public Molecule(IAtomContainer molecule) {

        // todo: avoid removing hydrogen atoms

        /**********************************************************************
         * Transform explicit hydrogen atoms to implicit ones
         *********************************************************************/

        for (int i = 0; i < molecule.getAtomCount(); i++) {

            //determine the hydrogen count and set it as attribute
            IAtom currentAtom = molecule.getAtom(i);
            List<IAtom> coAts = molecule.getConnectedAtomsList(currentAtom);

            //set the hybridisation as atom property for all atoms except hydrogens
            if (!(currentAtom.getSymbol().equals("H"))) {

                int hydrogens = 0;
                int deuterium = 0;
                for (int cA = 0; cA < coAts.size(); cA++) {
                    IAtom current_coAt = coAts.get(cA);

                    if (current_coAt.getSymbol().equals("H")) {

                        if (current_coAt.getMassNumber() != null) {
                            deuterium++;
                            molecule.removeBond(currentAtom, current_coAt);

                        } else {
                            hydrogens++;
                            molecule.removeBond(currentAtom, current_coAt);
                        }

                    }

                }
                currentAtom.setImplicitHydrogenCount(hydrogens);
                currentAtom.setProperty("Deuterium", deuterium);

            } else if (currentAtom.getSymbol().equals("H") && (currentAtom.getFormalCharge() != 0)) {
                int hydrogens = 0;
                int deuterium = 0;

                if (currentAtom.getMassNumber() != null) {
                    deuterium++;

                } else {
                    hydrogens++;
                }

                currentAtom.setImplicitHydrogenCount(hydrogens);
                currentAtom.setProperty("Deuterium", deuterium);
            }
        }

        //remove all hydrogens
        for (int i = 0; i < molecule.getAtomCount(); i++) {
            IAtom currentAtom = molecule.getAtom(i);
            if ((currentAtom.getSymbol().equals("H")) && (currentAtom.getFormalCharge() == 0)) {
                molecule.removeAtom(currentAtom);
                i--;
            }
        }

        /**********************************************************************
         * Transform CDK met.molecule into adjacency graph representation.
         *********************************************************************/

        atomContainer = molecule;
        int n = molecule.getAtomCount();

        // prepare lists of atoms and adjacencies
        atoms = new ArrayList<>();
        adjacentAtoms = new ArrayList<>();

        // for each atom in the CDK container
        for (IAtom iAtom : molecule.atoms()) {

            // determine the index of this atom in the CDK container
            int id = molecule.indexOf(iAtom);

            // create a new atom
            atoms.add(new Atom(id, iAtom));

            // create an empty adjacency list
            adjacentAtoms.add(new ArrayList<>());
        }

        // transform bonds into adjacency lists
        bonds = new ArrayList<>();
        for (IBond bond : molecule.bonds()) {

            if (bond.getAtomCount() != 2) {
                System.err.println("Error: met.molecule.Bond with more than 2 atoms!");
            }

            IAtom ia1 = bond.getBegin();
            IAtom ia2 = bond.getEnd();

            int index1 = molecule.indexOf(ia1);
            int index2 = molecule.indexOf(ia2);

            Atom atom1 = atoms.get(index1);
            Atom atom2 = atoms.get(index2);

            Bond b = new Bond(atom1, atom2);
            bonds.add(b);

            // each edge (v,w) is stored twice
            adjacentAtoms.get(index1).add(atom2);
            adjacentAtoms.get(index2).add(atom1);
        }

        // sort each adjacency list
        for (List<Atom> neighbors : adjacentAtoms) {
            Collections.sort(neighbors);
        }

        /**********************************************************************
         * Calculate met.molecule properties and statistics.
         *********************************************************************/

        moleculeProperties = new MoleculeProperties(this);
    }

    /**
     * Return the number of atoms.
     *
     * @return
     */
    public int getAtomCount() {
        return atoms.size();
    }

    /**
     * Return an atom with a certain index i.
     *
     * @param i met.molecule.Atom index.
     * @return
     */
    public Atom getAtom(int i) {
        return atoms.get(i);
    }

    /**
     * Return the set of bonds.
     *
     * @return
     */
    Iterable<Bond> getBonds() {
        return bonds;
    }

    /**
     * Return the number of bonds.
     *
     * @return
     */
    public int getBondCount() {
        return bonds.size();
    }

    /**
     * Test whether a certain bond (v,w) exists.
     *
     * @return
     */
    public boolean hasBond(Atom v, Atom w) {

        // scan through the smaller edge list
        if (getDegree(v) > getDegree(w)) {

            // swap v and w
            Atom tmp = v;
            v = w;
            w = tmp;
        }

        // apply binary search to find w in the adjacency list of v

        int lower = 0;
        int upper = getDegree(v);

        while (lower < upper) {

            // invariant: for all i with 0 <= i < l and r <= i < getDegree(v): adjacentNodes.get(v).get(i) != w

            int m = (lower + upper) / 2;
            Atom a = getAdjacentAtoms(v).get(m);

            if (a.getID() == w.getID())
                return true;
            else if (a.getID() < w.getID())
                lower = m + 1;
            else
                upper = m;
        }

        return false;
    }

    /**
     * Return the set of atoms.
     *
     * @return
     */
    public Iterable<Atom> getAtoms() {
        return atoms;
    }


    /**
     * Return the set of adjacent nodes.
     *
     * @return
     */
    public List<Atom> getAdjacentAtoms(Atom v) {
        return adjacentAtoms.get(v.getID());
    }

    /**
     * Return the number of neighbors.
     *
     * @param v Node id.
     * @return
     */
    int getDegree(Atom v) {
        return getAdjacentAtoms(v).size();
    }

    /**
     * Return the associated CDK atom container.
     *
     * @return
     */
    public IAtomContainer getCDKContainer() {
        return atomContainer;
    }

    /**
     * Return the properties and statistics that characterize this met.molecule.
     *
     * @return
     */
    public MoleculeProperties getProperties() {
        return moleculeProperties;
    }

}
