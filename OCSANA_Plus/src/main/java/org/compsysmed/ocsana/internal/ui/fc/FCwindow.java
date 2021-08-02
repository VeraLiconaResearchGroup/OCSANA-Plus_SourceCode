package org.compsysmed.ocsana.internal.ui.fc;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import javax.swing.*;

import org.compsysmed.ocsana.internal.tasks.fc.FCRunnerTaskFactory;
import org.compsysmed.ocsana.internal.tasks.sfa.SFARunnerTaskFactory;
import org.compsysmed.ocsana.internal.tasks.sfa.SFATaskFactory;
import org.compsysmed.ocsana.internal.ui.fc.subpanels.FCNetworkConfigurationSubpanel;
import org.compsysmed.ocsana.internal.ui.results.OCSANAResultsPanel;
import org.compsysmed.ocsana.internal.ui.sfa.SFAwindow.ScrollablePanel;
import org.compsysmed.ocsana.internal.ui.sfa.subpanels.AbstractSFAwindow;
import org.compsysmed.ocsana.internal.ui.sfa.subpanels.SFANetworkConfigurationSubpanel;
import org.compsysmed.ocsana.internal.ui.sfaresults.SFAResultsPanel;
import org.compsysmed.ocsana.internal.ui.sfaresults.SFAResultsSubpanel;
import org.compsysmed.ocsana.internal.util.fc.FCBundle;
import org.compsysmed.ocsana.internal.util.fc.FCBundleBuilder;
//import org.compsysmed.ocsana.internal.ui.sfa.subpanels.SFAResultsSubpanel;
import org.compsysmed.ocsana.internal.util.sfa.SFABundle;
import org.compsysmed.ocsana.internal.util.sfa.SFABundleBuilder;
//import org.compsysmed.ocsana.internal.util.sfa.SFAResultsBundle;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.events.SetCurrentNetworkEvent;
import org.cytoscape.application.events.SetCurrentNetworkListener;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.work.swing.PanelTaskManager;  
public class FCwindow extends JPanel 
implements SetCurrentNetworkListener{  
    private FCNetworkConfigurationSubpanel fcnetworkConfigSubpanel;
    //internal data
    private FCBundleBuilder fcBundleBuilder;
    
    //UI
    private FCResultsPanel fcresultsPanel;
    private final PanelTaskManager panelTaskManager;
	private CyApplicationManager cyApplicationManager;

	FCwindow(CyApplicationManager cyApplicationManager,
            FCResultsPanel fcresultsPanel, PanelTaskManager panelTaskManager)  
        {  
		super();

        Objects.requireNonNull(cyApplicationManager, "Cytoscape application manager cannot be null");
        this.cyApplicationManager = cyApplicationManager;

        Objects.requireNonNull(fcresultsPanel, "Results panel cannot be null");
        this.fcresultsPanel = fcresultsPanel;

        Objects.requireNonNull(panelTaskManager, "Panel task manager cannot be null");
        this.panelTaskManager = panelTaskManager;
        
        handleNewNetwork(cyApplicationManager.getCurrentNetwork());
    }
	
	/**
     * (Re)build the panel in response to the selection of a network
     **/
    @Override
    public void handleEvent (SetCurrentNetworkEvent e) {
        handleNewNetwork(e.getNetwork());
    }
	
	private void handleNewNetwork (CyNetwork network) {
        if (network == null) {
            return;
        }

        fcBundleBuilder = new FCBundleBuilder(network);

        buildPanel(network);
    }
        


	private void buildPanel (CyNetwork network) {
        removeAll();

        if (network == null) {
            return;
        }

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        JFrame f= new JFrame("Feedback Vertex Set Control"); 
        JPanel tunablePanel = getFCBundleBuilderPanel();
        JScrollPane contentScrollPane = new JScrollPane(tunablePanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        contentScrollPane.setBounds(40,80,200,200);
        f.add(contentScrollPane, BorderLayout.NORTH);
       
        JPanel runpanel = new JPanel();
        JButton runButton = new JButton("Identify Feedback Vertex Set Control Set");
        
        runpanel.add(runButton, BorderLayout.SOUTH);
        f.add(runpanel);

        runButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed (ActionEvent e) {
                    runTask();
                    f.dispose();
                                
                }
            });
       
        
        f.pack();
        f.setVisible(true);
    }    
	 /**
     * Retrieve the ContextBundle corresponding to the current
     * settings in the UI
     **/
    public FCBundle getFCBundle () {
        updateFCBundleBuilder();
        return fcBundleBuilder.getFCBundle();
    }
    
	
	/**
     * Update the SFABundleBuilder with the latest changes in the UI
     **/
    public void updateFCBundleBuilder () {
        {
        	fcnetworkConfigSubpanel.updatefcBuilder();
        }
    }
    private JPanel getFCBundleBuilderPanel () {
        JPanel panel = new ScrollablePanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        fcnetworkConfigSubpanel = new FCNetworkConfigurationSubpanel(this, fcBundleBuilder,panelTaskManager);
        panel.add(fcnetworkConfigSubpanel);
        return panel;
    }
	
	
	
	/**
     * Launch the task
     **/
    private void runTask () {
    	FCRunnerTaskFactory fcRunnerTaskFactory= new FCRunnerTaskFactory(panelTaskManager,getFCBundle(), fcresultsPanel);
        panelTaskManager.execute(fcRunnerTaskFactory.createTaskIterator());
    }

    

    
    public static class ScrollablePanel
    extends JPanel
    implements Scrollable {
    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 20;
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 60;
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return true;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        return (getParent().getHeight() > getPreferredSize().height);
    }
     
    }
}
