# PyDrugFEATURE
PyDrugFEATURE is a Python wrapper around the [DrugFeature software](drugfeature) of Liu and Altman, first published in ["Identifying druggable targets by protein microenvironments matching: application to transcription factors"](drugfeature-paper) by those authors.

## Installing and compiling
The wrapper is written in Python; the original DrugFEATURE software is written in Perl.
Thus, you must have both languages available on your computer.
In addition, DrugFEATURE relies on the FEATURE software for microenvironment calculations.
Before running PyDrugFEATURE, you must navigate to the `feature` directory and run `make`.

## Running PyDrugFEATURE
The wrapper is implemented in `pydrugfeature.py`.

PyDrugFEATURE is meant for batch processing of PDB files.
We assume that you have already downloaded a copy of the full PDB.
If not, you can use the included script `pdb-fetch.sh` to do so in a directory of your choice.
(CAUTION: as of this writing, this will pull 22GB of files.)

To run PyDrugFEATURE, you will need to choose a PDB file and ligand (identified by its name, chain, and index).
We will use the running example of PDB structure `1rv1`, available at the [PDB site](pdb-1rv1).
Ligand `IMZ` is found on chain `A` at index `110`.
You can then run DrugFEATURE by calling
    ./pydrugfeature.py 1rv1 IMZ A 110 -p YOUR_PDB_DIR

where `YOUR_PDB_DIR` is the directory where you downloaded the PDB.

## Running PyDrugFEATURE on batches
Shell scripts are included to run batches of DrugFEATURE jobs, for example on a computing cluster.
See `runner.single-job.sh`, `runner.parallel-job.sh`, and `runner.spawn-jobs.sh` for information.

[drugfeature]: https://simtk.org/home/drugfeature
[drugfeature-paper]: https://dx.doi.org/10.1038/psp.2013.66
[pdb-1rv1]: http://www.rcsb.org/pdb/explore.do?structureId=1rv1
