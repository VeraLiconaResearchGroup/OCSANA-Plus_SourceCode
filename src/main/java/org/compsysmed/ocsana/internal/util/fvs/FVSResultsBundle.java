package org.compsysmed.ocsana.internal.util.fvs;

import java.util.Collection;
import java.util.List;
import java.util.Objects;




public class FVSResultsBundle {
	
	
    // FC data
    private String FC;
    private Double FCExecutionSeconds;
    private Boolean FCCanceled = false;
    
    public String getFC () {
        return FC;
    }
   
    public void setFC (String FC) {

		Objects.requireNonNull(FC, "Collection of MFRs to targets cannot be null");
    this.FC = FC;
    }
    public Double getFCExecutionSeconds () {
        return FCExecutionSeconds;
    }
    public void setFCExecutionSeconds (Double FCExecutionSeconds) {
        Objects.requireNonNull(FCExecutionSeconds, "Time to compute FC cannot be null");
        this.FCExecutionSeconds = FCExecutionSeconds;
    }
    public Boolean FCWasCanceled () {
        return FCCanceled;
    }

    public void setFCWasCanceled () {
       FCCanceled = true;
    }
}