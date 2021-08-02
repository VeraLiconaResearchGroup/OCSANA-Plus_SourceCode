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

package org.compsysmed.ocsana.internal.tasks.runner;

// Java imports
import java.util.*;

import org.cytoscape.model.CyNode;
// Cytoscape imports
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.TaskObserver;

// OCSANA imports
import org.compsysmed.ocsana.internal.util.context.ContextBundle;

import org.compsysmed.ocsana.internal.ui.results.OCSANAResultsPanel;

/**
 * Factory for OCSANA runner tasks
 **/
public class RunnerTaskFactory extends AbstractTaskFactory {
    private TaskManager<?, ?> taskManager;
    private ContextBundle contextBundle;
    private OCSANAResultsPanel resultsPanel;

    /**
     * Constructor
     *
     * @param taskManager  a TaskManager to run child tasks
     * @param contextBundle  the context for this run
     * @param resultsPanel  the panel to display the results
     **/
    public RunnerTaskFactory (TaskManager<?, ?> taskManager,
                              ContextBundle contextBundle,
                              OCSANAResultsPanel resultsPanel) {
        super();

        Objects.requireNonNull(taskManager, "Task manager cannot be null");
        this.taskManager = taskManager;

        Objects.requireNonNull(contextBundle, "Context bundle cannot be null");
        this.contextBundle = contextBundle;

        Objects.requireNonNull(resultsPanel, "Results panel cannot be null");
        this.resultsPanel = resultsPanel;
       
        
    }

    @Override
    public TaskIterator createTaskIterator () {
        TaskIterator tasks = new TaskIterator();
        
        tasks.append(new RunnerTask(taskManager, contextBundle, resultsPanel));
        return tasks;
    }
}
