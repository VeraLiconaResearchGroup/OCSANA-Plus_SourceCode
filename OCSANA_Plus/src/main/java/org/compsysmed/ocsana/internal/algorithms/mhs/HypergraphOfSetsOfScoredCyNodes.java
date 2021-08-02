/**
 * Helper class to handle converting Collection<Set<CyNode>> to
 * Hypergraph and back while respecting OCSANA scores
 *
 * Copyright Vera-Licona Research Group (C) 2016
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.algorithms.mhs;

// Java imports
import java.util.*;
import java.util.stream.Collectors;

// Cytoscape imports
import org.cytoscape.model.CyNode;

// OCSANA imports
import org.compsysmed.ocsana.internal.util.results.OCSANAScores;

public class HypergraphOfSetsOfScoredCyNodes
    extends Hypergraph {
    private final List<CyNode> sortedNodes; // Sorted in *descending* order
    private final Map<CyNode, Integer> nodeIndex;
    private final Map<CyNode, Double> nodeScores;

    /**
     * Constructor.
     *
     * @param sets  the sets of nodes to store in this Hypergraph
     * @param scores  the OCSANA scores of the nodes
     **/
    public HypergraphOfSetsOfScoredCyNodes (Collection<Set<CyNode>> sets,
                                            OCSANAScores scores) {
        Objects.requireNonNull(sets, "Collection of sets cannot be null");
        Objects.requireNonNull(scores, "OCSANA scores cannot be null");

        // Build node and score data
        Set<CyNode> nodes = new HashSet<>();
        nodeScores = new HashMap<>();

        for (Set<CyNode> set: sets) {
            for (CyNode node: set) {
                if (nodes.add(node)) {
                    nodeScores.put(node, scores.OCSANA(node));
                }
            }
        }

        sortedNodes = new ArrayList<>(nodes);
        sortNodes();

        nodeIndex = new HashMap<>();
        for (int index = 0; index < sortedNodes.size(); index++) {
            CyNode node = sortedNodes.get(index);
            nodeIndex.put(node, index);
        }

        for (Set<CyNode> set: sets) {
            BitSet newEdge = new BitSet();
            for (CyNode node: set) {
                newEdge.set(getIndex(node));
            }

            add(newEdge);
        }

        minimize();
    }

    /**
     * Return the score of a node.
     **/
    public Double score (CyNode node) {
        Objects.requireNonNull(node, "Node cannot be null");
        return nodeScores.get(node);
    }

    /**
     * Return the score of a node by its index.
     **/
    public Double score (Integer index) {
        Objects.requireNonNull(index, "Index cannot be null");
        return score(sortedNodes.get(index));
    }

    /**
     * Return the score of a set of nodes encoded as a BitSet.
     **/
    public Double score (BitSet edge) {
        Objects.requireNonNull(edge, "Edge cannot be null");
        return getCyNodesFromBitSet(edge).stream().mapToDouble(this::score).sum();
    }

    /**
     * Sort the internal node store in descending score order.
     * <p>
     * NOTE: this can only be called before edges are added.
     **/
    private void sortNodes () {
        if (!isEmpty()) {
            throw new IllegalStateException("Cannot reorder nodes once edges are added");
        }

        sortedNodes.sort((CyNode left, CyNode right) -> -1 * Double.compare(score(left), score(right))); // Descending order of OCSANA score
    }

    /**
     * Get the node with a given index.
     **/
    private CyNode getNode (Integer index) {
        Objects.requireNonNull(index, "Index cannot be null");
        return sortedNodes.get(index);
    }

    /**
     * Get the index of a given node.
     **/
    private Integer getIndex (CyNode node) {
        Objects.requireNonNull(node, "Node cannot be null");
        return nodeIndex.get(node);
    }

    /**
     * Convert a BitSet back into a Set of CyNodes.
     **/
    public Set<CyNode> getCyNodesFromBitSet (BitSet edge) {
        Objects.requireNonNull(edge, "Edge cannot be null");
        return edge.stream().boxed()
            .map(this::getNode)
            .collect(Collectors.toSet());
    }

    /**
     * Convert a Hypergraph back into a Set of CyNodes.
     **/
    public Collection<Set<CyNode>> getCyNodeSetsFromHypergraph (Hypergraph H) {
        Objects.requireNonNull(H, "Hypergraph cannot be null");
        return H.stream().map(this::getCyNodesFromBitSet).collect(Collectors.toList());
    }
}
