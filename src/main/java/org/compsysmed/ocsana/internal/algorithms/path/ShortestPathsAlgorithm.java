/**
 * Algorithm which finds only shortest paths
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

// Cytoscape imports
import org.cytoscape.work.ContainsTunables;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;

// OCSANA imports


/**
 * Use depth-first search to generate the shortest directed paths
 *
 * @param network  the CyNetwork to compute on
 **/
public class ShortestPathsAlgorithm
    extends AbstractPathFindingAlgorithm {
    private static final String NAME = "Shortest paths";
    private static final String SHORTNAME = "SHORT";

    @ContainsTunables
    public DijkstraPathDecoratorAlgorithm dijkstra;

    public ShortestPathsAlgorithm (CyNetwork network) {
        super(network);

        dijkstra = new DijkstraPathDecoratorAlgorithm(network);
        dijkstra.restrictPathLength = false;
    }

    @Override
    public Collection<List<CyEdge>> paths (Set<CyNode> sources,
                                           Set<CyNode> targets) {
        Collection<List<CyEdge>> shortestPaths = new ArrayList<>();
        for (CyNode source: sources) {
            if (isCanceled()) {
                return null;
            }

            Collection<List<CyEdge>> newPaths = shortestPaths(source, targets);
            if (newPaths != null) {
                shortestPaths.addAll(newPaths);
            }
        }

        return shortestPaths;
    }

    /**
     * Find all shortest paths from one source to each of the target
     * nodes, as long as they satisfy the constraints on the
     * underlying Dijkstra decorator algorithm
     *
     * @param source  the source node
     * @param targets  the target nodes
     * @return a list of all shortest paths from the source to the
     * targets, each given as a List of CyEdges in order from source to
     * target, or null if the operation was canceled
     **/
    private Collection<List<CyEdge>> shortestPaths (CyNode source,
                                                    Set<CyNode> targets) {
        Set<CyNode> sourceSet = Collections.singleton(source);

        // Decorate graph with minimal distances to target
        Map<CyEdge, Integer> edgeMinDistancesFromSource = dijkstra.edgeMinDistancesForwards(sourceSet);

        // Walk backwards from each target
        Collection<List<CyEdge>> shortestPaths = new ArrayList<>();
        for (CyNode target: targets) {
            if (isCanceled()) {
                return null;
            }
            Collection<List<CyEdge>> shortestPathsToTarget = shortestPaths(source, target, edgeMinDistancesFromSource);
            shortestPaths.addAll(shortestPathsToTarget);
        }

        return shortestPaths;
    }

    /**
     * Find all shortest paths from one source to one target node
     * using pre-computed edge decorations.
     *
     * @param source  the source node
     * @param target  the target node
     * @param edgeMinDistancesFromSource map assigning to each
     * relevant CyEdge the minimum number of edges in a path from the
     * source to that edge
     * @return a collection of all the shortest paths from the source
     * to the target, each given as a list of CyEdges in order from
     * source to target
     **/
    private Collection<List<CyEdge>> shortestPaths (CyNode source,
                                                    CyNode target,
                                                    Map<CyEdge, Integer> edgeMinDistancesFromSource) {
        if (source.equals(target)) {
            Collection<List<CyEdge>> result = new ArrayList<>();
            result.add(new ArrayList<>());
            return result;
        }

        // Find all incoming edges with least weight
        Set<CyEdge> closestIncomingEdges = new HashSet<>();
        Integer closestIncomingEdgeWeight = null;
        for (CyEdge incomingEdge: network.getAdjacentEdgeIterable(target, CyEdge.Type.INCOMING)) {
            if (!edgeMinDistancesFromSource.containsKey(incomingEdge)) {
                continue;
            }

            if (isCanceled()) {
                continue;
            }

            if (closestIncomingEdges.isEmpty() ||
                edgeMinDistancesFromSource.get(incomingEdge) < closestIncomingEdgeWeight) {
                closestIncomingEdges.clear();
                closestIncomingEdges.add(incomingEdge);
                closestIncomingEdgeWeight = edgeMinDistancesFromSource.get(incomingEdge);
            } else if (edgeMinDistancesFromSource.get(incomingEdge).equals(closestIncomingEdgeWeight)) {
                closestIncomingEdges.add(incomingEdge);
            }
        }

        Collection<List<CyEdge>> shortestPaths = new ArrayList<>();
        // Recurse to find the upstream paths
        for (CyEdge incomingEdge: closestIncomingEdges) {
            if (isCanceled()) {
                break;
            }

            Collection<List<CyEdge>> upstreamPaths = shortestPaths(source, incomingEdge.getSource(), edgeMinDistancesFromSource);

            for (List<CyEdge> upstreamPath: upstreamPaths) {
                if (isCanceled()) {
                    break;
                }

                List<CyEdge> result = new ArrayList<>(upstreamPath);
                result.add(incomingEdge);
                shortestPaths.add(result);
            }
        }

        return shortestPaths;
    }

    @Override
    public void cancel () {
        super.cancel();
        dijkstra.cancel();
    }

    @Override
    public void uncancel () {
        super.uncancel();
        dijkstra.uncancel();
    }

    @Override
    public String fullName () {
        return NAME;
    }

    @Override
    public String shortName () {
        return SHORTNAME;
    }

    @Override
    public String description () {
        StringBuilder result = new StringBuilder(fullName());

        result.append(" (");

        if (dijkstra.restrictPathLength) {
            result.append(String.format("max path length: %d", dijkstra.maxPathLength));
        } else {
            result.append("no max path length");
        }


        result.append(")");
        return result.toString();
   }
}
