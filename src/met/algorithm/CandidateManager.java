package met.algorithm;

import met.helper.EquivalenceClass;
import met.helper.Partition;
import met.molecule.Atom;
import met.molecule.Molecule;

import java.util.*;

/**
 * To each atom v in g1 is assigned a set of atoms in g2 that share the same
 * atom properties. This set of atoms is called the candidate set of atom v.
 */
class CandidateManager {

    // reference to the associated graphs
    private Molecule g1;
    private Molecule g2;

    /*
     * Each atom in g1 is associated to a list of atom in g2 that have the same
     * atom properties. Thus, if x is an atom from mol1, then candidates.get(x)
     * stores a list of all atoms in mol2 that are equivalent to x.
     */
    private Map<Atom, Collection<Atom>> candidates;

    // a logbook that registers each removal action
    ChangeLog log;

    /**
     * Create a candidate manager that holds and updates for each atom in
     * met.molecule 1 a list of equivalent atoms in met.molecule 2.
     *
     * @param g1
     * @param g2
     */
    public CandidateManager(
            Molecule g1,
            Molecule g2) {

        this.g1 = g1;
        this.g2 = g2;

        // initialize candidate sets
        candidates = new HashMap<>();

        // create fingerprints of atoms
        AtomFingerprint fp = new AtomFingerprint();

        // test whether two atoms have the same set of properties
        AtomEquivalence eq = new AtomEquivalence();

        // partition g2 into classes of equivalent atoms that share the same properties
        Partition<Atom> part2 = new Partition<>(g2.getAtoms(), eq, fp);

        // for each atom in g1
        for (Atom atom1 : g1.getAtoms()) {

            // determine all atoms in g2 with the same properties as atom1
            EquivalenceClass<Atom> equivalentAtoms = part2.getEquivalentItems(atom1);

            // create a copy of the equivalence class
            List<Atom> candidateSet = new ArrayList<>(equivalentAtoms.getItems());

            // assign the set of equivalent atoms to atom1
            candidates.put(atom1, candidateSet);
        }

        /*System.out.println("EQ of g1");
        for (met.molecule.Atom a : g1.getAtoms()) {
            Collection<met.molecule.Atom> all = candidates.get(a);
            System.out.println(a.getID() + ": ");
            for (met.molecule.Atom a_current : all) {
                System.out.print(a_current.getID() + ", ");
            }
            System.out.println();
        }*/
        //    System.exit(0);

    }

    /**
     * Return the candidate list of a certain atom in g1.
     *
     * @param atom1 met.molecule.Atom in g1.
     * @return
     */
    public Collection<Atom> getCandidatesOfAtom(Atom atom1) {
        return candidates.get(atom1);
    }

    /**
     * Remove a candidate from the candidate set of a certain node.
     *
     * @param node      Node whose candidate set will be purged from candidate.
     * @param candidate Candidate which will be removed.
     */
    public void removeCandidate(Atom atom, Atom candidate) {

        if (!candidates.get(atom).contains(candidate)) {
            System.out.println("Warning! " + candidate + " not in candidate list of atom " + atom);
            System.exit(0);
        }

        // remove candidate
        candidates.get(atom).remove(candidate);
        log.registerRemoval(this, atom, candidate);
    }

    /**
     * Remove all candidates of a certain node.
     */
    public void clearCandidateListOfAtom(Atom atom) {

        // remove candidates
        for (Atom candidate : getCandidatesOfAtom(atom)) {
            log.registerRemoval(this, atom, candidate);
        }
        candidates.get(atom).clear();
    }


    /**
     * Define a logfile which tracks all changes.
     *
     * @param log
     */
    public void registerChangeLog(ChangeLog log) {
        this.log = log;
    }

    /**
     * Return candidate lists formatted as string.
     *
     * @return
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Atom atom1 : g1.getAtoms()) {
            Collection<Atom> cand = getCandidatesOfAtom(atom1);
            if (!cand.isEmpty()) {
                sb.append(atom1.getID()).append(":");
                for (Atom candidate : cand) {
                    sb.append(" ").append(candidate.getID());
                }
                sb.append(System.lineSeparator());
            }
        }
        return sb.toString();
    }

}
