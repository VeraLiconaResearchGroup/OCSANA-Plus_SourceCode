/**
 * Data structure to hold OCSANA node scores
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
import java.util.stream.Collectors;

import org.cytoscape.model.CyEdge;
// Cytoscape imports
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.work.TaskMonitor;
import org.compsysmed.ocsana.internal.util.context.ContextBundle;
// OCSANA imports
import org.compsysmed.ocsana.internal.util.context.ContextBundle;
/**
 * Data structure to hold and work with OCSANA scores
 **/
public class OCSANAScores {
    private final CyNetwork network;

    private final Set<CyNode> activeTargets;
    private final Set<CyNode> activeOffTargets;

    private final Map<CyNode, Map<CyNode, Double>> effectsOnTargets;
    private final Map<CyNode, Set<CyNode>> targetsHitMap;
    private final Map<CyNode, Map<CyNode, Integer>> targetPathCountMap;

    private final Map<CyNode, Map<CyNode, Double>> effectsOnOffTargets;
    private final Map<CyNode, Set<CyNode>> offTargetsHitMap;
    private final Map<CyNode, Map<CyNode, Integer>> offTargetPathCountMap;
    
    public OCSANAScores (CyNetwork network,
                         Map<CyNode, Map<CyNode, Double>> effectsOnTargets,
                         Map<CyNode, Set<CyNode>> targetsHitMap,
                         Map<CyNode, Map<CyNode, Integer>> targetPathCountMap,
                         Map<CyNode, Map<CyNode, Double>> effectsOnOffTargets,
                         Map<CyNode, Set<CyNode>> offTargetsHitMap,
                         Map<CyNode, Map<CyNode, Integer>> offTargetPathCountMap) {
    		Objects.requireNonNull(network, "Network cannot be null");
        this.network = network;
        
        Objects.requireNonNull(effectsOnTargets, "EFFECTS_ON_TARGETS map cannot be null");
        this.effectsOnTargets = effectsOnTargets;

        Objects.requireNonNull(targetsHitMap, "Targets hit map cannot be null");
        this.targetsHitMap = targetsHitMap;

        Objects.requireNonNull(targetPathCountMap, "Target path count map cannot be null");
        this.targetPathCountMap = targetPathCountMap;

        Objects.requireNonNull(effectsOnOffTargets, "SIDE_EFFECTS map cannot be null");
        this.effectsOnOffTargets = effectsOnOffTargets;

        Objects.requireNonNull(offTargetsHitMap, "Off-targets hit map cannot be null");
        this.offTargetsHitMap = offTargetsHitMap;

        Objects.requireNonNull(offTargetPathCountMap, "Off-target path count map cannot be null");
        this.offTargetPathCountMap = offTargetPathCountMap;

        activeTargets = targetsHitMap.values().stream().flatMap(Set::stream).collect(Collectors.toSet());
        activeOffTargets = offTargetsHitMap.values().stream().flatMap(Set::stream).collect(Collectors.toSet());
    }

    /**
     * Return the EFFECT_ON_TARGETS score of a node on a target
     **/
    public Double EFFECT_ON_TARGETS (CyNode elementaryNode,
                                     CyNode target) {
        Objects.requireNonNull(elementaryNode, "Elementary node cannot be null");
        Objects.requireNonNull(target, "Target node cannot be null");
        return effectsOnTargets.getOrDefault(elementaryNode, new HashMap<>()).getOrDefault(target, 0d);
    }

    /**
     * Return the EFFECT_ON_TARGETS score of a node on a set of targets
     **/
    public Double EFFECT_ON_TARGETS (CyNode elementaryNode,
                                     Set<CyNode> targets) {
        Objects.requireNonNull(elementaryNode, "Elementary node cannot be null");
        Objects.requireNonNull(targets, "Set of target nodes cannot be null");
        return targets.stream().mapToDouble(target -> EFFECT_ON_TARGETS(elementaryNode, target)).sum();
    }

    /**
     * Return the EFFECT_ON_TARGETS score of a node on the entire set of targets
     **/
    public Double EFFECT_ON_TARGETS (CyNode elementaryNode) {
        Objects.requireNonNull(elementaryNode, "Elementary node cannot be null");
        return EFFECT_ON_TARGETS(elementaryNode, activeTargets);
    }

    /**
     * Return the EFFECT_ON_TARGETS score of a set of elementary nodes
     * on the entire set of targets
     **/
    public Double EFFECT_ON_TARGETS (Set<CyNode> elementaryNodes) {
        Objects.requireNonNull(elementaryNodes, "Set of elementary nodes cannot be null");
        return elementaryNodes.stream().mapToDouble(this::EFFECT_ON_TARGETS).sum();
    }

    /**
     * Return the SIDE_EFFECT score of a node on an off-target
     **/
    public Double SIDE_EFFECTS (CyNode elementaryNode,
                                CyNode offTarget) {
        Objects.requireNonNull(elementaryNode, "Elementary node cannot be null");
        Objects.requireNonNull(offTarget, "Off-target node cannot be null");
        return effectsOnOffTargets.getOrDefault(elementaryNode, new HashMap<>()).getOrDefault(offTarget, 0d);
    }

