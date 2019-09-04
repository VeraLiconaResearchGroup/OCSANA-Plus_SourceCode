/**
 * Runner task for the OCSANA process
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

// Cytoscape imports
import org.cytoscape.task.AbstractNetworkTask;
import org.cytoscape.work.FinishStatus;
import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.TaskObserver;

// OCSANA imports
import org.compsysmed.ocsana.internal.tasks.OCSANAStep;
import org.compsysmed.ocsana.internal.tasks.mhs.MHSAlgorithmTaskFactory;
import org.compsysmed.ocsana.internal.tasks.path.PathFindingAlgorithmTaskFactory;
import org.compsysmed.ocsana.internal.tasks.results.PresentResultsTaskFactory;
import org.compsysmed.ocsana.internal.tasks.scoring.OCSANAScoringTaskFactory;
import org.compsysmed.ocsana.internal.tasks.fc.FCAlgorithmTaskFactory;
import org.compsysmed.ocsana.internal.util.context.ContextBundle;
import org.compsysmed.ocsana.internal.util.results.ResultsBundle;

import org.compsysmed.ocsana.internal.ui.results.OCSANAResultsPanel;

/**
 * Runner task for the OCSANA process
 *
 * This task runs the OCSANA algorithm on the inputs specified in a
 * CIStageContext. In particular, it:
 * 1) Finds paths from the sources to the targets;
 * 2) Finds MHSes/CIs of those paths; and
 * 3) Scores the influence of the CI nodes on the targets.
 **/
public class RunnerTask
    extends AbstractNetworkTask
    implements TaskObserver, ObservableTask {
    private final TaskManager<?, ?> taskManager;
    private final ContextBundle contextBundle;
    private final ResultsBundle resultsBundle;
    private final OCSANAResultsPanel resultsPanel;

    private Boolean hasCleanResults = true;

    /**
     * Constructor
     *
     * @param taskManager  a TaskManager to run child tasks
     * @param contextBundle  the context for this run
     * @param resultsPanel  the panel to display the results
     **/
    public RunnerTask (TaskManager<?, ?> taskManager,
                       ContextBundle contextBundle,
                       OCSANAResultsPanel resultsPanel) {
        super(contextBundle.getNetwork());

        Objects.requireNonNull(taskManager, "Task manager cannot be null");
        this.taskManager = taskManager;

        Objects.requireNonNull(contextBundle, "Context bundle cannot be null");
        this.contextBundle = contextBundle;

        Objects.requireNonNull(resultsPanel, "Results panel cannot be null");
        this.resultsPanel = resultsPanel;

        this.resultsBundle = new ResultsBundle();
    }

    @Override
    public void run (TaskMonitor taskMonitor) {
        Objects.requireNonNull(taskMonitor, "Task monitor cannot be null");

        // Give the task a title
        taskMonitor.setTitle("OCSANA");

        // Flag that the results are not clean
        hasCleanResults = false;

        // Start the first step of the algorithm
        spawnPathsToTargetsTask();

        // The rest of the tasks will be spawned by taskFinished().
    }

    private void spawnPathsToTargetsTask () {
        PathFindingAlgorithmTaskFactory pathsToTargetsTaskFactory =
            new PathFindingAlgorithmTaskFactory(this, contextBundle, resultsBundle,
                                                OCSANAStep.FIND_PATHS_TO_TARGETS);

        taskManager.execute(pathsToTargetsTaskFactory.createTaskIterator(), this);
    }

    private void spawnPathsToOffTargetsTask () {
        PathFindingAlgorithmTaskFactory pathsToOffTargetsTaskFactory =
            new PathFindingAlgorithmTaskFactory(this, contextBundle, resultsBundle,
                                                OCSANAStep.FIND_PATHS_TO_OFF_TARGETS);

        taskManager.execute(pathsToOffTargetsTaskFactory.createTaskIterator(), this);
    }

    private void spawnOCSANAScoringTask () {
        OCSANAScoringTaskFactory scoringTaskFactory =
            new OCSANAScoringTaskFactory(this, contextBundle, resultsBundle);

        taskManager.execute(scoringTaskFactory.createTaskIterator(), this);
    }

    private void spawnMHSTask () {
        MHSAlgorithmTaskFactory mhsTaskFactory =
            new MHSAlgorithmTaskFactory(this, contextBundle, resultsBundle);

        taskManager.execute(mhsTaskFactory.createTaskIterator(), this);
    }
    

    private void spawnPresentResultsTask () {
        PresentResultsTaskFactory presentResultsTaskFactory =
            new PresentResultsTaskFactory(this, contextBundle, resultsBundle, resultsPanel);

        taskManager.execute(presentResultsTaskFactory.createTaskIterator(), this);
    }

    private void spawnCleanupTask () {
        // Flag that the results are clean
        hasCleanResults = true;

        contextBundle.uncancelAll();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getResults (Class<? extends T> type) {
        if (hasCleanResults) {
            return (T) resultsBundle;
        } else {
            return null;
        }
    }

    @Override
    public void taskFinished(ObservableTask task) {
        Objects.requireNonNull(task, "Task cannot be null");

        if (cancelled) {
            spawnCleanupTask();
            return;
        }

        // Process the results based on the step just completed
        OCSANAStep currentStep = task.getResults(OCSANAStep.class);

        switch (currentStep) {
        case GET_SETS:
            break;

        case FIND_PATHS_TO_TARGETS:
            spawnPathsToOffTargetsTask();
            break;

        case FIND_PATHS_TO_OFF_TARGETS:
            spawnOCSANAScoringTask();
            break;

        case SCORE_PATHS:
            spawnMHSTask();
            break;

        case FIND_MHSES:
        	spawnPresentResultsTask();
            break;
        case PRESENT_RESULTS:
            spawnCleanupTask();
            break;

        default:
            throw new IllegalStateException("Unknown OCSANA step " + currentStep);
        }
    }

    @Override
    public void allFinished(FinishStatus finishStatus) {
        if (finishStatus.getType() != FinishStatus.Type.SUCCEEDED) {
            cancel();
        }
    }
}