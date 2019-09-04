/**
 * Subpanel containing network configuration for OCSANA
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import java.util.*;

// Cytoscape imports
import org.cytoscape.model.CyColumn;

import org.cytoscape.work.swing.PanelTaskManager;

// OCSANA imports
import org.compsysmed.ocsana.internal.util.context.ContextBundleBuilder;

import org.compsysmed.ocsana.internal.ui.control.OCSANAControlPanel;

import org.compsysmed.ocsana.internal.ui.control.widgets.*;

/**
 * Subpanel for user configuration of network parameters
 **/
public class NetworkConfigurationSubpanel
    extends AbstractControlSubpanel
    implements ActionListener {
    private final ContextBundleBuilder contextBundleBuilder;
    private final PanelTaskManager taskManager;

    // UI elements
    private JPanel modePanel;
    private JComboBox<SelectionMode> nodeSelectionModeSelecter;

    private JPanel columnPanel;
    private JComboBox<CyColumn> nodeNameColumnSelecter;
    private JComboBox<CyColumn> nodeIDColumnSelecter;

    private JPanel nodeSetsPanel;

    private AbstractNodeSetSelecter sourceNodeSelecter;
    private AbstractNodeSetSelecter targetNodeSelecter;
    private AbstractNodeSetSelecter offTargetNodeSelecter;

    /**
     * Constructor
     *
     * @param contextBundleBuilder  the builder for context bundles
     * @param taskManager  a PanelTaskManager to provide @Tunable panels
     **/
    public NetworkConfigurationSubpanel (OCSANAControlPanel controlPanel,
                                         ContextBundleBuilder contextBundleBuilder,
                                         PanelTaskManager taskManager) {
        super(controlPanel);

        // Initial setup
        this.contextBundleBuilder = contextBundleBuilder;
        this.taskManager = taskManager;

        setStandardLayout(this);

        // Selection mode selection widgets
        JLabel header = makeHeader("Configure network processing");
        add(header);

        modePanel = new JPanel();
        setStandardLayout(modePanel);
        add(modePanel);

        modePanel.add(new JLabel("Node selection mode"));

        SelectionMode[] modes = {SelectionMode.listMode, SelectionMode.stringMode};
        nodeSelectionModeSelecter = new JComboBox<>(modes);
        nodeSelectionModeSelecter.addActionListener(this);
        nodeSelectionModeSelecter.setToolTipText(String.format("Choose %s to select nodes with a multi-select combo box; choose %s to enter the list as text.", SelectionMode.listMode, SelectionMode.stringMode));
        modePanel.add(nodeSelectionModeSelecter);

        columnPanel = new JPanel();
        setStandardLayout(columnPanel);
        add(columnPanel);

        CyColumn[] nodeNameColumns = contextBundleBuilder.getNetwork().getDefaultNodeTable().getColumns().stream().toArray(CyColumn[]::new);
        columnPanel.add(new JLabel("Select node name column"));
        nodeNameColumnSelecter = new JComboBox<>(nodeNameColumns);
        nodeNameColumnSelecter.addActionListener(this);
        nodeNameColumnSelecter.setToolTipText("Each node will be assigned a name taken from the selected column of the node table. In particular, this will be the name used in the selecters below.");
        columnPanel.add(nodeNameColumnSelecter);

        // Node set selection widgets
        nodeSetsPanel = new JPanel();
        setStandardLayout(nodeSetsPanel);

        add(nodeSetsPanel);

        populateSetsPanelWithListSelecters(nodeSetsPanel);

        // Edge processor
        JPanel edgepanel= new JPanel();
        edgepanel.setLayout(new BoxLayout(edgepanel,BoxLayout.Y_AXIS));
        JLabel edgehead= new JLabel("Note: For Edge direction, please include");
        JLabel edgetext2= new JLabel("an \"interaction\" column that specifies positive");
        JLabel edgetext3= new JLabel("edges as \"activates\" and negative edges as \"inhibits\".");
        JLabel edgetext4= new JLabel("Otherwise, all edges will be considered positive.");
        edgepanel.add(edgehead); 
        edgepanel.add(edgetext2);
        edgepanel.add(edgetext3);
        edgepanel.add(edgetext4);
        add(edgepanel);
    }

    @Override
    public void actionPerformed (ActionEvent e) {
        updateContextBuilder();

        if (e.getSource().equals(nodeNameColumnSelecter)) {
            rebuildNodeSetSelecters();
        } else if (e.getSource().equals(nodeIDColumnSelecter)) {
            // Do nothing
        } else if (e.getSource().equals(nodeSelectionModeSelecter)) {
            rebuildNodeSetSelecters();
        } else {
            throw new IllegalStateException("Unknown source of action event: " + e);
        }
    }

    private void rebuildNodeSetSelecters () {
        SelectionMode mode = (SelectionMode) nodeSelectionModeSelecter.getSelectedItem();
        switch (mode) {
        case listMode:
            populateSetsPanelWithListSelecters(nodeSetsPanel);
            break;

        case stringMode:
            populateSetsPanelWithStringSelecters(nodeSetsPanel);
            break;

        default:
            throw new IllegalStateException("Unknown selection mode selected");
        }
    }


    private void populateSetsPanelWithListSelecters (JPanel nodeSetsPanel) {
        nodeSetsPanel.removeAll();

        if (sourceNodeSelecter == null) {
            sourceNodeSelecter = new ListNodeSetSelecter("Source nodes", new HashSet<>(contextBundleBuilder.getNetwork().getNodeList()), contextBundleBuilder.getNodeHandler());
        } else {
            sourceNodeSelecter = new ListNodeSetSelecter(sourceNodeSelecter, contextBundleBuilder.getNodeHandler());
        }
        nodeSetsPanel.add(sourceNodeSelecter);

        if (targetNodeSelecter == null) {
            targetNodeSelecter = new ListNodeSetSelecter("Target nodes", new HashSet<>(contextBundleBuilder.getNetwork().getNodeList()), contextBundleBuilder.getNodeHandler());
        } else {
            targetNodeSelecter = new ListNodeSetSelecter(targetNodeSelecter, contextBundleBuilder.getNodeHandler());
        }
        nodeSetsPanel.add(targetNodeSelecter);

        if (offTargetNodeSelecter == null) {
            offTargetNodeSelecter = new ListNodeSetSelecter("Off-target nodes", new HashSet<>(contextBundleBuilder.getNetwork().getNodeList()), contextBundleBuilder.getNodeHandler());
        } else {
            offTargetNodeSelecter = new ListNodeSetSelecter(offTargetNodeSelecter, contextBundleBuilder.getNodeHandler());
        }
        nodeSetsPanel.add(offTargetNodeSelecter);

        nodeSetsPanel.revalidate();
        nodeSetsPanel.repaint();
    }

    private void populateSetsPanelWithStringSelecters (JPanel nodeSetsPanel) {
        nodeSetsPanel.removeAll();

        if (sourceNodeSelecter == null) {
            sourceNodeSelecter = new StringNodeSetSelecter("Source nodes", new HashSet<>(contextBundleBuilder.getNetwork().getNodeList()), contextBundleBuilder.getNodeHandler());
        } else {
            sourceNodeSelecter = new StringNodeSetSelecter(sourceNodeSelecter, contextBundleBuilder.getNodeHandler());
        }
        nodeSetsPanel.add(sourceNodeSelecter);

        if (targetNodeSelecter == null) {
            targetNodeSelecter = new StringNodeSetSelecter("Target nodes", new HashSet<>(contextBundleBuilder.getNetwork().getNodeList()), contextBundleBuilder.getNodeHandler());
        } else {
            targetNodeSelecter = new StringNodeSetSelecter(targetNodeSelecter, contextBundleBuilder.getNodeHandler());
        }
        nodeSetsPanel.add(targetNodeSelecter);

        if (offTargetNodeSelecter == null) {
            offTargetNodeSelecter = new StringNodeSetSelecter("Off-target nodes", new HashSet<>(contextBundleBuilder.getNetwork().getNodeList()), contextBundleBuilder.getNodeHandler());
        } else {
            offTargetNodeSelecter = new StringNodeSetSelecter(offTargetNodeSelecter, contextBundleBuilder.getNodeHandler());
        }
        nodeSetsPanel.add(offTargetNodeSelecter);

        nodeSetsPanel.revalidate();
        nodeSetsPanel.repaint();
    }

    @Override
    public void updateContextBuilder () {
        contextBundleBuilder.setSourceNodes(sourceNodeSelecter.getSelectedNodes());
        contextBundleBuilder.setTargetNodes(targetNodeSelecter.getSelectedNodes());
        contextBundleBuilder.setOffTargetNodes(offTargetNodeSelecter.getSelectedNodes());

        CyColumn nodeNameColumn = (CyColumn) nodeNameColumnSelecter.getSelectedItem();
        contextBundleBuilder.getNodeHandler().setNodeNameColumn(nodeNameColumn);

        
    }

    private static enum SelectionMode {
        listMode("List"),
        stringMode("String");

        private final String label;
        private SelectionMode (String label) {
            this.label = label;
        }

        public String toString () {
            return label;
        }
    }
}
