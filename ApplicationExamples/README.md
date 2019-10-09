# Application Examples

This folder contains the computations and results files for the two Application examples in the OCSANA+ paper and Supplementary File.

## Kobayashi_AscidianEmbryo
To show experimentally the FC controllability to specify cell fates in an ascidian embryo, Kobayashi (2018. et al.) identified FC nodes in the gene
regulatory network of Ciona intestinalis embryos and performed in vitro knock-down and upregulation experiments of the FC nodes to control cell fate specification. Using OCSANA+, we reproduced the FC sets identified in Kobayashi (2018. et al.), and were able to correctly simulate 66% of the experimental results using simulated FC perturbations with SFA. To
improve our in silico results, we used FC with source nodes to obtain the network’s source nodes. Then, we used OCSANA to identify CIs from the network’s source nodes to the cell fate specifying network nodes. Using
SFA for in silico perturbations of the FC and CIs, we were able to correctly predict up to 85% of the observed experimental results, highlighting that addition of source nodes and CIs can improve the prediction of simulated results.

## Zanudo_DrosophilaSegmentPolarity

Zañudo et al. (2017) applied FC to the validated Drosophila developmental segment polarity network to
benchmark the ability of FC to guide cell fates by simulating perturbations to a subset of FC nodes when there is an attractor of interest. We used OCSANA+ to successfully identify the FC set. We further used SFA
to simulate FC perturbations that drive correct embryonic patterning in comparison with the results from Zañudo et al. (2017).