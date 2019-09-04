#!/bin/sh
if [ "$#" -ne 1 ]; then
    echo "usage:" $0 "ligand_description"
    echo "ligand_description should be a tab-separated list"
    exit 1
fi

LIGAND_DESC=${1}

PDB=$(echo ${LIGAND_DESC} | cut -d' ' -f1)
LIGAND=$(echo ${LIGAND_DESC} | cut -d' ' -f2)
CHAIN=$(echo ${LIGAND_DESC} | cut -d' ' -f3)
INDEX=$(echo ${LIGAND_DESC} | cut -d' ' -f4)

${HOME}/agd/pdb/pydrugfeature/pydrugfeature.py -vv ${PDB} ${LIGAND} ${CHAIN} ${INDEX} -p ${HOME}/agd/pdb/data/structures/drugfeature/pdb -d ${HOME}/agd/pdb/data/drugfeature/results/
