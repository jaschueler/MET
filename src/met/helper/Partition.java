package met.helper;

import met.interfaces.EquivalenceRelation;
import met.interfaces.Fingerprint;

import java.util.*;

/**
 * Partition the a of items of type T into subsets C[1], C[2], ..., C[m]
 * of pair-wisely equivalent items. Two items x and y are said to be equivalent
 * if and only if relation.equivalent(x,y) == 0.
 */
public class Partition<T> {

    /*
     * We use a two-step approach to create equivalence classes.
     *
     * First, we use hashCode() to determine a fingerprint of each item.
     * Two equivalent items must have the same fingerprint. However, it
     * may be the case that to items that are not equivalent nevertheless
     * share the same fingerprint.
     *
     * Thus, we maintain a map that assigns to each known fingerprint f
     * the list of collections C[1], C[2], ..., C[k] of items such that
     *
     *   a) for each 1 <= i <= k: C[i].hashCode() = f
     *      (thus, all items in this list share the same fingerprint f)
     *
     *   b) for all 1 <= i < j <= k: C[i] is not equivalent to C[j].
     *
     * Consequently, the k equivalence classes C[1], C[2], ..., C[k]
     * are all associated to the same fingerprint and no pair of items
     * within each C[i] is equivalent.
     *
     * Hence, the total number of equivalence classes is the total number
     * of items stored in this map.
     */

    // a list of all equivalence classes
    protected List<EquivalenceClass<T>> classes;

    // the equivalence relation used to partition:
    // x = y if and only if relation.equivalent(x,y) == 0
    protected EquivalenceRelation<T> relation;

    // fingerprint function that maps items of type T to integers
    // default implementation: map all items to zero
    protected Fingerprint<T> fingerprint;

    // fingerprint to list of equivalence classes
    protected Map<Integer, List<EquivalenceClass<T>>> classesWithFingerprint;

    /**
     * met.helper.Partition a given collection of items into equivalence classes in which all
     * items are pair-wisely equivalent.
     *
     * @param items       Collection of items to be partitioned.
     * @param relation    Equivalence relation by which the items are partitioned.
     * @param fingerprint Fingerprint function that maps items of type T to integers,
     *                    such that two equivalent items have the same fingerprint.
     *                    May be used to accelerate the partitioning.
     */
    public Partition(
            Iterable<T> items,
            EquivalenceRelation<T> relation,
            Fingerprint<T> fingerprint
    ) {
        this.relation = relation;
        this.fingerprint = fingerprint;
        classes = new ArrayList<>();
        classesWithFingerprint = new HashMap<>();

        for (T item : items) {
            add(item);
        }
    }

    /**
     * met.helper.Partition a given collection of items into equivalence classes in which all
     * items are pair-wisely equivalent.
     *
     * @param items    Collection of items to be partitioned.
     * @param relation MoleculeEquivalenceAbstract relation by which the items are partitioned.
     */
    public Partition(
            Iterable<T> items,
            EquivalenceRelation<T> relation
    ) {

        // default implementation for the fingerprint function: map all items to zero
        this(items, relation, (x) -> 0);
    }

    /**
     * met.helper.Partition a set of items into equivalence classes in which all
     * items are pair-wisely equivalent.
     */
    public Partition(EquivalenceRelation<T> relation, Fingerprint<T> fingerprint) {
        this(new ArrayList<T>(), relation, fingerprint);
    }

    /**
     * met.helper.Partition a set of items into equivalence classes in which all
     * items are pair-wisely equivalent.
     */
    public Partition(EquivalenceRelation<T> relation) {
        this(relation, (x) -> 0);
    }


    /**
     * Insert an item into its associated equivalence class.
     *
     * @param item
     */
    public void add(T item) {

        // determine fingerprint
        int f = fingerprint.fingerprint(item);

        // if no other item with the same fingerprint exists
        if (!classesWithFingerprint.containsKey(f)) {

            // create a new family of classes associated to the fingerprint
            EquivalenceClass<T> newClass = new EquivalenceClass<T>(item);
            List<EquivalenceClass<T>> newFamily = new ArrayList<>();
            newFamily.add(newClass);

            classesWithFingerprint.put(f, newFamily);
            classes.add(newClass);
        } else {


            // determine the family of equivalence classes associated to this fingerprint
            List<EquivalenceClass<T>> family = classesWithFingerprint.get(f);


            // for each equivalence class in this family
            for (EquivalenceClass<T> eqclass : family) {

                // test whether item fits in this class
                T repr = eqclass.getRepresentative();

                // if item fits into the current class
                if (relation.equivalent(repr, item)) {
                    eqclass.add(item);  // uncomment to save storage
                    return;
                }
            }

            // if item fits into no class in this family
            // create a new class with itself as representative
            EquivalenceClass<T> newClass = new EquivalenceClass<>(item);
            classes.add(newClass);
            family.add(newClass);
        }
    }


    /**
     * Return the list of equivalence classes in which all items
     * are pair-wisely isomorphic.
     *
     * @return
     */
    public List<EquivalenceClass<T>> getEquivalenceClasses() {
        return classes;
    }

    /**
     * Return the class of items which are equivalent to the given item.
     *
     * @param item
     * @return
     */
    public EquivalenceClass<T> getEquivalentItems(T item) {

        // determine fingerprint
        int f = fingerprint.fingerprint(item);

        // if no item with the same fingerprint exists
        if (!classesWithFingerprint.containsKey(f)) {

            // return an empty equivalence class
            return new EquivalenceClass<>();
        }

        // determine the family of equivalence classes associated to this fingerprint
        List<EquivalenceClass<T>> family = classesWithFingerprint.get(f);

        // for each equivalence class in this family
        for (EquivalenceClass<T> eqclass : family) {

            // test whether item fits in this class
            T repr = eqclass.getRepresentative();

            // if item fits into the current class
            if (relation.equivalent(repr, item)) {
                return eqclass;
            }
        }

        // there is no class associated to the given item

        // return an empty equivalence class
        return new EquivalenceClass<>();
    }
}

