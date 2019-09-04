package org.compsysmed.ocsana.internal.ui.sfa;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import javax.swing.*;


import org.compsysmed.ocsana.internal.tasks.sfa.SFARunnerTaskFactory;
import org.compsysmed.ocsana.internal.tasks.sfa.SFATaskFactory;
import org.compsysmed.ocsana.internal.ui.results.OCSANAResultsPanel;
import org.compsysmed.ocsana.internal.ui.sfa.subpanels.AbstractSFAwindow;
import org.compsysmed.ocsana.internal.ui.sfa.subpanels.SFANetworkConfigurationSubpanel;
import org.compsysmed.ocsana.internal.ui.sfaresults.SFAResultsPanel;
import org.compsysmed.ocsana.internal.ui.sfaresults.SFAResultsSubpanel;
//import org.compsysmed.ocsana.internal.ui.sfa.subpanels.SFAResultsSubpanel;
import org.compsysmed.ocsana.internal.util.sfa.SFABundle;
import org.compsysmed.ocsana.internal.util.sfa.SFABundleBuilder;
//import org.compsysmed.ocsana.internal.util.sfa.SFAResultsBundle;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.events.SetCurrentNetworkEvent;
import org.cytoscape.application.events.SetCurrentNetworkListener;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.work.swing.PanelTaskManager;  
public class SFAwindow extends JPanel 
implements SetCurrentNetworkListener{  
    private SFANetworkConfigurationSubpanel sfanetworkConfigSubpanel;
    //internal data
    private SFABundleBuilder sfaBundleBuilder;
    
    //UI
    private SFAResultsPanel sfaresultsPanel;
    private final PanelTaskManager panelTaskManager;
	private CyApplicationManager cyApplicationManager;

	SFAwindow(CyApplicationManager cyApplicationManager,
            SFAResultsPanel sfaresultsPanel, PanelTaskManager panelTaskManager)  
        {  
		super();

        Objects.requireNonNull(cyApplicationManager, "Cytoscape application manager cannot be null");
        this.cyApplicationManager = cyApplicationManager;

        Objects.requireNonNull(sfaresultsPanel, "Results panel cannot be null");
        this.sfaresultsPanel = sfaresultsPanel;

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

        sfaBundleBuilder = new SFABundleBuilder(network);

       

        buildPanel(network);
    }
        


	private void buildPanel (CyNetwork network) {
        removeAll();

        if (network == null) {
            return;
        }

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        JFrame f= new JFrame("Signal Flow Analysis"); 
        JPanel tunablePanel = getSFABundleBuilderPanel();
        JScrollPane contentScrollPane = new JScrollPane(tunablePanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        //contentScrollPane.setBounds(40,80,200,200);
        f.add(contentScrollPane, BorderLayout.NORTH);
        
        
        JPanel runpanel = new JPanel();
        JButton runButton = new JButton("Run Signal Flow Analysis");
        
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
    public SFABundle getSFABundle () {
        updateSFABundleBuilder();
        return sfaBundleBuilder.getSFABundle();
    }
    
	
	/**
     * Update the SFABundleBuilder with the latest changes in the UI
     **/
    public void updateSFABundleBuilder () {
        {
        	sfanetworkConfigSubpanel.updatesfaBuilder();
        }
    }

	private JPanel getSFABundleBuilderPanel () {
        JPanel panel = new ScrollablePanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        sfanetworkConfigSubpanel = new SFANetworkConfigurationSubpanel(this, sfaBundleBuilder,panelTaskManager);
        panel.add(sfanetworkConfigSubpanel);
        return panel;
    }
	
	/**
     * Launch the task
     **/
    private void runTask () {
        SFARunnerTaskFactory sfaRunnerTaskFactory = new SFARunnerTaskFactory(panelTaskManager,getSFABundle(), sfaresultsPanel);
        panelTaskManager.execute(sfaRunnerTaskFactory.createTaskIterator());
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
