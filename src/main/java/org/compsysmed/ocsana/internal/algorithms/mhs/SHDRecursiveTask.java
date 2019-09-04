/**
 * Abstract class for the SHD algorithms
 *
 * Implementations must provide constructor and {@code compute}
 *
 * Copyright Vera-Licona Research Group (C) 2015
 **/

package org.compsysmed.ocsana.internal.algorithms.mhs;

import java.util.*;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.RecursiveAction;

/**
 * Abstract class for the SHD algorithms
 **/

abstract class SHDRecursiveTask
    extends RecursiveAction {
    Hypergraph H;
    Hypergraph T;
    BitSet S;
    Hypergraph crit;
    BitSet uncov;
    Integer maxCardinality;
    Integer maxCandidates;
    ConcurrentLinkedQueue<BitSet> confirmedMHSes;

    // Must provide compute()

    /**
     * Determine whether an addition would violate any vertices in a candidate HS.
     *
     * Note: does not check whether crit and uncov are accurate for S.
     *
     * @param v  the vertex to consider adding to S
     **/
    protected Boolean vertexWouldViolate(Integer v) {
        // Input validation
        if (S.get(v)) {
            throw new IllegalArgumentException("S already contains v.");
        }

        if (!crit.get(v).isEmpty()) {
            throw new IllegalArgumentException("crit[v] is not empty.");
        }

        // v is violating if any crit[w] is a subset of the edges hit
        // by v. Thus, we first find the vertices hit by v
        // (disregarding those which are uncovered).
        BitSet testEdges = (BitSet) T.get(v).clone();
        testEdges.andNot(uncov);

        // Then we check whether any crit[w] is a subset.
        for (int w = S.nextSetBit(0); w >= 0; w = S.nextSetBit(w+1)) {
            if (crit.get(w).isEmpty()) {
                throw new IllegalArgumentException("Empty crit[" + w + "]!");
            }

            BitSet intersection = (BitSet) crit.get(w).clone();
            intersection.and(testEdges);

            if (intersection.equals(crit.get(w))) {
                return true;
            }
        }

        return false;
    };

    /**
     * Update algorithm helper arrays to reflect S+v.
     *
     * Note: does not check that crit[] and uncov are accurate for S.
     *
     * @param v  the vertex to consider adding to S
     **/
    protected Map<Integer, BitSet> updateCritAndUncov(Integer v) {
        // Input validation
        if (S.get(v)) {
            throw new IllegalArgumentException("S already contains v.");
        }

        if (!crit.get(v).isEmpty()) {
            throw new IllegalArgumentException("crit[v] is not empty.");
        }

        // v is critical for edges it hits which were previously uncovered
        BitSet vHitEdges = T.get(v);
        BitSet vNewCrit = (BitSet) vHitEdges.clone();
        vNewCrit.and(uncov);
        crit.set(v, vNewCrit);

        // Anything hit by v is no longer uncovered
        uncov.andNot(vHitEdges);

        // Hypergraph to record what changed in crit[]
        Map<Integer, BitSet> critMark = new HashMap<>();

        // Remove anything v hits from the other crit[w]s and record
        // it in critMark[w]s
        for (int w = S.nextSetBit(0); w >= 0; w = S.nextSetBit(w+1)) {
            BitSet wCritMark = (BitSet) crit.get(w).clone();
            wCritMark.and(vHitEdges);
            critMark.put(w, wCritMark);

            crit.get(w).andNot(vHitEdges);
        }

        return critMark;
    };

    /**
     * Update algorithm helper arrays to reflect S-v.
     *
     * Note: does not check whether crit and uncov are accurate for S.
     *
     * @param critmark output from {@code updateCritAndUncov} encoding
     * the critical edges to restore
     * @param v  the vertex to consider adding to S
     **/
    protected void restoreCritAndUncov(Map<Integer, BitSet> critMark,
                                       Integer v) {
        // Input validation
        if (S.get(v)) {
            throw new IllegalArgumentException("S still contains v.");
        }

        if (uncov.intersects(crit.get(v))) {
            throw new IllegalArgumentException("v is critical for an uncovered edge.");
        }

        // If v was critical for any edges, they are now uncovered
        uncov.or(crit.get(v));

        // v is no longer critical for any edge
        crit.get(v).clear();

        // Restore all other crit vertices using critMark
        for (int w = S.nextSetBit(0); w >= 0; w = S.nextSetBit(w+1)) {
            if (!critMark.containsKey(w)) {
                throw new IllegalArgumentException("w is not in critMark.");
            }
            crit.get(w).or(critMark.get(w));
        }
    };

    /**
     * Helper RecursiveTask to wait for all computations
     **/
    public static class TaskWaiter extends RecursiveAction {
        @Override
        public void compute () {
            helpQuiesce();
        }
    }
}
