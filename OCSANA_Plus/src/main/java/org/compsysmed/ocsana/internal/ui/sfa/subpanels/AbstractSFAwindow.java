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

package org.compsysmed.ocsana.internal.ui.sfa.subpanels;

// Java imports
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

// Cytoscape imports

import org.compsysmed.ocsana.internal.ui.sfa.SFAwindow;
abstract public class AbstractSFAwindow
    extends JPanel {
    protected final SFAwindow sfawindow;
    /**
     * Constructor
     *
     * @param controlPanel  the parent OCSANAControlPanel
     **/
    protected AbstractSFAwindow(SFAwindow sfawindow) {
        this.sfawindow = sfawindow;
    }

    /**
     * Update the ContextBuilder with the changes in the UI
     **/
    abstract public void updatesfaBuilder ();

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
    protected void requestSFABundleBuilderUpdate () {
    	sfawindow.updateSFABundleBuilder();
    }

    /**
     * Set the standard layout on a given panel
     **/
    protected static void setStandardLayout (JPanel panel) {
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
    }
}
