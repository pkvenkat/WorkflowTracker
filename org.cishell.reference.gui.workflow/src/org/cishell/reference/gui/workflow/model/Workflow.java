package org.cishell.reference.gui.workflow.model;

public interface Workflow {
	
	public String  getName();
	// user can create workflows with the same name
	public Long getInternalId();
	public void run();
	public void add(WorkflowItem item);
	public void remove(WorkflowItem item);

}
