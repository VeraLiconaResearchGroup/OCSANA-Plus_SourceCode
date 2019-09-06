package org.compsysmed.ocsana.internal.tasks.fvs;

import java.util.Objects;

import org.compsysmed.ocsana.internal.tasks.results.PresentResultsTask;
import org.compsysmed.ocsana.internal.ui.fc.FCResultsPanel;
import org.compsysmed.ocsana.internal.ui.sfaresults.SFAResultsPanel;
import org.compsysmed.ocsana.internal.util.fc.FCBundle;
import org.compsysmed.ocsana.internal.util.fc.FCResultsBundle;
import org.compsysmed.ocsana.internal.util.fvs.FVSBundle;
import org.compsysmed.ocsana.internal.util.sfa.SFABundle;
import org.compsysmed.ocsana.internal.util.sfa.SFAResultsBundle;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;

public class PresentResultsFVSTaskFactory
extends AbstractTaskFactory {
private final FVSRunnerTask fvsrunnerTask;
private final FCBundle fcBundle;
private final FCResultsBundle fvsresultsBundle;
private final FCResultsPanel fcresultsPanel;

public PresentResultsFVSTaskFactory (FVSRunnerTask fvsrunnerTask,
		FCBundle fcBundle,
		FCResultsBundle fvsresultsBundle,
		FCResultsPanel fcresultsPanel) {
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
	public TaskIterator createTaskIterator () {
	    TaskIterator tasks = new TaskIterator();
	    tasks.append(new PresentFVSResultsTask(fvsrunnerTask, fcBundle, fvsresultsBundle, fcresultsPanel));
	    return tasks;
	}
}