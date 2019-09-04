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

package org.compsysmed.ocsana.internal.util.sfa;

// Java imports
import java.util.*;
import java.util.stream.Collectors;

// Cytoscape imports
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
// OCSANA imports
import org.compsysmed.ocsana.internal.util.tunables.NodeHandler;
import org.compsysmed.ocsana.internal.algorithms.AbstractOCSANAAlgorithm;
import org.compsysmed.ocsana.internal.algorithms.fc.FC;
import org.compsysmed.ocsana.internal.algorithms.sfa.AbstractSFAAlgorithm;
import org.compsysmed.ocsana.internal.algorithms.sfa.SFA;
/**
 * Context for an OCSANA run
 *
 * This class stores the configuration required to run OCSANA. A
 * populated instance will be passed to a RunnerTask at the
 * beginning of a run.
 * <p>
 * This class is immutable by design. Instances should be constructed using
 * {@link ContextBundleBuilder}.
 **/
public final class SFABundle {
    private final CyNetwork network;

    private final Set<CyNode> activatedNodes;
    private final Set<CyNode> inhibitedNodes;

    private final NodeHandler nodeHandler;
    private final AbstractSFAAlgorithm SFAalgorithm;
    

    public SFABundle (CyNetwork network,
                          Set<CyNode> activatedNodes,
                          Set<CyNode> inhibitedNodes,
                          NodeHandler nodeHandler,
                          AbstractSFAAlgorithm SFAalgorithm) {
        // Assignments
        Objects.requireNonNull(network, "Network cannot be null");
        this.network = network;

        Objects.requireNonNull(activatedNodes, "Source node set cannot be null");
        this.activatedNodes= activatedNodes;

        Objects.requireNonNull(inhibitedNodes, "Target node set cannot be null");
        this.inhibitedNodes = inhibitedNodes;


        Objects.requireNonNull(nodeHandler, "Node name handler cannot be null");
        this.nodeHandler = nodeHandler;
        
       
        
        Objects.requireNonNull(SFAalgorithm, "SFA algorithm cannot be null");
        this.SFAalgorithm = SFAalgorithm;


        // Sanity checks
        if (activatedNodes.stream().anyMatch(node -> !network.containsNode(node))) {
            throw new IllegalArgumentException("All source nodes must come from underlying network");
        }

        if (inhibitedNodes.stream().anyMatch(node -> !network.containsNode(node))) {
            throw new IllegalArgumentException("All target nodes must come from underlying network");
        }


        if (!Collections.disjoint(activatedNodes, inhibitedNodes)) {
            throw new IllegalArgumentException("Source and target nodes must be disjoint");
        }

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

    /**
     * Get the name of the column that contains the node names
     **/
    public String getNodeNameColumnName () {
        return nodeHandler.getNodeNameColumnName();
    }

    /**
     * Return the source nodes
     **/
    public Set<CyNode> getActivatedNodes () {
        return activatedNodes;
    }

    /**
     * Return the target nodes
     **/
    public Set<CyNode> getInhibitedNodes () {
        return inhibitedNodes;
    }

    /**
     * Helper method to convert a collection of nodes to their names
     **/
    public Collection<String> getNodeNames (Collection<CyNode> nodes) {
        return nodes.stream().map(node -> getNodeName(node)).collect(Collectors.toList());
    }

    /**
     * Return the node name handler
     **/
    public NodeHandler getNodeHandler () {
        return nodeHandler;
    }



    public AbstractSFAAlgorithm getSFAAlgorithm () {
        return SFAalgorithm;
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
    public void cancelAll () {
        
            //SFA.cancel();
        }

	public void uncancelAll() {
		
           SFAalgorithm.uncancel();
        
    }

   
    
}