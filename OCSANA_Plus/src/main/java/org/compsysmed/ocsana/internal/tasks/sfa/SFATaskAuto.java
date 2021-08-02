

/**
 * Task to run path-finding algorithm in OCSANA
 *
 * Copyright Vera-Licona Research Group (C) 2015
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.tasks.sfa;

// Java imports
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.ProvidesTitle;


// Cytoscape imports
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;


import org.cytoscape.model.CyNode;

import org.cytoscape.model.CyNetwork;
import org.compsysmed.ocsana.internal.algorithms.sfa.AbstractSFAAlgorithm;
import org.compsysmed.ocsana.internal.algorithms.sfa.SFA;

import org.compsysmed.ocsana.internal.util.sfa.SFABundle;
//import org.compsysmed.ocsana.internal.util.sfa.SFAResultsBundle;
import org.compsysmed.ocsana.internal.util.tunables.NodeHandler;


public class SFATaskAuto extends AbstractTask implements ObservableTask{
    public SFATaskAuto () {
        
    	super();
    }
    @ProvidesTitle
	public String getTitle() { return "computes SFA"; }
    
	@Tunable(description="Network to compute SFA on", required=true)
	  public CyNetwork network;
	@Tunable(description="node name column", required=true)
	  public String column;
	@Tunable(description="nodes to activate")
	  public String activations="";
	@Tunable(description="nodes to inhibit")
	  public String inhibitions="";
	
    public String sfastring;
    public AbstractSFAAlgorithm SFAalgorithm;



	@Override
    public void run (TaskMonitor taskMonitor) {
    	NodeHandler nodehandler=new NodeHandler(network);
    	nodehandler.setNodeNameColumn(network.getDefaultNodeTable().getColumn(column));
    	Set<CyNode> anodes = getSelectedNodes(activations,nodehandler);
    	Set<CyNode> inodes = getSelectedNodes(inhibitions,nodehandler);
        SFAalgorithm= new SFA(network);
    	
    	SFABundle sfaBundle= new SFABundle(network, anodes, inodes, nodehandler, SFAalgorithm);
       
        Set<CyNode> actNodes = sfaBundle.getActivatedNodes();
        Set<CyNode> inhNodes = sfaBundle.getInhibitedNodes();
            

        //Objects.requireNonNull(actNodes, "Source nodes not set by user");
        //Objects.requireNonNull(inhNodes, "Target nodes not set by user");
        //taskMonitor.setStatusMessage(String.format("Finding paths"));
        //taskMonitor.showMessage(TaskMonitor.Level.INFO, actNodes.toString());
        
        Long preTime = System.nanoTime();
        Map<CyNode, Double> sfa = sfaBundle.getSFAAlgorithm().computesfa(actNodes, inhNodes);
        Long postTime = System.nanoTime();

        Double runTime = (postTime - preTime) / 1E9;

//        if (sfa == null) {
//        	 return;
//        }
        
        for(Entry<CyNode, Double> entry : sfa.entrySet()) {
			 String node = sfaBundle.getNodeName(entry.getKey());
			 Double xval=entry.getValue();
			 sfastring=sfastring+node+": "+String.valueOf(xval)+"\n";
        }
        

    }




    public Set<CyNode> getSelectedNodes (String nodes, NodeHandler nodehandler) {
    	NodeHandler nodeHandler=nodehandler;
        Set<String> selectedNodeNames = Arrays.asList(nodes.split("[,\t\n]")).stream().map(nodeName -> nodeName.trim()).filter(nodeName -> !nodeName.isEmpty()).collect(Collectors.toSet());
        Set<CyNode> selectedNodes = selectedNodeNames.stream().map(nodeName -> nodeHandler.getNodeByName(nodeName)).filter(node -> node != null).collect(Collectors.toSet());
        return selectedNodes;
    }

	@SuppressWarnings("unchecked")
	public <R> R getResults(Class<? extends R> type) {
		if (type.equals(String.class)) {
			return (R) (sfastring.toString());
		} 
		else {
			return null;
		}
	}
    
}
