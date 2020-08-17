package met.molecule;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

public class AtomProperties {

    /*
     * This class is used to specify an atom by giving them unique traits.
     * An met.algorithm met.algorithm must never assign two atoms to each other if
     * they have a different set of properties.
     *
     * The property set is divided into mandatory and auxiliary properties.
     *
     * 1. Mandatory properties are requested by the problem definition.
     *    For example, we want to find an met.algorithm that assigns only atoms
     *    with the same symbol and charge to each other.
     *
     * 2. Auxiliary properties may be used to guide an met.algorithm met.algorithm.
     *    These properties are not strictly needed but may come in handy to reduce
     *    the number of combinations that need to be tested.
     */

    Atom atom;      // associated atom

    // mandatory properties
    private int symbol;
    private int formalCharge;
    private int singleElectronCount;
    private int hydrogenCount;
    private int deuteriumCount;

    // auxiliary properties
    private int singleBonds;
    private int doubleBonds;
    private int tripleBonds;
    //private int hybridisation;
    //private int ringSize;
    private int neighborhoodDescriptor;

    /**
     * Create descriptor for an atom but do not yet initialize its members.
     *
     * @param atom
     */
    AtomProperties(Atom atom) {
        this.atom = atom;
    }

    /**
     * Initialize the atom descriptor.
     *
     * @param molecule CDK atom container.
     * @param index    met.molecule.Atom index i.
     */
    void initialize(Molecule molecule) {

        // associated CDK objects
        IAtom iAtom = atom.getIAtom();
        IAtomContainer atomContainer = molecule.getCDKContainer();

        //*********************************************************************
        // Determine the element symbol
        //*********************************************************************

        symbol = iAtom.getAtomicNumber();

        //*********************************************************************
        // Determine the number of single, double, triple bonds.
        //*********************************************************************

        for (IBond b : atomContainer.getConnectedBondsList(iAtom)) {
            String bo_type = b.getOrder().toString();
            switch (bo_type) {
                case "SINGLE":
                    singleBonds++;
                    break;
                case "DOUBLE":
                    doubleBonds++;
                    break;
                case "TRIPLE":
                    tripleBonds++;
                    break;
            }
        }

        //*********************************************************************
        // Determine the number of associated hydrogen atoms
        //*********************************************************************
        hydrogenCount = iAtom.getImplicitHydrogenCount();

        //*********************************************************************
        // Determine the number of deuterium.
        //*********************************************************************

        var prop = iAtom.getProperty("Deuterium");

        // if the property does not exist, it is implicitly assumed to be zero
        if (prop == null)
            deuteriumCount = 0;
        else
            deuteriumCount = (int) prop;

        //*********************************************************************
        // Determine the formal charge
        //*********************************************************************

        formalCharge = iAtom.getFormalCharge();

        //*********************************************************************
        // Determine the number of single electrons.
        //*********************************************************************

        singleElectronCount = atomContainer.getConnectedSingleElectronsCount(iAtom);

        /*
        //*********************************************************************
        // Determine the ring size
        //*********************************************************************
        List<List<IAtomContainer>> lists = atomContainer.getProperty("Rings");

        try {

            // determine the ring size with full rings
            List<IAtomContainer> fused = lists.get(0);
            for (int i = 0; i < fused.size(); i++) {
                if (fused.get(i).contains(iAtom)) {
                    ringSize += fused.get(i).getAtomCount();
                }
            }

            // determine the ring size with isolated rings
            List<IAtomContainer> isolated = lists.get(1);
            for (int i = 0; i < isolated.size(); i++) {
                if (isolated.get(i).contains(iAtom)) {
                    ringSize += isolated.get(i).getAtomCount();
                }
            }
        } catch (Exception e) {
            // do nothing
        }

        //*********************************************************************
        // Determine the hybridisation level (sp3, sp2, sp1)
        //*********************************************************************

        try {

            //
            String hyb = iAtom.getProperty("Hybridisation");

            switch (hyb) {
                case "SP3":
                    hybridisation = 3;
                    break;
                case "PLANAR3":
                    hybridisation = 3;
                    break;
                case "SP2":
                    hybridisation = 2;
                    break;
                case "SP1":
                    hybridisation = 1;
                    break;
                default:
                    hybridisation = 3;
                    // System.out.println("No match was possible for MoleculeGraph: " + m.getID() + " met.molecule.Atom: "
                    //        + atomnumber + " Hyb " + hyb + " Sym " + m.getAtom(atomnumber).getSymbol());
            }
        } catch (NullPointerException e) {
            // do nothing
        }*/
    }

