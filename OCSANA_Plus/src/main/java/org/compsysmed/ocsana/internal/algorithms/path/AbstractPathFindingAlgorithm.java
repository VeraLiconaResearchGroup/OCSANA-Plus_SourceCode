/**
 * Abstract base class for all path-finding algorithms
 *
 * Copyright Vera-Licona Research Group (C) 2015
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.algorithms.path;

// Java imports
import java.util.*;

// Cytoscape imports
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;

// OCSANA imports
import org.compsysmed.ocsana.internal.algorithms.AbstractOCSANAAlgorithm;

/**
 * Public abstract base class for all path-finding algorithms.
 **/

public abstract class AbstractPathFindingAlgorithm
    extends AbstractOCSANAAlgorithm {
    protected static final String UNDIRECTED_ERROR_MESSAGE = "Undirected edges are not supported";

    protected final CyNetwork network;

    public AbstractPathFindingAlgorithm (CyNetwork network) {
        this.network = network;
    }

    /**
     * Compute paths from a set of source nodes to a set of target nodes.
     *
     * @param sources  the source nodes
     * @param targets  the target nodes
     * @return the paths, each of which is given as a List of
     * CyEdges, in order from source to target
     **/
    public abstract Collection<List<CyEdge>> paths (Set<CyNode> sources,
                                                    Set<CyNode> targets);
}
