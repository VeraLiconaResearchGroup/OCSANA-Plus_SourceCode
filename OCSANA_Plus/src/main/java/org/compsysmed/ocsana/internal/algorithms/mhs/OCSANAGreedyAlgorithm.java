/**
 * Implementation of the OCSANA "greedy" algorithm for finding minimal
 * hitting sets
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
import java.util.stream.Stream;

// Cytoscape imports
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.BoundedDouble;
import org.cytoscape.work.util.BoundedInteger;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;

// OCSANA imports
import org.compsysmed.ocsana.internal.algorithms.scoring.OCSANAScoringAlgorithm;

import org.compsysmed.ocsana.internal.util.results.OCSANAScores;

/**
 * "Greedy" algorithm for finding minimal hitting sets with a
 * weighting heuristic
 **/
public class OCSANAGreedyAlgorithm
    extends AbstractMHSAlgorithm
    implements OCSANAScoringAlgorithm.OCSANAScoresListener {
    private static final String NAME = "Greedy heuristic algorithm";
    private static final String SHORTNAME = "GREEDY";

    //Tunables
    @Tunable(description = "Bound CI size",
             gravity = 350,
             tooltip="Unbounded search may take a very long time!")
             public Boolean useMaxCardinality = true;

    @Tunable(description = "Maximum CI size",
             gravity = 351,
             dependsOn = "useMaxCardinality=true")
             public BoundedInteger maxCardinalityBInt = new BoundedInteger(1, 6, 20, false, false);

    @Tunable(description = "Bound number of candidates",
             gravity = 360,
             tooltip="Unbounded search may take a very long time!")
             public Boolean useMaxCandidates = true;

    @Tunable(description = "Maximum number of candidates (millions)",
             gravity = 361,
             dependsOn = "useMaxCandidates=true")
             public Double maxMegaCandidates = 5d;

    // Internal data
    private CyNetwork network;
    private OCSANAScores ocsanaScores;

    public OCSANAGreedyAlgorithm (CyNetwork network) {
        Objects.requireNonNull(network, "Network cannot be null");
        this.network = network;
    }

    @Override
    public void receiveScores (OCSANAScores ocsanaScores) {
        Objects.requireNonNull(ocsanaScores, "OCSANA scores cannot be null");

        if (!network.equals(ocsanaScores.getNetwork())) {
            throw new IllegalArgumentException("OCSANA scores must match declared network");
        }

        this.ocsanaScores = ocsanaScores;
    }

    @Override
    public Collection<Set<CyNode>> MHSes (Collection<Set<CyNode>> sets) {
        Objects.requireNonNull(sets, "Collection of sets cannot be null");
        Objects.requireNonNull(ocsanaScores, "OCSANA scores must be set before running this algorithm");

        HypergraphOfSetsOfScoredCyNodes H = new HypergraphOfSetsOfScoredCyNodes(sets, ocsanaScores);

        Hypergraph T = transversalHypergraph(H);
        return H.getCyNodeSetsFromHypergraph(T);
    }

    /**
     * Compute the transversals of a given hypergraph.
     **/
    public Hypergraph transversalHypergraph (HypergraphOfSetsOfScoredCyNodes H) {
        Hypergraph largeEdges = new Hypergraph();
        Set<Integer> largeEdgeIndexSet = new HashSet<>();
        BitSet singletons = new BitSet();

        for (BitSet edge: H) {
            if (edge.cardinality() == 1) {
                singletons.or(edge);
            } else if (edge.cardinality() > 1) {
                largeEdges.add(edge);
                for (int index = edge.nextSetBit(0); index >= 0; index = edge.nextSetBit(index + 1)) {
                    largeEdgeIndexSet.add(index);
                }
            }
        }

        List<Integer> largeEdgeIndices = new ArrayList<>(largeEdgeIndexSet);
        largeEdgeIndices.sort((Integer left, Integer right) -> -1 * Double.compare(H.score(left), H.score(right)));

        // Short-circuit if there are no large sets
        if (largeEdges.isEmpty()) {
            Hypergraph T = new Hypergraph();
            T.add(singletons);
            return T;
        }

        // Search for hitting sets
        Hypergraph T = new Hypergraph();
        Hypergraph candidates = new Hypergraph();
        for (Integer index: largeEdgeIndices) {
            BitSet candidate = new BitSet();
            candidate.set(index);
            candidates.add(candidate);
        }

        Integer candidatesChecked = 0;
        Integer currentCardinality = 1;

        while (!candidates.isEmpty() && !haltForCandidates(candidatesChecked) && !haltForCardinality(currentCardinality, singletons.cardinality())) {
            if (isCanceled()) {
                return new Hypergraph();
            }

            // Sort candidates in descending OCSANA score order
            candidates.sort((BitSet left, BitSet right) -> -1 * Double.compare(H.score(left), H.score(right)));

            // Check whether any candidate is a hitting set
            // Minimality is guaranteed from the extension procedure below
            Iterator<BitSet> candidateIterator = candidates.iterator();
            while (candidateIterator.hasNext() && !haltForCandidates(candidatesChecked)) {
                if (isCanceled()) {
                    return new Hypergraph();
                }

                candidatesChecked += 1;

                BitSet candidate = candidateIterator.next();
                assert candidate.cardinality() == currentCardinality;

                if (largeEdges.isTransversedBy(candidate)) {
                    candidateIterator.remove();
                    T.add(candidate);
                }
            }

            // Build new candidates
            currentCardinality += 1;

            if (!haltForCandidates(candidatesChecked) && !haltForCardinality(currentCardinality, singletons.cardinality())) {
                Set<BitSet> newCandidates = new HashSet<>();

                for (BitSet oldCandidate: candidates) {
                    if (isCanceled()) {
                        return new Hypergraph();
                    }

                    if (haltForCandidates(candidatesChecked + newCandidates.size())) {
                        break;
                    }

                    Set<Integer> extensionIndices = largeEdges.stream()
                        .filter(edge -> !edge.intersects(oldCandidate)) // Find edges we haven't hit
                        .map(edge -> edge.stream().boxed()).flatMap(s -> s) // Get their indices
                        .collect(Collectors.toSet()); // Form a set

                    // Build the new candidates
                    for (Integer index: extensionIndices) {
                        // For each extension index, build a new bitset by adding that index to the old candidate
                        BitSet newCandidate = (BitSet) oldCandidate.clone();
                        newCandidate.set(index);
                        assert newCandidate.cardinality() == currentCardinality;

                        // Test minimality
                        if (!T.stream().anyMatch(mhs -> mhs.intersects(newCandidate))) {
                            newCandidates.add(newCandidate);
                        }
                    }
                }

                candidates = new Hypergraph();
                for (BitSet candidate: newCandidates) {
                    candidates.add(candidate);
                }
            }
        }

        // Combine MHSes of large sets with singleton sets and return
        T.stream().forEachOrdered(edge -> edge.or(singletons));
        return T;
    }

    /**
     * Return true if the computation should be stopped due to the
     * number of candidates and false if it should not.
     **/
    private Boolean haltForCandidates (Integer candidatesChecked) {
        return !(!useMaxCandidates || candidatesChecked < maxMegaCandidates * 1e6);
    }

    /**
     * Return true if the computation should be stopped due to the
     * cardinality of the candidates and false if it should not.
     **/
    private Boolean haltForCardinality (Integer candidateCardinality,
                                        Integer singletonNodesSize) {
        return !(!useMaxCardinality || candidateCardinality + singletonNodesSize <= maxCardinalityBInt.getValue());
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

        if (useMaxCardinality) {
            result.append(String.format("max CI size: %d; ", maxCardinalityBInt.getValue()));
        } else {
            result.append("no max CI size; ");
        }

        if (useMaxCandidates) {
            result.append(String.format("maximum candidates: %f million", maxMegaCandidates));
        } else {
            result.append("no max candidate count");
        }

        result.append(")");
        return result.toString();
    }
}
