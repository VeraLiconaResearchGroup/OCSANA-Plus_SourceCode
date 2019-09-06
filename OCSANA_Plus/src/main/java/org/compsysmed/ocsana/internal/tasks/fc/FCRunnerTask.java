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

package org.compsysmed.ocsana.internal.tasks.fc;

// Java imports
import java.util.*;

// Cytoscape imports
import org.cytoscape.task.AbstractNetworkTask;
import org.cytoscape.work.FinishStatus;
import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.TaskObserver;
import org.compsysmed.ocsana.internal.tasks.FCStep;
import org.compsysmed.ocsana.internal.tasks.OCSANAStep;
// OCSANA imports
import org.compsysmed.ocsana.internal.tasks.SFAStep;
import org.compsysmed.ocsana.internal.tasks.mhs.MHSAlgorithmTaskFactory;
import org.compsysmed.ocsana.internal.tasks.path.PathFindingAlgorithmTaskFactory;
import org.compsysmed.ocsana.internal.tasks.results.PresentResultsTaskFactory;
import org.compsysmed.ocsana.internal.tasks.scoring.OCSANAScoringTaskFactory;
import org.compsysmed.ocsana.internal.tasks.fc.FCAlgorithmTaskFactory;
import org.compsysmed.ocsana.internal.util.context.ContextBundle;
import org.compsysmed.ocsana.internal.util.fc.FCBundle;
import org.compsysmed.ocsana.internal.util.fc.FCResultsBundle;
import org.compsysmed.ocsana.internal.util.results.ResultsBundle;
import org.compsysmed.ocsana.internal.util.sfa.SFABundle;
import org.compsysmed.ocsana.internal.util.sfa.SFAResultsBundle;
import org.compsysmed.ocsana.internal.ui.fc.FCResultsPanel;
import org.compsysmed.ocsana.internal.ui.results.OCSANAResultsPanel;
import org.compsysmed.ocsana.internal.ui.sfaresults.SFAResultsPanel;

/**
 * Runner task for the OCSANA process
 *
 * This task runs the OCSANA algorithm on the inputs specified in a
 * CIStageContext. In particular, it:
 * 1) Finds paths from the sources to the targets;
 * 2) Finds MHSes/CIs of those paths; and
 * 3) Scores the influence of the CI nodes on the targets.
 **/
public class FCRunnerTask
    extends AbstractNetworkTask
    implements TaskObserver, ObservableTask {
    private final TaskManager<?, ?> taskManager;
    private final FCBundle fcBundle;
    private final FCResultsBundle fcresultsBundle;
    private final FCResultsPanel fcresultsPanel;

    private Boolean hasCleanResults = true;

    /**
     * Constructor
     *
     * @param taskManager  a TaskManager to run child tasks
     * @param contextBundle  the context for this run
     * @param resultsPanel  the panel to display the results
     **/
    public FCRunnerTask (TaskManager<?, ?> taskManager,
    					FCBundle fcBundle,
    					FCResultsPanel fcresultsPanel) {
        super( fcBundle.getNetwork());

        Objects.requireNonNull(taskManager, "Task manager cannot be null");
        this.taskManager = taskManager;

        Objects.requireNonNull( fcBundle, "Context bundle cannot be null");
        this. fcBundle =  fcBundle;

        Objects.requireNonNull(fcresultsPanel, "Results panel cannot be null");
        this.fcresultsPanel = fcresultsPanel;

        this.fcresultsBundle = new FCResultsBundle();
    }

    @Override
    public void run (TaskMonitor taskMonitor) {
        Objects.requireNonNull(taskMonitor, "Task monitor cannot be null");

        // Give the task a title
        taskMonitor.setTitle("Feedback Vertex Set Control");

        // Flag that the results are not clean
        hasCleanResults = false;

        // Start the first step of the algorithm
        spawnFCTask();

        // The rest of the tasks will be spawned by taskFinished().
    }

    private void spawnFCTask () {
        FCAlgorithmTaskFactory FCTaskFactory =
            new FCAlgorithmTaskFactory(this, fcBundle, fcresultsBundle);
        
        taskManager.execute(FCTaskFactory.createTaskIterator(), this);
    }
    
    private void spawnPresentFCResultsTask () {
        PresentResultsFCTaskFactory presentResultsFCTaskFactory =
            new PresentResultsFCTaskFactory(this, fcBundle, fcresultsBundle, fcresultsPanel);

        taskManager.execute(presentResultsFCTaskFactory.createTaskIterator(), this);
    }
    private void spawnCleanupTask () {
        // Flag that the results are clean
        hasCleanResults = true;

        fcBundle.uncancelAll();
    }
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getResults (Class<? extends T> type) {
        if (hasCleanResults) {
            return (T) fcresultsBundle;
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
        FCStep currentStep = task.getResults(FCStep.class);

        switch (currentStep) {
        case GET_FC_SETS:
            break;
        case DO_FC:
        	spawnPresentFCResultsTask();
            break;
        case PRESENT_FC_RESULTS:
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