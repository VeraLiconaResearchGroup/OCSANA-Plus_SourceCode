/**
 * Implementation of Berge's algorithm for finding minimal hitting sets
 *
 * Copyright Vera-Licona Research Group (C) 2015
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.algorithms.mhs;

// Java imports
import java.util.*;

// Cytoscape imports
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.BoundedInteger;

import org.cytoscape.model.CyNode;

// OCSANA imports

/**
 * Berge's algorithm for finding minimal hitting sets
 **/

public class BergeAlgorithm
    extends AbstractMHSAlgorithm {
    private static final String NAME = "Berge's algorithm";
    private static final String SHORTNAME = "Berge";

    // Tunables for bounded-cardinality search
    @Tunable(description = "Bound CI size",
             gravity = 350,
             tooltip="Unbounded search may take a very long time!")
    public Boolean useMaxCardinality = true;

    @Tunable(description = "Maximum CI size",
             gravity = 351,
             dependsOn = "useMaxCardinality=true")
    public BoundedInteger maxCardinalityBInt = new BoundedInteger(1, 6, 20, false, false);

    public BergeAlgorithm () {
        super();
    }

    // No docstring because the interface has one
    @Override
    public Collection<Set<CyNode>> MHSes (Collection<Set<CyNode>> sets) {
        HypergraphOfSetsOfCyNodes inputHypergraph
            = new HypergraphOfSetsOfCyNodes(sets);

        inputHypergraph.minimize();

        Hypergraph resultHypergraph = transversalHypergraph(inputHypergraph);

        return inputHypergraph.getCyNodeSetsFromHypergraph(resultHypergraph);
    };

    /**
     * Compute MHSes of a given hypergraph.
     *
     * @param H  the hypergraph whose MHSes we should find
     **/
    public Hypergraph transversalHypergraph (Hypergraph H) {
    	Hypergraph transversals = new Hypergraph(H.numVerts(), 1);

        for (BitSet edge: H) {
            updateTransversalsWithEdge(transversals, edge);
        }

        if (useMaxCardinality) {
            for (BitSet transversal: transversals) {
                assert transversal.cardinality() <= maxCardinalityBInt.getValue();
            }
        }

        // Handle cancellation
        return transversals;
    }

    /**
     * Update a set of partial transversals with a new edge.
     *
     * Specifically, add each element of the edge to each transversal,
     * then minimize transversals.
     *
     * @param transversals  the known partial transversals
     * @param edge  the edge to use to update the transversals
     **/
    private void updateTransversalsWithEdge (Hypergraph transversals,
                                             BitSet edge) {
        
            // Extend each transversal with each element of the edge
            Hypergraph newTransversals = new Hypergraph();
            for (BitSet transversal: transversals) {
                // Handle cancellation
                if (isCanceled()) {
                    transversals.clear();
                    return;
                }

                if (transversal.intersects(edge)) {
                    // If the transversal hits this edge, we don't need to extend it
                    newTransversals.add(transversal);
                } else {
                    // Otherwise, we extend it by adding the elements
                    // of the edge, one at a time
                    for (int e = edge.nextSetBit(0); e >= 0; e = edge.nextSetBit(e+1)) {
                        // Handle cancellation
                        if (isCanceled()) {
                            transversals.clear();
                            return;
                        }

                        BitSet newTransversal = (BitSet) transversal.clone();
                        newTransversal.set(e);

                        assert newTransversal.cardinality() == transversal.cardinality() + 1;

                        // Keep the new transversal if the cardinality conditions are satisfied (if applicable)
                        if (!useMaxCardinality || newTransversal.cardinality() <= maxCardinalityBInt.getValue()) {
                            newTransversals.add(newTransversal);
                        }
                    }
                }
            }

            newTransversals = newTransversals.minimization();

            transversals.clear();
            transversals.addAll(newTransversals);
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
            result.append(String.format("max CI size: %d", maxCardinalityBInt.getValue()));
        } else {
            result.append("no max CI size");
        }

        result.append(")");
        return result.toString();
    }
}
