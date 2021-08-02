/**
 * Factory for OCSANA runner tasks
 *
 * Copyright Vera-Licona Research Group (C) 2016
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.tasks.sfa;

// Java imports
import java.util.*;

// Cytoscape imports
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.TaskObserver;
import org.cytoscape.work.swing.PanelTaskManager;
import org.compsysmed.ocsana.internal.tasks.OCSANAStep;
import org.compsysmed.ocsana.internal.tasks.SFAStep;
import org.compsysmed.ocsana.internal.tasks.fc.FCAlgorithmTaskAuto;
import org.compsysmed.ocsana.internal.tasks.runner.RunnerTask;
import org.compsysmed.ocsana.internal.util.results.ResultsBundle;
// OCSANA imports
import org.compsysmed.ocsana.internal.util.sfa.SFABundle;
//import org.compsysmed.ocsana.internal.util.sfa.SFABundleBuilder;
//import org.compsysmed.ocsana.internal.util.sfa.SFAResultsBundle;
import org.compsysmed.ocsana.internal.util.sfa.SFAResultsBundle;


/**
 * Factory for OCSANA runner tasks
 **/
public class SFAAutoTaskFactory extends AbstractTaskFactory {
	
    public SFAAutoTaskFactory () {
        super();

    }
	public boolean isReady() {
		return true;
	}


    public TaskIterator createTaskIterator () {
    	return new TaskIterator(new SFATaskAuto());
    }


}
