/**
 * Builder for contexts for an OCSANA run
 *
 * Copyright Vera-Licona Research Group (C) 2016
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.util.fvs;

// Java imports
import java.util.*;

// Cytoscape imports
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.compsysmed.ocsana.internal.util.fc.FCBundle;
// OCSANA imports
import org.compsysmed.ocsana.internal.util.tunables.NodeHandler;
import org.compsysmed.ocsana.internal.algorithms.fc.AbstractFCAlgorithm;
import org.compsysmed.ocsana.internal.algorithms.fc.FC;


/**
 * Context builder for an FC run
 * <p>
 * This class allows incremental construction of a
 * {@link ContextBundle}.
 * Its getters should only be used during configuration; the immutable
 * FCBundle should be used once configuration is complete.
 **/
public class FVSBundleBuilder {
    private final CyNetwork network;

    private final Boolean sourcenodes;
    private AbstractFCAlgorithm FCalgorithm;
    private NodeHandler nodeHandler;
    
    public FVSBundleBuilder (CyNetwork network) {
        Objects.requireNonNull(network, "Network cannot be null");
        this.network = network;

        setNodeHandler(new NodeHandler(network));
        FCalgorithm= new FC(network);
        sourcenodes = false;

    }

    /**
     * Set the node name handler
     **/
    public void setNodeHandler (NodeHandler nodeHandler) {
        Objects.requireNonNull(nodeHandler, "Node name handler cannot be null");

        this.nodeHandler = nodeHandler;
    }

    /**
     * Return the currently-selected node name handler
     **/
    public NodeHandler getNodeHandler () {
        return nodeHandler;
    }
    /**
     * Return the underlying network
     **/
    public CyNetwork getNetwork () {
        return network;
    }

    
	public AbstractFCAlgorithm getFCalgorithm() {
		return FCalgorithm;
	}

    /**
     * Return the context as currently configured
     **/
    public FCBundle getFCBundle () {
        return new FCBundle(network, nodeHandler, FCalgorithm,sourcenodes);
    }

}