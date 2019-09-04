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

package org.compsysmed.ocsana.internal.tasks.fvs;

import java.net.URISyntaxException;
// Java imports
import java.util.*;

// Cytoscape imports
import org.cytoscape.work.TaskMonitor;
import org.lappsgrid.pycaller.PyCallerException;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyEdge;
import org.compsysmed.ocsana.internal.tasks.AbstractFCTask;
import org.compsysmed.ocsana.internal.tasks.AbstractFVSTask;
// OCSANA imports
import org.compsysmed.ocsana.internal.tasks.AbstractOCSANATask;
import org.compsysmed.ocsana.internal.tasks.FCStep;
import org.compsysmed.ocsana.internal.tasks.FVSStep;
import org.compsysmed.ocsana.internal.tasks.OCSANAStep;
import org.compsysmed.ocsana.internal.tasks.runner.RunnerTask;
import org.compsysmed.ocsana.internal.tasks.sfa.SFARunnerTask;
import org.compsysmed.ocsana.internal.util.context.ContextBundle;
import org.compsysmed.ocsana.internal.util.fc.FCBundle;
import org.compsysmed.ocsana.internal.util.fc.FCResultsBundle;
import org.compsysmed.ocsana.internal.util.fvs.FVSBundle;
import org.compsysmed.ocsana.internal.util.fvs.FVSResultsBundle;
import org.compsysmed.ocsana.internal.util.results.ResultsBundle;
import org.compsysmed.ocsana.internal.util.sfa.SFABundle;
import org.compsysmed.ocsana.internal.util.sfa.SFAResultsBundle;
import org.compsysmed.ocsana.internal.util.tunables.NodeHandler;

public class FVSAlgorithmTask extends AbstractFVSTask {
	private static final FVSStep algStep = FVSStep.DO_FVS;
	private final FVSRunnerTask fvsrunnerTask;
    private final FVSBundle fvsBundle;
    private final FVSResultsBundle fvsresultsBundle;
    private List<CyNode> FVS;

    public FVSAlgorithmTask (FVSRunnerTask fvsrunnerTask,
				    		FVSBundle fvsBundle,
				    		FVSResultsBundle fvsresultsBundle) {
        super(fvsBundle.getNetwork());

        Objects.requireNonNull(fvsrunnerTask, "Runner task cannot be null");
        this.fvsrunnerTask = fvsrunnerTask;

        Objects.requireNonNull(fvsBundle, "Context bundle cannot be null");
        this.fvsBundle = fvsBundle;

        Objects.requireNonNull(fvsresultsBundle, "Context results cannot be null");
        this.fvsresultsBundle = fvsresultsBundle;

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
        Map<String, List<CyNode>> FC = fvsBundle.getFCAlgorithm().FVS();
        taskMonitor.setTitle(String.format("formatting output"));
        FVS=FC.get("fvsnodes");
        //List<CyNode> sourcenodes = FC.get("sourcenodes");
        String FC_string = "Source Nodes not computed";
//        for (CyNode node:sourcenodes) {
//     	   String nodename=fvsBundle.getNodeName(node);
//     	   FC_string=FC_string+nodename+"\t";
//        }
        
        int numberFVS=FC.size();
        for (int i = 1;i<numberFVS;i++) {
        	FC_string = FC_string+"\nFVS_"+String.valueOf(i)+": ";
        	FVS=FC.get("FVS_"+String.valueOf(i));
        	for (CyNode node:FVS) {
            	String nodename = fvsBundle.getNodeName(node);
            	FC_string=FC_string+nodename+"\t";
            }
        }
        
        fvsresultsBundle.setFC(FC_string);
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
        if (type.isAssignableFrom(FVSStep.class)) {
            return (T) algStep;
        } else {
            throw new IllegalArgumentException("Invalid results type for FVS");
        }
    }

    @Override
    public void cancel () {
        super.cancel();
        fvsBundle.getFCAlgorithm().cancel();
        fvsresultsBundle.setFCWasCanceled();
        fvsrunnerTask.cancel();
    }
}
