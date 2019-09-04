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

package org.compsysmed.ocsana.internal.util.context;

// Java imports
import java.util.*;

// Cytoscape imports
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;

// OCSANA imports
import org.compsysmed.ocsana.internal.util.tunables.NodeHandler;

import org.compsysmed.ocsana.internal.algorithms.mhs.AbstractMHSAlgorithm;
import org.compsysmed.ocsana.internal.algorithms.mhs.MMCSAlgorithm;
import org.compsysmed.ocsana.internal.algorithms.fc.FC;
import org.compsysmed.ocsana.internal.algorithms.path.AbstractPathFindingAlgorithm;
import org.compsysmed.ocsana.internal.algorithms.path.AllNonSelfIntersectingPathsAlgorithm;

import org.compsysmed.ocsana.internal.algorithms.scoring.OCSANAScoringAlgorithm;

/**
 * Context builder for an OCSANA run
 * <p>
 * This class allows incremental construction of a
 * {@link ContextBundle}.
 * Its getters should only be used during configuration; the immutable
 * ContextBundle should be used once configuration is complete.
 **/
public class ContextBundleBuilder {
    private final CyNetwork network;

    private Set<CyNode> sourceNodes = new HashSet<>();
    private Set<CyNode> targetNodes = new HashSet<>();
    private Set<CyNode> offTargetNodes = new HashSet<>();

    private Set<CyNode> targetsToActivate = new HashSet<>();

    private NodeHandler nodeHandler;
    private boolean includeEndpointsInCIs;

    private AbstractPathFindingAlgorithm pathFindingAlgorithm;
    private AbstractMHSAlgorithm mhsAlgorithm;
    private OCSANAScoringAlgorithm ocsanaAlgorithm;
    
    public ContextBundleBuilder (CyNetwork network) {
        Objects.requireNonNull(network, "Network cannot be null");
        this.network = network;

        ocsanaAlgorithm = new OCSANAScoringAlgorithm(network);
        setNodeHandler(new NodeHandler(network));
        setIncludeEndpointsInCIs(false);

        setPathFindingAlgorithm(new AllNonSelfIntersectingPathsAlgorithm(network));
        setMHSAlgorithm(new MMCSAlgorithm());
        

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
    public void setSourceNodes (Set<CyNode> sourceNodes) {
        Objects.requireNonNull(sourceNodes, "Source node set cannot be null");

        if (sourceNodes.stream().anyMatch(node -> !network.containsNode(node))) {
            throw new IllegalArgumentException("All source nodes must come from underlying network");
        }

        this.sourceNodes = sourceNodes;
    }

    /**
     * Return the currently-selected source nodes
     **/
    public Set<CyNode> getSourceNodes () {
        return sourceNodes;
    }

    /**
     * Set the target nodes
     * <p>
     * NOTE: this has the side effect of clearing the configured targets
     * to activate
     **/
    public void setTargetNodes (Set<CyNode> targetNodes) {
        Objects.requireNonNull(targetNodes, "Target node set cannot be null");

        if (targetNodes.stream().anyMatch(node -> !network.containsNode(node))) {
            throw new IllegalArgumentException("All target nodes must come from underlying network");
        }
        if (targetNodes.stream().anyMatch(node -> sourceNodes.contains(node))) {
            throw new IllegalArgumentException("All target nodes must come from underlying network");
        }
        this.targetNodes = targetNodes;
        setTargetsToActivate(new HashSet<>());
    }

    /**
     * Return the currently-selected target nodes
     **/
    public Set<CyNode> getTargetNodes () {
        return targetNodes;
    }

    /**
     * Set the target nodes which should be activated by the sign
     * assignment algorithm
     **/
    public void setTargetsToActivate (Set<CyNode> targetsToActivate) {
        Objects.requireNonNull(targetsToActivate, "Set of targets to activate cannot be null");
        if (!targetNodes.containsAll(targetsToActivate)) {
            throw new IllegalArgumentException("Targets to activate must be in target set");
        }

        this.targetsToActivate = targetsToActivate;
    }

    /**
     * Return the targets which should be activated
     **/
    public Set<CyNode> getTargetsToActivate () {
        return targetsToActivate;
    }

    /**
     * Set the off-target nodes
     **/
    public void setOffTargetNodes (Set<CyNode> offTargetNodes) {
        Objects.requireNonNull(offTargetNodes, "Off-target node set cannot be null");

        if (offTargetNodes.stream().anyMatch(node -> !network.containsNode(node))) {
            throw new IllegalArgumentException("All off-target nodes must come from underlying network");
        }

        this.offTargetNodes = offTargetNodes;
    }

    /**
     * Return the currently-selected off-target nodes
     **/
    public Set<CyNode> getOffTargetNodes () {
        return offTargetNodes;
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

    /**
     * Set whether to include endpoints in CIs
     **/
    public void setIncludeEndpointsInCIs (boolean includeEndpointsInCIs) {
        this.includeEndpointsInCIs = includeEndpointsInCIs;
    }

    /**
     * Return whether to include endpoints in CIs
     **/
    public boolean getIncludeEndpointsInCIs () {
        return includeEndpointsInCIs;
    }

    /**
     * Set the path-finding algorithm
     **/
    public void setPathFindingAlgorithm (AbstractPathFindingAlgorithm pathFindingAlgorithm) {
        Objects.requireNonNull(pathFindingAlgorithm, "Path-finding algorithm cannot be null");

        this.pathFindingAlgorithm = pathFindingAlgorithm;
    }

    /**
     * Return the currently-selected path-finding algorithm
     **/
    public AbstractPathFindingAlgorithm getPathFindingAlgorithm () {
        return pathFindingAlgorithm;
    }

    /**
     * Set the MHS-finding algorithm
     **/
    public void setMHSAlgorithm (AbstractMHSAlgorithm mhsAlgorithm) {
        Objects.requireNonNull(mhsAlgorithm, "MHS-finding algorithm cannot be null");

        this.mhsAlgorithm = mhsAlgorithm;
    }

    /**
     * Return the currently-selected MHS-finding algorithm
     **/
    public AbstractMHSAlgorithm getMHSAlgorithm () {
        return mhsAlgorithm;
    }

    /**
     * Return the currently-selected OCSANA scoring algorithm
     **/
    public OCSANAScoringAlgorithm getOCSANAAlgorithm () {
        return ocsanaAlgorithm;
    }
    
    /**
     * Return the context as currently configured
     **/
    public ContextBundle getContextBundle () {
        return new ContextBundle(network, sourceNodes, targetNodes, offTargetNodes, nodeHandler, includeEndpointsInCIs, pathFindingAlgorithm, mhsAlgorithm, ocsanaAlgorithm);
    }
}