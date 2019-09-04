/**
 * Factory for tasks to run path-finding algorithms in OCSANA
 *
 * Copyright Vera-Licona Research Group (C) 2015
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

// OCSANA imports
import org.compsysmed.ocsana.internal.tasks.OCSANAStep;
import org.compsysmed.ocsana.internal.tasks.runner.RunnerTask;
import org.compsysmed.ocsana.internal.ui.fc.FCResultsPanel;
import org.compsysmed.ocsana.internal.ui.sfaresults.SFAResultsPanel;
import org.compsysmed.ocsana.internal.util.context.ContextBundle;
import org.compsysmed.ocsana.internal.util.fc.FCBundle;
import org.compsysmed.ocsana.internal.util.fc.FCResultsBundle;
import org.compsysmed.ocsana.internal.util.fvs.FVSBundle;
import org.compsysmed.ocsana.internal.util.fvs.FVSResultsBundle;
import org.compsysmed.ocsana.internal.util.results.ResultsBundle;
import org.compsysmed.ocsana.internal.util.sfa.SFAResultsBundle;

public class FVSAlgorithmTaskFactory
    extends AbstractTaskFactory {
    private final FVSRunnerTask fvsRunnerTask;
    private final FVSBundle fvsBundle;
    private final FVSResultsBundle fvsresultsBundle;

    public  FVSAlgorithmTaskFactory (FVSRunnerTask fvsRunnerTask, 
                                            FVSBundle fvsBundle,
                                            FVSResultsBundle fvsresultsBundle) {
        super();

        Objects.requireNonNull(fvsRunnerTask, "Runner task cannot be null");
        this.fvsRunnerTask = fvsRunnerTask;

        Objects.requireNonNull(fvsBundle, "Context bundle cannot be null");
        this.fvsBundle = fvsBundle;

        Objects.requireNonNull(fvsresultsBundle, "Context results cannot be null");
        this.fvsresultsBundle = fvsresultsBundle;

       
    }

    @Override
    public TaskIterator createTaskIterator () {
        TaskIterator tasks = new TaskIterator();
        tasks.append(new FVSAlgorithmTask(fvsRunnerTask, fvsBundle, fvsresultsBundle));
        return tasks;
    }
}
