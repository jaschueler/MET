package met.example;

import met.algorithm.MoleculePartition;
import met.helper.EquivalenceClass;
import met.molecule.Molecule;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.iterator.IteratingSDFReader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Main class for partitioning molecules into equivalence classes.
 */
public class TestMoleculePartitioning {

    public static void main(String[] args) throws FileNotFoundException {

        // parse arguments
        if (args.length != 1) {
            System.err.println("Usage: java TestMoleculePartitioning <SDF>");
            System.err.println("   where <SDF> is an SDF file with molecules.");
            return;
        }

        // open SDF file
        IteratingSDFReader reader = new IteratingSDFReader(new FileInputStream(args[0]), DefaultChemObjectBuilder.getInstance());

        // create partitioner object
        MoleculePartition part = new MoleculePartition();

        // read molecules one by one from file
        // read all molecules from file
        while ((reader.hasNext())) {

            // read next molecule from file
            IAtomContainer mol = reader.next();

            // convert into molecule graph
            Molecule g = new Molecule(mol);

            // distribute mol to its equivalence class
            part.add(g);
        }

        // output number of equivalence classes
        int size = part.getEquivalenceClasses().size();
        System.out.println(size + " equivalence class(es)!");

        // for each equivalence class
        for (EquivalenceClass<Molecule> c : part.getEquivalenceClasses()) {

            // determine the representative of each class
            Molecule rep = c.getRepresentative();
            String id = rep.getCDKContainer().getProperty("PUBCHEM_COMPOUND_CID").toString();
            System.out.println(id + " (" + c.size() + " members)");

            // for each member of this class
            for (int i = 0; i < c.size(); i++) {

                Molecule mol = c.getItems().get(i);
                id = mol.getCDKContainer().getProperty("PUBCHEM_COMPOUND_CID").toString();

                if (i < c.size() - 1)
                    System.out.println("├ " + id);
                else
                    System.out.println("└ " + id);

            }
        }
    }
}
