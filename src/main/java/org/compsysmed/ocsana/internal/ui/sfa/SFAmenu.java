package org.compsysmed.ocsana.internal.ui.sfa;

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

import org.compsysmed.ocsana.internal.ui.results.OCSANAResultsPanel;
import org.compsysmed.ocsana.internal.ui.sfaresults.SFAResultsPanel;
import org.compsysmed.ocsana.internal.util.context.ContextBundleBuilder;
import org.compsysmed.ocsana.internal.util.sfa.SFABundleBuilder;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.events.SetCurrentNetworkListener;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.work.swing.PanelTaskManager;

public class SFAmenu extends AbstractCyAction{

	private CyApplicationManager cyApplicationManager;
    private final PanelTaskManager panelTaskManager;

    private SFABundleBuilder sfaBundleBuilder;
    private final SFAResultsPanel sfaresultsPanel;
	public SFAmenu(CyApplicationManager cyApplicationManager, CySwingApplication cySwingApplication, SFAResultsPanel sfaresultsPanel, PanelTaskManager panelTaskManager) {
		super("Signal Flow Analysis");

        Objects.requireNonNull(cyApplicationManager, "Cytoscape application manager cannot be null");
        this.cyApplicationManager = cyApplicationManager;
        
        Objects.requireNonNull(sfaresultsPanel, "Results panel cannot be null");
        this.sfaresultsPanel = sfaresultsPanel;
        
        Objects.requireNonNull(panelTaskManager, "Panel task manager cannot be null");
        this.panelTaskManager = panelTaskManager;
        setPreferredMenu("Apps.OCSANA");
        
        
	}
    
	@Override
	public void actionPerformed(ActionEvent e) {
		new SFAwindow(cyApplicationManager, sfaresultsPanel,panelTaskManager);
	
	}

}