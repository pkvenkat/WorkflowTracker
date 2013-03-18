package org.cishell.reference.gui.workflow.controller;

import java.util.LinkedHashMap;

import org.cishell.reference.gui.workflow.Utilities.Constants;
import org.cishell.reference.gui.workflow.model.Workflow;
import org.cishell.reference.gui.workflow.model.NormalWorkflow;

public class WorkflowManager {
	static  private WorkflowManager manager = null; 
	private LinkedHashMap<Long, Workflow> map ;
	private Workflow currentWorkflow;
	private Long lastCreatedID;
	
   private	WorkflowManager()
   {
	   map = new LinkedHashMap<Long, Workflow>();
	   lastCreatedID = new Long(1);
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
	  if(type == Constants.NormalWorkflow){
		  currentWorkflow= new NormalWorkflow(name,newID);
		  map.put(newID, currentWorkflow);
       }
	  return currentWorkflow;
  }
  
  private Long getUniqueInternalId()
  {
	  return lastCreatedID +1;
  }
   
}
