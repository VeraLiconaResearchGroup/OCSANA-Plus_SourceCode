/**
 * Implementation of the MMCS algorithm for finding minimal hitting sets
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

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ForkJoinPool;

// Cytoscape imports
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.BoundedInteger;

import org.cytoscape.model.CyNode;

// OCSANA imports

/**
 * The 'MMCS' algorithm for finding minimal hitting sets
 **/

public class MMCSAlgorithm
    extends AbstractMHSAlgorithm {
    private static final String NAME = "MMCS algorithm";
    private static final String SHORTNAME = "MMCS";

    // Tunables for threading
    @Tunable(description = "Bound thread count",
             gravity = 350,
             tooltip="By default, all CPUs will be utilized")
    public Boolean configureThreads = false;

    @Tunable(description = "Number of threads",
             gravity = 351,
             dependsOn = "configureThreads=true")
    public BoundedInteger numThreads;

    // Tunables for bounded-cardinality search
    @Tunable(description = "Bound CI size",
             gravity = 352,
             tooltip="Unbounded search may take a very long time!")
    public Boolean useMaxCardinality = true;

    @Tunable(description = "Maximum CI size",
             gravity = 353,
             dependsOn = "useMaxCardinality=true")
    public BoundedInteger maxCardinalityBInt;

    public MMCSAlgorithm () {
        super();
        numThreads = new BoundedInteger(1, 1, Runtime.getRuntime().availableProcessors(), false, false);
        maxCardinalityBInt = new BoundedInteger(1, 6, 20, false, false);
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
        // Generate inputs to algorithm
        Hypergraph T = H.transpose();
        ConcurrentLinkedQueue<BitSet> results = new ConcurrentLinkedQueue<>();

        // Handle argument processing
        int maxCardinality;
        if (useMaxCardinality) {
            maxCardinality = maxCardinalityBInt.getValue();
        } else {
            maxCardinality = 0;
        }

        // Candidate hitting set, initially empty
        BitSet S = new BitSet(H.numVerts());

        // Eligible vertices, initially full
        BitSet CAND = new BitSet(H.numVerts());
        CAND.set(0, H.numVerts());

        // Which edges each vertex is critical for (initially all empty)
        Hypergraph crit = new Hypergraph (H.numEdges(), H.numVerts());

        // Which edges are uncovered (initially full)
        BitSet uncov = new BitSet(H.numEdges());
        uncov.set(0, H.numEdges());

        // Set up and run the calculation
        MMCSRecursiveTask calculation = new MMCSRecursiveTask(H, T, S, CAND, crit, uncov, maxCardinality, results);

        ForkJoinPool pool;
        if (configureThreads) {
            pool = new ForkJoinPool(numThreads.getValue());
        } else {
            pool = new ForkJoinPool();
        }

        pool.invoke(calculation);

        // Wait for all algorithms to complete
        pool.invoke(new SHDRecursiveTask.TaskWaiter());

        // Construct a Hypergraph with the resulting MHSes
        Hypergraph MHSes = new Hypergraph(H.numVerts());
        for (BitSet edge: results) {
            if (isCanceled()) {
                break;
            }

            MHSes.add(edge);
        }
        MHSes.updateNumVerts();

        return MHSes;
    }

    private class MMCSRecursiveTask extends SHDRecursiveTask {
        BitSet CAND;

        /**
         * Recursive task for the MMCS algorithm
         *
         * @param H  {@code Hypergraph} to process
         * @param T  transversal hypergraph of H
         * @param S  candidate hitting set to process
         * @param CAND  vertices which are eligible to add to S (must be nonempty)
         * @param crit for each vertex v of H, crit[v] records the edges
         * for which v is critical
         * @param uncov  which edges are uncovered (must be nonempty)
         * @param maxCardinality  the maximum size of MHS to consider
         * @param confirmedMHSes  to store any confirmed MHSes
         **/
        MMCSRecursiveTask (Hypergraph H,
                           Hypergraph T,
                           BitSet S,
                           BitSet CAND,
                           Hypergraph crit,
                           BitSet uncov,
                           Integer maxCardinality,
                           ConcurrentLinkedQueue<BitSet> confirmedMHSes) {
            this.H = H;
            this.T = T;
            this.S = S;
            this.CAND = CAND;
            this.crit = crit;
            this.uncov = uncov;
            this.maxCardinality = maxCardinality;
            this.confirmedMHSes = confirmedMHSes;

            // Argument checking
            if (H.numEdges() == 0) {
                // Edgeless case is handled in compute()
                return;
            }

            if (CAND.isEmpty()) {
                throw new IllegalArgumentException("CAND cannot be empty.");
            }

            if (uncov.isEmpty()) {
                throw new IllegalArgumentException("uncov cannot be empty.");
            }

            if ((maxCardinality > 0) && (maxCardinality < S.cardinality())) {
                throw new IllegalArgumentException("S must be no larger than than maxCardinality.");
            }
        }

        /**
         * Run the algorithm.
         **/
        @Override
        protected void compute () {
            // Handle empty hypergraph case
            if (H.numEdges() == 0) {
                return;
            }

            // Handle cancellation
            if (isCanceled()) {
                return;
            }

            // Prune the vertices to search
            // Per M+U, find the uncovered edge e with the smallest intersection
            // with CAND
            // We name this intersection C for easy reference
            BitSet C = new BitSet(H.numVerts());
            C.set(0, H.numVerts() - 1);
            for (int e = uncov.nextSetBit(0); e >= 0; e = uncov.nextSetBit(e+1)) {
                BitSet searchIntersection = (BitSet) H.get(e).clone();
                searchIntersection.and(CAND);
                if (searchIntersection.cardinality() < C.cardinality()) {
                    C = searchIntersection;
                }
            }

            // Temporarily remove these vertices from CAND
            CAND.andNot(C);

            // Record which vertices of C were violating for S
            BitSet violators = new BitSet(H.numVerts());

            // Iterate through the vertices in the intersection (in reverse order)
            for (int v = C.length(); (v = C.previousSetBit(v-1)) >= 0; ) {
                if (isCanceled()) {
                    return;
                }

                // First, check for violators
                if (vertexWouldViolate(v)) {
                    violators.set(v);
                    continue;
                }

                // Add v to S and update crit and uncov
                Map<Integer, BitSet> critMark = updateCritAndUncov(v);
                S.set(v);

                // Process the new candidate S
                if ((uncov.isEmpty()) && ((maxCardinality == 0) || (S.cardinality() <= maxCardinality))) {
                    // S is a genuine MHS, so we store it and move on
                    BitSet cloneS = (BitSet) S.clone();
                    confirmedMHSes.add(cloneS);
                } else if ((!CAND.isEmpty()) && ((maxCardinality == 0) || (S.cardinality() < maxCardinality))) {
                    // S is a viable candidate, so we fork a new job to process it
                    if ((getQueuedTaskCount() < 4) && (uncov.cardinality() > 2)) {
                        // Spawn a new task if the queue is getting
                        // low.  We define "low" as "has fewer than
                        // four algorithms waiting", which is entirely
                        // ad-hoc and should be tuned before serious
                        // use.

                        // Make defensive copies of mutable variables
                        BitSet newS = (BitSet) S.clone();
                        BitSet newCAND = (BitSet) CAND.clone();
                        Hypergraph newcrit = new Hypergraph(crit);
                        BitSet newuncov = (BitSet) uncov.clone();

                        MMCSRecursiveTask child = new MMCSRecursiveTask(H, T, newS, newCAND, newcrit, newuncov, maxCardinality, confirmedMHSes);
                        child.fork();
                    } else {
                        // Do the work in this thread without forking or copying
                        MMCSRecursiveTask child = new MMCSRecursiveTask(H, T, S, CAND, crit, uncov, maxCardinality, confirmedMHSes);
                        child.invoke();
                    }
                }

                // Finally, we update CAND, crit, uncov and S
                CAND.set(v);
                S.clear(v);
                restoreCritAndUncov(critMark, v);

            }

            // Restore the violators to CAND before any other run uses it
            CAND.or(violators);
        }
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

        if (configureThreads) {
            result.append(String.format("threads: %d", numThreads.getValue()));
        } else {
            result.append("all cores");
        }

        result.append(")");
        return result.toString();
    }
}