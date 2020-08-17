package met.algorithm;

import met.molecule.Atom;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ChangeLog {

    /**
     * Base class for log entries
     */
    abstract private class LogEntry {
        abstract void undo();
    }

    /**
     * Logbook entry.
     * Each call of can.remove(atom, candidate) is stored.
     */
    private class RemovalEntry extends LogEntry {
        CandidateManager can;
        Atom atom;
        Atom candidate;

        public RemovalEntry(CandidateManager can, Atom atom, Atom candidate) {
            this.can = can;
            this.atom = atom;
            this.candidate = candidate;
        }

        /**
         * undo the call can.remove(node, candidate)
         */
        void undo() {

            //   System.out.println("undoing can.remove(" + atom + ", " + candidate + ")");
            Collection<Atom> candidates = can.getCandidatesOfAtom(atom);
            candidates.add(candidate);
        }
    }


    private List<LogEntry> logbook;     // recorded history of events

    /**
     * Create a logbook that registers the removals of candidates.
     * These removals may later be undone by calling undo().
     */
    public ChangeLog() {
        logbook = new ArrayList<>();
    }


    /**
     * Register the removal of an atom from a certain set.
     */
    public void registerRemoval(CandidateManager can, Atom atom, Atom candidate) {
        logbook.add(new RemovalEntry(can, atom, candidate));
    }

    /**
     * Undo all changes.
     */
    public void undo() {

        // System.out.println("logbook has " + logbook.size() + " entries");

        // in inverse order
        for (int i = logbook.size() - 1; i >= 0; i--) {
            logbook.get(i).undo();
        }

        // clear history
        logbook.clear();
    }

}
