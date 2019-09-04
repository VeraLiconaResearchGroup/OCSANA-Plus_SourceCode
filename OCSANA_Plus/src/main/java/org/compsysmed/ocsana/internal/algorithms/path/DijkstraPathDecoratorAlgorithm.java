/**
 * Base class for path-finding algorithms using Dikjstra annotation
 *
 * Copyright Vera-Licona Research Group (C) 2015
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.algorithms.path;

// Java imports
import java.util.*;
import java.util.function.Function;

// Cytoscape imports
import org.cytoscape.work.Tunable;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;

// OCSANA imports
import org.compsysmed.ocsana.internal.algorithms.AbstractOCSANAAlgorithm;

public class DijkstraPathDecoratorAlgorithm
    extends AbstractOCSANAAlgorithm {
    @Tunable(description = "Bound path length",
             gravity=210)
    public Boolean restrictPathLength = true;

    // TODO: require non-negative
    @Tunable(description = "Maximum path length",
             tooltip = "Maximum number of edges to allow in a path",
             gravity = 211,
             dependsOn = "restrictPathLength=true")
    public Integer maxPathLength = 20;

    private final CyNetwork network;

    public DijkstraPathDecoratorAlgorithm (CyNetwork network) {
        this.network = network;
    }

    /**
     * Compute the minimum length of a path from the sources to each edge
     *
     * @param sources  the source nodes
     * @return a Map which assigns to some CyEdges a non-negative
     * integer representing the minimum number of edges in a path
     * starting from the sources and leading to the given edge, as long
     * as that number is not greater than maxPathLength
     **/
    protected Map<CyEdge, Integer> edgeMinDistancesForwards (Set<CyNode> sources) {
        return edgeMinDistances(sources, CyEdge.Type.OUTGOING);
    }

    /**
     * Compute the minimum length of a path to the targets from each edge
     *
     * @param targets  the target nodes
     * @return a Map which assigns to some CyEdges a non-negative
     * integer representing the minimum number of edges in a path
     * passing through that edge and reaching some target, so long as
     * that number is not greater than maxPathLength
     **/
    protected Map<CyEdge, Integer> edgeMinDistancesBackwards (Set<CyNode> targets) {
        return edgeMinDistances(targets, CyEdge.Type.INCOMING);
    }

    /**
     * Decorate edges with distances to/from nodes
     *
     * @param nodes  the target nodes
     * @param edgeType  the type of edge to follow (INCOMING for
     * backward iteration, OUTGOING for forward)
     *
     * @return a Map which assigns to some CyEdges a non-negative
     * integer representing the minimum number of edges in a path
     * to/from the nodes and including that edge, so long as that
     * number is not greater than maxPathLength
     **/
    private Map<CyEdge, Integer> edgeMinDistances (Set<CyNode> nodes,
                                                   CyEdge.Type edgeType) {
        // We'll walk through the network, starting at the nodes and
        // walking in the specified direction along edges. Each time
        // we find an edge which has not been marked with a distance,
        // we mark it with one more than the distance which got us
        // there. Each time we find an edge which *has* been marked
        // with a distance, we overwrite it if appropriate.
        Map<CyEdge, Integer> edgeMinDistances = new HashMap<>();
        Map<CyNode, Integer> nodeMinDistances = new HashMap<>();
        Queue<CyNode> nodesToProcess = new LinkedList<>();

        // Helper function to retrieve the other end of an edge in the specified direction
        Function<CyEdge, CyNode> edgeEndGetter = (edgeType == CyEdge.Type.OUTGOING) ?
            (edge -> edge.getTarget()) : (edge -> edge.getSource());

        // Bootstrap with the nodes
        for (CyNode node: nodes) {
            nodeMinDistances.put(node, 0);
            nodesToProcess.add(node);
        }

        // Work through the node queue
        for (CyNode nodeToProcess; (nodeToProcess = nodesToProcess.poll()) != null;) {
            // Handle cancellation
            if (isCanceled()) {
                return null;
            }

            assert nodeMinDistances.containsKey(nodeToProcess);
            // Look at all the edges connected to this node
            for (CyEdge edge: network.getAdjacentEdgeIterable(nodeToProcess, edgeType)) {
                if (isCanceled()) {
                    break;
                }

                if (!edge.isDirected()) {
                    throw new IllegalArgumentException("Undirected edges are not supported.");
                }

                CyNode nextNode = edgeEndGetter.apply(edge);

                // Mark this edge if needed
                Integer newEdgeDist = nodeMinDistances.get(nodeToProcess) + 1;
                if ((!edgeMinDistances.containsKey(edge))
                    || (edgeMinDistances.get(edge) > newEdgeDist)) {
                    edgeMinDistances.put(edge, newEdgeDist);
                }

                // Mark the other node if needed
                if ((!nodeMinDistances.containsKey(nextNode))
                    || (nodeMinDistances.get(nextNode) > newEdgeDist)) {
                    nodeMinDistances.put(nextNode, newEdgeDist);

                    if (!restrictPathLength || (newEdgeDist < maxPathLength)) {
                        nodesToProcess.add(nextNode);
                    }
                }
            }
        }

        assert nodesToProcess.isEmpty();
        return edgeMinDistances;
    }

    @Override
    public String fullName () {
        return "Generalized Dijkstra's path-decorating algorithm";
    }

    @Override
    public String shortName () {
        return "DIJKSTRA";
    }

    @Override public String toString() {
        return fullName();
    }

    public String description () {
        StringBuilder result = new StringBuilder(fullName());

        result.append(" (");

        if (restrictPathLength) {
            result.append(String.format("max path length: %d", maxPathLength));
        } else {
            result.append("no max path length");
        }


        result.append(")");
        return result.toString();
    }
}
