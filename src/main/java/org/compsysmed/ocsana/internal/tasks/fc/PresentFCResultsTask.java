package org.compsysmed.ocsana.internal.tasks.fc;

import java.util.Objects;

import org.compsysmed.ocsana.internal.tasks.AbstractFCTask;
import org.compsysmed.ocsana.internal.tasks.AbstractOCSANATask;
import org.compsysmed.ocsana.internal.tasks.AbstractSFATask;
import org.compsysmed.ocsana.internal.tasks.FCStep;
import org.compsysmed.ocsana.internal.tasks.SFAStep;
import org.compsysmed.ocsana.internal.ui.fc.FCResultsPanel;
import org.compsysmed.ocsana.internal.ui.sfaresults.SFAResultsPanel;
import org.compsysmed.ocsana.internal.util.fc.FCBundle;
import org.compsysmed.ocsana.internal.util.fc.FCResultsBundle;
import org.compsysmed.ocsana.internal.util.sfa.SFABundle;
import org.compsysmed.ocsana.internal.util.sfa.SFAResultsBundle;
import org.cytoscape.work.TaskMonitor;
public class PresentFCResultsTask
extends AbstractFCTask {
private static final FCStep algStep = FCStep.PRESENT_FC_RESULTS;

	private final FCRunnerTask fcrunnerTask;
	private final FCBundle fcBundle;
	private final FCResultsBundle fcresultsBundle;
	private final FCResultsPanel fcresultsPanel;

public PresentFCResultsTask (FCRunnerTask fcrunnerTask,
		FCBundle fcBundle,
		FCResultsBundle fcresultsBundle,
		FCResultsPanel fcresultsPanel) {
    super(fcBundle.getNetwork());

    Objects.requireNonNull(fcrunnerTask, "Runner task cannot be null");
    this.fcrunnerTask = fcrunnerTask;

    Objects.requireNonNull(fcBundle, "Context bundle cannot be null");
    this.fcBundle = fcBundle;

    Objects.requireNonNull(fcresultsBundle, "Context results cannot be null");
    this.fcresultsBundle = fcresultsBundle;

    Objects.requireNonNull(fcresultsPanel, "Results panel cannot be null");
    this.fcresultsPanel = fcresultsPanel;
}

@Override
public void run (TaskMonitor taskMonitor) {
    Objects.requireNonNull(taskMonitor, "Task monitor cannot be null");

    taskMonitor.setTitle("Results");

    taskMonitor.setStatusMessage("Generating results report.");

    fcresultsPanel.update(fcBundle, fcresultsBundle);
}

@Override
@SuppressWarnings("unchecked")
public <T> T getResults (Class<? extends T> type) {
    if (type.isAssignableFrom(FCStep.class)) {
        return (T) algStep;
    } else {
        throw new IllegalArgumentException("Invalid results type for presenter.");
    }
}

@Override
public void cancel () {
    super.cancel();
    fcrunnerTask.cancel();
}
}
