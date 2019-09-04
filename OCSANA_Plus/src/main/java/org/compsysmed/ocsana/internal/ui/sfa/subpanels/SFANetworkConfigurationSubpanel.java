package org.compsysmed.ocsana.internal.ui.sfa.subpanels;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.compsysmed.ocsana.internal.util.sfa.SFABundleBuilder;
import org.cytoscape.model.CyColumn;
import org.cytoscape.work.swing.PanelTaskManager;
import org.compsysmed.ocsana.internal.ui.sfa.SFAwindow;
import org.compsysmed.ocsana.internal.ui.sfa.subpanels.AbstractSFAwindow;
import org.compsysmed.ocsana.internal.ui.control.widgets.*;

/**
 * Subpanel for user configuration of network parameters
 **/
public class SFANetworkConfigurationSubpanel
    extends AbstractSFAwindow
    implements ActionListener {
    private final SFABundleBuilder sfaBundleBuilder;
    private final PanelTaskManager taskManager;


    // UI elements
    private JPanel modePanel;
    private JComboBox<SelectionMode> nodeSelectionModeSelecter;

    private JPanel columnPanel;
    private JComboBox<CyColumn> nodeNameColumnSelecter;

    private JPanel nodeSetsPanel;

    private AbstractNodeSetSelecter acitvatedNodeSelecter;
    private AbstractNodeSetSelecter inhibitedNodeSelecter;

    /**
     * Constructor
     *
     * @param sfaBundleBuilder  the builder for context bundles
     * @param panelTaskManager 
     **/
    public SFANetworkConfigurationSubpanel (SFAwindow sfawindow,
                                         SFABundleBuilder sfaBundleBuilder, PanelTaskManager taskManager) {
        super(sfawindow);

        // Initial setup
        this.sfaBundleBuilder = sfaBundleBuilder;
        this.taskManager = taskManager;

        setStandardLayout(this);

    /*    // Selection mode selection widgets
        JLabel header = makeHeader("Configure network processing");
        header.setAlignmentY(TOP_ALIGNMENT);
        //header.setHorizontalAlignment(JLabel.NORTH);
        add(header);*/
        

        modePanel = new JPanel();
        add(modePanel);
        modePanel.setLayout(new BoxLayout(modePanel, BoxLayout.Y_AXIS));
        modePanel.add(new JLabel("Node selection mode"));

        SelectionMode[] modes = {SelectionMode.listMode, SelectionMode.stringMode};
        nodeSelectionModeSelecter = new JComboBox<>(modes);
        nodeSelectionModeSelecter.setMaximumSize(nodeSelectionModeSelecter.getPreferredSize());
        nodeSelectionModeSelecter.addActionListener(this);
        nodeSelectionModeSelecter.setToolTipText(String.format("Choose %s to select nodes with a multi-select combo box; choose %s to enter the list as text.", SelectionMode.listMode, SelectionMode.stringMode));
        modePanel.add(nodeSelectionModeSelecter);
        nodeSelectionModeSelecter.setAlignmentY(Component.BOTTOM_ALIGNMENT);
        modePanel.setAlignmentY(BOTTOM_ALIGNMENT);
       
        columnPanel = new JPanel();
        //setStandardLayout(columnPanel);
        columnPanel.setLayout(new BoxLayout(columnPanel,BoxLayout.Y_AXIS));
        add(columnPanel);

        CyColumn[] nodeNameColumns = sfaBundleBuilder.getNetwork().getDefaultNodeTable().getColumns().stream().toArray(CyColumn[]::new);
        
        columnPanel.add(new JLabel("Select node name column",SwingConstants.CENTER));
       
        nodeNameColumnSelecter = new JComboBox<>(nodeNameColumns);
        nodeNameColumnSelecter.setMaximumSize(nodeNameColumnSelecter.getPreferredSize());
        nodeNameColumnSelecter.addActionListener(this);
        nodeNameColumnSelecter.setToolTipText("Each node will be assigned a name taken from the selected column of the node table. In particular, this will be the name used in the selecters below.");
        columnPanel.add(nodeNameColumnSelecter);
        nodeNameColumnSelecter.setAlignmentY(Component.BOTTOM_ALIGNMENT);
        columnPanel.setAlignmentY(BOTTOM_ALIGNMENT);
        
        
        // Node set selection widgets
        nodeSetsPanel = new JPanel();
        setStandardLayout(nodeSetsPanel);

        add(nodeSetsPanel);

        populateSetsPanelWithListSelecters(nodeSetsPanel);
        JPanel edgepanel= new JPanel();
        edgepanel.setLayout(new BoxLayout(edgepanel,BoxLayout.Y_AXIS));
        JLabel edgehead= new JLabel("Note: For Edge direction, please include");
        JLabel edgetext2= new JLabel("an \"interaction\" column that specifies positive");
        JLabel edgetext3= new JLabel("edges as \"activates\" and negative edges as \"inhibits\"");
        JLabel edgetext4= new JLabel("Otherwise, all edges will be considered positive");
        edgepanel.add(edgehead); 
        edgepanel.add(edgetext2);
        edgepanel.add(edgetext3);
        edgepanel.add(edgetext4);
       add(edgepanel);
    }

    @Override
    public void actionPerformed (ActionEvent e) {
        updatesfaBuilder();

        if (e.getSource().equals(nodeNameColumnSelecter)) {
            rebuildNodeSetSelecters();
        }  else if (e.getSource().equals(nodeSelectionModeSelecter)) {
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

        if (acitvatedNodeSelecter == null) {
            acitvatedNodeSelecter = new ListNodeSetSelecter("Activated nodes", new HashSet<>(sfaBundleBuilder.getNetwork().getNodeList()), sfaBundleBuilder.getNodeHandler());
        } else {
            acitvatedNodeSelecter = new ListNodeSetSelecter(acitvatedNodeSelecter, sfaBundleBuilder.getNodeHandler());
        }
        nodeSetsPanel.add(acitvatedNodeSelecter);
        nodeSetsPanel.add(Box.createHorizontalStrut(10));
        if (inhibitedNodeSelecter == null) {
            inhibitedNodeSelecter = new ListNodeSetSelecter("Inactivated nodes", new HashSet<>(sfaBundleBuilder.getNetwork().getNodeList()), sfaBundleBuilder.getNodeHandler());
        } else {
            inhibitedNodeSelecter = new ListNodeSetSelecter(inhibitedNodeSelecter, sfaBundleBuilder.getNodeHandler());
        }
        nodeSetsPanel.add(inhibitedNodeSelecter);
        nodeSetsPanel.add(Box.createHorizontalStrut(10));

        nodeSetsPanel.revalidate();
        nodeSetsPanel.repaint();
    }

    private void populateSetsPanelWithStringSelecters (JPanel nodeSetsPanel) {
        nodeSetsPanel.removeAll();

        if (acitvatedNodeSelecter == null) {
            acitvatedNodeSelecter = new StringNodeSetSelecter("Activated nodes", new HashSet<>(sfaBundleBuilder.getNetwork().getNodeList()), sfaBundleBuilder.getNodeHandler());
        } else {
            acitvatedNodeSelecter = new StringNodeSetSelecter(acitvatedNodeSelecter, sfaBundleBuilder.getNodeHandler());
        }
        nodeSetsPanel.add(acitvatedNodeSelecter);
        nodeSetsPanel.add(Box.createHorizontalStrut(10));
        if (inhibitedNodeSelecter == null) {
            inhibitedNodeSelecter = new StringNodeSetSelecter("Inhibited nodes", new HashSet<>(sfaBundleBuilder.getNetwork().getNodeList()), sfaBundleBuilder.getNodeHandler());
        } else {
            inhibitedNodeSelecter = new StringNodeSetSelecter(inhibitedNodeSelecter, sfaBundleBuilder.getNodeHandler());
        }
        nodeSetsPanel.add(inhibitedNodeSelecter);
        nodeSetsPanel.add(Box.createHorizontalStrut(10));


        nodeSetsPanel.revalidate();
        nodeSetsPanel.repaint();
    }

    @Override
    public void updatesfaBuilder () {
    	sfaBundleBuilder.setActivatedNodes(acitvatedNodeSelecter.getSelectedNodes());
    	sfaBundleBuilder.setInhibitedNodes(inhibitedNodeSelecter.getSelectedNodes());

        CyColumn nodeNameColumn = (CyColumn) nodeNameColumnSelecter.getSelectedItem();
        sfaBundleBuilder.getNodeHandler().setNodeNameColumn(nodeNameColumn);

       
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
