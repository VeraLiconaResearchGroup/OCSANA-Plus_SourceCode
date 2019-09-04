/**
 * Abstract base class for OCSANA tasks
 *
 * Copyright Vera-Licona Research Group (C) 2015
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.tasks;

// Java imports

// Cytoscape imports
import org.cytoscape.model.CyNetwork;

import org.cytoscape.task.AbstractNetworkTask;

import org.cytoscape.work.ObservableTask;

/**
 * Abstract base class for OCSANA tasks.
 *
 * The getResults() method must respond reasonably to a request for an
 * OCSANAStep.
 **/
public abstract class AbstractSFATask extends AbstractNetworkTask
    implements ObservableTask {
    public AbstractSFATask (CyNetwork network) {
        super(network);
    }
}
