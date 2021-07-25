/**
 * Factory for tasks to run path-finding algorithms in OCSANA
 *
 * Copyright Vera-Licona Research Group (C) 2015
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.tasks.fc;

// Java imports
import java.util.*;

// Cytoscape imports
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;

// OCSANA imports
import org.compsysmed.ocsana.internal.tasks.OCSANAStep;
import org.compsysmed.ocsana.internal.tasks.runner.RunnerTask;
import org.compsysmed.ocsana.internal.ui.fc.FCResultsPanel;
import org.compsysmed.ocsana.internal.ui.sfaresults.SFAResultsPanel;
import org.compsysmed.ocsana.internal.util.context.ContextBundle;
import org.compsysmed.ocsana.internal.util.fc.FCBundle;
import org.compsysmed.ocsana.internal.util.fc.FCResultsBundle;
import org.compsysmed.ocsana.internal.util.results.ResultsBundle;
import org.compsysmed.ocsana.internal.util.sfa.SFAResultsBundle;

public class FCAlgorithmTaskFactory
    extends AbstractTaskFactory {
	    private final FCRunnerTask fcRunnerTask;
	    private final FCBundle fcBundle;
	    private final FCResultsBundle fcresultsBundle;
	
	    public  FCAlgorithmTaskFactory (FCRunnerTask fcRunnerTask, 
	                                            FCBundle fcBundle,
	                                            FCResultsBundle fcresultsBundle) {
	        super();
	
	        Objects.requireNonNull(fcRunnerTask, "Runner task cannot be null");
	        this.fcRunnerTask = fcRunnerTask;
	
	        Objects.requireNonNull(fcBundle, "Context bundle cannot be null");
	        this.fcBundle = fcBundle;
	
	        Objects.requireNonNull(fcresultsBundle, "Context results cannot be null");
	        this.fcresultsBundle = fcresultsBundle;
	
	       
	    }
	
	    @Override
	    public TaskIterator createTaskIterator () {
	        TaskIterator tasks = new TaskIterator();
	        tasks.append(new FCAlgorithmTask(fcRunnerTask, fcBundle, fcresultsBundle));
	        return tasks;
	    }
}
