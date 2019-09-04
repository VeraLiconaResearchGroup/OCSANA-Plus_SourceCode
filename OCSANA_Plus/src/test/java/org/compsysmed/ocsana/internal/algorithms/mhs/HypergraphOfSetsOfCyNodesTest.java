/**
 * Test cases for the HypergraphOfSetOfCyNodes class
 *
 * Copyright Vera-Licona Research Group (C) 2015
 **/

package org.compsysmed.ocsana.internal.algorithms.mhs;

// JUnit imports
import org.junit.*;
import static org.junit.Assert.*;

// Cytoscape imports
import org.cytoscape.model.NetworkTestSupport;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
// Java imports
import java.util.*;

// OCSANA imports
import org.compsysmed.ocsana.internal.algorithms.mhs.HypergraphOfSetsOfCyNodes;

public class HypergraphOfSetsOfCyNodesTest {
    NetworkTestSupport nts;

    CyNetwork network;

    @Before
    public void setUp () {
        // Set up the test environment here
        // In particular, initialize any shared variables
        nts = new NetworkTestSupport();
        network = nts.getNetwork();
    }

    @After
    public void tearDown () {
        // Tear down the test environment here
        // In particular, null out any shared variables so the garbage
        // collector can trash them
        nts = null;
        network = null;
    }

    @Test
    public void nodeConstructorShouldPreserveSize () {
        List<Set<CyNode>> sets = new ArrayList<>();
        CyNode A = network.addNode();
        CyNode B = network.addNode();
        CyNode C = network.addNode();
        CyNode D = network.addNode();
        CyNode E = network.addNode();

        Set<CyNode> set1 = new HashSet<>();
        set1.add(A);
        set1.add(B);
        set1.add(E);
        sets.add(set1);

        Set<CyNode> set2 = new HashSet<>();
        set2.add(B);
        set2.add(C);
        set2.add(D);
        sets.add(set2);

        Set<CyNode> set3 = new HashSet<>();
        set3.add(A);
        set3.add(C);
        sets.add(set3);

        HypergraphOfSetsOfCyNodes H = new HypergraphOfSetsOfCyNodes(sets);

        assertEquals("Edge count", sets.size(), H.numEdges());
        assertEquals("Vertex count", 5, H.numVerts());
    }
}
