/**
 * Panel to contain OCSANA controls
 *
 * Copyright Vera-Licona Research Group (C) 2015
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.ui.control;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.*;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.Icon;
import javax.swing.Scrollable;

// Cytoscape imports
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.work.swing.PanelTaskManager;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.events.SetCurrentNetworkEvent;
import org.cytoscape.application.events.SetCurrentNetworkListener;

import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;

// OCSANA imports
import org.compsysmed.ocsana.internal.tasks.runner.RunnerTaskFactory;

import org.compsysmed.ocsana.internal.util.context.ContextBundle;
import org.compsysmed.ocsana.internal.util.context.ContextBundleBuilder;
import org.compsysmed.ocsana.internal.util.results.ResultsBundle;

import org.compsysmed.ocsana.internal.ui.results.OCSANAResultsPanel;

import org.compsysmed.ocsana.internal.ui.control.subpanels.*;

/**
 * Panel to configure and run OCSANA
 **/
public class OCSANAControlPanel
    extends JPanel
    implements CytoPanelComponent, SetCurrentNetworkListener {
    // Cytoscape pieces and parts
    private final CyApplicationManager cyApplicationManager;
    private final PanelTaskManager panelTaskManager;
    private final CytoPanel cyControlPanel;

    // Other parts of OCSANA
    private final OCSANAResultsPanel resultsPanel;

    // Internal data
    private ContextBundleBuilder contextBundleBuilder;

    // UI elements
    private Collection<AbstractControlSubpanel> subpanels;
    private NetworkConfigurationSubpanel networkConfigSubpanel;
    private PathFindingSubpanel pathFindingSubpanel;
    private MHSSubpanel mhsSubpanel;

    /**
     * Constructor
     **/
    public OCSANAControlPanel (CyApplicationManager cyApplicationManager,
                               CytoPanel cyControlPanel,
                               OCSANAResultsPanel resultsPanel,
                               PanelTaskManager panelTaskManager) {
        super();

        Objects.requireNonNull(cyApplicationManager, "Cytoscape application manager cannot be null");
        this.cyApplicationManager = cyApplicationManager;

        Objects.requireNonNull(cyControlPanel, "Cytoscape control panel cannot be null");
        this.cyControlPanel = cyControlPanel;

        Objects.requireNonNull(resultsPanel, "Results panel cannot be null");
        this.resultsPanel = resultsPanel;

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

    /**
     * (Re)build the panel to reflect a specified network
     *
     * @param network  the network
     **/
    private void handleNewNetwork (CyNetwork network) {
        if (network == null) {
            return;
        }

        contextBundleBuilder = new ContextBundleBuilder(network);

        subpanels = new ArrayList<>();

        resultsPanel.reset();
        buildPanel(network);
    }

    private void buildPanel (CyNetwork network) {
        removeAll();

        if (network == null) {
            return;
        }

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JPanel tunablePanel = getContextBundleBuilderPanel();
        JScrollPane contentScrollPane = new JScrollPane(tunablePanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(contentScrollPane);

        JButton runButton = new JButton("Run OCSANA calculations");
        add(runButton);

        runButton.addActionListener(new ActionListener() {
        	
                @Override
                public void actionPerformed (ActionEvent e) {
                    runTask();
                }
            });

        revalidate();
        repaint();
    }

    /**
     * Retrieve the ContextBundle corresponding to the current
     * settings in the UI
     **/
    public ContextBundle getContextBundle () {
        updateContextBundleBuilder();
        return contextBundleBuilder.getContextBundle();
    }

    /**
     * Update the ContextBundleBuilder with the latest changes in the UI
     **/
    public void updateContextBundleBuilder () {
        for (AbstractControlSubpanel subpanel: subpanels) {
            subpanel.updateContextBuilder();
        }
    }

    /**
     * Build a panel with the UI elements for the ContextBundleBuilder
     **/
    private JPanel getContextBundleBuilderPanel () {
        JPanel panel = new ScrollablePanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        networkConfigSubpanel = new NetworkConfigurationSubpanel(this, contextBundleBuilder, panelTaskManager);
        panel.add(networkConfigSubpanel);
        subpanels.add(networkConfigSubpanel);

        pathFindingSubpanel = new PathFindingSubpanel(this, contextBundleBuilder, panelTaskManager);
        panel.add(pathFindingSubpanel);
        subpanels.add(pathFindingSubpanel);

        mhsSubpanel = new MHSSubpanel(this, contextBundleBuilder, panelTaskManager);
        panel.add(mhsSubpanel);
        subpanels.add(mhsSubpanel);


        return panel;
    }

    /**
     * Launch the task
     **/
    private void runTask () {
        RunnerTaskFactory runnerTaskFactory = new RunnerTaskFactory(panelTaskManager, getContextBundle(), resultsPanel);
        panelTaskManager.execute(runnerTaskFactory.createTaskIterator());
    }

    // Helper functions to get information about the panel
    /**
     * Get the results panel component
     */
    @Override
    public Component getComponent() {
        return this;
    }

    /**
     * Get the results panel name
     */
    @Override
    public CytoPanelName getCytoPanelName () {
        return CytoPanelName.WEST;
    }

    /**
     * Get the results panel title
     */
    @Override
    public String getTitle() {
        return "OCSANA";
    }

    /**
     * Get the results panel icon
     */
    @Override
    public Icon getIcon() {
        return null;
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
