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

package org.compsysmed.ocsana.internal.tasks.sfa;

// Java imports
import java.util.*;

// Cytoscape imports
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.TaskObserver;

// OCSANA imports
import org.compsysmed.ocsana.internal.util.context.ContextBundle;
import org.compsysmed.ocsana.internal.util.sfa.SFABundle;
import org.compsysmed.ocsana.internal.ui.results.OCSANAResultsPanel;
import org.compsysmed.ocsana.internal.ui.sfaresults.SFAResultsPanel;

/**
 * Factory for OCSANA runner tasks
 **/
public class SFARunnerTaskFactory extends AbstractTaskFactory {
    private TaskManager<?, ?> taskManager;
    private SFABundle sfaBundle;
    private SFAResultsPanel sfaresultsPanel;

    /**
     * Constructor
     *
     * @param taskManager  a TaskManager to run child tasks
     * @param contextBundle  the context for this run
     * @param resultsPanel  the panel to display the results
     **/
    public SFARunnerTaskFactory (TaskManager<?, ?> taskManager,
                              SFABundle sfaBundle,
                              SFAResultsPanel sfaresultsPanel) {
        super();

        Objects.requireNonNull(taskManager, "Task manager cannot be null");
        this.taskManager = taskManager;

        Objects.requireNonNull(sfaBundle, "Context bundle cannot be null");
        this.sfaBundle = sfaBundle;

        Objects.requireNonNull(sfaresultsPanel, "Results panel cannot be null");
        this.sfaresultsPanel = sfaresultsPanel;
    }

    @Override
    public TaskIterator createTaskIterator () {
        TaskIterator tasks = new TaskIterator();
        tasks.append(new SFARunnerTask(taskManager, sfaBundle, sfaresultsPanel));
        return tasks;
    }
}
