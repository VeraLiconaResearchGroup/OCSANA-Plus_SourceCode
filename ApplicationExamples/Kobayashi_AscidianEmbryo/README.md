This folder contains the input and output files for the OCSANA+ application example of reproducing results of [Kobayshi et al., 2018](https://www.sciencedirect.com/science/article/pii/S2589004218300592?via%3Dihub#appsec2).

This application example has been included in our OCSANA+ manual [walkthrough tutorial](https://ocsana-plus.readthedocs.io/en/latest/walkthrough.html)

**kobayashi.sif**: the gene regulatory network (GRN) file</br>
**FC_withoutsourcenodes**: the results of "FC without source nodes" algorithm from OCSANA+</br>
## SFA result files
The SFA result files contain the configuration of activated or inhibited nodes, and the steady state log values for all nodes in the network. 
**SFAresult_unperturbed**: the results of SFA with activation of Gata.a and Zic.r-a </br>
**SFAresult_adnze_epithelial**: the results of SFA with FC perturbation that yielded epithlial specifciation in Kobayshi et al., 2018</br>
**SFAresult_adnZe_brainpan**: the results of SFA with FC perturbation that yielded brain+pan-neural specifciation in Kobayshi et al., 2018</br>
**SFAresult_adNze_pannueral**: the results of SFA with FC perturbation that yielded pan-neural specifciation in Kobayshi et al., 2018</br>
**SFAresult_Adnze_endoderm**: the results of SFA with FC perturbation that yielded endoderm specifciation in Kobayshi et al., 2018</br>
**SFAresult_aDnze_notochord**: the results of SFA with FC perturbation that yielded notochord specifciation in Kobayshi et al., 2018</br>
**SFAresult_adnZE_mesenchyme**: the results of SFA with FC perturbation that yielded mesenchyme specifciation in Kobayshi et al., 2018</br>

**FC_withsourcenodes**: the results of "FC with source nodes" algorithm from OCSANA+</br>
**kobayashi_ocsana_report.txt**: the configuration and results for CI discovery from the network source nodes, to epithelial cell fate marker, *Epi1*</br>

**SFAresult_epithelial_ci**: the results of SFA with FC perturbation that yielded epithlial specifciation in Kobayshi et al., 2018 PLUS the activation of CI node Gata.a</br>

**perturbation_logFCcalculation.xslx**: This sheet contains a summary of the SFA results for the cell fate specification nodes of interest, and the calcualted logFC results from SFA("perturbed log steady state value"-"unperturbed log steady state value")
