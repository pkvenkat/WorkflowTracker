package org.cishell.reference.gui.workflow.model;

public interface Workflow {
	
	public String  getName();
	// user can create workflows with the same name
	public Long getInternalId();
	public void run();
	
}