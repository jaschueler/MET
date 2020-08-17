# MET: A Faster Java Package for Molecule Equivalence Testing

Molecule Equivalence Tester (MET) is a small Java library to test whether two molecules are equivalent, i.e. whether they can be made identical by relabeling their atom ids. 
Our software can be configured to consider several atom traits like radicals and hydrogen or deuterium count.
MET is build on the [Chemical Development Kit (CDK)](https://github.com/cdk/cdk), which it requires to run. 

**If you use our software in research, please cite**

> Schüler et al., Journal of Cheminformatics 2020?, DOI ?

## Usage 

Our software is designed to be included in CDK software projects as a blackbox method for testing molecule equivalence. In addition, we added two command line scripts that briefly demonstrate MET's core functionality and in addition may be of use on their own.

### 1. Java Code

Include the [MET jar-file](artifacts/) as a library to your Java project. 

The class `met.MoleculeEquivalence` (included in the file  [`src/met/MoleculeEquivalence.java`](src/met/MoleculeEquivalence.java)) is the only class you need to know.
We included two examplary Java files  [`TestMoleculeEquivalence.java`](src/met/example/TestMoleculeEquivalence.java) and  [`TestMoleculePartitioning.java`](src/met/example/TestMoleculePartitioning.java) that demonstrate how this class can be used.
The core part of these files are the following code lines:

    // read molecules from file 
    IAtomContainer mol1 = reader.next();
    IAtomContainer mol2 = reader.next();

    // run molecule equivalence test
    met.MoleculeEquivalence eq = new met.MoleculeEquivalence(mol1, mol2);
    
After that, the method `boolean MoleculeEquivalence.areEquivalent()` gives the result of the test.

### 2. Command Line

For users who wish to test MET's functionality without writing their own application, we included two command line tools to the [scripts](scripts/) directory.

#### 2.1 Equivalence Test

For a quick test on whether two SDF-encoded molecules are equivalent, you can use [`check.sh`](bin/check.sh) script. On a linux system, run

    sh scripts/check.sh <SDF>
     
where the single argument `<SDF>` points to an SDF file with at least two molecules. 
The script tests whether the first two molecules are equivalent, and, in the affirmative, outputs a possible atom mapping.
For example, in linux systems, the command

    sh scripts/check.sh data/example.sdf
    
tests the first two molecules in our [example SDF file](data/example.sdf) and finds that these molecules are not equivalent. 

    5731 and 441240 are EQUIVALENT!

     0	 0
     1	 1
     2	 2
     3	 3
     4	 4
     5	 5
     6	 6
     7	 7
     8	 8
     9	 9
    10	10
    11	11
    12	12
    13	13
    14	14
    15	15
    16	16
    17	17
    18	18
    19	20
    20	19

    
The first and second column of the output gives the atom id's of the first, respective second molecule. 
In the above example, the atoms with the id 0 to 18 have the same id in both molecules.
In contrast, the atom with ids 19 und 20 are exchanged.

#### 2.2 Partitioning

The second tool included in the [bin](bin/) directory can be used to partition a set of molecules into classes of equivalent atoms. Run

    sh partition.sh <SDF>
    
to partition all molecules listed in the `<SDF>` file into equivalence classes. For example, the command

    sh scripts/partition.sh data/example.sdf
    
finds that two molecules (5731 and 441240) included in our [example SDF file](data/example.sdf) are equivalent, while all other are not.

    3 equivalence class(es)!
    5731 (2 members)
    ├ 5731
    └ 441240
    59719069 (1 members)
    └ 59719069
    59719547 (1 members)
    └ 59719547


