/**
 * Class representing a target node with a score
 *
 * Copyright Vera-Licona Research Group (C) 2016
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.util.results;

// Java imports

// Cytoscape imports
import org.cytoscape.model.CyNode;

public class ScoredTargetNode {
    public final CyNode node;
    public final Double score;
    public final String name;

    public ScoredTargetNode (CyNode node,
                             Double score,
                             String name) {
        this.node = node;
        this.score = score;
        this.name = name;
    }

    public Long getSUID () {
        return node.getSUID();
    }
}
