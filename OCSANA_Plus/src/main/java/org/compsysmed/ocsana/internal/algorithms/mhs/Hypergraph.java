/**
 * Implementation of a hypergraph as an array of BitSet
 *
 * Copyright Vera-Licona Research Group (C) 2015
 **/

package org.compsysmed.ocsana.internal.algorithms.mhs;

// Java imports
import java.util.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Implementation of hypergraphs/set families
 **/

public class Hypergraph
    extends ArrayList<BitSet> {
    // TODO: Do we need a no-arg constructor?
    // TODO: Document throws

    private int numVerts;

    /**
     * Construct an empty Hypergraph
     **/
    public Hypergraph () {
        this(0);
    }

    /**
     * Construct an empty {@code Hypergraph}.
     *
     * @param numVerts  (positive) number of vertices
     **/
    public Hypergraph (int numVerts) {
        if (numVerts < 0) {
            throw new IllegalArgumentException("A hypergraph can only have positively many vertices.");
        }

        this.numVerts = numVerts;
    };

    /**
     * Construct an empty {@code Hypergraph}.
     *
     * @param numVerts  (positive) number of vertices
     * @param numEdges  (positive) number of edges
     **/
    public Hypergraph (int numVerts, int numEdges) {
        this(numVerts);
        for (int i = 0; i < numEdges; i++) {
            add(new BitSet(numVerts));
        }
    }

    /**
     * Construct a {@code Hypergraph} from a data file.
     *
     * @param inFile path to a file giving the edges of a hypergraph,
     * one per line, as space-separated lists of positive integer
     * indices
     **/
    public Hypergraph (File inFile)
        throws IOException {
        numVerts = 0;

        try (BufferedReader inFileReader
             = new BufferedReader(new InputStreamReader(new FileInputStream(inFile), StandardCharsets.UTF_8))) {
            String line;
            while ((line = inFileReader.readLine()) != null) {
                String[] lineWords = line.trim().split(" ");

                BitSet lineEdge = new BitSet();
                for (String word: lineWords) {
                    try {
                        Integer v = Integer.valueOf(word);
                        lineEdge.set(v);
                    } catch (NumberFormatException e) {
                        throw new NumberFormatException("Could not convert " + word + " to int in input line " + line + ".");
                    }
                }

                add(lineEdge);

                numVerts = Math.max(lineEdge.length(), numVerts);
            }
        }
    };

    /**
     * Construct a Hypergraph from edges in List format.
     *
     * Specifically, the hypergraph should be represented as a {@code
     * List}, each element of which represents one edge with a {@code
     * List} of the {@code Integer} indices of its vertices.
     *
     * @param edges a {@code List<List<Integer>>} representing the
     * edges of the hypergraph.
     **/
    public Hypergraph (List<List<Integer>> edges) {
        numVerts = 0;

        for (List<Integer> edge: edges) {
            BitSet edgeAsBitSet = new BitSet();
            for (int v: edge) {
                edgeAsBitSet.set(v);
            }
            add(edgeAsBitSet);

            numVerts = Math.max(edgeAsBitSet.length(), numVerts);
        }
    }

    /**
     * Copy constructor.
     *
     * Specifically, makes a "deep copy" of the input, whose
     * edges are equal to but distinct as objects from the edges of
     * the input.
     *
     * @param otherHypergraph  a {@code Hypergraph} to copy
     **/
    public Hypergraph (Hypergraph otherHypergraph) {
        numVerts = otherHypergraph.numVerts();
        for (BitSet edge: otherHypergraph) {
            add((BitSet) edge.clone());
        }
    }

    /**
     * Return the edges of this in {@code List} format.
     *
     * Specifically, the hypergraph will be represented as a {@code
     * List}, each element of which represents one edge with a {@code
     * List} of the {@code Integer} indices of its vertices.
     *
     * @return a {@code List<List<Integer>>} representing the edges of
     * the hypergraph.
     **/
    public List<List<Integer>> edgesAsList () {
        List<List<Integer>> result = new ArrayList<> ();

        for (BitSet edge: this) {
            List<Integer> edgeList = new ArrayList<> ();
            for (int i = edge.nextSetBit(0); i >= 0; i = edge.nextSetBit(i+1)) {
                edgeList.add(i);
            }
            result.add(edgeList);
        }

        return result;
    };

    /**
     * Write a data file of this {@code Hypergraph}.
     *
     * The edges will be written one per line, with the indices of the
     * vertices of that edge as a space-separated list.
     *
     * @param outFile  path of file to write
     **/
    public void writeToFile(String outFile)
        throws IOException {
        try (BufferedWriter fileWriter =
             new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile), StandardCharsets.UTF_8))) {
            for (BitSet edge: this) {
                StringBuilder lineBuilder = new StringBuilder();
                for (int i = edge.nextSetBit(0); i >= 0; i = edge.nextSetBit(i+1)) {
                    lineBuilder.append(i + " ");
                }
                lineBuilder.append("\n");
                fileWriter.write(lineBuilder.toString());
            }
        }
    };

    /**
     * Return the number of vertices in the {@code Hypergraph}.
     *
     * @return number of vertices
     **/
    public int numVerts () {
        return numVerts;
    };

    /**
     * Update the recorded number of vertices.
     *
     * Use after manually adding edges.
     **/
    public void updateNumVerts () {
        numVerts = 0;
        for (BitSet edge: this) {
            numVerts = Math.max(edge.length(), numVerts);
        }
    }

    /**
     * Return the number of edges in the {@code Hypergraph}.
     *
     * NOTE: This is a non-trivial operation, so it should be cached
     * in tight loops.
     *
     * @return number of edges
     **/
    public int numEdges () {
        return size();
    };

    /**
     * Minimize this {@code Hypergraph} in place.
     *
     * That is, discard any edge which is a superset of any other edge.
     *
     * NOTE: The implementation of this algorithm will re-order edges.
     **/
    public void minimize () {
        if (size() == 0) {
            return;
        }

        // Sort edges by increasing cardinality, since subsets must be
        // smaller than their supersets
        List<BitSet> edgesSortedByIncreasingCardinality = new ArrayList<> (this);
        Collections.sort(edgesSortedByIncreasingCardinality, new Comparator<BitSet>() {
                @Override
                public int compare(BitSet left, BitSet right) {
                    return left.cardinality() - right.cardinality();
                }
            });

        // Build a new hypergraph with only the inclusion-minimal edges
        this.clear();
        for (BitSet edge: edgesSortedByIncreasingCardinality) {
            if (edge.isEmpty()) {
                continue;
            }

            // Check whether any confirmed edge is a subset of this edge
            boolean edgeIsMinimal = true;
            for (BitSet confirmedEdge: this) {
                BitSet intersection = (BitSet) confirmedEdge.clone();
                intersection.and(edge);

                if (intersection.equals(confirmedEdge)) {
                    edgeIsMinimal = false;
                    break;
                }
            }

            // Add the edge if appropriate
            if (edgeIsMinimal) {
                add(edge);
            }
        }

        updateNumVerts();
    }

    /**
     * Return the minimization of this {@code Hypergraph}.
     *
     * The result has the same vertices as this and all the
     * inclusion-minimal edges of this.
     *
     * Note: this algorithm is O(n²) and should be used with caution
     * in performance-sensitive contexts.
     *
     * @return a new {@code Hypergraph} which is the minimization of
     * this
     **/
    public Hypergraph minimization () {
        Hypergraph result = new Hypergraph(this);
        result.minimize();
        return result;
    };

    /**
     * Return the transpose of this {@code Hypergraph}.
     *
     * Specifically, each vertex of this becomes an edge of the
     * transpose, and the elements of that transpose edge are the
     * edges which contained the original vertex.
     *
     * The name 'transpose' reflects the fact that this operation
     * corresponds to transposition of the adjacency matrix.
     * It is sometimes called the 'dual' operator.
     *
     * @return a new {@code Hypergraph} which is the transpose of this
     **/
    public Hypergraph transpose () {
        int numE = numEdges();

        Hypergraph result = new Hypergraph (numE, numVerts());

        for (int e = 0; e < numE; e++) {
            BitSet edge = get(e);
            for (int v = edge.nextSetBit(0); v >= 0; v = edge.nextSetBit(v+1)) {
                result.get(v).set(e);
            }
        }

        return result;
    };

    /**
     * Return the vertex support of this {@code Hypergraph}.
     *
     * @return a {@code BitSet} with bit i set if vertex i is covered
     * by some edge of this
     **/
    public BitSet support () {
        BitSet result = new BitSet();

        for (BitSet edge: this) {
            result.and(edge);
        }

        return result;
    };

    /**
     * Return the degree list of this {@code Hypergraph}.
     *
     * @return a {@code List<Integer>} where the value in position i
     * is the degree of vertex i
     **/
    public List<Integer> vertexDegrees () {
        List<Integer> result = new ArrayList<> ();

        for (BitSet edge: this) {
            for (int v = 0; v < numVerts(); v++) {
                if (edge.get(v)) {
                    result.set(v, result.get(v) + 1);
                }
            }
        }

        return result;
    };

    /**
     * Test whether a given set is a hitting set of this.
     *
     * @param S  a candidate hitting set
     * @return true if S hits every edge, false if not
     **/
    public boolean isTransversedBy (BitSet S) {
        for (BitSet edge: this) {
            if (! edge.intersects(S)) {
                return false;
            }
        }

        return true;
    };

    /**
     * Test whether a given set covers some edge of this.
     *
     * @param S  a candidate edge-covering set
     * @return true if some edge is a subset of S, false if not
     **/
    public boolean hasEdgeCoveredBy (BitSet S) {
        for (BitSet edge: this) {
            BitSet intersection = (BitSet) edge.clone();
            intersection.and(S);
            if (intersection == edge) {
                return true;
            }
        }

        return false;
    };
}
