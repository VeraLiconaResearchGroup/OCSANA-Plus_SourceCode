package org.compsysmed.ocsana.internal.ui.fvs;

import java.awt.event.ActionEvent;
import java.util.Objects;



import org.compsysmed.ocsana.internal.tasks.fc.FCRunnerTaskFactory;
import org.compsysmed.ocsana.internal.tasks.sfa.SFARunnerTaskFactory;
import org.compsysmed.ocsana.internal.ui.fc.FCResultsPanel;
import org.compsysmed.ocsana.internal.ui.results.OCSANAResultsPanel;
import org.compsysmed.ocsana.internal.util.fc.FCBundle;
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
    private final FCResultsPanel fcresultsPanel;
	public FVSmenu(CyApplicationManager cyApplicationManager, CySwingApplication cySwingApplication, FCResultsPanel fcresultsPanel, PanelTaskManager panelTaskManager) {
		super("without source nodes");

        Objects.requireNonNull(cyApplicationManager, "Cytoscape application manager cannot be null");
        this.cyApplicationManager = cyApplicationManager;
        
        Objects.requireNonNull(fcresultsPanel, "Results panel cannot be null");
        this.fcresultsPanel = fcresultsPanel;
        
        Objects.requireNonNull(panelTaskManager, "Panel task manager cannot be null");
        this.panelTaskManager = panelTaskManager;
        setPreferredMenu("Apps.OCSANA.Feedback Vertex Set Control");
        
        
	}
	public FCBundle getFVSBundle () {
        //updateFCBundleBuilder();
        return fvsBundleBuilder.getFCBundle();
    }
	@Override
	public void actionPerformed(ActionEvent e) {
		runTask();
		
	}
	private void runTask () {
		new FVSwindow(cyApplicationManager, fcresultsPanel,panelTaskManager);
		
    }

}