/**
 * Algorithm which finds all non-self-intersecting paths
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
import java.util.stream.Collectors;


// Cytoscape imports
import org.cytoscape.work.Tunable;
import org.cytoscape.work.ContainsTunables;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;

// OCSANA imports

/**
 * Use depth-first search to generate all non-self-intersecting
 * directed paths up to a specified length
 *
 * @param network  the CyNetwork to compute on
 **/
public class AllNonSelfIntersectingPathsAlgorithm
    extends AbstractPathFindingAlgorithm {
    private static final String NAME = "All non-self-intersecting paths";
    private static final String SHORTNAME = "ALL";

    @ContainsTunables
    public DijkstraPathDecoratorAlgorithm dijkstra;

    public AllNonSelfIntersectingPathsAlgorithm (CyNetwork network) {
        super(network);
        dijkstra = new DijkstraPathDecoratorAlgorithm(network);
    }

    @Override
    public Collection<List<CyEdge>> paths (Set<CyNode> sources,
                                           Set<CyNode> targets) {
        Map<CyEdge, Integer> edgeMinDistancesFromTargets = dijkstra.edgeMinDistancesBackwards(targets);

        // Only run the next step if the previous succeeded
        if (edgeMinDistancesFromTargets != null) {
            return computeAllPaths(sources, targets, edgeMinDistancesFromTargets);
        } else {
            return null;
        }
    }

    /**
     * Use the pre-computed minimal distances to find all paths from
     * one set of nodes to another
     *
     * @param sources  the source nodes
     * @param targets  the target nodes
     * @param edgeMinDistancesFromTargets  the minimum number of edges in a path
     * to a target beginning with the given edge
     * @return a List of paths, each given as a List of CyEdges in
     * order from a source to a target
     **/
    private Collection<List<CyEdge>> computeAllPaths (Set<CyNode> sources,
                                                      Set<CyNode> targets,
                                                      Map<CyEdge, Integer> edgeMinDistancesFromTargets) {
        // This time, we'll iterate down through the network, starting
        // at the sources and walking forwards along edges. As we
        // walk, we'll extend the incomplete paths we find along any
        // edges whose minimum distance to a target is small enough to
        // keep the total path length no larger than maxPathLength.
        List<List<CyEdge>> completePaths = new ArrayList<>();
        Deque<List<CyEdge>> incompletePaths = new LinkedList<>();

        // Bootstrap the queue with the edges coming out of the sources
        for (CyNode sourceNode: sources) {
            for (CyEdge outEdge: network.getAdjacentEdgeIterable(sourceNode, CyEdge.Type.OUTGOING)) {
                // Handle cancellation
                if (isCanceled()) {
                    return null;
                }
                //Handle loop case.
                if (outEdge.getSource().equals(outEdge.getTarget())) {
                    continue;
                }

                if (!outEdge.isDirected()) {
                    throw new IllegalArgumentException(UNDIRECTED_ERROR_MESSAGE);
                }

                assert outEdge.getSource().equals(sourceNode);

                List<CyEdge> newPath = Arrays.asList(outEdge);
                incompletePaths.add(newPath);
                // If the new edge ends at a target, it's complete
                if (targets.contains(outEdge.getTarget())) {
                    assert (!dijkstra.restrictPathLength || newPath.size() <= dijkstra.maxPathLength);
                    assert sources.contains(newPath.get(0).getSource());
                    //assert targets.contains(newPath.get(newPath.size() - 1).getTarget());
                    completePaths.add(newPath);}
            }
        }

        // Work through the queue of incomplete paths
        for (List<CyEdge> incompletePath; (incompletePath = incompletePaths.poll()) != null;) {
            // Number of edges in path
            Integer pathLength = incompletePath.size();

            // Consider all edges coming out of the leaf of the path
            CyEdge leafEdge = incompletePath.get(pathLength - 1);

            if (!leafEdge.isDirected()) {
                throw new IllegalArgumentException(UNDIRECTED_ERROR_MESSAGE);
            }

            CyNode leafNode = leafEdge.getTarget();

            edgeTestingLoop:
            for (CyEdge outEdge: network.getAdjacentEdgeIterable(leafNode, CyEdge.Type.OUTGOING)) {
                // Handle cancellation
                if (isCanceled()) {
                    return null;
                }

                // Handle undirected edge (error case)
                if (!outEdge.isDirected()) {
                    throw new IllegalArgumentException(UNDIRECTED_ERROR_MESSAGE);
                }

                // Handle loop case (ignore this edge)
                if (outEdge.getSource().equals(outEdge.getTarget())) {
                    break;
                }

                // Only consider the edge if it's marked and its
                // shortest descending path satisfies the length bound
                // (if applicable)
                if ((edgeMinDistancesFromTargets.containsKey(outEdge)) &&
                    ((!dijkstra.restrictPathLength) ||
                     (edgeMinDistancesFromTargets.get(outEdge) + pathLength <= dijkstra.maxPathLength))
                    ) {
                    // Make sure this doesn't create a self-intersecting path
                    Set<CyNode> pathNodes = incompletePath.stream()
                        .map(edge -> Arrays.asList(edge.getSource(), edge.getTarget()))
                        .flatMap(List::stream).collect(Collectors.toSet());

                    assert pathNodes.contains(outEdge.getSource());

                    if (pathNodes.contains(outEdge.getTarget())) {
                        continue edgeTestingLoop;
                    }

                    // Create the new path
                    List<CyEdge> newPath = new ArrayList<>(incompletePath.size() + 1);
                    newPath.addAll(incompletePath);
                    newPath.add(outEdge);

                    // If the new path ends at a target, it's complete
                    if (targets.contains(outEdge.getTarget())) {
                        assert (!dijkstra.restrictPathLength || newPath.size() <= dijkstra.maxPathLength);
                        assert sources.contains(newPath.get(0).getSource());
                        assert targets.contains(newPath.get(newPath.size() - 1).getTarget());
                        completePaths.add(newPath);
                    }

                    // Regardless, we consider further extensions of it
                    incompletePaths.addFirst(newPath);
                }
            }
        }

        assert incompletePaths.isEmpty();

        return completePaths;
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
