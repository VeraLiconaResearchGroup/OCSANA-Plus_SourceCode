/**
 * Task to present results to user in OCSANA
 *
 * Copyright Vera-Licona Research Group (C) 2015
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.tasks.results;

// Java imports
import java.util.*;

// Cytoscape imports
import org.cytoscape.work.TaskMonitor;

// OCSANA imports
import org.compsysmed.ocsana.internal.tasks.AbstractOCSANATask;
import org.compsysmed.ocsana.internal.tasks.OCSANAStep;
import org.compsysmed.ocsana.internal.tasks.runner.RunnerTask;

import org.compsysmed.ocsana.internal.ui.results.OCSANAResultsPanel;

import org.compsysmed.ocsana.internal.util.context.ContextBundle;
import org.compsysmed.ocsana.internal.util.results.ResultsBundle;

public class PresentResultsTask
    extends AbstractOCSANATask {
    private static final OCSANAStep algStep = OCSANAStep.PRESENT_RESULTS;

    private final RunnerTask runnerTask;
    private final ContextBundle contextBundle;
    private final ResultsBundle resultsBundle;
    private final OCSANAResultsPanel resultsPanel;

    public PresentResultsTask (RunnerTask runnerTask,
                               ContextBundle contextBundle,
                               ResultsBundle resultsBundle,
                               OCSANAResultsPanel resultsPanel) {
        super(contextBundle.getNetwork());

        Objects.requireNonNull(runnerTask, "Runner task cannot be null");
        this.runnerTask = runnerTask;

        Objects.requireNonNull(contextBundle, "Context bundle cannot be null");
        this.contextBundle = contextBundle;

        Objects.requireNonNull(resultsBundle, "Context results cannot be null");
        this.resultsBundle = resultsBundle;

        Objects.requireNonNull(resultsPanel, "Results panel cannot be null");
        this.resultsPanel = resultsPanel;
    }

    @Override
    public void run (TaskMonitor taskMonitor) {
        Objects.requireNonNull(taskMonitor, "Task monitor cannot be null");

        taskMonitor.setTitle("Results");

        taskMonitor.setStatusMessage("Generating results report.");

        resultsPanel.update(contextBundle, resultsBundle);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getResults (Class<? extends T> type) {
        if (type.isAssignableFrom(OCSANAStep.class)) {
            return (T) algStep;
        } else {
            throw new IllegalArgumentException("Invalid results type for presenter.");
        }
    }

    @Override
    public void cancel () {
        super.cancel();
        runnerTask.cancel();
    }
}
