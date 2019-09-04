package org.compsysmed.ocsana.internal.ui.fc;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Properties;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.compsysmed.ocsana.internal.tasks.fc.FCRunnerTaskFactory;
import org.compsysmed.ocsana.internal.tasks.sfa.SFARunnerTaskFactory;
import org.compsysmed.ocsana.internal.ui.results.OCSANAResultsPanel;
import org.compsysmed.ocsana.internal.ui.sfa.SFAwindow;
import org.compsysmed.ocsana.internal.ui.sfaresults.SFAResultsPanel;
import org.compsysmed.ocsana.internal.util.context.ContextBundleBuilder;
import org.compsysmed.ocsana.internal.util.fc.FCBundle;
import org.compsysmed.ocsana.internal.util.fc.FCBundleBuilder;
import org.compsysmed.ocsana.internal.util.sfa.SFABundle;
import org.compsysmed.ocsana.internal.util.sfa.SFABundleBuilder;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.events.SetCurrentNetworkListener;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.work.swing.PanelTaskManager;

public class FCmenu extends AbstractCyAction{

	private CyApplicationManager cyApplicationManager;
    private final PanelTaskManager panelTaskManager;

    private FCBundleBuilder fcBundleBuilder;
    private final FCResultsPanel fcresultsPanel;
	public FCmenu(CyApplicationManager cyApplicationManager, CySwingApplication cySwingApplication, FCResultsPanel fcresultsPanel, PanelTaskManager panelTaskManager) {
		super("with source nodes");

        Objects.requireNonNull(cyApplicationManager, "Cytoscape application manager cannot be null");
        this.cyApplicationManager = cyApplicationManager;
        
        Objects.requireNonNull(fcresultsPanel, "Results panel cannot be null");
        this.fcresultsPanel = fcresultsPanel;
        
        Objects.requireNonNull(panelTaskManager, "Panel task manager cannot be null");
        this.panelTaskManager = panelTaskManager;
        setPreferredMenu("Apps.OCSANA.Feedback Vertex Set Control");
        
        
	}
	public FCBundle getFCBundle () {
        //updateFCBundleBuilder();
        return fcBundleBuilder.getFCBundle();
    }
	@Override
	public void actionPerformed(ActionEvent e) {
		runTask();
		
	}
	private void runTask () {
		new FCwindow(cyApplicationManager, fcresultsPanel,panelTaskManager);
		
    }

}