package org.cishell.reference.gui.workflow.controller;

import java.util.LinkedHashMap;

import org.cishell.app.service.datamanager.DataManagerService;
import org.cishell.framework.data.Data;
import org.cishell.framework.data.DataProperty;
import org.cishell.reference.gui.workflow.Activator;
import org.cishell.reference.gui.workflow.Utilities.Constant;
import org.cishell.reference.gui.workflow.model.Workflow;
import org.cishell.reference.gui.workflow.model.NormalWorkflow;
import org.osgi.framework.BundleContext;

public class WorkflowManager {
	static  private WorkflowManager manager = null; 
	private LinkedHashMap<Long, Workflow> map ;
	private Workflow currentWorkflow;
	private Long lastCreatedID;
	private static BundleContext bundleContext;
	
   private	WorkflowManager()
   {
	   map = new LinkedHashMap<Long, Workflow>();
	   lastCreatedID = new Long(1);
	   bundleContext = Activator.getContext();
   }
  static public WorkflowManager getInstance()
  {
	  if(manager == null)
	  {
		  manager = new WorkflowManager();
	  }
	  
	 return manager;
  }
  
  public Workflow createWorkflow(String name, String type)
  {
	  Long newID =getUniqueInternalId();
	  //needed to move this to constants file
	  if(type == Constant.NormalWorkflow){
		  currentWorkflow= new NormalWorkflow(name,newID);
		  map.put(newID, currentWorkflow);
       }
	  return currentWorkflow;
  }
  
  
  public boolean runWorkflow(){
	  DataManagerService dataManager = (DataManagerService)
				bundleContext.getService(
						bundleContext.getServiceReference(
								DataManagerService.class.getName()));
	  Data[] data = dataManager.getSelectedData();
	  System.out.println(data[0].getMetadata().get(DataProperty.LABEL));
	  return true;
  }
  
  
public Long getUniqueInternalId()
  {
	  return lastCreatedID++;
  }
   
}
