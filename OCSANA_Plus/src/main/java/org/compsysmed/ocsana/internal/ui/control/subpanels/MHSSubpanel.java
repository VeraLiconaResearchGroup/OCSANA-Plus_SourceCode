/**
 * Subpanel configuring MHS algorithm
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

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

// Cytoscape imports
import org.cytoscape.work.swing.PanelTaskManager;

// OCSANA imports
import org.compsysmed.ocsana.internal.algorithms.mhs.*;

import org.compsysmed.ocsana.internal.ui.control.OCSANAControlPanel;

import org.compsysmed.ocsana.internal.util.context.ContextBundleBuilder;

/**
 * Subpanel for user configuration of MHS algorithm
 **/
public class MHSSubpanel
    extends AbstractControlSubpanel
    implements ActionListener {
    private ContextBundleBuilder contextBundleBuilder;
    private PanelTaskManager taskManager;

    // UI elements
    private JPanel algSelectionPanel;
    private JComboBox<AbstractMHSAlgorithm> algorithmSelecter;
    private JCheckBox includeEndpointsInCIs;

    private JPanel tunablePanel;

    /**
     * Constructor
     *
     * @param contextBundleBuilder  the context bundle builder
     * @param taskManager  a PanelTaskManager to provide @Tunable panels
     **/
    public MHSSubpanel (OCSANAControlPanel controlPanel,
                        ContextBundleBuilder contextBundleBuilder,
                        PanelTaskManager taskManager) {
        super(controlPanel);

        // Initial setup
        this.contextBundleBuilder = contextBundleBuilder;
        this.taskManager = taskManager;

        setStandardLayout(this);

        add(makeHeader("Configure Combinations of Interventions (CIs) with Minimal Hitting Sets Discovery"));

        // Algorithm selecter
        algSelectionPanel = new JPanel();
        setStandardLayout(algSelectionPanel);
        add(algSelectionPanel);

        algSelectionPanel.add(new JLabel("Algorithm:"));

        List<AbstractMHSAlgorithm> algorithms = new ArrayList<>();
        
        algorithms.add(new RSAlgorithm());
        algorithms.add(new BergeAlgorithm());

        OCSANAGreedyAlgorithm greedyAlgorithm = new OCSANAGreedyAlgorithm(contextBundleBuilder.getNetwork());
        contextBundleBuilder.getOCSANAAlgorithm().addListener(greedyAlgorithm);
        algorithms.add(greedyAlgorithm);

        algorithmSelecter = new JComboBox<>(algorithms.toArray(new AbstractMHSAlgorithm[algorithms.size()]));
        algSelectionPanel.add(algorithmSelecter);
        algorithmSelecter.addActionListener(this);

        // CI configuration
        includeEndpointsInCIs = new JCheckBox("Allow sources in  CIs", contextBundleBuilder.getIncludeEndpointsInCIs());
        add(includeEndpointsInCIs);

        // Algorithm configuration panel
        tunablePanel = new JPanel();
        setStandardLayout(tunablePanel);
        add(tunablePanel);

        updateTunablePanel();
    }

    private void updateTunablePanel () {
        tunablePanel.removeAll();

        JPanel content = taskManager.getConfiguration(null, getAlgorithm());
        if (content != null) {
            tunablePanel.add(content);
        }

        tunablePanel.revalidate();
        tunablePanel.repaint();
    }

    private AbstractMHSAlgorithm getAlgorithm () {
        return (AbstractMHSAlgorithm) algorithmSelecter.getSelectedItem();
    }

    @Override
    public void actionPerformed (ActionEvent e) {
        updateTunablePanel();
    }

    @Override
    public void updateContextBuilder () {
        contextBundleBuilder.setIncludeEndpointsInCIs(includeEndpointsInCIs.isSelected());
        contextBundleBuilder.setMHSAlgorithm(getAlgorithm());
    }
}
