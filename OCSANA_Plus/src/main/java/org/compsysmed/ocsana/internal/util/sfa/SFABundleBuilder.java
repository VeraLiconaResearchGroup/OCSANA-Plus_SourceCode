/**
 * Builder for contexts for an OCSANA run
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

// Cytoscape imports
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
// OCSANA imports
import org.compsysmed.ocsana.internal.util.tunables.NodeHandler;
import org.compsysmed.ocsana.internal.algorithms.sfa.AbstractSFAAlgorithm;
import org.compsysmed.ocsana.internal.algorithms.sfa.SFA;


/**
 * Context builder for an OCSANA run
 * <p>
 * This class allows incremental construction of a
 * {@link ContextBundle}.
 * Its getters should only be used during configuration; the immutable
 * ContextBundle should be used once configuration is complete.
 **/
public class SFABundleBuilder {
    private final CyNetwork network;

    private Set<CyNode> activatedNodes = new HashSet<>();
    private Set<CyNode> inhibitedNodes = new HashSet<>();

    

    private NodeHandler nodeHandler;
    private AbstractSFAAlgorithm SFAalgorithm;
    
    public SFABundleBuilder (CyNetwork network) {
        Objects.requireNonNull(network, "Network cannot be null");
        this.network = network;

        setNodeHandler(new NodeHandler(network));
        SFAalgorithm= new SFA(network);

    }

    /**
     * Return the underlying network
     **/
    public CyNetwork getNetwork () {
        return network;
    }

    /**
     * Set the source nodes
     **/
    public void setActivatedNodes (Set<CyNode> activatedNodes) {
        Objects.requireNonNull(activatedNodes, "Source node set cannot be null");

        if (activatedNodes.stream().anyMatch(node -> !network.containsNode(node))) {
            throw new IllegalArgumentException("All source nodes must come from underlying network");
        }

        this.activatedNodes = activatedNodes;
    }

    /**
     * Return the currently-selected source nodes
     **/
    public Set<CyNode> getActivatedNodes () {
        return activatedNodes;
    }

    /**
     * Set the target nodes
     * <p>
     * NOTE: this has the side effect of clearing the configured targets
     * to activate
     **/
    public void setInhibitedNodes (Set<CyNode> inhibitedNodes) {
        Objects.requireNonNull(inhibitedNodes, "Target node set cannot be null");

        if (inhibitedNodes.stream().anyMatch(node -> !network.containsNode(node))) {
            throw new IllegalArgumentException("All target nodes must come from underlying network");
        }

        this.inhibitedNodes = inhibitedNodes;
        
    }

    /**
     * Return the currently-selected target nodes
     **/
    public Set<CyNode> getInhibitedNodes () {
        return inhibitedNodes;
    }

    /**
     * Set the node name handler
     **/
    public void setNodeHandler (NodeHandler nodeHandler) {
        Objects.requireNonNull(nodeHandler, "Node name handler cannot be null");

        this.nodeHandler = nodeHandler;
    }

    /**
     * Return the currently-selected node name handler
     **/
    public NodeHandler getNodeHandler () {
        return nodeHandler;
    }
    

	public AbstractSFAAlgorithm getSFAalgorithm() {
		return SFAalgorithm;
	}

    /**
     * Return the context as currently configured
     **/
    public SFABundle getSFABundle () {
        return new SFABundle(network, activatedNodes, inhibitedNodes, nodeHandler, SFAalgorithm);
    }

}