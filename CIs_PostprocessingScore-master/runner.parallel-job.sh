#!/bin/sh
if [ "$#" -ne 2 ]; then
    echo "usage:" $0 "num_cores ligand_list_file"
    exit 1
fi

NUM_CORES="$1"
LIGANDS_FILE="$2"
<${LIGANDS_FILE} xargs -n 1 -P ${NUM_CORES} -I {} ./runner.single-job.sh {} ;
