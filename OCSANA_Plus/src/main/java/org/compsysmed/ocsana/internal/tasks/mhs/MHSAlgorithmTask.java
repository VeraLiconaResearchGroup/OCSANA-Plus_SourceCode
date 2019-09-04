/**
 * Task to run MHS algorithm in OCSANA
 *
 * Copyright Vera-Licona Research Group (C) 2015
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.tasks.mhs;

// Java imports
import java.util.*;
import java.util.stream.Collectors;

// Cytoscape imports
import org.cytoscape.work.TaskMonitor;

import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyEdge;

// OCSANA imports
import org.compsysmed.ocsana.internal.tasks.AbstractOCSANATask;
import org.compsysmed.ocsana.internal.tasks.OCSANAStep;
import org.compsysmed.ocsana.internal.tasks.runner.RunnerTask;

import org.compsysmed.ocsana.internal.util.context.ContextBundle;
import org.compsysmed.ocsana.internal.util.results.ResultsBundle;
import org.compsysmed.ocsana.internal.util.results.CombinationOfInterventions;

public class MHSAlgorithmTask extends AbstractOCSANATask {
    private static final OCSANAStep algStep = OCSANAStep.FIND_MHSES;

    private final RunnerTask runnerTask;
    private final ContextBundle contextBundle;
    private final ResultsBundle resultsBundle;

    public MHSAlgorithmTask (RunnerTask runnerTask,
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
        if (resultsBundle.pathFindingWasCanceled()) {
            return;
        }

        taskMonitor.setTitle("Minimal CIs");

        Objects.requireNonNull(resultsBundle.getPathsToTargets(), "Paths to targets not set.");

        taskMonitor.setStatusMessage(String.format("Converting %d paths to node sets.", resultsBundle.getPathsToTargets().size()));
        Long preConversionTime = System.nanoTime();
        List<Set<CyNode>> nodeSets = new ArrayList<>();
        Set<CyNode> sourceNodes = contextBundle.getSourceNodes();
        Set<CyNode> targetNodes = contextBundle.getTargetNodes();
        
        for (List<CyEdge> path: resultsBundle.getPathsToTargets()) {
            Set<CyNode> nodes = new HashSet<>();

            // Scan every edge in the path, adding its nodes as
            // appropriate
            for (int i = 0; i <= path.size() - 1; i++) {
                CyEdge edge = path.get(i);
                // Since we're using a Set, we don't have to worry
                // about multiple addition, so we'll just go ahead and
                // add the source and target every time
                if (contextBundle.getIncludeEndpointsInCIs() ||
                    (!sourceNodes.contains(edge.getSource()) && !targetNodes.contains(edge.getSource()))) {
                    nodes.add(edge.getSource());
                }

              
                if (
                    (!sourceNodes.contains(edge.getTarget()) && !targetNodes.contains(edge.getTarget()))) {
                    nodes.add(edge.getTarget());
                }
            }

            if (!nodes.isEmpty()) {
                nodeSets.add(nodes);
            }
        }
        Long postConversionTime = System.nanoTime();

        Double conversionTime = (postConversionTime - preConversionTime) / 1E9;
        taskMonitor.setStatusMessage(String.format("Converted paths in %f s.", conversionTime));

        taskMonitor.setStatusMessage(String.format("Finding minimal combinations of interventions (algorithm: %s).", contextBundle.getMHSAlgorithm().shortName()));
        
        Long preMHSTime = System.nanoTime();
        Collection<Set<CyNode>> MHSes = contextBundle.getMHSAlgorithm().MHSes(nodeSets);
        if (MHSes != null) {
          resultsBundle.setCIs(MHSes.stream().map(mhs -> new CombinationOfInterventions(mhs, targetNodes, contextBundle.getNodeHandler()::getNodeName, contextBundle.getNodeHandler()::getNodeID, resultsBundle.getOCSANAScores().OCSANA(mhs),resultsBundle.getOCSANAScores().EFFECT_ON_TARGETS(mhs),resultsBundle.getOCSANAScores().SIDE_EFFECTS(mhs))).collect(Collectors.toList()));
          //THE LINE ABOVE IS GIVING AN EXCEPTION. I have already checked that MHSes is not null.
        }
        
        Long postMHSTime = System.nanoTime();

        Double mhsTime = (postMHSTime - preMHSTime) / 1E9;

        taskMonitor.showMessage(TaskMonitor.Level.INFO, String.format("Found %d minimal CIs in %f s.", resultsBundle.getCIs().size(), mhsTime));

        resultsBundle.setMHSExecutionSeconds(mhsTime);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getResults (Class<? extends T> type) {
        if (type.isAssignableFrom(OCSANAStep.class)) {
            return (T) algStep;
        } else {
            return (T) resultsBundle.getCIs();
        }
    }

    @Override
    public void cancel () {
        super.cancel();
        contextBundle.getMHSAlgorithm().cancel();
        resultsBundle.setMHSFindingWasCancelled();
        runnerTask.cancel();
    }
}
