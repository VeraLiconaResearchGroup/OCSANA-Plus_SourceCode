/**
 * Class representing a combination of interventions in a signaling network
 *
 * Copyright Vera-Licona Research Group (C) 2016
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.util.results;

// Java imports
import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;

// Cytoscape imports
import org.cytoscape.model.CyNode;

/**
 * Class representing a combination of interventions in a signaling
 * network
 **/
public class CombinationOfInterventions {
    private final Set<CyNode> ciNodes;
    private final Set<CyNode> targetNodes;
    private final Function<CyNode, String> nodeNameFunction;
    private final Function<CyNode, String> nodeIDFunction;

    private final Double ocsanaScore;
    private final Double targetScore;
    private final Double sideEffectScore;
    /**
     * Constructor
     *
     * @param ciNodes  the nodes in this CI
     * @param targetNodes  the target nodes that this CI dominates
     * @param nodeNameFunction  function returning the name of a given
     * node (if null, use Cytoscape's automatic name, which is based
     * on SUID)
     * @param nodeIDFunction  function returning the biomolecule ID of
     * a given node (if null, use Cytoscape's automatic name, which is
     * based on SUID)
     * @param ocsanaScore the OCSANA score of this CI
     **/
    public CombinationOfInterventions (Set<CyNode> ciNodes,
                                       Set<CyNode> targetNodes,
                                       Function<CyNode, String> nodeNameFunction,
                                       Function<CyNode, String> nodeIDFunction,
                                       Double ocsanaScore,
                                       Double targetScore,
                                       Double sideEffectScore) {
    	Objects.requireNonNull(ciNodes, "CI nodes cannot be null");
        this.ciNodes = ciNodes;

        Objects.requireNonNull(targetNodes, "Target nodes collection cannot be null");
        this.targetNodes = targetNodes;

        if (nodeNameFunction == null) {
            this.nodeNameFunction = node -> node.toString();
        } else {
            this.nodeNameFunction = nodeNameFunction;
        }

        if (nodeIDFunction == null) {
            this.nodeIDFunction = node -> node.toString();
        } else {
            this.nodeIDFunction = nodeIDFunction;
        }

        Objects.requireNonNull(ocsanaScore, "OCSANA score cannot be null");
        this.ocsanaScore = ocsanaScore;
       
        Objects.requireNonNull(targetScore, "OCSANA score cannot be null");
        this.targetScore = targetScore;
        
        Objects.requireNonNull(sideEffectScore, "OCSANA score cannot be null");
        this.sideEffectScore = sideEffectScore;
    }
    

    /**
     * Copy constructor
     *
     * @param other  the CombinationOfInterventions to copy
     **/
    public CombinationOfInterventions (CombinationOfInterventions other) {
        ciNodes = new HashSet<>(other.ciNodes);
        targetNodes = new HashSet<>(other.targetNodes);

        nodeNameFunction = other.nodeNameFunction;
        nodeIDFunction = other.nodeIDFunction;

        ocsanaScore = other.ocsanaScore;
        sideEffectScore= other.sideEffectScore;
        targetScore = other.targetScore;
    }

    /**
     * Get the nodes in this CI
     **/
    public Set<CyNode> getNodes () {
        return ciNodes;
    }

    /**
     * Get the targets of this CI
     **/
    public Set<CyNode> getTargets () {
        return targetNodes;
    }

    /**
     * Get the size of this CI
     **/
    public Integer size () {
        return ciNodes.size();
    }

    /**
     * Get the OCSANA score of this CI
     **/
    public Double getOCSANAScore () {
        return ocsanaScore;
    }
    /**
     * Get the target score of this CI
     **/
    public Double getTargetScore () {
        return targetScore;
    }

    /**
     * Get the side effect score of this CI
     **/
    public Double getSideEffectScore () {
        return sideEffectScore;
    }

    /**
     * Get a string representation of the nodes of this CI.
     *
     * @see #nodeSetString
     **/
    public String interventionNodesString () {
        return nodeSetString(getNodes());
    }

    /**
     * Return the name of a node
     **/
    public String nodeName (CyNode node) {
        return nodeNameFunction.apply(node);
    }

    /**
     * Return the biomolecule ID of a node
     **/
    public String nodeID (CyNode node) {
        return nodeIDFunction.apply(node);
    }

    /**
     * Get a string representation of a collection of nodes
     *
     * The current format is "[node1, node2, node3]".
     *
     * @param nodes  the Collection of nodes
     **/
    public String nodeSetString(Collection<CyNode> nodes) {
        return nodes.stream().map(node -> nodeName(node)).collect(Collectors.joining(", ", "[", "]"));
    }

}
