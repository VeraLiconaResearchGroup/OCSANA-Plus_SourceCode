package org.compsysmed.ocsana.internal.tasks.sfa;

import java.util.Objects;

import org.compsysmed.ocsana.internal.tasks.AbstractOCSANATask;
import org.compsysmed.ocsana.internal.tasks.AbstractSFATask;
import org.compsysmed.ocsana.internal.tasks.SFAStep;
import org.compsysmed.ocsana.internal.ui.sfaresults.SFAResultsPanel;
import org.compsysmed.ocsana.internal.util.sfa.SFABundle;
import org.compsysmed.ocsana.internal.util.sfa.SFAResultsBundle;
import org.cytoscape.work.TaskMonitor;
public class PresentSFAResultsTask
extends AbstractSFATask {
private static final SFAStep algStep = SFAStep.PRESENT_SFA_RESULTS;

	private final SFARunnerTask sfarunnerTask;
	private final SFABundle sfaBundle;
	private final SFAResultsBundle sfaresultsBundle;
	private final SFAResultsPanel sfaresultsPanel;

public PresentSFAResultsTask (SFARunnerTask sfarunnerTask,
		SFABundle sfaBundle,
		SFAResultsBundle sfaresultsBundle,
		SFAResultsPanel sfaresultsPanel) {
    super(sfaBundle.getNetwork());

    Objects.requireNonNull(sfarunnerTask, "Runner task cannot be null");
    this.sfarunnerTask = sfarunnerTask;

    Objects.requireNonNull(sfaBundle, "Context bundle cannot be null");
    this.sfaBundle = sfaBundle;

    Objects.requireNonNull(sfaresultsBundle, "Context results cannot be null");
    this.sfaresultsBundle = sfaresultsBundle;

    Objects.requireNonNull(sfaresultsPanel, "Results panel cannot be null");
    this.sfaresultsPanel = sfaresultsPanel;
}

@Override
public void run (TaskMonitor taskMonitor) {
    Objects.requireNonNull(taskMonitor, "Task monitor cannot be null");

    taskMonitor.setTitle("Results");

    taskMonitor.setStatusMessage("Generating results report.");

    sfaresultsPanel.update(sfaBundle, sfaresultsBundle);
}

@Override
@SuppressWarnings("unchecked")
public <T> T getResults (Class<? extends T> type) {
    if (type.isAssignableFrom(SFAStep.class)) {
        return (T) algStep;
    } else {
        throw new IllegalArgumentException("Invalid results type for presenter.");
    }
}

@Override
public void cancel () {
    super.cancel();
    sfarunnerTask.cancel();
}
}