    /**
     * Return the element symbol of this atom.
     *
     * @return
     */
    int getSymbol() {
        return symbol;
    }

    /**
     * Return the number of implicit hydrogen atoms.
     *
     * @return
     */
    public int getHydrogenCount() {
        return hydrogenCount;
    }

    /**
     * Return the number of single bonds.
     *
     * @return
     */
    public int getNumSingleBonds() {
        return singleBonds;
    }

    /**
     * Return the number of double bonds.
     *
     * @return
     */
    public int getNumDoubleBonds() {
        return doubleBonds;
    }

    /**
     * Return the number of triple bonds.
     *
     * @return
     */
    public int getNumTripleBonds() {
        return tripleBonds;
    }

    /**
     * Return the deuterium count.
     *
     * @return
     */
    public int getDeuteriumCount() {
        return deuteriumCount;
    }

    /**
     * Return the formal charge.
     *
     * @return
     */
    public int getFormalCharge() {
        return formalCharge;
    }

    /**
     * Return the number of radicals.
     *
     * @return
     */
    public int getSingleElectronCount() {
        return singleElectronCount;
    }

    /**
     * Return the neighborhood descriptor.
     *
     * @return
     */
    public int getNeighborhoodDescriptor() {
        return neighborhoodDescriptor;
    }

    /**
     * Define the neighborhood descriptor of an atom.
     *
     * @param d
     */
    public void setNeighborhoodDescriptor(int d) {
        neighborhoodDescriptor = d;
    }

    /**
     * Return the concatenation of all properties.
     *
     * @return
     */
    public String toString() {

        StringBuilder sb = new StringBuilder();

        sb.append(symbol).append("_");
        sb.append(formalCharge).append("_");
        sb.append(singleElectronCount).append("_");
        sb.append(hydrogenCount).append("_");
        sb.append(deuteriumCount).append("_");
        sb.append(singleBonds).append("_");
        sb.append(doubleBonds).append("_");
        sb.append(tripleBonds).append("_");
        //sb.append(hybridisation).append("_");
        //sb.append(ringSize).append("_");
        sb.append(neighborhoodDescriptor).append("_");

        return sb.toString();
    }

    @Override
    public int hashCode() {
        String str = toString();
        return str.hashCode();
    }

    @Override
    public boolean equals(Object o) {

        if (!(o instanceof AtomProperties))
            return false;

        AtomProperties other = (AtomProperties) o;

        if (this.symbol != other.symbol) return false;
        if (this.singleBonds != other.singleBonds) return false;
        if (this.doubleBonds != other.doubleBonds) return false;
        if (this.tripleBonds != other.tripleBonds) return false;
        if (this.hydrogenCount != other.hydrogenCount) return false;
        if (this.deuteriumCount != other.deuteriumCount) return false;
        if (this.formalCharge != other.formalCharge) return false;
        if (this.singleElectronCount != other.singleElectronCount) return false;
        if (this.neighborhoodDescriptor != other.neighborhoodDescriptor) return false;

        //  if (this.getHybridisation() != other.getHybridisation()) return false;
        //  if (this.getRingSize() != other.getRingSize()) return false;

        return true;
    }

}
