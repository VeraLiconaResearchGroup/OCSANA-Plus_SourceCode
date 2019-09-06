package org.compsysmed.ocsana.internal.tasks.fvs;

import java.util.Objects;

import org.compsysmed.ocsana.internal.tasks.AbstractFCTask;
import org.compsysmed.ocsana.internal.tasks.AbstractFVSTask;
import org.compsysmed.ocsana.internal.tasks.AbstractOCSANATask;
import org.compsysmed.ocsana.internal.tasks.AbstractSFATask;
import org.compsysmed.ocsana.internal.tasks.FCStep;
import org.compsysmed.ocsana.internal.tasks.FVSStep;
import org.compsysmed.ocsana.internal.tasks.SFAStep;
import org.compsysmed.ocsana.internal.ui.fc.FCResultsPanel;
import org.compsysmed.ocsana.internal.ui.sfaresults.SFAResultsPanel;
import org.compsysmed.ocsana.internal.util.fc.FCBundle;
import org.compsysmed.ocsana.internal.util.fc.FCResultsBundle;
import org.compsysmed.ocsana.internal.util.fvs.FVSBundle;
import org.compsysmed.ocsana.internal.util.sfa.SFABundle;
import org.compsysmed.ocsana.internal.util.sfa.SFAResultsBundle;
import org.cytoscape.work.TaskMonitor;
public class PresentFVSResultsTask
extends AbstractFVSTask {
private static final FVSStep algStep = FVSStep.PRESENT_FVS_RESULTS;

	private final FVSRunnerTask fvsrunnerTask;
	private final FCBundle fcBundle;
	private final FCResultsBundle fvsresultsBundle;
	private final FCResultsPanel fcresultsPanel;

public PresentFVSResultsTask (FVSRunnerTask fvsrunnerTask,
		FCBundle fcBundle,
		FCResultsBundle fvsresultsBundle,
		FCResultsPanel fcresultsPanel) {
    super(fcBundle.getNetwork());

    Objects.requireNonNull(fvsrunnerTask, "Runner task cannot be null");
    this.fvsrunnerTask = fvsrunnerTask;

    Objects.requireNonNull(fcBundle, "Context bundle cannot be null");
    this.fcBundle = fcBundle;

    Objects.requireNonNull(fvsresultsBundle, "Context results cannot be null");
    this.fvsresultsBundle = fvsresultsBundle;

    Objects.requireNonNull(fcresultsPanel, "Results panel cannot be null");
    this.fcresultsPanel = fcresultsPanel;
}

@Override
public void run (TaskMonitor taskMonitor) {
    Objects.requireNonNull(taskMonitor, "Task monitor cannot be null");

    taskMonitor.setTitle("Results");

    taskMonitor.setStatusMessage("Generating results report.");

    fcresultsPanel.update(fcBundle, fvsresultsBundle);
}

@Override
@SuppressWarnings("unchecked")
public <T> T getResults (Class<? extends T> type) {
    if (type.isAssignableFrom(FVSStep.class)) {
        return (T) algStep;
    } else {
        throw new IllegalArgumentException("Invalid results type for presenter.");
    }
}

@Override
public void cancel () {
    super.cancel();
    fvsrunnerTask.cancel();
}
}
