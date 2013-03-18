/* **************************************************************************** 
 * CIShell: Cyberinfrastructure Shell, An Algorithm Integration Framework.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Apache License v2.0 which accompanies
 * this distribution, and is available at:
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Created on Aug 21, 2006 at Indiana University.
 * Changed on Dec 19, 2006 at Indiana University
 * 
 * Contributors:
 * 	   Weixia(Bonnie) Huang, Bruce Herr, Ben Markines
 *     School of Library and Information Science, Indiana University 
 * ***************************************************************************/
package org.cishell.reference.gui.workflow.views;


import java.util.Calendar;
import java.util.Hashtable;
import java.util.Map;


import org.cishell.app.service.scheduler.SchedulerListener;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.algorithm.AlgorithmExecutionException;
import org.cishell.framework.algorithm.AlgorithmProperty;
import org.cishell.framework.data.Data;
import org.cishell.reference.gui.workflow.Utilities.Constants;
import org.cishell.reference.gui.workflow.controller.WorkflowManager;
import org.cishell.reference.gui.workflow.model.Workflow;
import org.cishell.reference.gui.workflow.model.NormalWorkflow;
import org.cishell.reference.gui.workflow.views.DataGUIItem;
import org.cishell.reference.gui.workflow.views.DataTreeContentProvider;
import org.cishell.reference.gui.workflow.views.DataTreeLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.part.ViewPart;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

import org.cishell.reference.gui.workflow.Activator;


/**
 * Creates and maintains the overall GUI for the scheduler.  Controls the
 * table and controls (moving, removing, etc.).
 * 
 * @author Ben Markines (bmarkine@cs.indiana.edu)
 */
public class WorkflowView extends ViewPart implements SchedulerListener {
    private static WorkflowView workFlowView;
    public static final String ID_VIEW = "org.cishell.reference.gui.workflow.views.WorkflowView";
	private TreeViewer viewer;
	private DataGUIItem rootItem, currentWorkFlowItem;
	private Tree tree;    
	private Map<Algorithm, DataGUIItem> alToDataGUIItem;
	private Menu menu;
	private SaveListener saveListener;

    /**
     * Registers itself to a model, and creates the map from algorithm to 
     * GUI item.
     */
    public WorkflowView() {
 		workFlowView = this;
    }
    
    /**
     * Get the current scheduler view
     * @return The scheduler view
     */
    public static WorkflowView getDefault() {
    	return workFlowView;
    }

    /**
     * Creates buttons, table, and registers listeners
     * 
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 * @param parent The SWT parent
	 */
    @Override
    public void createPartControl(Composite parent) {
    	
     /*   Composite control = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 4;
        control.setLayout(layout);
        
        Button removeButton = new Button(control, SWT.PUSH);
        removeButton.setText("Remove From WorkflowList");
        removeButton.setEnabled(true); */
    	
    	this.viewer = new TreeViewer(parent);
		this.viewer.setContentProvider(new DataTreeContentProvider());
		this.viewer.setLabelProvider(new DataTreeLabelProvider());

		this.rootItem = new DataGUIItem(null, null);
		this.viewer.setInput(this.rootItem);
		this.viewer.expandAll();
		this.tree = this.viewer.getTree();
		this.tree.addMouseListener(new ContextMenuListener());

		// Setup the context menu for the tree.
		this.menu = new Menu(tree);
		this.menu.setVisible(false);

		MenuItem saveItem = new MenuItem(this.menu, SWT.PUSH);
		saveItem.setText("Save");
		this.saveListener = new SaveListener();
		saveItem.addListener(SWT.Selection, this.saveListener);

          addNewWorkflow("Default Workflow");
		// Grab the tree and add the appropriate listeners.
		SchedulerContentModel.getInstance().register(this);             
     }

	public void setFocus() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void algorithmScheduled(Algorithm algorithm, Calendar time) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void algorithmRescheduled(Algorithm algorithm, Calendar time) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void algorithmUnscheduled(Algorithm algorithm) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void algorithmStarted(Algorithm algorithm) {
		ServiceReference serviceReference = Activator
				.getSchedulerService().getServiceReference(algorithm);
		String algorithmLabel = "";
		if (serviceReference != null) {
			algorithmLabel = (String) serviceReference
					.getProperty(AlgorithmProperty.LABEL);
		}
		System.out.println("Algorithm with name"+algorithmLabel+"started");
		final DataGUIItem dataItem=	new DataGUIItem(algorithm, this.currentWorkFlowItem);
		this.currentWorkFlowItem.addChild(dataItem);
		guiRun(new Runnable() {
			public void run() {
				if (!tree.isDisposed()) {
					// update the TreeView
					WorkflowView.this.viewer.refresh();
					// context menu may need to have options enabled/disabled
					// based on the new selection
					// update the global selection
					WorkflowView.this.viewer.expandToLevel(dataItem, 0);
				}
			}
		});
	
		
	}
	
	public void addNewWorkflow(String name)
	{
		Workflow workfFlow = WorkflowManager.getInstance().createWorkflow(name, Constants.NormalWorkflow);
		final DataGUIItem dataItem=	new DataGUIItem(workfFlow, this.currentWorkFlowItem, 1);
		this.currentWorkFlowItem =  dataItem;
		this.rootItem.addChild(dataItem);
		guiRun(new Runnable() {
			public void run() {
				if (!tree.isDisposed()) {
					// update the TreeView
					WorkflowView.this.viewer.refresh();
					// context menu may need to have options enabled/disabled
					// based on the new selection
					// update the global selection
					WorkflowView.this.viewer.expandToLevel(dataItem, 0);
				}
			}
		});	
	}
	
	@Override
	public void algorithmFinished(Algorithm algorithm, Data[] createdData) {
		// TODO Auto-generated method stub
		
		ServiceReference serviceReference = Activator
				.getSchedulerService().getServiceReference(algorithm);
		String algorithmLabel = "";
		if (serviceReference != null) {
			algorithmLabel = (String) serviceReference
					.getProperty(AlgorithmProperty.LABEL);
		}
		 System.out.println("Algorithm with name"+algorithmLabel+"finished");
		
	}

	@Override
	public void algorithmError(Algorithm algorithm, Throwable error) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void schedulerRunStateChanged(boolean isRunning) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void schedulerCleared() {
		// TODO Auto-generated method stub
		
	}
	
	private void guiRun(Runnable run) {
		if (Thread.currentThread() == Display.getDefault().getThread()) {
			run.run();
		} else {
			Display.getDefault().syncExec(run);
		}
	}
	
	private class SaveListener implements Listener {
		public void handleEvent(Event event) {
					}
	}

	/*
	 * Listens for right-clicks on TreeItems and opens the context menu when
	 * needed.
	 */
	private class ContextMenuListener extends MouseAdapter {
		public void mouseUp(MouseEvent event) {
			System.out.println("Mouse Event");
			if (event.button == 3) {
				TreeItem item =
					WorkflowView.this.tree.getItem(new Point(event.x, event.y));

				if (item != null) {
					WorkflowView.this.tree.getMenu().setVisible(true);
				} else {
					System.out.println("Item is null");

					WorkflowView.this.tree.getMenu().setVisible(false);
				}
			}
		}
	}

	
	
}