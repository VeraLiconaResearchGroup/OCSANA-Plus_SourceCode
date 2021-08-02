# Running the DrugFEATURE pipeline

## Step 0
Choose a PDB file.
Download both the PDB and the DSSP files.

## Step 1
Choose a ligand/pocket from the PDB file.
Save it in a file, hereafter called `input.1.pocket.txt`.
The format is as follows:

    1rv1	IMZ	A	110

Run the first step of Liu's pipeline with this input file:

    ./GenerateCavityPoint_Vectorize.pl input.1.pocket.txt 6A

`6A` is a magic science number.

The output includes important information!
The last line looks like:

    1rv1	41Z	18      110

Copy `input.1.pocket.txt` to a new file `input.3.pocket.txt`, then add these values to the end of the line.
In our example, the file should read

    1rv1	IMZ	A	110	41	18

Approximate running time: 1s.

## Step 2
Make a new input file representing ligands to score, hereafter called `input.2.site.txt`.
The format is as follows:

    1rv1_IMZ

Compare it against all the known binding sites from the DrugBank:

    ./CompareTwoSetsSites.pl input.2.site.txt $(pwd)/ DrugSiteList.txt /home/andrew/documents/work/research/ocsana/druggability/DrugFEATURE/ParameterFiles/DrugBindingVectors/ ParameterFiles/All1160Cavity.std ParameterFiles/TcCutoff4Normalize.txt output.2.pocketscores.txt

The results are saved in `output.2.pocketscores.txt`.
Approximate running time: 70s.
This should be trivially parallelizable, but we'd need to rewrite the Perl to do it.

## Step 3
Apply the DrugFEATURE formula to the results of the previous step:

    ./CountHit.pl input.3.pocket-enriched.txt output.2.pocketscores.txt 1.9
