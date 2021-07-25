/**
 * Outer wrapper class for OCSANA Cytoscape plugin
 *
 * Copyright Vera-Licona Research Group (C) 2015
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal;

import java.util.HashMap;
import java.util.Map;
// Java imports
import java.util.Properties;

import javax.swing.JMenu;
import javax.swing.JToolBar;

import org.cytoscape.work.TaskFactory;
// Cytoscape imports
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.swing.PanelTaskManager;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.events.SetCurrentNetworkListener;
import org.cytoscape.application.swing.CyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkManager;

import static org.cytoscape.application.swing.ActionEnableSupport.ENABLE_FOR_NETWORK;
import static org.cytoscape.work.ServiceProperties.ENABLE_FOR;
import static org.cytoscape.work.ServiceProperties.COMMAND;
import static org.cytoscape.work.ServiceProperties.COMMAND_DESCRIPTION;
import static org.cytoscape.work.ServiceProperties.COMMAND_NAMESPACE;
import static org.cytoscape.work.ServiceProperties.IN_CONTEXT_MENU;
import static org.cytoscape.work.ServiceProperties.IN_MENU_BAR;
import static org.cytoscape.work.ServiceProperties.PREFERRED_MENU;
import static org.cytoscape.work.ServiceProperties.TOOLTIP;


import org.cytoscape.service.util.AbstractCyActivator;
import org.osgi.framework.BundleContext;

// OCSANA imports
import org.compsysmed.ocsana.internal.ui.control.OCSANAControlPanel;
import org.compsysmed.ocsana.internal.ui.fc.FCResultsPanel;
import org.compsysmed.ocsana.internal.ui.fc.FCmenu;
import org.compsysmed.ocsana.internal.algorithms.fc.AbstractFCAlgorithm;
import org.compsysmed.ocsana.internal.tasks.fc.FCAlgorithmTaskFactory;
import org.compsysmed.ocsana.internal.tasks.fc.FCAlgorithmTaskFactoryAuto;
import org.compsysmed.ocsana.internal.tasks.hello.ReturnAValueTaskFactory;
import org.compsysmed.ocsana.internal.tasks.hello.SayHelloTaskFactory;
import org.compsysmed.ocsana.internal.tasks.sfa.SFAAutoTaskFactory;
import org.compsysmed.ocsana.internal.ui.fvs.FVSmenu;
import org.compsysmed.ocsana.internal.ui.results.OCSANAResultsPanel;

import org.compsysmed.ocsana.internal.ui.sfa.SFAmenu;
import org.compsysmed.ocsana.internal.ui.sfaresults.SFAResultsPanel;
import org.compsysmed.ocsana.internal.util.fc.FCBundle;
import org.compsysmed.ocsana.internal.util.tunables.NodeHandler;
import org.cytoscape.service.util.CyServiceRegistrar;

public class CyActivator extends AbstractCyActivator {
	
	public CyActivator() {
		super();
	}
	@Override
	
    public void start (BundleContext bc) throws Exception {
        // Get Cytoscape internal utilities
        TaskManager<?, ?> taskManager = getService(bc, TaskManager.class);

        CyApplicationManager cyApplicationManager = getService(bc, CyApplicationManager.class);

        CySwingApplication cySwingApplication = getService(bc, CySwingApplication.class);
        CytoPanel cyControlPanel = cySwingApplication.getCytoPanel(CytoPanelName.WEST);
        PanelTaskManager panelTaskManager = getService(bc, PanelTaskManager.class);
        CyNetworkManager networkManager = getService(bc, CyNetworkManager.class);
        // Results panel registration
        OCSANAResultsPanel resultsPanel =
            new OCSANAResultsPanel(cySwingApplication);
        registerService(bc, resultsPanel, CytoPanelComponent.class, new Properties());

        // Control panel registration
        OCSANAControlPanel controlPanel =
            new OCSANAControlPanel(cyApplicationManager, cyControlPanel, resultsPanel, panelTaskManager);
        registerService(bc, controlPanel, CytoPanelComponent.class, new Properties());
        registerService(bc, controlPanel, SetCurrentNetworkListener.class, new Properties());
        
        FCResultsPanel fcresultsPanel =
                new FCResultsPanel(cySwingApplication);
            registerService(bc, fcresultsPanel, CytoPanelComponent.class, new Properties());
        
        FCmenu fcmenu = new FCmenu(cyApplicationManager,cySwingApplication,fcresultsPanel,panelTaskManager);	
        	registerService(bc,fcmenu,CyAction.class, new Properties());
        
        FVSmenu fvsmenu = new FVSmenu(cyApplicationManager,cySwingApplication,fcresultsPanel,panelTaskManager);	
        	registerService(bc,fvsmenu,CyAction.class, new Properties());
       
       SFAResultsPanel sfaresultsPanel =
                new SFAResultsPanel(cySwingApplication);
            registerService(bc, sfaresultsPanel, CytoPanelComponent.class, new Properties());
        
        SFAmenu sfamenu = new SFAmenu(cyApplicationManager,cySwingApplication,sfaresultsPanel,panelTaskManager);	
        	registerService(bc,sfamenu,CyAction.class, new Properties());
        
        //automation functions
    	String returnAValueDescription = "Add two numbers (a and b) and return their value using ObservableTask.";
		
		Properties returnAValueTaskFactoryProperties = new Properties();
		returnAValueTaskFactoryProperties.setProperty(COMMAND_NAMESPACE, "ocsanaplus");
		returnAValueTaskFactoryProperties.setProperty(COMMAND, "return_a_value");
		returnAValueTaskFactoryProperties.setProperty(COMMAND_DESCRIPTION,  returnAValueDescription);
		returnAValueTaskFactoryProperties.setProperty(PREFERRED_MENU, "ocsanaplus");
		returnAValueTaskFactoryProperties.setProperty(IN_MENU_BAR, "false");
		returnAValueTaskFactoryProperties.setProperty(IN_CONTEXT_MENU, "false");
		returnAValueTaskFactoryProperties.setProperty(TOOLTIP,  returnAValueDescription);

		TaskFactory returnAValueTaskFactory = new ReturnAValueTaskFactory();
		registerAllServices(bc, returnAValueTaskFactory, returnAValueTaskFactoryProperties);
    		
    	String sayHelloDescription = "Say hello to the someone by name using the Task Monitor.";
		
		Properties sayHelloTaskFactoryProperties = new Properties();
		sayHelloTaskFactoryProperties.setProperty(COMMAND_NAMESPACE, "ocsanaplus");
		sayHelloTaskFactoryProperties.setProperty(COMMAND, "say_hello");
		sayHelloTaskFactoryProperties.setProperty(COMMAND_DESCRIPTION, sayHelloDescription);
		sayHelloTaskFactoryProperties.setProperty(PREFERRED_MENU, "ocsanaplus");
		sayHelloTaskFactoryProperties.setProperty(IN_MENU_BAR, "false");
		sayHelloTaskFactoryProperties.setProperty(IN_CONTEXT_MENU, "false");


		TaskFactory pauseCommandFactory = new SayHelloTaskFactory();
		registerAllServices(bc, pauseCommandFactory, sayHelloTaskFactoryProperties);
		
		String FCDescription = "run FC finding.";
		
		Properties FCTaskFactoryProperties = new Properties();
		FCTaskFactoryProperties.setProperty(COMMAND_NAMESPACE, "ocsanaplus");
		FCTaskFactoryProperties.setProperty(COMMAND, "FC");
		FCTaskFactoryProperties.setProperty(COMMAND_DESCRIPTION, FCDescription);
		FCTaskFactoryProperties.setProperty(PREFERRED_MENU, "ocsanaplus");
		FCTaskFactoryProperties.setProperty(IN_MENU_BAR, "false");
		FCTaskFactoryProperties.setProperty(IN_CONTEXT_MENU, "false");

		TaskFactory FCAlgorithmTaskFactoryAuto = new FCAlgorithmTaskFactoryAuto();
		registerAllServices(bc, FCAlgorithmTaskFactoryAuto, FCTaskFactoryProperties);
		
		
		String SFADescription = "run SFA simulations.";
		
		Properties SFATaskFactoryProperties = new Properties();
		SFATaskFactoryProperties.setProperty(COMMAND_NAMESPACE, "ocsanaplus");
		SFATaskFactoryProperties.setProperty(COMMAND, "SFA");
		SFATaskFactoryProperties.setProperty(COMMAND_DESCRIPTION, SFADescription);
		SFATaskFactoryProperties.setProperty(PREFERRED_MENU, "ocsanaplus");
		SFATaskFactoryProperties.setProperty(IN_MENU_BAR, "false");
		SFATaskFactoryProperties.setProperty(IN_CONTEXT_MENU, "false");

		TaskFactory SFAAlgorithmTaskFactoryAuto = new SFAAutoTaskFactory();
		registerAllServices(bc, SFAAlgorithmTaskFactoryAuto, SFATaskFactoryProperties);
	    
		}
    
    
}
