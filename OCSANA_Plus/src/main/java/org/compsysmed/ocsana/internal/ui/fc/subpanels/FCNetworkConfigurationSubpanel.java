package org.compsysmed.ocsana.internal.ui.fc.subpanels;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.compsysmed.ocsana.internal.util.fc.FCBundleBuilder;
import org.compsysmed.ocsana.internal.util.sfa.SFABundleBuilder;
import org.cytoscape.model.CyColumn;
import org.cytoscape.work.swing.PanelTaskManager;
import org.compsysmed.ocsana.internal.ui.sfa.SFAwindow;
import org.compsysmed.ocsana.internal.ui.sfa.subpanels.AbstractSFAwindow;
import org.compsysmed.ocsana.internal.algorithms.fc.AbstractFCAlgorithm;
import org.compsysmed.ocsana.internal.algorithms.fc.FC;
import org.compsysmed.ocsana.internal.algorithms.mhs.AbstractMHSAlgorithm;
import org.compsysmed.ocsana.internal.algorithms.mhs.MMCSAlgorithm;
import org.compsysmed.ocsana.internal.algorithms.path.AbstractPathFindingAlgorithm;
import org.compsysmed.ocsana.internal.algorithms.path.AllNonSelfIntersectingPathsAlgorithm;
import org.compsysmed.ocsana.internal.algorithms.path.ShortestPathsAlgorithm;
import org.compsysmed.ocsana.internal.ui.control.widgets.*;
import org.compsysmed.ocsana.internal.ui.fc.FCwindow;

/**
 * Subpanel for user configuration of network parameters
 **/
public class FCNetworkConfigurationSubpanel
    extends AbstractFCwindow
    implements ActionListener {
    private final FCBundleBuilder fcBundleBuilder;
    private final PanelTaskManager taskManager;
    private AbstractFCAlgorithm fcalgo;

    // UI elements
  
    private JPanel tunablePanel;
    
    private JPanel columnPanel;
    private JComboBox<CyColumn> nodeNameColumnSelecter;

   
    /**
     * Constructor
     *
     * @param sfaBundleBuilder  the builder for context bundles
     * @param panelTaskManager 
     **/
    public FCNetworkConfigurationSubpanel (FCwindow fcwindow,
                                         FCBundleBuilder fcBundleBuilder, PanelTaskManager taskManager) {
        super(fcwindow);

        // Initial setup
        this.fcBundleBuilder = fcBundleBuilder;
        this.taskManager = taskManager;

        setStandardLayout(this);

   
        columnPanel = new JPanel();
        columnPanel.setLayout(new BoxLayout(columnPanel,BoxLayout.Y_AXIS));
        add(columnPanel);

        CyColumn[] nodeNameColumns = fcBundleBuilder.getNetwork().getDefaultNodeTable().getColumns().stream().toArray(CyColumn[]::new);
        columnPanel.add(new JLabel("Select node name column",SwingConstants.LEFT));
       
        nodeNameColumnSelecter = new JComboBox<>(nodeNameColumns);
        nodeNameColumnSelecter.setMaximumSize(nodeNameColumnSelecter.getPreferredSize());
        nodeNameColumnSelecter.addActionListener(this);
        nodeNameColumnSelecter.setToolTipText("Each node will be assigned a name taken from the selected column of the node table. In particular, this will be the name used in the selecters below.");
        columnPanel.add(nodeNameColumnSelecter);
        nodeNameColumnSelecter.setAlignmentY(Component.BOTTOM_ALIGNMENT);
        columnPanel.setAlignmentY(BOTTOM_ALIGNMENT);
        
        JPanel commentPanel = new JPanel();
        commentPanel.setLayout(new BoxLayout(commentPanel,BoxLayout.Y_AXIS));
        commentPanel.add(new JLabel("For larger networks (nodes >100, edges>300)"));
        commentPanel.add(new JLabel("\nidentifying all minimal FVSes may be time/memory consuming."));
        commentPanel.add(new JLabel("\nPlease enter the maximum number of FVSes to be identified."));
        add(commentPanel);
        
     // Algorithm configuration panel
        tunablePanel = new JPanel();
        
        setStandardLayout(tunablePanel);
        add(tunablePanel);
        updateTunablePanel();
       
    }

    @Override
    public void actionPerformed (ActionEvent e) {
    	updateTunablePanel();
    	updatefcBuilder();
      
    }

    @Override
    public void updatefcBuilder () {
        CyColumn nodeNameColumn = (CyColumn) nodeNameColumnSelecter.getSelectedItem();
        fcBundleBuilder.getNodeHandler().setNodeNameColumn(nodeNameColumn);
        fcBundleBuilder.getFCalgorithm();
      
    }
    private void updateTunablePanel () {
        tunablePanel.removeAll();
       
        JPanel content = taskManager.getConfiguration(null, fcBundleBuilder.getFCalgorithm());
        if (content != null) {
            tunablePanel.add(content);
        }
        tunablePanel.revalidate();
        tunablePanel.repaint();
    }
    
    
}
