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


import java.io.File;
import java.util.Calendar;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;


import org.cishell.app.service.scheduler.SchedulerListener;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.algorithm.AlgorithmCreationFailedException;
import org.cishell.framework.algorithm.AlgorithmExecutionException;
import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.algorithm.AlgorithmProperty;
import org.cishell.framework.algorithm.AllParametersMutatedOutException;
import org.cishell.framework.algorithm.ParameterMutator;
import org.cishell.framework.data.Data;
import org.cishell.reference.gui.menumanager.menu.AlgorithmWrapper;
import org.cishell.reference.gui.workflow.Utilities.Constant;
import org.cishell.reference.gui.workflow.controller.WorkflowManager;
import org.cishell.reference.gui.workflow.model.AlgorithmWorkflowItem;
import org.cishell.reference.gui.workflow.model.SchedulerContentModel;
import org.cishell.reference.gui.workflow.model.Workflow;
import org.cishell.reference.gui.workflow.model.NormalWorkflow;
import org.cishell.reference.gui.workflow.model.WorkflowItem;
import org.cishell.reference.gui.workflow.views.WorkflowGUI;
import org.cishell.reference.gui.workflow.views.DataTreeContentProvider;
import org.cishell.reference.gui.workflow.views.DataTreeLabelProvider;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
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
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;
import org.osgi.service.metatype.AttributeDefinition;
import org.osgi.service.metatype.MetaTypeProvider;
import org.osgi.service.metatype.MetaTypeService;
import org.osgi.service.metatype.ObjectClassDefinition;

import org.cishell.reference.gui.workflow.Activator;
import org.osgi.framework.Constants;
//import org.cishell.service.metadata.*;

/**
 * Creates and maintains the overall GUI for the scheduler.  Controls the
 * table and controls (moving, removing, etc.).
 * 
 */
public class WorkflowView extends ViewPart implements SchedulerListener {
    private static WorkflowView workFlowView;
    public static final String ID_VIEW = "org.cishell.reference.gui.workflow.views.WorkflowView";
	private TreeViewer viewer;
	private WorkflowGUI rootItem, currentWorkFlowItem;
	private Tree tree;    
	private Map<Algorithm, WorkflowGUI> alToDataGUIItem;
	private Menu menu;
	private Menu whiteSpacemenu;
	private SaveListener saveListener;
	private RunListener runListener;
	private Algorithm errorAlgorithm;


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
    	
        /*Composite control = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        control.setLayout(layout);
        //control.setSize(parent.getSize());
        Button removeButton = new Button(control, SWT.PUSH);
        removeButton.setText("New Workflow");
        removeButton.setEnabled(true); */
        
    	this.viewer = new TreeViewer(parent);
		this.viewer.setContentProvider(new DataTreeContentProvider());
		this.viewer.setLabelProvider(new DataTreeLabelProvider());
		

		this.rootItem = new WorkflowGUI(null, null,2);
		this.viewer.setInput(this.rootItem);
		this.viewer.expandAll();
		this.tree = this.viewer.getTree();
		this.tree.addSelectionListener(new DatamodelSelectionListener());
		this.tree.addMouseListener(new ContextMenuListener());
         
		/*final RowData rowData = new RowData(); 
		rowData.height = 243; 
		rowData.width = 168; 
		this.tree.setLayoutData(rowData);*/
		// Setup the context menu for the tree.
		
		this.menu = new Menu(tree);
		this.menu.setVisible(false);

		MenuItem saveItem = new MenuItem(this.menu, SWT.PUSH);
		saveItem.setText("Save");
		this.saveListener = new SaveListener();
		saveItem.addListener(SWT.Selection, this.saveListener);
		
		MenuItem runItem = new MenuItem(this.menu, SWT.PUSH);
		runItem.setText("Run");
		this.runListener = new RunListener();
		runItem.addListener(SWT.Selection,runListener);
		
		MenuItem deleteItem = new MenuItem(this.menu, SWT.PUSH);
		deleteItem.setText("Delete");
		
		//this.tree.setMenu(this.menu);
		
		
		//create white spacemennu
		
		this.whiteSpacemenu = new Menu(tree);
		this.whiteSpacemenu.setVisible(false);
		
		MenuItem newItem = new MenuItem(this.whiteSpacemenu, SWT.PUSH);
		newItem.setText("New Workflow");

		newItem.addListener(SWT.Selection, new NewWorkflow());
		
