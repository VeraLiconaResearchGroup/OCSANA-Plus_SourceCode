/**
 * Panel to contain OCSANA paths report
 *
 * Copyright Vera-Licona Research Group (C) 2015
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.ui.results.subpanels;

// Java imports
import java.util.*;
import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

// Cytoscape imports
import org.cytoscape.model.CyEdge;

// OCSANA imports
import org.compsysmed.ocsana.internal.util.context.ContextBundle;
import org.compsysmed.ocsana.internal.util.results.ResultsBundle;

public class PathsSubpanel
    extends JPanel {
    public PathsSubpanel (ContextBundle contextBundle,
                       ResultsBundle resultsBundle,
                       PathType pathType) {
        Collection<List<CyEdge>> paths;
        Double pathFindingTime;

        switch (pathType) {
        case TO_TARGETS:
            paths = resultsBundle.getPathsToTargets();
            pathFindingTime = resultsBundle.getPathsToTargetsExecutionSeconds();
            break;

        case TO_OFF_TARGETS:
            paths = resultsBundle.getPathsToOffTargets();
            pathFindingTime = resultsBundle.getPathsToOffTargetsExecutionSeconds();
            break;

        default:
            throw new IllegalStateException("Undefined path type");
        }

        if (paths != null) {
            List<String> pathLines = new ArrayList<>();
            for (List<CyEdge> path: paths) {
                pathLines.add(contextBundle.pathString(path));
            }

            // Sort alphabetically
            Collections.sort(pathLines);

            // Create panel
            JTextArea pathTextArea = new JTextArea(String.join("\n", pathLines));

            JScrollPane pathScrollPane = new JScrollPane(pathTextArea);

            setLayout(new BorderLayout());
            String panelText = String.format("Found %d paths to %s in %fs.", paths.size(), pathType, pathFindingTime);
            add(new JLabel(panelText), BorderLayout.PAGE_START);
            add(pathScrollPane, BorderLayout.CENTER);
        }
    }

    public enum PathType {
        TO_TARGETS("targets"),
        TO_OFF_TARGETS("off-targets");

        private final String pluralName;

        private PathType(String pluralName) {
            this.pluralName = pluralName;
        }

        @Override
        public String toString() {
            return pluralName;
        }
    }
}
