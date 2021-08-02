DrugFEATURE (simplified version)
==============================================================================================================================
Tianyun Liu and Russ B Altman, Stanford University 2013
DrugFEATURE calls an external program FEATURE.  
https://simtk.org/project/xml/downloads.xml?group_id=16#package_id1286

==============================================================================================================================
0. Prepare data
PDB and dssp files (example, 1qhx.pdb and 1qhx.dssp).  
For some PDB, pre-process is necessary to remove duplicate atoms and hydrogen. 
Skipping this step may results in slightly different druggability scores.

==============================================================================================================================

1. GenerateCavityPoint_Vectorize.pl
(1). There are different ways to define cavities (pockets) of a give protein.  
This script starts from a pre-defiend ligand and select residues within 6 Angstroms.
1rv1.pdb=> 1rv1_IMZ.pdb 
The ligand information is saved in a separate file (list1.txt). 
The four columns are: pdb_id, ligand_id, ligand_chain_id, ligand_index 

(2). The selected residues are centered at their functional atoms/centers. 
This step is called "point generation".
1rv1_IMZ.pdb => 1rv1_IMZ.ptf

(3). Then the script calls functions in FEATURE (featurize -P points > vectors) 
1rv1_IMZ.ptf => 1rv1_IMZ.ff 

(4) The output of GenerateCavityPoint_Vectorize.pl summarize the information of the cavity:
list3.txt:
1zuw    DGL C   3301    10  17
1rv1    IMZ A   110 41  18
The last two columns are: 
number of non-hydrogen atoms and number of points in the cavity. 

================================================================================================================================

2. Compare query site to DrugBindingDatabase (DBD)
(1). DBD is a pre-compiled microenvironments. The current version is saved in the folder "DrugBindingVectors". 

(2). Compare query site to DBD
CompareTwoSetsSites.pl QueryList QueryPath DBD_List DBD_path STDFile(All1160Cavity.std) CutOffFile(TcCutoff4Normalize.txt) output
In output file (example: results_pair_score.txt), it saves the comparison between query site and DBD sites. 

3. Calculate druggability scores
CountHit.pl QueryPointList SiteComparisonResult ScoreCutoff (-2) 
It prints out results as:
1zuw    DGL C   3301    10  17  3   2      0.12
1rv1    IMZ A   110 41  18  170 73     4.06

The last column is the druggability score.  Using a cutoff 1.9, 1rv1 is considered druggable and 1zuw is a difficult target. 
================================================================================================================================
Parameter files
All1160Cavity.std 
TcCutoff4Normalize.txt
DrugSiteList.txt 
