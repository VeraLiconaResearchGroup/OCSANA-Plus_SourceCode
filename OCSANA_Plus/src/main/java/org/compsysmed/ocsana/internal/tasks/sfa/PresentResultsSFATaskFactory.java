package org.compsysmed.ocsana.internal.tasks.sfa;

import java.util.Objects;

import org.compsysmed.ocsana.internal.tasks.results.PresentResultsTask;
import org.compsysmed.ocsana.internal.ui.sfaresults.SFAResultsPanel;
import org.compsysmed.ocsana.internal.util.sfa.SFABundle;
import org.compsysmed.ocsana.internal.util.sfa.SFAResultsBundle;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;

public class PresentResultsSFATaskFactory
extends AbstractTaskFactory {
private final SFARunnerTask sfarunnerTask;
private final SFABundle sfaBundle;
private final SFAResultsBundle sfaresultsBundle;
private final SFAResultsPanel sfaresultsPanel;

public PresentResultsSFATaskFactory (SFARunnerTask sfarunnerTask,
		SFABundle sfaBundle,
		SFAResultsBundle sfaresultsBundle,
		SFAResultsPanel sfaresultsPanel) {
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
	public TaskIterator createTaskIterator () {
	    TaskIterator tasks = new TaskIterator();
	    tasks.append(new PresentSFAResultsTask(sfarunnerTask, sfaBundle, sfaresultsBundle, sfaresultsPanel));
	    return tasks;
	}
}