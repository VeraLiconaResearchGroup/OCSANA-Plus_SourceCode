package org.compsysmed.ocsana.internal.ui.fc;
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
import org.compsysmed.ocsana.internal.util.context.ContextBundle;
import org.compsysmed.ocsana.internal.util.fc.FCBundle;
import org.compsysmed.ocsana.internal.util.fc.FCResultsBundle;
import org.compsysmed.ocsana.internal.util.results.ResultsBundle;
import org.compsysmed.ocsana.internal.util.sfa.SFABundle;
import org.compsysmed.ocsana.internal.util.sfa.SFAResultsBundle;

public class FCResultsSubpanel
	extends JPanel {
		public FCResultsSubpanel (
		                   FCBundle fcBundle,
		                   FCResultsBundle fcresultsBundle,
		                   PathType pathType) {
		    String FC;
		   


		    FC = fcresultsBundle.getFC();
		    if (FC != null) {    
		        // Create panel
		        JTextArea pathTextArea = new JTextArea(String.join("\n", FC));

		        JScrollPane pathScrollPane = new JScrollPane(pathTextArea);

		        setLayout(new BorderLayout());
		        String panelText = String.format("Feedback Vertex Set Control");
		        add(new JLabel(panelText), BorderLayout.PAGE_START);
		        add(pathScrollPane, BorderLayout.CENTER);
		    }
		    }
		}


