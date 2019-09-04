/**
 * Context for an OCSANA run
 *
 * Copyright Vera-Licona Research Group (C) 2016
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.util.fvs;

// Java imports
import java.util.*;
import java.util.stream.Collectors;

// Cytoscape imports
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
// OCSANA imports
import org.compsysmed.ocsana.internal.util.tunables.NodeHandler;
import org.compsysmed.ocsana.internal.algorithms.fc.AbstractFCAlgorithm;
import org.compsysmed.ocsana.internal.algorithms.fc.FC;

/**
 * Context for an FC run
 *
 * This class stores the configuration required to run OCSANA. A
 * populated instance will be passed to a RunnerTask at the
 * beginning of a run.
 * <p>
 * This class is immutable by design. Instances should be constructed using
 * {@link ContextBundleBuilder}.
 **/
public final class FVSBundle {
    private final CyNetwork network;

    private final AbstractFCAlgorithm FCalgorithm;
    private final NodeHandler nodeHandler;

    public FVSBundle (CyNetwork network,
    				NodeHandler nodeHandler,
    				AbstractFCAlgorithm FCalgorithm) {
        // Assignments
        Objects.requireNonNull(network, "Network cannot be null");
        this.network = network;

        Objects.requireNonNull(FCalgorithm, "FC algorithm cannot be null");
        this.FCalgorithm = FCalgorithm;

        Objects.requireNonNull(nodeHandler, "Node name handler cannot be null");
        this.nodeHandler = nodeHandler;

    }

    /**
     * Return the CyNetwork used by this context
     **/
    public CyNetwork getNetwork () {
        return network;
    }

    /**
     * Return the name of the CyNetwork used by this context
     **/
    public String getNetworkName () {
        return network.getRow(network).get(CyNetwork.NAME, String.class);
    }


    public AbstractFCAlgorithm getFCAlgorithm () {
        return FCalgorithm;
    }
    
   
    public void cancelAll () {
        
            //SFA.cancel();
        }

	public void uncancelAll() {
		
           FCalgorithm.uncancel();
        
    }

	/**
     * Get the name of a node
     *
     * @param node  the node
     *
     * @return the node's name
     **/
    public String getNodeName (CyNode node) {
        return nodeHandler.getNodeName(node);
    }

   
    
}