/**
 * Factory for OCSANA runner tasks
 *
 * Copyright Vera-Licona Research Group (C) 2016
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.tasks.fvs;

// Java imports
import java.util.*;

// Cytoscape imports
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.TaskObserver;

// OCSANA imports
import org.compsysmed.ocsana.internal.util.context.ContextBundle;
import org.compsysmed.ocsana.internal.util.fc.FCBundle;
import org.compsysmed.ocsana.internal.util.fvs.FVSBundle;
import org.compsysmed.ocsana.internal.util.sfa.SFABundle;
import org.compsysmed.ocsana.internal.ui.fc.FCResultsPanel;
import org.compsysmed.ocsana.internal.ui.results.OCSANAResultsPanel;
import org.compsysmed.ocsana.internal.ui.sfaresults.SFAResultsPanel;

/**
 * Factory for OCSANA runner tasks
 **/
public class FVSRunnerTaskFactory extends AbstractTaskFactory {
    private TaskManager<?, ?> taskManager;
    private FCBundle fcBundle;
    private FCResultsPanel fcresultsPanel;

    /**
     * Constructor
     *
     * @param taskManager  a TaskManager to run child tasks
     * @param contextBundle  the context for this run
     * @param resultsPanel  the panel to display the results
     **/
    public FVSRunnerTaskFactory (TaskManager<?, ?> taskManager,
    							FCBundle fcBundle,
    							FCResultsPanel fcresultsPanel) {
        super();

        Objects.requireNonNull(taskManager, "Task manager cannot be null");
        this.taskManager = taskManager;

        Objects.requireNonNull(fcBundle, "Context bundle cannot be null");
        this.fcBundle = fcBundle;

        Objects.requireNonNull(fcresultsPanel, "Results panel cannot be null");
        this.fcresultsPanel = fcresultsPanel;
    }

    @Override
    public TaskIterator createTaskIterator () {
        TaskIterator tasks = new TaskIterator();
        tasks.append(new FVSRunnerTask(taskManager, fcBundle, fcresultsPanel));
        return tasks;
    }
}
