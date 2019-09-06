package org.compsysmed.ocsana.internal.util.sfa;

import java.util.Collection;
import java.util.List;
import java.util.Objects;




public class SFAResultsBundle {
	
	
    // FC data
    private String SFA;
    private Double SFAExecutionSeconds;
    private Boolean SFACanceled = false;
    private String SFAconfig;
    
    public String getSFA () {
        return SFA;
    }
   
    public void setSFA (String SFA) {

		Objects.requireNonNull(SFA, "SFA results cannot be null");
    this.SFA = SFA;
    }
    
    public String getSFAconfig() {
		return SFAconfig;
	}
    public void setSFAconfig (String SFAconfig) {

		Objects.requireNonNull(SFAconfig, "SFA configuration cannot be null");
    this.SFAconfig = SFAconfig;
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