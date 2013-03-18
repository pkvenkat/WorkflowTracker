package org.cishell.reference.gui.workflow.model;

import java.util.LinkedHashMap;

public class NormalWorkflow implements Workflow {
	private String name;
	private Long id;
	private LinkedHashMap<Long, WorkflowItem> map ;

	public  NormalWorkflow(String name, Long id)
	{
		this.name = name;
		this.id = id;
		map = new LinkedHashMap<Long, WorkflowItem> ();
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public Long getInternalId() {
		return id;
	}
	@Override
	public void run() {
	   // implemntaion to run the workflow
	}
}
