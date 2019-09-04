/**
 * Component to handle information about nodes
 *
 * Copyright Vera-Licona Research Group (C) 2016
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.util.tunables;

// Java imports
import java.util.*;

// Cytoscape imports
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyNetwork;

/**
 * Component to handle information about nodes
 * <p>
 * By default, we use the "SUID" column, since this is guaranteed to exist.
 **/
public class NodeHandler {
    private final CyNetwork network;
    private CyColumn nodeNameColumn;
    private CyColumn nodeIDColumn;

    private Map<String, CyNode> nodeNamesMap;

    /**
     * Constructor
     *
     * @param network  the network from which nodes are drawn
     **/
    public NodeHandler (CyNetwork network) {
        Objects.requireNonNull(network, "Network cannot be null");
        this.network = network;

        setNodeNameColumn(network.getDefaultNodeTable().getColumn("SUID"));
    }

    /**
     * Set the column in the node table which contains the names of nodes
     **/
    public void setNodeNameColumn (CyColumn nodeNameColumn) {
        Objects.requireNonNull(nodeNameColumn, "Node name column cannot be null");
        this.nodeNameColumn = nodeNameColumn;

        nodeNamesMap = new HashMap<>(network.getNodeCount());

        List<CyNode> nodes = network.getNodeList();
        for (CyNode node: nodes) {
            String nodeName = getNodeName(node);
            nodeNamesMap.put(nodeName, node);
        }
    }

    /**
     * Get the name of the column node names are taken from
     **/
    public String getNodeNameColumnName () {
        return nodeNameColumn.getName();
    }

    /**
     * Get the name of a node
     *
     * @param node  the node
     * @return a string representation of the node
     **/
    public String getNodeName (CyNode node) {
        Objects.requireNonNull(node, "Node cannot be null");
        return network.getRow(node).get(getNodeNameColumnName(), Object.class).toString();
    }

    /**
     * Set the column in the node table which contains the biomolecule
     * IDs of nodes
     **/
    public void setNodeIDColumn (CyColumn nodeIDColumn) {
        Objects.requireNonNull(nodeIDColumn, "Node ID column cannot be null");
        this.nodeIDColumn = nodeIDColumn;
    }

    /**
     * Get the name of the column node IDs are taken from
     **/
    public String getNodeIDColumnName () {
        return nodeIDColumn.getName();
    }

    /**
     * Get the ID of a node
     **/
    public String getNodeID (CyNode node) {
        Objects.requireNonNull(node, "Node cannot be null");
        return network.getRow(node).get(getNodeIDColumnName(), Object.class).toString();
    }

    /**
     * Get the node with a given name
     *
     * @param nodeName  the name
     * @return the node (or null if the name is not found)
     **/
    public CyNode getNodeByName (String nodeName) {
        return nodeNamesMap.getOrDefault(nodeName, null);
    }
}
