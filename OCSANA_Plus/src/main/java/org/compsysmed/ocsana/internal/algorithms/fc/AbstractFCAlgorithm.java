
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

package org.compsysmed.ocsana.internal.algorithms.fc;

// Java imports
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

// Cytoscape imports
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;
import org.ejml.simple.SimpleMatrix;

/**
 * Public abstract base class for all path-finding algorithms.
 **/

public abstract class AbstractFCAlgorithm{
    protected static final String UNDIRECTED_ERROR_MESSAGE = "Undirected edges are not supported";

    protected final CyNetwork network;

    public AbstractFCAlgorithm (CyNetwork network) {
        this.network = network;
    }
    private AtomicBoolean canceled = new AtomicBoolean(false);

    /**
     * Compute paths from a set of source nodes to a set of target nodes.
     *
     * @param sources  the source nodes
     * @param targets  the target nodes
     * @return the paths, each of which is given as a List of
     * CyEdges, in order from source to target
     **/
    public abstract Map<String, List<CyNode>> FVS ();
 /**
 * Return a long, explanatory name
 **/
public abstract String fullName ();

/**
 * Return a short name
 **/
public abstract String shortName ();

/**
 * Return a name suitable for printing in a dropdown menu or
 * status message
 * <p>
 * NOTE: returns {@link #fullName()} by default, but can be overridden
 **/
public String toString () {
    return fullName();
}

/**
 * Return a descriptive string suitable for printing in a report
 **/
public abstract String description ();


/**
 * Abort execution of the algorithm
 * <p>
 * NOTE: cancel/uncancel operations are not thread safe!
 **/
public void cancel () {
    canceled.set(true);
}

/**
 * Clear cancellation flag
 **/
public void uncancel () {
    canceled.set(false);
}
}

