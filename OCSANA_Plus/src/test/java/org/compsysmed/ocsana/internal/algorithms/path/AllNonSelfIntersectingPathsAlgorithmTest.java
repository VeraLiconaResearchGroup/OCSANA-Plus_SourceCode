/**
 * Test cases for the AllNonSelfIntersectingPathsAlgorithm class
 *
 * Copyright Vera-Licona Research Group (C) 2015
 **/

package org.compsysmed.ocsana.internal.algorithms.path;

// JUnit imports
import org.junit.*;
import static org.junit.Assert.*;

// Java imports
import java.util.*;
import java.io.*;

// Cytoscape imports
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyEdge;

// OCSANA imports
import org.compsysmed.ocsana.internal.algorithms.path.AllNonSelfIntersectingPathsAlgorithm;

import org.compsysmed.ocsana.internal.helpers.SIFFileConverter;

public class AllNonSelfIntersectingPathsAlgorithmTest {
    CyNetwork toyNetwork;
    Set<CyNode> toyNetworkSources;
    Set<CyNode> toyNetworkTargets;

    CyNetwork HER2Network;
    Set<CyNode> HER2NetworkSources;
    Set<CyNode> HER2NetworkTargets;

    @Before
    public void setUp ()
        throws IOException {
        // Set up the test environment here
        // In particular, initialize any shared variables

        // Toy network
        File toyFile = new File(getClass().getResource("/network-data/ToyNetwork.sif").getFile());
        SIFFileConverter toyConverter = new SIFFileConverter(toyFile);
        toyNetwork = toyConverter.getNetwork();

        toyNetworkSources = new HashSet<>();
        List<String> toyNetworkSourceNames = Arrays.asList("I1", "I2");
        for (String sourceName: toyNetworkSourceNames) {
            toyNetworkSources.add(toyConverter.getNode(sourceName));
        }
        assert toyNetworkSources.size() == 2;

        toyNetworkTargets = new HashSet<>();
        List<String> toyNetworkTargetNames = Arrays.asList("O1", "O2");
        for (String targetName: toyNetworkTargetNames) {
            toyNetworkTargets.add(toyConverter.getNode(targetName));
        }
        assert toyNetworkTargets.size() == 2;

        // HER2 network
        File HER2File = new File(getClass().getResource("/network-data/HER2.sif").getFile());
        SIFFileConverter HER2Converter = new SIFFileConverter(HER2File);
        HER2Network = HER2Converter.getNetwork();

        HER2NetworkSources = new HashSet<>();
        List<String> HER2NetworkSourceNames = Arrays.asList("n1064", "n1065", "n1066", "n1067", "n1069", "n1071", "n1073", "n1092", "n1093", "n1748", "n1855", "n1869", "n2270", "n2274", "n2276", "n2277", "n2679", "n2693", "n2695", "n2701", "n2704", "n2712", "n2727", "n2753", "n2758", "n2766", "n2781", "n66", "n76");
        for (String sourceName: HER2NetworkSourceNames) {
            HER2NetworkSources.add(HER2Converter.getNode(sourceName));
        }
        assert HER2NetworkSources.size() == 29;

        HER2NetworkTargets = new HashSet<>();
        List<String> HER2NetworkTargetNames = Arrays.asList("n1000", "n1034", "n1115", "n1119", "n1126", "n1158", "n1173", "n1177", "n1878", "n2492", "n2615", "n2652", "n839", "n940", "n983", "n996");
        for (String targetName: HER2NetworkTargetNames) {
            HER2NetworkTargets.add(HER2Converter.getNode(targetName));
        }
        assert HER2NetworkTargets.size() == 16;
    }

    @After
    public void tearDown () {
        // Tear down the test environment here
        // In particular, null out any shared variables so the garbage
        // collector can trash them
        toyNetwork = null;
        toyNetworkSources = null;
        toyNetworkTargets = null;

        HER2Network = null;
        HER2NetworkSources = null;
        HER2NetworkTargets = null;
    }

    @Test
    public void toyNetworkShouldHaveRightNumberOfShortPaths () {
        // Algorithm
        AllNonSelfIntersectingPathsAlgorithm pathAlg = new AllNonSelfIntersectingPathsAlgorithm(toyNetwork);
        pathAlg.dijkstra.restrictPathLength = true;
        pathAlg.dijkstra.maxPathLength = 3;

        // path-finding
        Collection<List<CyEdge>> paths = pathAlg.paths(toyNetworkSources, toyNetworkTargets);
        assertEquals("Path count", 4, paths.size());

        for (List<CyEdge> path: paths) {
            assertTrue("Path starts at a source", toyNetworkSources.contains(path.get(0).getSource()));
            assertTrue("Path ends at a target", toyNetworkTargets.contains(path.get(path.size() - 1).getTarget()));
        }
    }

    @Test
    public void toyNetworkShouldHaveRightNumberOfLongPaths () {
        // Algorithm
        AllNonSelfIntersectingPathsAlgorithm pathAlg = new AllNonSelfIntersectingPathsAlgorithm(toyNetwork);
        pathAlg.dijkstra.restrictPathLength = true;
        pathAlg.dijkstra.maxPathLength = 20;

        // path-finding
        Collection<List<CyEdge>> paths = pathAlg.paths(toyNetworkSources, toyNetworkTargets);
        assertEquals("Path count", 7, paths.size());

        for (List<CyEdge> path: paths) {
            assertTrue("Path starts at a source", toyNetworkSources.contains(path.get(0).getSource()));
            assertTrue("Path ends at a target", toyNetworkTargets.contains(path.get(path.size() - 1).getTarget()));
        }
    }

    @Test
    public void HER2NetworkShouldHaveRightNumberOfShortPaths () {
        // Algorithm
        AllNonSelfIntersectingPathsAlgorithm pathAlg = new AllNonSelfIntersectingPathsAlgorithm(HER2Network);
        pathAlg.dijkstra.restrictPathLength = true;
        pathAlg.dijkstra.maxPathLength = 5;

        // Path-finding
        Collection<List<CyEdge>> paths = pathAlg.paths(HER2NetworkSources, HER2NetworkTargets);
        assertEquals("Path count", 3, paths.size());

        for (List<CyEdge> path: paths) {
            assertTrue("Path starts at a source", HER2NetworkSources.contains(path.get(0).getSource()));
            assertTrue("Path ends at a target", HER2NetworkTargets.contains(path.get(path.size() - 1).getTarget()));
        }
    }

    @Test
    public void HER2NetworkShouldHaveRightNumberOfLongPaths () {
        // Algorithm
        AllNonSelfIntersectingPathsAlgorithm pathAlg = new AllNonSelfIntersectingPathsAlgorithm(HER2Network);
        pathAlg.dijkstra.restrictPathLength = true;
        pathAlg.dijkstra.maxPathLength = 20;

        // Path-finding
        Collection<List<CyEdge>> paths = pathAlg.paths(HER2NetworkSources, HER2NetworkTargets);
        assertEquals("Path count", 69805, paths.size());

        for (List<CyEdge> path: paths) {
            assertTrue("Path starts at a source", HER2NetworkSources.contains(path.get(0).getSource()));
            assertTrue("Path ends at a target", HER2NetworkTargets.contains(path.get(path.size() - 1).getTarget()));
        }
    }
}
