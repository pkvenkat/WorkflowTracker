package org.cishell.reference.gui.workflow.model;

public class AlgorithmWorkflowItem implements WorkflowItem {
	private String name;
	private Long internalId;

	public  AlgorithmWorkflowItem(String name, Long id)
	{
		this.name = name;
		this.internalId = id;
		
	}
	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Long getIternalId() {
		return internalId;
	}

}
