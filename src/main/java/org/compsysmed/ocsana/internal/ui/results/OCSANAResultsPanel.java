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

package org.compsysmed.ocsana.internal.ui.results;

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

import org.compsysmed.ocsana.internal.ui.results.subpanels.*;

/**
 * Panel to display OCSANA results
 **/
public class OCSANAResultsPanel
    extends JPanel
    implements CytoPanelComponent {
    private final CySwingApplication cySwingApplication;
    private final CytoPanel cyResultsPanel;

    private ContextBundle contextBundle;
    private ResultsBundle resultsBundle;

    private ResultsReportManager resultsReportManager = new ResultsReportManager();

    private JPanel resultsPanel;
    private JPanel operationsPanel;
    private JPanel buttonPanel;

    /**
     * Constructor
     * <p>
     * Produces a blank panel. To populate, use the updateResults methods.
     *
     * @param cySwingApplication  the CySwingApplication of this
     * Cytoscape instance
     **/
    public OCSANAResultsPanel (CySwingApplication cySwingApplication) {
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
    public void update (ContextBundle contextBundle,
                        ResultsBundle resultsBundle) {
        Objects.requireNonNull(contextBundle, "Context bundle cannot be null");
        this.contextBundle = contextBundle;

        Objects.requireNonNull(resultsBundle, "Context results cannot be null");
        this.resultsBundle = resultsBundle;

        resultsReportManager.update(contextBundle, resultsBundle);

        rebuildPanels();
    }

    private void rebuildPanels () {
        reset();

        resultsPanel = getResultsPanel();
        operationsPanel = getOperationsPanel();

        add(resultsPanel, BorderLayout.CENTER);
        add(operationsPanel, BorderLayout.SOUTH);

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
    private JPanel getOperationsPanel () {
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
    }

    /**
     * Show the CI results report in a dialog
     **/
    private void showResultsReport () {
        JTextPane reportTextPane = new JTextPane();
        reportTextPane.setContentType("text/html");
        reportTextPane.setEditable(false);
        reportTextPane.setCaretPosition(0); // Show top of file initially

        String reportText = resultsReportManager.reportAsHTML();
        reportTextPane.setText(reportText);

        JScrollPane reportPane = new JScrollPane(reportTextPane);
        JOptionPane.showMessageDialog(this, reportPane, "OCSANA report", JOptionPane.PLAIN_MESSAGE);
    }

    /**
     * Let the user save the results report
     **/
    private void saveResultsReport () {
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

    /**
     * Build the results panel
     *
     * This is the panel that displays the results of whatever OCSANA
     * operations have completed
     **/
    private JPanel getResultsPanel () {
        JPanel resultsPanel = new JPanel(new BorderLayout());

        if (resultsBundle == null) {
            return resultsPanel;
        }

        JTabbedPane resultsTabbedPane = new JTabbedPane();
        resultsPanel.add(resultsTabbedPane, BorderLayout.CENTER);
        resultsPanel.setBorder(null);

        if (resultsBundle.getCIs() != null) {
            CIListSubpanel ciSubpanel = new CIListSubpanel(contextBundle, resultsBundle, cySwingApplication.getJFrame());
            resultsTabbedPane.addTab("CIs", ciSubpanel);
        }

        if (resultsBundle.getPathsToTargets() != null) {
            PathsSubpanel targetPathsSubpanel = new PathsSubpanel(contextBundle, resultsBundle, PathsSubpanel.PathType.TO_TARGETS);
            resultsTabbedPane.addTab("Paths to targets", targetPathsSubpanel);
        }

        if (resultsBundle.getPathsToOffTargets() != null) {
            PathsSubpanel targetPathsSubpanel = new PathsSubpanel(contextBundle, resultsBundle, PathsSubpanel.PathType.TO_OFF_TARGETS);
            resultsTabbedPane.addTab("Paths to Off-targets", targetPathsSubpanel);
        }
        

        return resultsPanel;
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
        return "OCSANA";
    }

    /**
     * Get the results panel icon
     */
    @Override
    public Icon getIcon() {
        return null;
    }
}
