package met.algorithm;

import met.helper.IndexPriorityQueue;
import met.interfaces.Algorithm;
import met.molecule.Atom;
import met.molecule.Molecule;
import met.molecule.MoleculeProperties;

import java.util.*;

/**
 * Test the equivalence of two molecule graphs.
 * <p>
 * Two met.molecule graphs x=(Vx, Ex) and y=(Vy, Ey) are equivalent iff there
 * is some met.algorithm function f : Vx -> Vy such that
 * <p>
 * v.getAtomProperties() = f(v).getAtomProperties()
 * <p>
 * holds for all atoms v in Vx.
 */
public class METDefault implements Algorithm {

    // references to the given met.molecule graphs
    private Molecule g1;
    private Molecule g2;

    // whether g1 and g2 are isomorphic
    private boolean isomorphic = false;

    // met.algorithm function from g1 to g2
    private Map<Atom, Atom> mapping;

    /*
     * To each atom in g1 is assigned a set of atoms in g2 that share the same
     * atom properties. This set of atoms is called candidate set. As the sets
     * of candidates are modified during the execution of the met.algorithm, there
     * are special candidate manager objects that organize the candidate sets.
     *
     * Vice versa, each atom in g2 has a candidate set of atoms in g1.
     */
    private CandidateManager can1;     // manages the candidate sets of atoms in g1
    private CandidateManager can2;     // manages the candidate sets of atoms in g2

    /*
     * To efficiently select atoms with few candidates, we use a priority queue
     * that stores the indices of each atom in g1. The priority of each atom is
     * equal to its number of candidates.
     *
     * We will process atoms with smallest priority first.
     */
    private IndexPriorityQueue pq;


    /**
     * Run the default algorithm to test whether mol1 and mol2 are equivalent molecules.
     *
     * @param mol1 Molecule graph.
     * @param mol2 Molecule graph.
     */
    public METDefault(Molecule mol1, Molecule mol2) {

        // create empty hash map
        mapping = new HashMap<>();

        if (cannotBeIsomorphic(mol1, mol2))
            return;

        this.g1 = mol1;
        this.g2 = mol2;

        // initialize candidate managers
        can1 = new CandidateManager(g1, g2);
        can2 = new CandidateManager(g2, g1);

        // initialize priority queue
        pq = new IndexPriorityQueue(g1.getAtomCount());

        // the priority of an atom is the size of its candidate set
        for (Atom atom1 : g1.getAtoms()) {
            int priority = can1.getCandidatesOfAtom(atom1).size();
            pq.add(atom1.getID(), priority);
        }

        // test whether an isomorphism cannot exist
        if (!forwardCheck())
            return;

        // run full met.algorithm check
        isomorphic = testEquivalenceRecursive();
    }


    /**
     * Recursive equivalence test.
     */
    private boolean testEquivalenceRecursive() {

        // if all atoms in g1 have been assigned to an atom in g2
        if (pq.isEmpty()) {
            return true; // success! met.molecule graphs are isomorphic
        }

        // choose and remove an unmatched atom in g1 with smallest priority
        Atom atom1 = g1.getAtom(pq.poll());

        // create a copy of atom1's candidate set
        Collection<Atom> candidates = new ArrayList<>(can1.getCandidatesOfAtom(atom1));

        // for each candidate that may be assigned to atom1
        for (Atom atom2 : candidates) {

            // assign atom1 to atom2
            mapping.put(atom1, atom2);
            ChangeLog log = attach(atom1, atom2);

            // if equivalence of g1 and g2 is still possible after assigning atom1 to atom2
            if (forwardCheck()) {

                // if recursive equivalence test is successful
                if (testEquivalenceRecursive()) {
                    return true;    // success!
                }
            }

            // uncouple atom1 and atom2 and restore previous candidate sets
            mapping.remove(atom1);
            log.undo();
        }

        // if all candidates have been negatively tested

        // re-insert atom1 into the priority queue
        pq.add(atom1.getID(), candidates.size());

        return false;
    }


