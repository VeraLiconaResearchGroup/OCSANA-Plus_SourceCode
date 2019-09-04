/**
 * Abstract base class for OCSANA control subpanels
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
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

// Cytoscape imports

// OCSANA imports
import org.compsysmed.ocsana.internal.ui.control.OCSANAControlPanel;

abstract public class AbstractControlSubpanel
    extends JPanel {
    protected final OCSANAControlPanel controlPanel;
    /**
     * Constructor
     *
     * @param controlPanel  the parent OCSANAControlPanel
     **/
    protected AbstractControlSubpanel(OCSANAControlPanel controlPanel) {
        this.controlPanel = controlPanel;
    }

    /**
     * Update the ContextBuilder with the changes in the UI
     **/
    abstract public void updateContextBuilder ();

    /**
     * Generate an appropriately-formatted JLabel for a subpanel header
     *
     * @param label  the label text for the header
     * @return a JLabel object
     **/
    protected static JLabel makeHeader (String label) {
        JLabel header = new JLabel(String.format("<html><h3>%s</h3></html>", label));
        return header;
    }

    /**
     * Request context bundle builder update
     **/
    protected void requestContextBundleBuilderUpdate () {
        controlPanel.updateContextBundleBuilder();
    }

    /**
     * Set the standard layout on a given panel
     **/
    protected static void setStandardLayout (JPanel panel) {
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    }
}
