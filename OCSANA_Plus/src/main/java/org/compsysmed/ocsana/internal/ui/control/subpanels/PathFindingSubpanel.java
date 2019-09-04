/**
 * Subpanel configuring path-finding algorithm in OCSANA
 *
 * Copyright Vera-Licona Research Group (C) 2016
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.ui.control.subpanels;

// Java imports
import java.util.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

// Cytoscape imports
import org.cytoscape.work.swing.PanelTaskManager;

// OCSANA imports
import org.compsysmed.ocsana.internal.algorithms.path.*;

import org.compsysmed.ocsana.internal.ui.control.OCSANAControlPanel;

import org.compsysmed.ocsana.internal.util.context.ContextBundleBuilder;

/**
 * Subpanel for user configuration of path-finding algorithm
 **/
public class PathFindingSubpanel
    extends AbstractControlSubpanel
    implements ActionListener {
    private ContextBundleBuilder contextBundleBuilder;
    private PanelTaskManager taskManager;

    // UI elements
    private JPanel algSelectionPanel;
    private JComboBox<AbstractPathFindingAlgorithm> algorithmSelecter;

    private JPanel tunablePanel;

    /**
     * Constructor
     *
     * @param contextBundleBuilder  the context bundle builder
     * @param taskManager  a PanelTaskManager to provide @Tunable panels
     **/
    public PathFindingSubpanel (OCSANAControlPanel controlPanel,
                                ContextBundleBuilder contextBundleBuilder,
                                PanelTaskManager taskManager) {
        super(controlPanel);

        // Initial setup
        this.contextBundleBuilder = contextBundleBuilder;
        this.taskManager = taskManager;

        setStandardLayout(this);

        add(makeHeader("Configure path-finding"));

        // Algorithm selecter
        algSelectionPanel = new JPanel();
        setStandardLayout(algSelectionPanel);
        add(algSelectionPanel);

        algSelectionPanel.add(new JLabel("Algorithm:"));

        List<AbstractPathFindingAlgorithm> algorithms = new ArrayList<>();
        algorithms.add(new AllNonSelfIntersectingPathsAlgorithm(contextBundleBuilder.getNetwork()));
        algorithms.add(new ShortestPathsAlgorithm(contextBundleBuilder.getNetwork()));

        algorithmSelecter = new JComboBox<>(algorithms.toArray(new AbstractPathFindingAlgorithm[algorithms.size()]));
        algSelectionPanel.add(algorithmSelecter);
        algorithmSelecter.addActionListener(this);

        // Algorithm configuration panel
        tunablePanel = new JPanel();
        setStandardLayout(tunablePanel);
        add(tunablePanel);

        updateTunablePanel();
    }

    private void updateTunablePanel () {
        tunablePanel.removeAll();

        tunablePanel.add(taskManager.getConfiguration(null, getAlgorithm()));

        tunablePanel.revalidate();
        tunablePanel.repaint();
    }

    private AbstractPathFindingAlgorithm getAlgorithm () {
        return (AbstractPathFindingAlgorithm) algorithmSelecter.getSelectedItem();
    }

    @Override
    public void actionPerformed (ActionEvent e) {
        updateTunablePanel();
    }

    @Override
    public void updateContextBuilder () {
        contextBundleBuilder.setPathFindingAlgorithm(getAlgorithm());
    }
}