package org.compsysmed.ocsana.internal.ui.fvs;

import java.awt.event.ActionEvent;
import java.util.Objects;



import org.compsysmed.ocsana.internal.tasks.fc.FCRunnerTaskFactory;
import org.compsysmed.ocsana.internal.tasks.sfa.SFARunnerTaskFactory;
import org.compsysmed.ocsana.internal.ui.results.OCSANAResultsPanel;

import org.compsysmed.ocsana.internal.util.fvs.FVSBundle;
import org.compsysmed.ocsana.internal.util.fvs.FVSBundleBuilder;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.work.swing.PanelTaskManager;

public class FVSmenu extends AbstractCyAction{

	private CyApplicationManager cyApplicationManager;
    private final PanelTaskManager panelTaskManager;

    private FVSBundleBuilder fvsBundleBuilder;
    private final FVSResultsPanel fvsresultsPanel;
	public FVSmenu(CyApplicationManager cyApplicationManager, CySwingApplication cySwingApplication, FVSResultsPanel fvsresultsPanel, PanelTaskManager panelTaskManager) {
		super("without source nodes");

        Objects.requireNonNull(cyApplicationManager, "Cytoscape application manager cannot be null");
        this.cyApplicationManager = cyApplicationManager;
        
        Objects.requireNonNull(fvsresultsPanel, "Results panel cannot be null");
        this.fvsresultsPanel = fvsresultsPanel;
        
        Objects.requireNonNull(panelTaskManager, "Panel task manager cannot be null");
        this.panelTaskManager = panelTaskManager;
        setPreferredMenu("Apps.OCSANA.Feedback Vertex Set Control");
        
        
	}
	public FVSBundle getFVSBundle () {
        //updateFCBundleBuilder();
        return fvsBundleBuilder.getFVSBundle();
    }
	@Override
	public void actionPerformed(ActionEvent e) {
		runTask();
		
	}
	private void runTask () {
		new FVSwindow(cyApplicationManager, fvsresultsPanel,panelTaskManager);
		
    }

}