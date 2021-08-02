/**
 * Task to run OCSANA path-scoring algorithm
 *
 * Copyright Vera-Licona Research Group (C) 2015
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.tasks.scoring;

import java.util.Objects;
import java.util.function.Predicate;

// Cytoscape imports
import org.cytoscape.work.TaskMonitor;

import org.cytoscape.model.CyEdge;

// OCSANA imports
import org.compsysmed.ocsana.internal.tasks.AbstractOCSANATask;
import org.compsysmed.ocsana.internal.tasks.OCSANAStep;
import org.compsysmed.ocsana.internal.tasks.runner.RunnerTask;

import org.compsysmed.ocsana.internal.util.context.ContextBundle;
import org.compsysmed.ocsana.internal.util.results.ResultsBundle;
import org.compsysmed.ocsana.internal.util.results.OCSANAScores;

public class OCSANAScoringTask
    extends AbstractOCSANATask {
    private static final OCSANAStep algStep = OCSANAStep.SCORE_PATHS;

    private final RunnerTask runnerTask;
    private final ContextBundle contextBundle;
    private final ResultsBundle resultsBundle;

    public OCSANAScoringTask (RunnerTask runnerTask,
                              ContextBundle contextBundle,
                              ResultsBundle resultsBundle) {
        super(contextBundle.getNetwork());

        Objects.requireNonNull(runnerTask, "Runner task cannot be null");
        this.runnerTask = runnerTask;

        Objects.requireNonNull(contextBundle, "Context bundle cannot be null");
        this.contextBundle = contextBundle;

        Objects.requireNonNull(resultsBundle, "Context results cannot be null");
        this.resultsBundle = resultsBundle;
    }

    @Override
    public void run (TaskMonitor taskMonitor) {
        Objects.requireNonNull(taskMonitor, "Task monitor cannot be null");

        if (resultsBundle.pathFindingWasCanceled()) {
            return;
        }

        Objects.requireNonNull(resultsBundle.getPathsToTargets(), "Paths to targets have not been computed");
        Objects.requireNonNull(resultsBundle.getPathsToOffTargets(), "Paths to off-targets have not been computed");

        taskMonitor.setTitle("OCSANA scoring");

        taskMonitor.setStatusMessage("Computing OCSANA scores.");

        Long OCSANAPreTime = System.nanoTime();
        OCSANAScores ocsanaScores = contextBundle.getOCSANAAlgorithm().computeScores(resultsBundle.getPathsToTargets(), resultsBundle.getPathsToOffTargets());
        resultsBundle.setOCSANAScores(ocsanaScores);
        Long OCSANAPostTime = System.nanoTime();

        Double OCSANARunTime = (OCSANAPostTime - OCSANAPreTime) / 1E9;
        resultsBundle.setOCSANAScoringExecutionSeconds(OCSANARunTime);
        taskMonitor.setStatusMessage(String.format("Computed OCSANA scores in %fs.", OCSANARunTime));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getResults (Class<? extends T> type) {
        if (type.isAssignableFrom(OCSANAStep.class)) {
            return (T) algStep;
        } else {
            throw new IllegalArgumentException("Invalid results type for scorer.");
        }
    }

    @Override
    public void cancel () {
        super.cancel();
        contextBundle.getOCSANAAlgorithm().cancel();
        resultsBundle.setOCSANAScoringWasCanceled();
        runnerTask.cancel();
    }
}