#!/bin/bash

# Make build
cd src
javac */*.java
# Creation of input and output directory
mkdir -p Input
mkdir -p Output
# creation of a simple file
echo this is a basic file for a simple test > Input/simple.txt
cd ../