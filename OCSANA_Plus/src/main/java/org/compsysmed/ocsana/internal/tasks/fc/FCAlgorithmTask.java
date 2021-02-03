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

package org.compsysmed.ocsana.internal.tasks.fc;

import java.net.URISyntaxException;
// Java imports
import java.util.*;

// Cytoscape imports
import org.cytoscape.work.TaskMonitor;
import org.lappsgrid.pycaller.PyCallerException;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyEdge;
import org.compsysmed.ocsana.internal.tasks.AbstractFCTask;
// OCSANA imports
import org.compsysmed.ocsana.internal.tasks.AbstractOCSANATask;
import org.compsysmed.ocsana.internal.tasks.FCStep;
import org.compsysmed.ocsana.internal.tasks.OCSANAStep;
import org.compsysmed.ocsana.internal.tasks.runner.RunnerTask;
import org.compsysmed.ocsana.internal.tasks.sfa.SFARunnerTask;
import org.compsysmed.ocsana.internal.util.context.ContextBundle;
import org.compsysmed.ocsana.internal.util.fc.FCBundle;
import org.compsysmed.ocsana.internal.util.fc.FCResultsBundle;
import org.compsysmed.ocsana.internal.util.results.ResultsBundle;
import org.compsysmed.ocsana.internal.util.sfa.SFABundle;
import org.compsysmed.ocsana.internal.util.sfa.SFAResultsBundle;
import org.compsysmed.ocsana.internal.util.tunables.NodeHandler;

public class FCAlgorithmTask extends AbstractFCTask {
	private static final FCStep algStep = FCStep.DO_FC;
	private final FCRunnerTask fcrunnerTask;
    private final FCBundle fcBundle;
    private final FCResultsBundle fcresultsBundle;
    private List<CyNode> FVS;

    public FCAlgorithmTask (FCRunnerTask fcrunnerTask,
				    		FCBundle fcBundle,
				    		FCResultsBundle fcresultsBundle) {
        super(fcBundle.getNetwork());

        Objects.requireNonNull(fcrunnerTask, "Runner task cannot be null");
        this.fcrunnerTask = fcrunnerTask;

        Objects.requireNonNull(fcBundle, "Context bundle cannot be null");
        this.fcBundle = fcBundle;

        Objects.requireNonNull(fcresultsBundle, "Context results cannot be null");
        this.fcresultsBundle = fcresultsBundle;

    }

    @Override
    public void run (TaskMonitor taskMonitor)  {
    	Objects.requireNonNull(taskMonitor, "Task monitor cannot be null");

        taskMonitor.setTitle(String.format("FVS"));
        /**String targetType;
        Set<CyNode> sourceNodes = contextBundle.getSourceNodes();**/
        

        taskMonitor.setTitle(String.format("Finding FVS"));


        Long preTime = System.nanoTime();
        
        taskMonitor.setTitle(String.format("running FC"));
        Map<String, List<CyNode>> FC = fcBundle.getFCAlgorithm().FVS();
        taskMonitor.setTitle(String.format("formatting output"));
        List<CyNode> sourcenodes = FC.get("sourcenodes");
        String FC_string = "Source Nodes: ";
        for (CyNode node:sourcenodes) {
     	   String nodename=fcBundle.getNodeName(node);
     	   FC_string=FC_string+nodename+"\t";
        }

        int numberFVS=FC.size();
        if (numberFVS<3) {
        	FVS=FC.get("FVS_1");
        	if ((FVS).size()==0) {
        		FC_string=FC_string+"\nno FVSes identified";	
        	}else {
        		FC_string = FC_string+"\nFVS_1: ";
        		for (CyNode node:FVS) {
	            	String nodename = fcBundle.getNodeName(node);
	            	FC_string=FC_string+nodename+"\t";
	            }	
        	}
        	
        } else {
	        for (int i = 1;i<numberFVS;i++) {
	        	FC_string = FC_string+"\nFVS_"+String.valueOf(i)+": ";
	        	FVS=FC.get("FVS_"+String.valueOf(i));
	        	for (CyNode node:FVS) {
	            	String nodename = fcBundle.getNodeName(node);
	            	FC_string=FC_string+nodename+"\t";
	            }
        }
        }
        
        fcresultsBundle.setFC(FC_string);
        taskMonitor.setTitle(String.format(FC.toString()));
        Long postTime = System.nanoTime();

        Double runTime = (postTime - preTime) / 1E9;

        if (FVS==null){
        	taskMonitor.setTitle(String.format("Finding was null"));
            return; 
        }

        taskMonitor.showMessage(TaskMonitor.Level.INFO, String.format("Found the FVS in %fs.", runTime));
    }

    

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getResults (Class<? extends T> type) {
        if (type.isAssignableFrom(FCStep.class)) {
            return (T) algStep;
        } else {
            throw new IllegalArgumentException("Invalid results type for FVS");
        }
    }

    @Override
    public void cancel () {
        super.cancel();
        fcBundle.getFCAlgorithm().cancel();
        fcresultsBundle.setFCWasCanceled();
        fcrunnerTask.cancel();
    }
}
