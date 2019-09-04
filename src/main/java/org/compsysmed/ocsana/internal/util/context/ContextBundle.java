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

package org.compsysmed.ocsana.internal.util.context;

// Java imports
import java.util.*;
import java.util.stream.Collectors;

import org.cytoscape.model.CyColumn;
// Cytoscape imports
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
// OCSANA imports
import org.compsysmed.ocsana.internal.util.tunables.NodeHandler;

import org.compsysmed.ocsana.internal.algorithms.AbstractOCSANAAlgorithm;
import org.compsysmed.ocsana.internal.algorithms.path.AbstractPathFindingAlgorithm;
import org.compsysmed.ocsana.internal.algorithms.mhs.AbstractMHSAlgorithm;
import org.compsysmed.ocsana.internal.algorithms.fc.FC;
import org.compsysmed.ocsana.internal.algorithms.scoring.OCSANAScoringAlgorithm;

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
public final class ContextBundle {
    private final CyNetwork network;

    private final Set<CyNode> sourceNodes;
    private final Set<CyNode> targetNodes;
    private final Set<CyNode> offTargetNodes;

    private final NodeHandler nodeHandler;
    private final boolean includeEndpointsInCIs;

    private final AbstractPathFindingAlgorithm pathFindingAlgorithm;
    private final AbstractMHSAlgorithm mhsAlgorithm;
    private final OCSANAScoringAlgorithm ocsanaAlgorithm;
   
    private final Collection<AbstractOCSANAAlgorithm> allAlgorithms;

    public ContextBundle (CyNetwork network,
                          Set<CyNode> sourceNodes,
                          Set<CyNode> targetNodes,
                          Set<CyNode> offTargetNodes,
                          NodeHandler nodeHandler,
                          boolean includeEndpointsInCIs,
                          AbstractPathFindingAlgorithm pathFindingAlgorithm,
                          AbstractMHSAlgorithm mhsAlgorithm,
                          OCSANAScoringAlgorithm ocsanaAlgorithm) {
        // Assignments
        Objects.requireNonNull(network, "Network cannot be null");
        this.network = network;

        Objects.requireNonNull(sourceNodes, "Source node set cannot be null");
        this.sourceNodes = sourceNodes;

        Objects.requireNonNull(targetNodes, "Target node set cannot be null");
        this.targetNodes = targetNodes;

        Objects.requireNonNull(offTargetNodes, "Off-target node set cannot be null");
        this.offTargetNodes = offTargetNodes;

        Objects.requireNonNull(nodeHandler, "Node name handler cannot be null");
        this.nodeHandler = nodeHandler;

        
        this.includeEndpointsInCIs = includeEndpointsInCIs;

        Objects.requireNonNull(pathFindingAlgorithm, "Path-finding algorithm cannot be null");
        this.pathFindingAlgorithm = pathFindingAlgorithm;

        Objects.requireNonNull(mhsAlgorithm, "MHS algorithm cannot be null");
        this.mhsAlgorithm = mhsAlgorithm;

        Objects.requireNonNull(ocsanaAlgorithm, "OCSANA scoring algorithm cannot be null");
        this.ocsanaAlgorithm = ocsanaAlgorithm;


        allAlgorithms = Arrays.asList(pathFindingAlgorithm, mhsAlgorithm, ocsanaAlgorithm);

        // Sanity checks
        if (sourceNodes.stream().anyMatch(node -> !network.containsNode(node))) {
            throw new IllegalArgumentException("All source nodes must come from underlying network");
        }

        if (targetNodes.stream().anyMatch(node -> !network.containsNode(node))) {
            throw new IllegalArgumentException("All target nodes must come from underlying network");
        }

        if (offTargetNodes.stream().anyMatch(node -> !network.containsNode(node))) {
            throw new IllegalArgumentException("All off-target nodes must come from underlying network");
        }

        if (!Collections.disjoint(sourceNodes, targetNodes)) {
            throw new IllegalArgumentException("Source and target nodes must be disjoint");
        }

        if (!Collections.disjoint(sourceNodes, offTargetNodes)) {
            throw new IllegalArgumentException("Source and off-target nodes must be disjoint");
        }

        if (!Collections.disjoint(targetNodes, offTargetNodes)) {
            throw new IllegalArgumentException("Target and off-target nodes must be disjoint");
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
    public Set<CyNode> getSourceNodes () {
        return sourceNodes;
    }

    /**
     * Return the target nodes
     **/
    public Set<CyNode> getTargetNodes () {
        return targetNodes;
    }

    /**
     * Return the off-target nodes
     **/
    public Set<CyNode> getOffTargetNodes () {
        return offTargetNodes;
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

  

    /**
     * Return whether to include endpoints in CIs
     **/
    public boolean getIncludeEndpointsInCIs () {
        return includeEndpointsInCIs;
    }

    /**
     * Return the path-finding algorithm
     **/
    public AbstractPathFindingAlgorithm getPathFindingAlgorithm () {
        return pathFindingAlgorithm;
    }

    /**
     * Return the MHS algorithm
     **/
    public AbstractMHSAlgorithm getMHSAlgorithm () {
        return mhsAlgorithm;
    }

    /**
     * Return the OCSANA scoring algorithm
     **/
    public OCSANAScoringAlgorithm getOCSANAAlgorithm () {
        return ocsanaAlgorithm;
    }
    

    /**
     * Get a string representation of a path of (directed) edges
     * <p>
     * The current format is "node1 -> node2 -| node3".
     *
     * @param path  the path
     **/
    public String pathString(List<CyEdge> path) {
    	Objects.requireNonNull(path, "Cannot convert a null path to a string");

        StringBuilder result = new StringBuilder();

        // Handle first node
        try {
            CyNode firstNode = path.iterator().next().getSource();
            result.append(getNodeName(firstNode));
        } catch (NoSuchElementException e) {
            return result.toString();
        }

        // Each other node is a target
        CyTable edgeTable=network.getDefaultEdgeTable();
        for (CyEdge edge: path) {
        	CyRow edgeRow=edgeTable.getRow(edge.getSUID());
			CyColumn signColumn = edgeTable.getColumn("interaction");
			String interaction=edgeRow.get(signColumn.getName(),signColumn.getType()).toString();
            if (interaction.equals("inhibits")) {
                result.append(" -| ");
            } else {
                result.append(" -> ");
            }
            result.append(getNodeName(edge.getTarget()));
        }

        return result.toString();
    }
    public String FCString(String FC) {
    	Objects.requireNonNull(FC, "Cannot convert a null FC to a string");

        StringBuilder result = new StringBuilder();
        result.append(FC);
        return result.toString();
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

    /**
     * Cancel all underlying algorithms
     **/
    public void cancelAll () {
        for (AbstractOCSANAAlgorithm algorithm: allAlgorithms) {
            algorithm.cancel();
        }
    }

    /**
     * Uncancel all underlying algorithms
     **/
    public void uncancelAll () {
        for (AbstractOCSANAAlgorithm algorithm: allAlgorithms) {
            algorithm.uncancel();
        }
    }
}