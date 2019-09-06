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
import org.cytoscape.application.swing.ToolBarComponent;
import static org.cytoscape.work.ServiceProperties.COMMAND;

import static org.cytoscape.work.ServiceProperties.APPS_MENU;
import static org.cytoscape.work.ServiceProperties.IN_MENU_BAR;
import static org.cytoscape.work.ServiceProperties.PREFERRED_MENU;
import static org.cytoscape.work.ServiceProperties.TITLE;
import static org.cytoscape.work.ServiceProperties.MENU_GRAVITY;

import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.task.NetworkViewTaskFactory;
import org.osgi.framework.BundleContext;
// OCSANA imports
import org.compsysmed.ocsana.internal.ui.control.OCSANAControlPanel;
import org.compsysmed.ocsana.internal.ui.fc.FCResultsPanel;
import org.compsysmed.ocsana.internal.ui.fc.FCmenu;
import org.compsysmed.ocsana.internal.ui.fvs.FVSmenu;
import org.compsysmed.ocsana.internal.ui.results.OCSANAResultsPanel;

import org.compsysmed.ocsana.internal.ui.sfa.SFAmenu;
import org.compsysmed.ocsana.internal.ui.sfaresults.SFAResultsPanel;

public class CyActivator extends AbstractCyActivator {
    @Override
    
    public void start (BundleContext bc) throws Exception {
        // Get Cytoscape internal utilities
        TaskManager<?, ?> taskManager = getService(bc, TaskManager.class);

        CyApplicationManager cyApplicationManager = getService(bc, CyApplicationManager.class);

        CySwingApplication cySwingApplication = getService(bc, CySwingApplication.class);
        CytoPanel cyControlPanel = cySwingApplication.getCytoPanel(CytoPanelName.WEST);
        PanelTaskManager panelTaskManager = getService(bc, PanelTaskManager.class);
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
	
        
    }
    
}
