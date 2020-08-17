package met.molecule;

/**************************************************************************
 * Each atom has a set of properties like its elemental symbol, formal
 * charge, deuterium count, etc.
 *
 * Our equivalence met.algorithm assigns only such atoms to each other whose
 * properties are identical.
 *
 * However, not all properties must be taken into account during the
 * equivalence test. The properties that should be considered during the
 * test must be "activated" before calling the areEquivalent() or
 * getAtomMapping() method.
 *
 *************************************************************************/

public enum AtomTrait {

    SYMBOL("Element of the atom (like hydrogen, oxygen, etc.)", 0),
    FORMAL_CHARGE("?", 1),
    SINGLE_ELECTRON_COUNT("?", 2),
    HYDROGEN_COUNT("?", 3),
    DEUTERIUM_COUNT("?", 4);

    public final String label;
    public final int id;

    private AtomTrait(String label, int id) {
        this.label = label;
        this.id = id;
    }
}