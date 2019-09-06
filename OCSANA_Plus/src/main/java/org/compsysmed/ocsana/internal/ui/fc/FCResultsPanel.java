/**
 * Panel to contain OCSANA results report
 *
 * Copyright Vera-Licona Research Group (C) 2015
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.ui.fc;

// Java imports
import java.awt.Component;
import java.awt.BorderLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.*;
import java.nio.charset.StandardCharsets;

import java.util.*;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;

// Cytoscape imports
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.application.swing.CytoPanelState;

// OCSANA imports
import org.compsysmed.ocsana.internal.util.context.ContextBundle;
import org.compsysmed.ocsana.internal.util.fc.FCBundle;
import org.compsysmed.ocsana.internal.util.fc.FCResultsBundle;
import org.compsysmed.ocsana.internal.util.fvs.FVSBundle;
import org.compsysmed.ocsana.internal.util.results.ResultsBundle;

import org.compsysmed.ocsana.internal.util.results.ResultsReportManager;
import org.compsysmed.ocsana.internal.util.sfa.SFABundle;
import org.compsysmed.ocsana.internal.util.sfa.SFAResultsBundle;
import org.compsysmed.ocsana.internal.ui.results.subpanels.*;

/**
 * Panel to display OCSANA results
 **/
public class FCResultsPanel
    extends JPanel
    implements CytoPanelComponent {
    private final CySwingApplication cySwingApplication;
    private final CytoPanel cyResultsPanel;

    private FCBundle fcBundle;
    private FVSBundle fvsBundle;
    private FCResultsBundle fcresultsBundle;

    private ResultsReportManager resultsReportManager = new ResultsReportManager();

    private JPanel fcresultsPanel;
    //private JPanel operationsPanel;
    //private JPanel buttonPanel;

    /**
     * Constructor
     * <p>
     * Produces a blank panel. To populate, use the updateResults methods.
     *
     * @param cySwingApplication  the CySwingApplication of this
     * Cytoscape instance
     **/
    public FCResultsPanel (CySwingApplication cySwingApplication) {
        super();
        this.cySwingApplication = cySwingApplication;
        this.cyResultsPanel = cySwingApplication.getCytoPanel(getCytoPanelName());

        setLayout(new BorderLayout());
    }

    /**
     * Update the panel with the specified results
     *
     * @param contextBundle  the configuration context
     * @param resultsBundle  the results
     **/
    public void update (FCBundle fcBundle,
                        FCResultsBundle fcresultsBundle) {
    	//Objects.requireNonNull(fcBundle, "Context bundle cannot be null");
    	if (Objects.nonNull(fcBundle)){
        this.fcBundle = fcBundle;
    	}
       
        //Objects.requireNonNull(fcresultsBundle, "Context results cannot be null");
        if (Objects.nonNull(fcresultsBundle)){
        this.fcresultsBundle = fcresultsBundle;
        }
        //Objects.requireNonNull(fvsresultsBundle, "Context results cannot be null");
       

        rebuildPanels();
    }

    private void rebuildPanels () {
        reset();

        fcresultsPanel = getFCResultsPanel();
        //operationsPanel = getOperationsPanel();

        add(fcresultsPanel, BorderLayout.CENTER);
        //add(operationsPanel, BorderLayout.SOUTH);

        setSize(getMinimumSize());

        revalidate();
        repaint();

        if (cyResultsPanel.getState() == CytoPanelState.HIDE) {
            cyResultsPanel.setState(CytoPanelState.DOCK);
        }
    }


    /**
     * Reset the panel to display nothing
     **/
    public void reset () {
        removeAll();
        revalidate();
        repaint();
    }

    /**
     * Build the results panel
     *
     * This is the panel that displays the results of whatever OCSANA
     * operations have completed
     **/
    private JPanel getFCResultsPanel () {
        JPanel fcresultsPanel = new JPanel(new BorderLayout());
        //if (fcresultsBundle == null && fvsresultsBundle == null) {
            //return fcresultsPanel;
       // }

        JTabbedPane resultsTabbedPane = new JTabbedPane();
        fcresultsPanel.add(resultsTabbedPane, BorderLayout.CENTER);
        fcresultsPanel.setBorder(null);

        
        if (fcresultsBundle.getFVS() != null) {
        	FVSResultsSubpanel FVSSubpanel = new FVSResultsSubpanel(fvsBundle, fcresultsBundle, PathsSubpanel.PathType.TO_TARGETS);
            resultsTabbedPane.addTab("Feedback Vertex Set Control Nodes",FVSSubpanel);
        }
        if (fcresultsBundle.getFC() != null) {
        	FCResultsSubpanel FCSubpanel = new FCResultsSubpanel(fcBundle, fcresultsBundle, PathsSubpanel.PathType.TO_OFF_TARGETS);
            resultsTabbedPane.addTab("Feedback Vertex Set Control Nodes",FCSubpanel);
        }

        return fcresultsPanel;
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
        return CytoPanelName.EAST;
    }

    /**
     * Get the results panel title
     */
    @Override
    public String getTitle() {
        return "FC";
    }

    /**
     * Get the results panel icon
     */
    @Override
    public Icon getIcon() {
        return null;
    }
}
