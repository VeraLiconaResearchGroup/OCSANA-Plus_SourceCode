

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

import org.cytoscape.work.Task;
import org.cytoscape.work.TaskManager;
// Cytoscape imports
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.swing.PanelTaskManager;
import org.ejml.simple.SimpleMatrix;

import org.cytoscape.model.CyNode;
import org.cytoscape.task.AbstractNetworkTask;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.compsysmed.ocsana.internal.tasks.AbstractSFATask;
import org.compsysmed.ocsana.internal.tasks.OCSANAStep;
import org.compsysmed.ocsana.internal.tasks.SFAStep;
import org.compsysmed.ocsana.internal.tasks.runner.RunnerTask;
import org.compsysmed.ocsana.internal.util.context.ContextBundle;
import org.compsysmed.ocsana.internal.util.results.ResultsBundle;
import org.compsysmed.ocsana.internal.util.sfa.SFABundle;
import org.compsysmed.ocsana.internal.util.sfa.SFAResultsBundle;
//import org.compsysmed.ocsana.internal.util.sfa.SFAResultsBundle;
import org.compsysmed.ocsana.internal.util.tunables.NodeHandler;


public class SFATask extends AbstractSFATask{
	private final SFARunnerTask sfarunnerTask;
    private final SFABundle sfaBundle;
    private final SFAResultsBundle sfaresultsBundle;
    private static final SFAStep algStep = SFAStep.DO_SFA;
    public String sfa;

    public SFATask (SFARunnerTask sfarunnerTask,
    		SFABundle sfaBundle,
    		SFAResultsBundle sfaresultsBundle) {
        
    	super(sfaBundle.getNetwork());
    	
       
        Objects.requireNonNull(sfarunnerTask, "Runner task cannot be null");
        this.sfarunnerTask = sfarunnerTask;
        Objects.requireNonNull(sfaBundle, "Context bundle cannot be null");
        this.sfaBundle = sfaBundle;
        Objects.requireNonNull(sfaresultsBundle, "Context bundle cannot be null");
        this.sfaresultsBundle = sfaresultsBundle;
        
        
        
    }

    @Override
    public void run (TaskMonitor taskMonitor) {
        Objects.requireNonNull(taskMonitor, "Task monitor cannot be null");

       
        Set<CyNode> actNodes = sfaBundle.getActivatedNodes();
        Set<CyNode> inhNodes = sfaBundle.getInhibitedNodes();
            

        Objects.requireNonNull(actNodes, "Source nodes not set by user");
        Objects.requireNonNull(inhNodes, "Target nodes not set by user");

       

        taskMonitor.setStatusMessage(String.format("Finding paths"));
        taskMonitor.showMessage(TaskMonitor.Level.INFO, actNodes.toString());
        Long preTime = System.nanoTime();
        Map<CyNode, Double> sfa = sfaBundle.getSFAAlgorithm().computesfa(actNodes, inhNodes);
        Long postTime = System.nanoTime();

        Double runTime = (postTime - preTime) / 1E9;

        if (sfa == null) {
        	 return;
        }
        String sfastring = "";
        for(Entry<CyNode, Double> entry : sfa.entrySet()) {
			 String node = sfaBundle.getNodeName(entry.getKey());
			 Double xval=entry.getValue();
			 sfastring=sfastring+node+": "+String.valueOf(xval)+"\n";
        }
        sfaresultsBundle.setSFA(sfastring);
        
        String configstring = "Activated Nodes: ";
        for (CyNode node : actNodes) {
        	String name = sfaBundle.getNodeName(node);
        	configstring=configstring+"\n"+name;
        }
        configstring=configstring+"\nInhibited Nodes";
        for (CyNode node : inhNodes) {
        	String name = sfaBundle.getNodeName(node);
        	configstring=configstring+"\n"+name;
        }
        sfaresultsBundle.setSFAconfig(configstring);
        taskMonitor.showMessage(TaskMonitor.Level.INFO, String.format("Found paths in %fs.",  runTime));
        
        //taskMonitor.showMessage(TaskMonitor.Level.INFO, sfastring);
    }


	@Override
	public void cancel() {
		super.cancel();
        sfaBundle.getSFAAlgorithm().cancel();
        sfaresultsBundle.setSFAWasCanceled();
        sfarunnerTask.cancel();
		
	}
	public String getSFA() {
        return sfa;
    }

	@SuppressWarnings("unchecked")
    public <T> T getResults (Class<? extends T> type) {
		
	        if (type.isAssignableFrom(SFAStep.class)) {
	            return (T) algStep;
	        } else {
	        	throw new IllegalArgumentException("Invalid results type for SFA");
	        }
	    }
    
}
