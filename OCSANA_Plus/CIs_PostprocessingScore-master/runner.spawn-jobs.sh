#!/bin/sh
if [ "$#" -ne 1 ]; then
	echo "usage:" $0 "ligand_list_dir"
	exit 1
fi

LIGAND_DIR=${1}
for LIGAND_FILE in LIGAND_DIR/*; do
    qsub -v LIGAND_FILE=${LIGAND_FILE} runner.parallel.job.pbs
done
