package org.compsysmed.ocsana.internal.tasks.fc;

import java.util.Objects;

import org.compsysmed.ocsana.internal.tasks.results.PresentResultsTask;
import org.compsysmed.ocsana.internal.ui.fc.FCResultsPanel;
import org.compsysmed.ocsana.internal.ui.sfaresults.SFAResultsPanel;
import org.compsysmed.ocsana.internal.util.fc.FCBundle;
import org.compsysmed.ocsana.internal.util.fc.FCResultsBundle;
import org.compsysmed.ocsana.internal.util.sfa.SFABundle;
import org.compsysmed.ocsana.internal.util.sfa.SFAResultsBundle;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;

public class PresentResultsFCTaskFactory
extends AbstractTaskFactory {
private final FCRunnerTask fcrunnerTask;
private final FCBundle fcBundle;
private final FCResultsBundle fcresultsBundle;
private final FCResultsPanel fcresultsPanel;

public PresentResultsFCTaskFactory (FCRunnerTask fcrunnerTask,
		FCBundle fcBundle,
		FCResultsBundle fcresultsBundle,
		FCResultsPanel fcresultsPanel) {
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
	public TaskIterator createTaskIterator () {
	    TaskIterator tasks = new TaskIterator();
	    tasks.append(new PresentFCResultsTask(fcrunnerTask, fcBundle, fcresultsBundle, fcresultsPanel));
	    return tasks;
	}
}