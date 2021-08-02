/**
 * Container to hold results of an OCSANA run
 *
 * Copyright Vera-Licona Research Group (C) 2015
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.util.results;

// Java imports
import java.util.*;
import java.util.stream.*;

// Cytoscape imports
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyEdge;

// OCSANA imports
import org.compsysmed.ocsana.internal.util.results.CombinationOfInterventions;

public class ResultsBundle {
    // Paths data
    private Collection<List<CyEdge>> pathsToTargets;
    private Collection<List<CyEdge>> pathsToOffTargets;

    private Double pathsToTargetsExecutionSeconds;
    private Double pathsToOffTargetsExecutionSeconds;

    private Boolean pathFindingCanceled = false;

    // Scoring data
    private Double OCSANAScoringExecutionSeconds;
    private Boolean OCSANAScoringCanceled = false;
    private OCSANAScores ocsanaScores;

    // MHS data
    private Collection<CombinationOfInterventions> CIs;
    private Double mhsExecutionSeconds;
    private Boolean mhsFindingCanceled = false;

    
   
    public Collection<List<CyEdge>> getPathsToTargets () {
        return pathsToTargets;
    }

    public void setPathsToTargets (Collection<List<CyEdge>> pathsToTargets) {
        Objects.requireNonNull(pathsToTargets, "Collection of paths to targets cannot be null");
        this.pathsToTargets = pathsToTargets;
    }
   
    public Collection<List<CyEdge>> getPathsToOffTargets () {
        return pathsToOffTargets;
    }

    public void setPathsToOffTargets (Collection<List<CyEdge>> pathsToOffTargets) {
        Objects.requireNonNull(pathsToOffTargets, "Collection of paths to off-targets cannot be null");
        this.pathsToOffTargets = pathsToOffTargets;
    }

    public Double getPathsToTargetsExecutionSeconds () {
        return pathsToTargetsExecutionSeconds;
    }

    public void setPathsToTargetsExecutionSeconds (Double pathsToTargetsExecutionSeconds) {
        Objects.requireNonNull(pathsToTargetsExecutionSeconds, "Time to compute paths to targets cannot be null");
        this.pathsToTargetsExecutionSeconds = pathsToTargetsExecutionSeconds;
    }

    public Double getPathsToOffTargetsExecutionSeconds () {
        return pathsToOffTargetsExecutionSeconds;
    }

    public void setPathsToOffTargetsExecutionSeconds (Double pathsToOffTargetsExecutionSeconds) {
        Objects.requireNonNull(pathsToOffTargetsExecutionSeconds, "Time to compute paths to off-targets cannot be null");
        this.pathsToOffTargetsExecutionSeconds = pathsToOffTargetsExecutionSeconds;
    }

    public Boolean pathFindingWasCanceled () {
        return pathFindingCanceled;
    }

    public void setPathFindingWasCanceled () {
        pathFindingCanceled = true;
    }

    public Double getOCSANAScoringExecutionSeconds () {
        return OCSANAScoringExecutionSeconds;
    }

    public void setOCSANAScoringExecutionSeconds (Double OCSANAScoringExecutionSeconds) {
        Objects.requireNonNull(OCSANAScoringExecutionSeconds, "Time to compute OCSANA scores cannot be null");
        this.OCSANAScoringExecutionSeconds = OCSANAScoringExecutionSeconds;
    }

    public Boolean OCSANAScoringWasCanceled () {
        return OCSANAScoringCanceled;
    }

    public void setOCSANAScoringWasCanceled () {
        OCSANAScoringCanceled = true;
    }

    public OCSANAScores getOCSANAScores () {
        return ocsanaScores;
    }

    public void setOCSANAScores (OCSANAScores ocsanaScores) {
        this.ocsanaScores = ocsanaScores;
    }

    public Collection<CombinationOfInterventions> getCIs () {
        return CIs;
    }

    public void setCIs (Collection<CombinationOfInterventions> CIs) {
        Objects.requireNonNull(CIs, "Collection of CIs cannot be null");
        this.CIs = CIs;
    }

    public Double getMHSExecutionSeconds () {
        return mhsExecutionSeconds;
    }

    public void setMHSExecutionSeconds (Double mhsExecutionSeconds) {
        Objects.requireNonNull(mhsExecutionSeconds, "Time to compute MHSes cannot be null");
        this.mhsExecutionSeconds = mhsExecutionSeconds;
    }

    public Boolean MHSFindingWasCanceled () {
        return mhsFindingCanceled;
    }

    public void setMHSFindingWasCancelled () {
        mhsFindingCanceled = true;
    }

}
