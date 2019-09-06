package org.compsysmed.ocsana.internal.util.fc;

import java.util.Collection;
import java.util.List;
import java.util.Objects;




public class FCResultsBundle {
	
	
    // FC data
    private String FC;
    private String FVS;
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

    public String getFVS () {
        return FVS;
    }
   
    public void setFVS (String FVS) {

		Objects.requireNonNull(FVS, "Collection of MFRs to targets cannot be null");
    this.FVS = FVS;
    }
}