/**
 * Abstract base class for all OCSANA algorithms
 *
 * Copyright Vera-Licona Research Group (C) 2015
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/
package org.compsysmed.ocsana.internal.algorithms;

// Java imports
import java.util.concurrent.atomic.AtomicBoolean;

// Cytoscape imports

// OCSANA imports

/**
 * Abstract base class for all OCSANA algorithms
 **/

public abstract class AbstractOCSANAAlgorithm {
    // Keep track of whether the user has canceled the algorithm
    private AtomicBoolean canceled = new AtomicBoolean(false);

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

    /**
     * Indicate whether the algorithm has been canceled by the user
     **/
    public Boolean isCanceled() {
        return canceled.get();
    }

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
}
