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
import org.compsysmed.ocsana.internal.ui.fvs.FVSResultsPanel;
import org.compsysmed.ocsana.internal.ui.results.OCSANAResultsPanel;
import org.compsysmed.ocsana.internal.ui.sfaresults.SFAResultsPanel;

/**
 * Factory for OCSANA runner tasks
 **/
public class FVSRunnerTaskFactory extends AbstractTaskFactory {
    private TaskManager<?, ?> taskManager;
    private FVSBundle fvsBundle;
    private FVSResultsPanel fvsresultsPanel;

    /**
     * Constructor
     *
     * @param taskManager  a TaskManager to run child tasks
     * @param contextBundle  the context for this run
     * @param resultsPanel  the panel to display the results
     **/
    public FVSRunnerTaskFactory (TaskManager<?, ?> taskManager,
    							FVSBundle fvsBundle,
    							FVSResultsPanel fvsresultsPanel) {
        super();

        Objects.requireNonNull(taskManager, "Task manager cannot be null");
        this.taskManager = taskManager;

        Objects.requireNonNull(fvsBundle, "Context bundle cannot be null");
        this.fvsBundle = fvsBundle;

        Objects.requireNonNull(fvsresultsPanel, "Results panel cannot be null");
        this.fvsresultsPanel = fvsresultsPanel;
    }

    @Override
    public TaskIterator createTaskIterator () {
        TaskIterator tasks = new TaskIterator();
        tasks.append(new FVSRunnerTask(taskManager, fvsBundle, fvsresultsPanel));
        return tasks;
    }
}
