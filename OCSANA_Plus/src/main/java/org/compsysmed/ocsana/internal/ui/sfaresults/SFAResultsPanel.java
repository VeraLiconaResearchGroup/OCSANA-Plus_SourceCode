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

package org.compsysmed.ocsana.internal.ui.sfaresults;

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
import org.compsysmed.ocsana.internal.util.results.ResultsBundle;

import org.compsysmed.ocsana.internal.util.results.ResultsReportManager;
import org.compsysmed.ocsana.internal.util.sfa.SFABundle;
import org.compsysmed.ocsana.internal.util.sfa.SFAResultsBundle;
import org.compsysmed.ocsana.internal.ui.results.subpanels.*;

/**
 * Panel to display OCSANA results
 **/
public class SFAResultsPanel
    extends JPanel
    implements CytoPanelComponent {
    private final CySwingApplication cySwingApplication;
    private final CytoPanel cyResultsPanel;

    private SFABundle sfaBundle;
    private SFAResultsBundle sfaresultsBundle;

    private ResultsReportManager resultsReportManager = new ResultsReportManager();

    private JPanel sfaresultsPanel;
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
    public SFAResultsPanel (CySwingApplication cySwingApplication) {
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
    public void update (SFABundle sfaBundle,
                        SFAResultsBundle sfaresultsBundle) {
        Objects.requireNonNull(sfaBundle, "Context bundle cannot be null");
        this.sfaBundle = sfaBundle;

        Objects.requireNonNull(sfaresultsBundle, "Context results cannot be null");
        this.sfaresultsBundle = sfaresultsBundle;

        //resultsReportManager.update2(sfaBundle, sfaresultsBundle);

        rebuildPanels();
    }

    private void rebuildPanels () {
        reset();

        sfaresultsPanel = getSFAResultsPanel();
        //operationsPanel = getOperationsPanel();

        add(sfaresultsPanel, BorderLayout.CENTER);
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
     * Build the operations panel
     *
     * This is the part of the results panel with buttons and other
     * user operations
     **/
   /* private JPanel getOperationsPanel () {
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

        JButton showReportButton = new JButton("Show report");
        buttonPanel.add(showReportButton);
        showReportButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed (ActionEvent e) {
                    showResultsReport();
                }
            });

        JButton saveReportButton = new JButton("Save report");
        buttonPanel.add(saveReportButton);

        saveReportButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed (ActionEvent event) {
                    saveResultsReport();
                }
            });


        getRootPane().setDefaultButton(showReportButton);

        return buttonPanel;
    }*/

    /**
     * Show the CI results report in a dialog
     **/
  /*  private void showResultsReport () {
        JTextPane reportTextPane = new JTextPane();
        reportTextPane.setContentType("text/html");
        reportTextPane.setEditable(false);
        reportTextPane.setCaretPosition(0); // Show top of file initially

        String reportText = resultsReportManager.reportAsHTML();
        reportTextPane.setText(reportText);

        JScrollPane reportPane = new JScrollPane(reportTextPane);
        JOptionPane.showMessageDialog(this, reportPane, "SFA report", JOptionPane.PLAIN_MESSAGE);
    }*/

    /**
     * Let the user save the results report
     **/
    /*private void saveResultsReport () {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showSaveDialog(buttonPanel) == JFileChooser.APPROVE_OPTION) {
            File outFile = fileChooser.getSelectedFile();
            try (BufferedWriter fileWriter =
                 new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile), StandardCharsets.UTF_8))) {
                String reportText = resultsReportManager.reportAsText();
                fileWriter.write(reportText);
            } catch (IOException exception) {
                String message = "Could not save to " + outFile.toString() + "\n" + exception;
                JOptionPane.showMessageDialog(buttonPanel,
                                              message,
                                              "Error",
                                              JOptionPane.ERROR_MESSAGE);
            }
        }
    }
*/
    /**
     * Build the results panel
     *
     * This is the panel that displays the results of whatever OCSANA
     * operations have completed
     **/
    private JPanel getSFAResultsPanel () {
        JPanel sfaresultsPanel = new JPanel(new BorderLayout());

        if (sfaresultsBundle == null) {
            return sfaresultsPanel;
        }

        JTabbedPane resultsTabbedPane = new JTabbedPane();
        sfaresultsPanel.add(resultsTabbedPane, BorderLayout.CENTER);
        sfaresultsPanel.setBorder(null);

        /*if (resultsBundle.getCIs() != null) {
            CIListSubpanel ciSubpanel = new CIListSubpanel(contextBundle, resultsBundle, cySwingApplication.getJFrame());
            resultsTabbedPane.addTab("Optimal CIs", ciSubpanel);
        }

        if (resultsBundle.getPathsToTargets() != null) {
            PathsSubpanel targetPathsSubpanel = new PathsSubpanel(contextBundle, resultsBundle, PathsSubpanel.PathType.TO_TARGETS);
            resultsTabbedPane.addTab("Paths to targets", targetPathsSubpanel);
        }

        if (resultsBundle.getPathsToOffTargets() != null) {
            PathsSubpanel targetPathsSubpanel = new PathsSubpanel(contextBundle, resultsBundle, PathsSubpanel.PathType.TO_OFF_TARGETS);
            resultsTabbedPane.addTab("Paths to Off-targets", targetPathsSubpanel);
        }*/
        if (sfaresultsBundle.getSFAconfig() != null) {
        	SFAConfigSubpanel SFAConfigSubpanel = new SFAConfigSubpanel(sfaBundle, sfaresultsBundle, PathsSubpanel.PathType.TO_TARGETS);
            resultsTabbedPane.addTab("SFA Config",SFAConfigSubpanel);
        }

        if (sfaresultsBundle.getSFA() != null) {
        	SFAResultsSubpanel SFASubpanel = new SFAResultsSubpanel(sfaBundle, sfaresultsBundle, PathsSubpanel.PathType.TO_OFF_TARGETS);
            resultsTabbedPane.addTab("SFA",SFASubpanel);
        }

        return sfaresultsPanel;
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
        return "SFA";
    }

    /**
     * Get the results panel icon
     */
    @Override
    public Icon getIcon() {
        return null;
    }
}
