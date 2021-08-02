/**
 * Implementation of the RS algorithm for finding minimal hitting sets
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
 * The 'RS' algorithm for finding minimal hitting sets.
 **/

public class RSAlgorithm
    extends AbstractMHSAlgorithm {
    private static final String NAME = "RS algorithm";
    private static final String SHORTNAME = "RS";

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

    public RSAlgorithm () {
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
    public Hypergraph transversalHypergraph(Hypergraph H) {
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

        // Which edges each vertex is critical for (initially all empty)
        Hypergraph crit = new Hypergraph (H.numEdges(), H.numVerts());

        // Which edges are uncovered (initially full)
        BitSet uncov = new BitSet(H.numEdges());
        uncov.set(0, H.numEdges());

        // Which vertices are known to be violating (initially empty)
        BitSet violatingVertices = new BitSet (H.numVerts());

        // Set up and run the calculation
        RSRecursiveTask calculation = new RSRecursiveTask(H, T, S, crit, uncov, violatingVertices, maxCardinality, results);

        ForkJoinPool pool;
        if (configureThreads) {
            pool = new ForkJoinPool (numThreads.getValue());
        } else {
            pool = new ForkJoinPool ();
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

        return MHSes;
    }

    private class RSRecursiveTask extends SHDRecursiveTask {
        BitSet violatingVertices;

        /**
         * Recursive task for the RS algorithm
         *
         * @param H  {@code Hypergraph} to process
         * @param T  transversal hypergraph of H
         * @param S  candidate hitting set to process
         * @param crit for each vertex v of H, crit[v] records the edges
         * for which v is critical
         * @param uncov  which edges are uncovered (must be nonempty)
         * @param violatingVertices  which vertices are known to be
         * violating for S
         * @param maxCardinality largest size hitting set to consider
         * (0 to find all, must be larger than {@code S.cardinality()}
         * otherwise)
         * @param confirmedMHSes  to store any confirmed MHSes
         **/
        RSRecursiveTask (Hypergraph H,
                         Hypergraph T,
                         BitSet S,
                         Hypergraph crit,
                         BitSet uncov,
                         BitSet violatingVertices,
                         Integer maxCardinality,
                         ConcurrentLinkedQueue<BitSet> confirmedMHSes) {
            this.H = H;
            this.T = T;
            this.S = S;
            this.crit = crit;
            this.uncov = uncov;
            this.violatingVertices = violatingVertices;
            this.maxCardinality = maxCardinality;
            this.confirmedMHSes = confirmedMHSes;

            // Argument checking
            if (H.numEdges() == 0) {
                // Edgeless case is handled in compute()
                return;
            }

            if (uncov.isEmpty()) {
                throw new IllegalArgumentException("uncov cannot be empty.");
            }

            if ((maxCardinality > 0) && (maxCardinality < S.cardinality())) {
                throw new IllegalArgumentException("S must be no larger than than maxCardinality.");
            }

            if (violatingVertices.intersects(S)) {
                throw new IllegalArgumentException("Vertices in S cannot be violating.");
            }
        }

        /**
         * Run the algorithm.
         **/
        @Override
        protected void compute() {
            // Handle empty hypergraph case
            if (H.numEdges() == 0) {
                return;
            }

            // Handle cancellation
            if (isCanceled()) {
                return;
            }

            // Get an uncovered edge
            Integer searchEdgeIndex = uncov.nextSetBit(0);
            BitSet searchEdge = (BitSet) H.get(searchEdgeIndex).clone();

            // Remove known violating vertices
            searchEdge.andNot(violatingVertices);

            // Check remaining vertices for violation and store the
            // results in a new BitSet
            BitSet newViolatingVertices = (BitSet) violatingVertices.clone();
            for (int v = searchEdge.nextSetBit(0); v >= 0; v = searchEdge.nextSetBit(v+1)) {
                if (vertexWouldViolate(v)) {
                    // Remove newfound violators from the search edge
                    newViolatingVertices.set(v);
                    searchEdge.clear(v);
                }
            }

            // Iterate through the vertices in the search edge in reverse order
            for (int v = searchEdge.length(); (v = searchEdge.previousSetBit(v-1)) >= 0; ) {
                if (isCanceled()) {
                    return;
                }

                // Update crit and uncov
                Map<Integer, BitSet> critMark = updateCritAndUncov(v);

                // Check the critical edge condition
                if (anyEdgeCriticalAfter(searchEdgeIndex)) {
                    restoreCritAndUncov(critMark, v);
                    continue;
                }

                // If we made it this far, S+v is valid
                S.set(v);

                // Process the new candidate S
                if ((uncov.isEmpty()) && ((maxCardinality == 0) || (S.cardinality() <= maxCardinality))) {
                    // S is a genuine MHS, so we store it and move on
                    BitSet cloneS = (BitSet) S.clone();
                    confirmedMHSes.add(cloneS);
                } else if ((maxCardinality == 0) || (S.cardinality() < maxCardinality)) {
                    // S is a viable candidate, so we fork a new job to process it
                    if ((getQueuedTaskCount() < 4) && (uncov.cardinality() > 2)) {
                        // Spawn a new task if the queue is getting
                        // low.  We define "low" as "has fewer than
                        // four algorithms waiting", which is entirely
                        // ad-hoc and should be tuned before serious
                        // use.

                        // Make defensive copies of mutable variables
                        BitSet cloneS = (BitSet) S.clone();
                        Hypergraph cloneCrit = new Hypergraph(crit);
                        BitSet cloneUncov = (BitSet) uncov.clone();
                        BitSet cloneViolatingVertices = (BitSet) newViolatingVertices.clone();

                        RSRecursiveTask child = new RSRecursiveTask(H, T, cloneS, cloneCrit, cloneUncov, cloneViolatingVertices, maxCardinality, confirmedMHSes);
                        child.fork();
                    } else {
                        // Do the work in this thread without forking or copying
                        RSRecursiveTask child = new RSRecursiveTask(H, T, S, crit, uncov, newViolatingVertices, maxCardinality, confirmedMHSes);
                        child.invoke();
                    }
                }

                // Restore helper variables and proceed to the next vertex
                S.clear(v);
                restoreCritAndUncov(critMark, v);
            }
        }

        /**
         * Determine whether any vertex in S has its first critical
         * edge after v.
         *
         * @param v  the vertex to search from
         **/
        private Boolean anyEdgeCriticalAfter(Integer v) {
            // Iterate through vertices in S
            for (int i = S.nextSetBit(0); i >= 0; i = S.nextSetBit(i+1)) {
                // Check first critical edge for vertex i
                int iFirstCritEdge = crit.get(i).nextSetBit(0);
                if (iFirstCritEdge < 0) {
                    throw new IllegalArgumentException("Vertex in S has no critical edges.");
                } else if (iFirstCritEdge >= v) {
                    return true;
                }
            }

            return false;
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
