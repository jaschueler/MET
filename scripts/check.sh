#!/bin/bash

# todo: parse arguments

# path to jar
jarpath=$(pwd)/../artifacts/met.jar

# class name
classname="met.example.TestMoleculeEquivalence"

# run jar
java -Xmx2G -cp ${jarpath} ${classname} "$@"
