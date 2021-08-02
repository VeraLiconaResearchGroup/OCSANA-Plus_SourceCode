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



public class FCAlgorithmTaskFactoryAuto extends AbstractTaskFactory {
	
	    public  FCAlgorithmTaskFactoryAuto () {
	    	super();
	
	    }

		public boolean isReady() {
			return true;
		}
	

	    public TaskIterator createTaskIterator () {
	    	return new TaskIterator(new FCAlgorithmTaskAuto());
	    }

}