    /**
     * Assign two atoms to each other.
     *
     * @param atom1 met.molecule.Atom of graph 1.
     * @param atom2 met.molecule.Atom of graph 2.
     */
    private ChangeLog attach(Atom atom1, Atom atom2) {

        // register a change log so that all changes may later be undone
        ChangeLog log = new ChangeLog();
        can1.registerChangeLog(log);
        can2.registerChangeLog(log);

        // store all atoms in g1 whose priority has changed by attaching atom1 to atom2
        Collection<Atom> priorityChanged = new HashSet<>();

        // remove node1 from all candidate sets of g2
        for (Atom candidate2 : can1.getCandidatesOfAtom(atom1)) {
            can2.removeCandidate(candidate2, atom1);
            //  System.out.println("A remove candidate of g2::" + candidate2 + ": g1::" + atom1);
        }

        // remove node2 from all candidate sets of g1
        for (Atom candidate1 : can2.getCandidatesOfAtom(atom2)) {
            can1.removeCandidate(candidate1, atom2);
            //  System.out.println("A remove candidate of g1::" + candidate1 + ": g2::" + atom2);
            priorityChanged.add(candidate1);
        }

        // as atom1 and atom2 are assigned to each other, their candidate sets are cleared
        can1.clearCandidateListOfAtom(atom1);
        can2.clearCandidateListOfAtom(atom2);

        // update candidate set of neighbored Integers of atom1
        for (Atom neighbor1 : g1.getAdjacentAtoms(atom1)) {

            /*
             * As neighbor1 is connected to atom1, the candidate set of neighbor1 can be reduced
             * by all atoms that are not connected to node2.
             *
             * Thus, remove the candidates of neighbor1 which are not adjacent to node2.
             */
            var candidatesToBeRemoved = extractCandidatesNotAdjacentTo(atom2, g2, can1.getCandidatesOfAtom(neighbor1));

            // if there is at least one candidate that will be removed
            if (!candidatesToBeRemoved.isEmpty()) {

                priorityChanged.add(neighbor1);

                // remove selected candidates
                for (Atom candidate2 : candidatesToBeRemoved) {
                    can1.removeCandidate(neighbor1, candidate2);
                    can2.removeCandidate(candidate2, neighbor1);
                }
            }

        }

        // now symmetrically for graph2

        // update candidate set of neighbored Integers of node2
        for (Atom neighbor2 : g2.getAdjacentAtoms(atom2)) {

            /*
             * As neighbor2 is connected to node2, its candidate set can be reduced
             * by the Integers that are not connected to node1.
             *
             * Thus, select to subset of Integers from the candidate set of neighbor2 which are not
             * adjacent to node1.
             */
            var candidatesToBeRemoved = extractCandidatesNotAdjacentTo(atom1, g1, can2.getCandidatesOfAtom(neighbor2));

            // remove selected candidates
            for (Atom candidate1 : candidatesToBeRemoved) {
                can1.removeCandidate(candidate1, neighbor2);
                can2.removeCandidate(neighbor2, candidate1);
                priorityChanged.add(candidate1);
            }
        }

        // update the priority of all Integers in graph 1 whose candidate set changed
        for (Atom atom : priorityChanged) {
            var candidateList = can1.getCandidatesOfAtom(atom);
            pq.changePriority(atom.getID(), candidateList.size());
        }

        return log;
    }

    /**
     * Extract the subset of atoms in the given candidate list which are *NOT* adjacent to the given atom v.
     *
     * @param atom       met.molecule.Atom v
     * @param g          met.molecule.Molecule graph
     * @param candidates Candidate list
     * @return
     */
    private Collection<Atom> extractCandidatesNotAdjacentTo(
            Atom atom,
            Molecule g,
            Collection<Atom> candidates) {

        // will be returned
        Collection<Atom> res = new ArrayList<>();

        for (Atom candidate : candidates) {
            if (!g.hasBond(candidate, atom)) {
                res.add(candidate);
            }
        }

        return res;
    }


    /**
     * Check whether an equivalence is still possible.
     *
     * @return
     */
    private boolean forwardCheck() {

        if (pq.isEmpty())
            return true;

        // todo: improve forward checking

        // test whether the Integer with smallest priority has at least one candidate
        Atom atom1 = g1.getAtom(pq.peek());
        return !can1.getCandidatesOfAtom(atom1).isEmpty();
    }

    /**
     * Test whether x and y cannot be ismorphic.
     *
     * @param x
     * @param y
     * @return
     */
    boolean cannotBeIsomorphic(Molecule x, Molecule y) {

        // extract the associated met.molecule properties
        MoleculeProperties prop_x = x.getProperties();
        MoleculeProperties prop_y = y.getProperties();

        // the molecules cannot be equivalent if their properties differ
        return !prop_x.equals(prop_y);
    }


    /**
     * Return the result of the isomorphism test.
     *
     * @return
     */
    @Override
    public boolean areEquivalent() {
        return isomorphic;
    }


    /**
     * Return the isomorphism function, or null if the graphs are non-isomorphic.
     *
     * @return
     */
    @Override
    public Map<Atom, Atom> getAtomMapping() {
        return mapping;
    }
}
