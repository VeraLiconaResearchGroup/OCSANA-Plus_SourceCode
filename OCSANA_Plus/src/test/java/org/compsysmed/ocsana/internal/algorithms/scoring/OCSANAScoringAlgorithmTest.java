/**
 * Test cases for the OCSANAScoringAlgorithm class
 *
 * Copyright Vera-Licona Research Group (C) 2015
 **/

package org.compsysmed.ocsana.internal.algorithms.scoring;

// JUnit imports
import org.junit.*;
import static org.junit.Assert.*;

// Java imports
import java.util.*;
import java.io.*;

import java.util.function.Function;

// Cytoscape imports
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;

// OCSANA imports
import org.compsysmed.ocsana.internal.algorithms.scoring.OCSANAScoringAlgorithm;

import org.compsysmed.ocsana.internal.util.results.OCSANAScores;

import org.compsysmed.ocsana.internal.helpers.SIFFileConverter;

public class OCSANAScoringAlgorithmTest {
    @Before
    public void setUp () {
        // Set up the test environment here
        // In particular, initialize any shared variables
    }

    @After
    public void tearDown () {
        // Tear down the test environment here
        // In particular, null out any shared variables so the garbage
        // collector can trash them
    }

    @Test
    public void shouldScoreToyNetworkCorrectly ()
        throws IOException {
        // Toy network setup
        File toyFile = new File(getClass().getResource("/network-data/ToyNetwork.sif").getFile());
        SIFFileConverter toyConverter = new SIFFileConverter(toyFile);
        CyNetwork toyNetwork = toyConverter.getNetwork();

        // Scoring algorithm setup
        OCSANAScoringAlgorithm scoringAlg = new OCSANAScoringAlgorithm(toyNetwork);

        // Get nodes
        CyNode I1 = toyConverter.getNode("I1");
        CyNode I2 = toyConverter.getNode("I2");
        CyNode A = toyConverter.getNode("A");
        CyNode B = toyConverter.getNode("B");
        CyNode C = toyConverter.getNode("C");
        CyNode D = toyConverter.getNode("D");
        CyNode E = toyConverter.getNode("E");
        CyNode F = toyConverter.getNode("F");
        CyNode O1 = toyConverter.getNode("O1");
        CyNode O2 = toyConverter.getNode("O2");

        // Define paths
        Collection<List<CyEdge>> pathsToTargets = new ArrayList<>();
        pathsToTargets.add(toyConverter.getPath(I1, A, D, E, O1));
        pathsToTargets.add(toyConverter.getPath(I1, B, E, O1));
        pathsToTargets.add(toyConverter.getPath(I1, A, B, E, O1));
        pathsToTargets.add(toyConverter.getPath(I2, C, F, O2));
        pathsToTargets.add(toyConverter.getPath(I2, C, F, B, E, O1));
        pathsToTargets.add(toyConverter.getPath(I2, C, E, O1));
        pathsToTargets.add(toyConverter.getPath(I2, B, E, O1));

        Collection<List<CyEdge>> pathsToOffTargets = Arrays.asList(toyConverter.getPath(I1, A, D));

        // Hand-coded method to test whether edges are activation or inhibition
        Function<CyEdge, String> inhibitionEdgeTester = (CyEdge edge) -> {
            CyNode source = edge.getSource();
            CyNode target = edge.getTarget();

            if (source.equals(I1) && target.equals(B)) {
                return "inhibits";
            }

            if (source.equals(A) && target.equals(D)) {
                return "inhibits";
            }

            if (source.equals(D) && target.equals(E)) {
                return "inhibits";
            }

            return "activates";
        };

        // Compute scores
        OCSANAScores scores = scoringAlg.computeScores(pathsToTargets, pathsToOffTargets);

        // Tests
        assertEquals("Toy network score: A", 0.0d, scores.OCSANA(A), 0.0d);
        assertEquals("Toy network score: B", 4.0d, scores.OCSANA(B), 0.0d);
        assertEquals("Toy network score: C", 3.75d, scores.OCSANA(C), 0.0d);
        //assertEquals("Toy network score: D", -1.0d, scores.OCSANA(D), 0.0d); // TODO: write a test once this case is defined
        assertEquals("Toy network score: E", 18.0d, scores.OCSANA(E), 0.0d);
        assertEquals("Toy network score: F", 2.66d, scores.OCSANA(F), 0.01d);
        assertEquals("Toy network score: C+E", 21.75d, scores.OCSANA(new HashSet<CyNode>(Arrays.asList(C, E))), 0.01d);
    }
}