    /**
     * Return the SIDE_EFFECTS score of a node on a set of off-targets
     **/
    public Double SIDE_EFFECTS (CyNode elementaryNode,
                                Set<CyNode> offTargets) {
        Objects.requireNonNull(elementaryNode, "Elementary node cannot be null");
        Objects.requireNonNull(offTargets, "Set of off-target nodes cannot be null");
        return offTargets.stream().mapToDouble(offTarget -> SIDE_EFFECTS(elementaryNode, offTarget)).sum();
    }

    /**
     * Return the SIDE_EFFECTS score of a node on the entire set of off-targets
     **/
    public Double SIDE_EFFECTS (CyNode elementaryNode) {
        Objects.requireNonNull(elementaryNode, "Elementary node cannot be null");
        return SIDE_EFFECTS(elementaryNode, activeOffTargets);
    }

    /**
     * Return the SIDE_EFFECTS score of a set of elementary nodes on
     * the entire set of off-targets
     **/
    public Double SIDE_EFFECTS (Set<CyNode> elementaryNodes) {
        Objects.requireNonNull(elementaryNodes, "Set of elementary nodes cannot be null");
        return elementaryNodes.stream().mapToDouble(this::SIDE_EFFECTS).sum();
    }

    /**
     * Return the OVERALL score of a node on set of targets and a set of off-targets
     **/
    public Double OVERALL (CyNode elementaryNode,
                           Set<CyNode> targets,
                           Set<CyNode> offTargets) {
        Objects.requireNonNull(elementaryNode, "Elementary node cannot be null");
        Objects.requireNonNull(targets, "Set of target nodes cannot be null");
        Objects.requireNonNull(offTargets, "Set of off-target nodes cannot be null");

        Double effectScore = 0d;
        if (!targets.isEmpty()) {
            Set<CyNode> targetsHit = new HashSet<>(targetsHitMap.getOrDefault(elementaryNode, Collections.emptySet()));
            targetsHit.retainAll(targets);
            effectScore = (Double.valueOf(targetsHit.size()) / targets.size()) * Math.abs(EFFECT_ON_TARGETS(elementaryNode, targets));
        }

        Double sideEffectScore = 0d;
        if (!offTargets.isEmpty()) {
            Set<CyNode> offTargetsHit = new HashSet<>(offTargetsHitMap.getOrDefault(elementaryNode, Collections.emptySet()));
            offTargetsHit.retainAll(offTargets);
            sideEffectScore = (Double.valueOf(offTargetsHit.size()) / offTargets.size()) * Math.abs(SIDE_EFFECTS(elementaryNode, offTargets));
        }

        if (effectScore < sideEffectScore) {
            return 0d;
        } else {
            return effectScore - sideEffectScore;
        }
    }

    /**
     * Return the OVERALL score of a node on the entire set of targets and off-targets
     **/
    public Double OVERALL (CyNode elementaryNode) {
        Objects.requireNonNull(elementaryNode, "Elementary node cannot be null");
        return OVERALL(elementaryNode, activeTargets, activeOffTargets);
    }

    /**
     * Return the OCSANA score of a node on a set of targets and a set of off-targets
     **/
    public Double OCSANA (CyNode elementaryNode,
                          Set<CyNode> targets,
                          Set<CyNode> offTargets) {
        Objects.requireNonNull(elementaryNode, "Elementary node cannot be null");
        Objects.requireNonNull(targets, "Set of target nodes cannot be null");
        Objects.requireNonNull(offTargets, "Set of off-target nodes cannot be null");

        int setScore = targetPathCountMap.get(elementaryNode).entrySet().stream().filter(entry -> targets.contains(entry.getKey())).mapToInt(entry -> entry.getValue()).sum();
        
		return OVERALL(elementaryNode, targets, offTargets) * setScore;
    }

    /**
     * Return the OCSANA score of a set of nodes on a set of targets and a set of off-targets
     **/
    public Double OCSANA (Set<CyNode> elementaryNodes,
                          Set<CyNode> targets,
                          Set<CyNode> offTargets) {
        Objects.requireNonNull(elementaryNodes, "Set of elementary nodes cannot be null");
        Objects.requireNonNull(targets, "Set of target nodes cannot be null");
        Objects.requireNonNull(offTargets, "Set of off-target nodes cannot be null");
        

return elementaryNodes.stream().mapToDouble(node -> OCSANA(node, targets, offTargets)).sum();
    }

    /**
     * Return the OCSANA score of a node on the entire set of targets and off-targets
     **/
    public Double OCSANA (CyNode elementaryNode) {
        Objects.requireNonNull(elementaryNode, "Elementary node cannot be null");

        return OCSANA(elementaryNode, activeTargets, activeOffTargets);
    }

    /**
     * Return the OCSANA score of a set of nodes on the entire set of targets and off-targets
     **/
    public Double OCSANA (Set<CyNode> elementaryNodes) {
        Objects.requireNonNull(elementaryNodes, "Set of elementary nodes cannot be null");

        return OCSANA(elementaryNodes, activeTargets, activeOffTargets);
    }

    /**
     * Return the underlying network
     **/
    public CyNetwork getNetwork () {
        return network;
    }
}
