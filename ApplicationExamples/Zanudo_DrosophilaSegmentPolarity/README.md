This folder contains the input and output files for the OCSANA+ application example of reproducing results of [Zanudo et al., 2017](https://www.pnas.org/content/114/28/7234).

**zanudo.sif**: the segment polarity GRN file.</br>
**FC_withsourcenodes**: the results of "FC without source nodes" algorithm from OCSANA+</br>
## SFA result files
The SFA result files contain the configuration of activated or inhibited nodes, and the steady state log values for all nodes in the network. 

**SFAresult_patterned**: the results of SFA with activation *0_ptc, 3_ci, 1_ci, 1_wg, 1_SLP, 3_ptc, 1_ptc, 2_hh, 0_SLP, 2_en, 0_ci* </br>
**SFAresult_unpatterned**: the results of SFA with unpatterned initial state (no activation of any nodes)</br>
**SFAresult_FCperturb**: the results of SFA with FC perturbation that leads to patterned steady state </br>

**perturbation_logFCcalculation.xslx**: This sheet contains a summary of the SFA results for the cell fate specification nodes of interest, and the calcualted logFC results from SFA("perturbed log steady state value"-"unperturbed log steady state value")