        addNewWorkflow("Default Workflow");
		SchedulerContentModel.getInstance().register(this);             
     }
    protected String getMetaTypeID(ServiceReference ref) {
		String pid = (String) ref.getProperty(Constants.SERVICE_PID);
		String metatype_pid = (String) ref.getProperty(AlgorithmProperty.PARAMETERS_PID);
		//String metatype_pid ="parameters_pid2;
		if (metatype_pid == null) {
			metatype_pid = pid;
		}

		return metatype_pid;
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
		
		
	}
	
	public void addNewWorkflow(String name)
	{
		Workflow workfFlow = WorkflowManager.getInstance().createWorkflow(name, Constant.NormalWorkflow);
		final WorkflowGUI dataItem=	new WorkflowGUI(workfFlow, this.currentWorkFlowItem, 1);
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
		if(algorithm.equals(errorAlgorithm)) return;
		System.out.println("Algorithm class name="+algorithm.getClass());
		Dictionary<String, Object> parameters = null;
		if(algorithm instanceof AlgorithmWrapper){
		
			AlgorithmWrapper algo = (AlgorithmWrapper)algorithm;
			parameters= algo.getParameters();
		}
		//get service reference
		ServiceReference serviceReference = Activator
				.getSchedulerService().getServiceReference(algorithm);
		String algorithmLabel = "";
		if (serviceReference != null) {
			algorithmLabel = (String) serviceReference
					.getProperty(AlgorithmProperty.LABEL);
		}
		AlgorithmFactory factory =
				(AlgorithmFactory) Activator.getContext().getService(serviceReference);

		String pid = (String) serviceReference.getProperty(Constants.SERVICE_PID);
		AlgorithmWorkflowItem wfi = new AlgorithmWorkflowItem(algorithmLabel, WorkflowManager.getInstance().getUniqueInternalId(),serviceReference);
		wfi.setParameters(parameters);
		currentWorkFlowItem.getWorkflow().add(wfi);
		System.out.println("Algorithm with name"+algorithmLabel+"started");
		final WorkflowItemGUI dataItem=	new WorkflowItemGUI(wfi, this.currentWorkFlowItem);
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
		
		// Create algorithm parameters.
		String metatypePID = getMetaTypeID(serviceReference);
		//get the input parameters 
		MetaTypeProvider provider = null;

		try {
			provider = getPossiblyMutatedMetaTypeProvider(metatypePID, pid, factory, serviceReference);
		} catch (AlgorithmCreationFailedException e) {
			String format =
				"An error occurred when creating the algorithm \"%s\" with the data you " +
				"provided.  (Reason: %s)";
			String logMessage = String.format(
				format,
				serviceReference.getProperty(AlgorithmProperty.LABEL),
				e.getMessage());
			//log(LogService.LOG_ERROR, logMessage, e);

			return ;
		} catch (Exception e) {
			//log(LogService.LOG_ERROR, e.getMessage(), e);

			return ;
		}	
		
		if(parameters == null || parameters.isEmpty() ) return;
				
		final GeneralTreeItem  paramItem = new GeneralTreeItem("Parameters",Constant.Label, dataItem,getImage("matrix.png","org.cishell.reference.gui.workflow"));
		dataItem.addChild(paramItem);
		ObjectClassDefinition obj = provider.getObjectClassDefinition(metatypePID, null);		
		if (obj != null) {
			AttributeDefinition[] attr =
				obj.getAttributeDefinitions(ObjectClassDefinition.ALL);

			for (int i = 0; i < attr.length; i++) {
				String id = attr[i].getID();
				String name = attr[i].getName();
				String value =(String)parameters.get(id);
				//System.out.println( "id=" +id +"name="+ name +"\n");
				GeneralTreeItem  paramName = new GeneralTreeItem( name, Constant.ParameterName, paramItem, getImage("matrix.png","org.cishell.reference.gui.workflow"));
				paramItem.addChildren(paramName);
				GeneralTreeItem  paramValue = new GeneralTreeItem( value, Constant.ParameterValue, paramName, getImage("matrix.png","org.cishell.reference.gui.workflow"));
				paramName.addChildren(paramValue);				
			}
		}
		
		guiRun(new Runnable() {
			public void run() {
				if (!tree.isDisposed()) {
					// update the TreeView
					WorkflowView.this.viewer.refresh();
					// context menu may need to have options enabled/disabled
					// based on the new selection
					// update the global selection
					WorkflowView.this.viewer.expandToLevel(paramItem, 0);
				}
			}
		});
		

	}

	@Override
	public void algorithmError(Algorithm algorithm, Throwable error) {
		this.errorAlgorithm = algorithm;
		
		
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
	protected MetaTypeProvider getPossiblyMutatedMetaTypeProvider(
			String metatypePID, String pid,	AlgorithmFactory factory, ServiceReference serviceRef)
			throws AlgorithmCreationFailedException {
		MetaTypeProvider provider = null;
		MetaTypeService metaTypeService = (MetaTypeService)
			Activator.getService(MetaTypeService.class.getName());
		
		//ParameterMetaTypeProvider provider2  =(ParameterMetaTypeProvider)Activator.getService(ParameterMetaTypeProvider.class.getName());
		if (metaTypeService != null) {
			provider = metaTypeService.getMetaTypeInformation(serviceRef.getBundle());
		}
		

		if ((factory instanceof ParameterMutator) && (provider != null)) {
			try {
				ObjectClassDefinition objectClassDefinition =
					provider.getObjectClassDefinition(metatypePID, null);

				if (objectClassDefinition == null) {
					//logNullOCDWarning(pid, metatypePID);
				}

				/*try {
					objectClassDefinition =
						((ParameterMutator) factory).mutateParameters(data, objectClassDefinition);

					if (objectClassDefinition != null) {
						provider = new BasicMetaTypeProvider(objectClassDefinition);
					}
				} catch (AllParametersMutatedOutException e) {
					provider = null;
				}*/
			} catch (IllegalArgumentException e) {
				//log(LogService.LOG_DEBUG, pid + " has an invalid metatype id: " + metatypePID, e);
			}
		}

	/*	if (provider != null) {
			provider = wrapProvider(serviceRef, provider);
		}*/

		return provider;
	}


	/*
	 * Listens for right-clicks on TreeItems and opens the context menu when
	 * needed.
	 */
	private class ContextMenuListener extends MouseAdapter {
		public void mouseUp(MouseEvent event) {
			//System.out.println("Mouse Event");
			if (event.button == 3) {
				System.out.println(" Inside Mouse Event");

				TreeItem item =
					WorkflowView.this.tree.getItem(new Point(event.x, event.y));

				if (item != null) {
					System.out.println("Item is not null");
					//WorkflowView.this.tree.getMenu().setVisible(false);
					WorkflowView.this.tree.setMenu(WorkflowView.this.menu);
					WorkflowView.this.tree.getMenu().setVisible(true);
				} else {
					//WorkflowView.this.tree.getMenu().setVisible(false);
					WorkflowView.this.tree.setMenu(WorkflowView.this.whiteSpacemenu);
					WorkflowView.this.tree.getMenu().setVisible(true);
				}
			}
		}
	}
    
	public boolean isRootItem(WorkflowGUI wfg )
	{
		if (this.rootItem.equals(wfg))
			return true;
		return false;
	}
	
	 public static Image getImage(String name, String brandPluginID) {
	        if (Platform.isRunning()) {
	        	String imageLocation =
	        		String.format("%sicons%s%s", File.separator, File.separator, name);
	            ImageDescriptor imageDescriptor =
	            	AbstractUIPlugin.imageDescriptorFromPlugin(brandPluginID, imageLocation);

	            if (imageDescriptor != null) {
	            	return imageDescriptor.createImage(); 
	            } else {
	            	String errorMessage = String.format(
	            		"Could not find the icon '%s' in '%s'. Using the default image instead.",
	            		imageLocation,
	            		brandPluginID);
	            	System.err.println(errorMessage);
                    //need to change
	            	return WorkflowItemGUI.getDefaultImage();
	            }
	   
	        } else {
	        	String format =
	        		"Could not obtain the image '%s' in '%s', since the platform was not " +
	        		"running (?). Using the default image instead.";
	        	String errorMessage = String.format(format, name, brandPluginID);
	        	System.err.println(errorMessage);
                //need to change
	        	return WorkflowItemGUI.getDefaultImage();
	        }            
	    }

	
	
	private class DatamodelSelectionListener extends SelectionAdapter {
		public void widgetSelected(SelectionEvent e) {
			//System.out.println("DatamodelSelectionListener called ");
		     Tree tree = (Tree) e.getSource();
			 TreeItem[] selection = tree.getSelection();
			 
			for (int i = 0; i < selection.length; i++) {
				//String = ((DataGUIItem) selection[i].getData()).getModel();	
			}
		}
	}

	private class SaveListener implements Listener {
		public void handleEvent(Event event) {
					}
	}
	
	private class RunListener implements Listener {
		public void handleEvent(Event event) {
			System.out.print("Inside the Run Listner");
		    TreeItem[] items =	WorkflowView.this.tree.getSelection();
		    if(items.length !=1) return;
			System.out.print("only one item selected"+items[0].getClass());
			
		    WorkflowTreeItem itm =(WorkflowTreeItem)items[0].getData();
		    String type = itm.getType();
		    if(type == Constant.Workflow)
		    {
				System.out.print("Is of type workflow");

			   ((WorkflowGUI)itm).getWorkflow().run();
		    }
		}
	}

	private class NewWorkflow implements Listener {
		public void handleEvent(Event event) {
			WorkflowView.this.addNewWorkflow("Test3");
					}
	}
	
	
	
	
	
}