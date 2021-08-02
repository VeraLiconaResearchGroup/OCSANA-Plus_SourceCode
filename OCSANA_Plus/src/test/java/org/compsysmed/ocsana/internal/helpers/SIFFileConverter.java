/**
 * Class to read a network from an SIF file
 *
 * Copyright Vera-Licona Research Group (C) 2015
 **/

package org.compsysmed.ocsana.internal.helpers;

// Java imports
import java.util.*;
import java.io.*;

// Cytoscape imports
import org.cytoscape.model.NetworkTestSupport;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyEdge;

/**
 * Build a CyNetwork from an SIF file
 *
 * @param sifFile  the SIF file
 **/
public class SIFFileConverter {
    NetworkTestSupport nts;
    CyNetwork network;
    Map<String, CyNode> nodeMap;

    public SIFFileConverter (File sifFile)
        throws IOException {
        nts = new NetworkTestSupport();
        network = nts.getNetwork();
        nodeMap = new HashMap<>();

        try (BufferedReader sifFileReader
             = new BufferedReader(new FileReader(sifFile))) {
            // Process the file line-by-line
            // Each line represents an edge of the network
            for (String line = sifFileReader.readLine();
                 line != null; line = sifFileReader.readLine()) {
                // Get the head and tail vertices of the edge
                // NOTE: we do not handle the multi-target case, which
                // is supported by SIF
                String[] lineWords = line.split("\t");
                if (lineWords.length != 3) {
                    throw new IllegalArgumentException("Invalid SIF file");
                }

                CyNode head = getNode(lineWords[0]);
                CyNode tail = getNode(lineWords[2]);

                // Create the edge
                CyEdge newEdge = getEdge(head, tail);
            }
        }
    }

    /**
     * Retrieve the network
     **/
    public CyNetwork getNetwork() {
        return network;
    }

    /**
     * Get the node with the specified name, creating it if necessary
     *
     * @param nodeName  the node name
     **/
    public CyNode getNode(String nodeName) {
        if (nodeMap.containsKey(nodeName)) {
            return nodeMap.get(nodeName);
        } else {
            CyNode newNode = network.addNode();
            network.getRow(newNode).set(CyNetwork.NAME, nodeName);
            nodeMap.put(nodeName, newNode);
            return newNode;
        }
    }

    /**
     * Get the edge with the specified source and target node names,
     * creating it if necessary
     *
     * @param sourceNodeName  the source node
     * @param targetNodeName  the target node
     **/
    public CyEdge getEdge(CyNode sourceNode, CyNode targetNode) {
        // Messy, goofy check for known edges because
        // CyNetwork.getConnectingEdgeList seems to be broken
        List<CyEdge> knownEdges = new ArrayList<>();
        for (CyEdge outEdge: network.getAdjacentEdgeIterable(sourceNode, CyEdge.Type.OUTGOING)) {
            assert outEdge.getSource() == sourceNode;
            if (outEdge.getTarget() == targetNode) {
                knownEdges.add(outEdge);
            }
        };

        if (!knownEdges.isEmpty()) {
            assert knownEdges.size() == 1;
            return knownEdges.get(0);
        } else {
            return network.addEdge(sourceNode, targetNode, true);
        }
    }

    /**
     * Get the edges in a path given by a sequence of nodes, creating
     * the edges if necessary
     *
     * @param nodeNames  the nodes
     **/
    public List<CyEdge> getPath(CyNode... nodeNames) {
        List<CyEdge> path = new ArrayList<>();
        for (int i = 0; i < nodeNames.length - 1; i++) {
            path.add(getEdge(nodeNames[i], nodeNames[i+1]));
        }
        return path;
    }
}
