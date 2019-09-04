/**
 * Test cases for the OCSANAGreedyAlgorithm class
 *
 * Copyright Vera-Licona Research Group (C) 2016
 **/

package org.compsysmed.ocsana.internal.algorithms.mhs;

// JUnit imports
import org.junit.*;
import static org.junit.Assert.*;

// Mockito imports
import static org.mockito.Mockito.*;

// Java imports
import java.util.*;
import java.io.*;

// Cytoscape imports
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyEdge;

// OCSANA imports
import org.compsysmed.ocsana.internal.algorithms.mhs.Hypergraph;
import org.compsysmed.ocsana.internal.algorithms.mhs.OCSANAGreedyAlgorithm;

import org.compsysmed.ocsana.internal.helpers.SIFFileConverter;

import org.compsysmed.ocsana.internal.util.results.OCSANAScores;

public class OCSANAGreedyAlgorithmTest {
    CyNetwork toyNetwork;

    OCSANAGreedyAlgorithm algFull;
    OCSANAGreedyAlgorithm algCutoff;

    Collection<Set<CyNode>> sets;

    @Before
    public void setUp ()
        throws IOException {
        // Set up the test environment here
        // In particular, initialize any shared variables
        // Toy network
        File toyFile = new File(getClass().getResource("/network-data/ToyNetwork.sif").getFile());
        SIFFileConverter toyConverter = new SIFFileConverter(toyFile);
        toyNetwork = toyConverter.getNetwork();
        CyNode A = toyConverter.getNode("A");
        CyNode B = toyConverter.getNode("B");
        CyNode C = toyConverter.getNode("C");
        CyNode D = toyConverter.getNode("D");
        CyNode E = toyConverter.getNode("E");

        OCSANAScores toyNetworkScores = mock(OCSANAScores.class);
        when(toyNetworkScores.OCSANA(any(CyNode.class))).thenReturn(0d);
        when(toyNetworkScores.getNetwork()).thenReturn(toyNetwork);

        algFull = new OCSANAGreedyAlgorithm(toyNetwork);
        algFull.useMaxCardinality = false;
        algFull.useMaxCandidates = false;
        algFull.receiveScores(toyNetworkScores);

        algCutoff = new OCSANAGreedyAlgorithm(toyNetwork);
        algCutoff.useMaxCardinality = true;
        algCutoff.maxCardinalityBInt.setValue(2);
        algCutoff.useMaxCandidates = false;
        algCutoff.receiveScores(toyNetworkScores);

        sets = new ArrayList<>();

        Set<CyNode> set1 = new HashSet<>();
        set1.add(A);
        set1.add(B);
        set1.add(C);
        sets.add(set1);

        Set<CyNode> set2 = new HashSet<>();
        set2.add(B);
        set2.add(D);
        set2.add(E);
        sets.add(set2);

        Set<CyNode> set3 = new HashSet<>();
        set3.add(A);
        set3.add(D);
        sets.add(set3);
    }

    @After
    public void tearDown () {
        // Tear down the test environment here
        // In particular, null out any shared variables so the garbage
        // collector can trash them
        algFull = null;
        algCutoff = null;

        toyNetwork = null;

        sets = null;
    }

    @Test
    public void fullEnumerationShouldWork () {
        Collection<Set<CyNode>> MHSes = algFull.MHSes(sets);
        assertEquals("MHS count", 5, MHSes.size());

        for (Set<CyNode> MHS: MHSes) {
            assertTrue("Hitting set condition", sets.stream().allMatch(set -> MHS.stream().anyMatch(node -> set.contains(node))));
        }
    }

    @Test
    public void cutoffEnumerationShouldWork () {
        Collection<Set<CyNode>> MHSes = algCutoff.MHSes(sets);
        assertEquals("MHS count", 5, MHSes.size());

        for (Set<CyNode> MHS: MHSes) {
            assertTrue("Hitting set size condition", MHS.size() <= 2);
            assertTrue("Hitting set condition", sets.stream().allMatch(set -> MHS.stream().anyMatch(node -> set.contains(node))));
        }
    }
}
