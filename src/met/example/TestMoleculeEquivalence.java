package met.example;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.iterator.IteratingSDFReader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class TestMoleculeEquivalence {

    /**
     * Example command line application to demonstrate how the TestMoleculeEquivalence class can be used.
     * <p>
     * This application reads an SDF file of *exactly* two molecules and tests whether these molecules are equivalent.
     * In the affirmative, the application outputs the mapping between the atoms.
     *
     * @param args
     */
    public static void main(String[] args) throws FileNotFoundException {

        // parse arguments
        if (args.length != 1) {
            System.err.println("Usage: java TestMoleculeEquivalence <SDF>");
            System.err.println("   where <SDF> is an SDF file containing exactly two molecules");
            return;
        }

        // open SDF file
        IteratingSDFReader reader = new IteratingSDFReader(new FileInputStream(args[0]), DefaultChemObjectBuilder.getInstance());

        // read molecules from file (assuming the file contains exactly two molecules)
        IAtomContainer mol1 = reader.next();
        IAtomContainer mol2 = reader.next();

        // run molecule equivalence test
        met.MoleculeEquivalence eq = new met.MoleculeEquivalence(mol1, mol2);

        // extract ids of molecules
        String id1 = mol1.getProperty("PUBCHEM_COMPOUND_CID").toString();
        String id2 = mol2.getProperty("PUBCHEM_COMPOUND_CID").toString();

        // if mol1 and mol2 are *not* equivalent
        if (!eq.areEquivalent()) {
            System.out.println(id1 + " and " + id2 + " are NOT EQUIVALENT!");
            return;
        }

        // if mol1 and mol2 are equivalent
        System.out.println(id1 + " and " + id2 + " are EQUIVALENT!\n");

        // determine the number of digits needed to display the atom ids
        int length = String.valueOf(mol1.getAtomCount()).length();
        length = Integer.min(length, 2);

        // print out headline for atom mapping
        //System.out.println(id1 + "\t" + id2);

        // print out atom mapping
        for (int i = 0; i < mol1.getAtomCount(); i++) {

            // atom v (in molecule 1) is attached to atom w (in molecule 2)
            IAtom v = mol1.getAtom(i);
            IAtom w = eq.getAtomMapping().get(v);

            // format output string
            String idv = String.format("%1$" + length + "s", mol1.indexOf(v));
            String idw = String.format("%1$" + length + "s", mol2.indexOf(w));

            System.out.println(idv + "\t" + idw);
        }

    }

}
