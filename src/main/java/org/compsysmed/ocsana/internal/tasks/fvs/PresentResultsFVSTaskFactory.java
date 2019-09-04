package org.compsysmed.ocsana.internal.tasks.fvs;

import java.util.Objects;

import org.compsysmed.ocsana.internal.tasks.results.PresentResultsTask;
import org.compsysmed.ocsana.internal.ui.fc.FCResultsPanel;
import org.compsysmed.ocsana.internal.ui.fvs.FVSResultsPanel;
import org.compsysmed.ocsana.internal.ui.sfaresults.SFAResultsPanel;
import org.compsysmed.ocsana.internal.util.fc.FCBundle;
import org.compsysmed.ocsana.internal.util.fc.FCResultsBundle;
import org.compsysmed.ocsana.internal.util.fvs.FVSBundle;
import org.compsysmed.ocsana.internal.util.fvs.FVSResultsBundle;
import org.compsysmed.ocsana.internal.util.sfa.SFABundle;
import org.compsysmed.ocsana.internal.util.sfa.SFAResultsBundle;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;

public class PresentResultsFVSTaskFactory
extends AbstractTaskFactory {
private final FVSRunnerTask fvsrunnerTask;
private final FVSBundle fvsBundle;
private final FVSResultsBundle fvsresultsBundle;
private final FVSResultsPanel fvsresultsPanel;

public PresentResultsFVSTaskFactory (FVSRunnerTask fvsrunnerTask,
		FVSBundle fvsBundle,
		FVSResultsBundle fvsresultsBundle,
		FVSResultsPanel fvsresultsPanel) {
	    Objects.requireNonNull(fvsrunnerTask, "Runner task cannot be null");
	    this.fvsrunnerTask = fvsrunnerTask;
	
	    Objects.requireNonNull(fvsBundle, "Context bundle cannot be null");
	    this.fvsBundle = fvsBundle;
	
	    Objects.requireNonNull(fvsresultsBundle, "Context results cannot be null");
	    this.fvsresultsBundle = fvsresultsBundle;
	
	    Objects.requireNonNull(fvsresultsPanel, "Results panel cannot be null");
	    this.fvsresultsPanel = fvsresultsPanel;
	}
	
	@Override
	public TaskIterator createTaskIterator () {
	    TaskIterator tasks = new TaskIterator();
	    tasks.append(new PresentFVSResultsTask(fvsrunnerTask, fvsBundle, fvsresultsBundle, fvsresultsPanel));
	    return tasks;
	}
}