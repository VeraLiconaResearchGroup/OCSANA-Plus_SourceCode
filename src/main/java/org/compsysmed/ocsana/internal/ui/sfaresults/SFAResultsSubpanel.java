package org.compsysmed.ocsana.internal.ui.sfaresults;
//Java imports
import java.util.*;
import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

//Cytoscape imports
import org.cytoscape.model.CyEdge;
import org.compsysmed.ocsana.internal.ui.results.subpanels.PathsSubpanel.PathType;
//OCSANA imports

import org.compsysmed.ocsana.internal.util.sfa.SFABundle;
import org.compsysmed.ocsana.internal.util.sfa.SFAResultsBundle;

public class SFAResultsSubpanel
	extends JPanel {
		public SFAResultsSubpanel (
		                   SFABundle sfaBundle,
		                   SFAResultsBundle sfaresultsBundle,
		                   PathType pathType) {
		    String SFA;
		   


		    SFA = sfaresultsBundle.getSFA();
		    if (SFA != null) {    
		        // Create panel
		        JTextArea pathTextArea = new JTextArea(String.join("\n", SFA));

		        JScrollPane pathScrollPane = new JScrollPane(pathTextArea);

		        setLayout(new BorderLayout());
		        String panelText = String.format("Signal Flow Analysis steady state values");
		        add(new JLabel(panelText), BorderLayout.PAGE_START);
		        add(pathScrollPane, BorderLayout.CENTER);
		    }
		    }
		}


