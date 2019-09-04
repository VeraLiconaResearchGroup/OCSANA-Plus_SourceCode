package org.compsysmed.ocsana.internal.util.sfa;

import java.util.Collection;
import java.util.List;
import java.util.Objects;




public class SFAResultsBundle {
	
	
    // FC data
    private String SFA;
    private Double SFAExecutionSeconds;
    private Boolean SFACanceled = false;
    
    public String getSFA () {
        return SFA;
    }
   
    public void setSFA (String SFA) {

		Objects.requireNonNull(SFA, "Collection of MFRs to targets cannot be null");
    this.SFA = SFA;
    }
    public Double getSFAExecutionSeconds () {
        return SFAExecutionSeconds;
    }
    public void setSFAExecutionSeconds (Double SFAExecutionSeconds) {
        Objects.requireNonNull(SFAExecutionSeconds, "Time to compute FC cannot be null");
        this.SFAExecutionSeconds = SFAExecutionSeconds;
    }
    public Boolean SFAWasCanceled () {
        return SFACanceled;
    }

    public void setSFAWasCanceled () {
       SFACanceled = true;
    }
}