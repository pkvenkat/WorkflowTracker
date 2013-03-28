package org.cishell.reference.gui.workflow.model;

import org.cishell.framework.algorithm.Algorithm;

public class AlgorithmWorkflowItem implements WorkflowItem {
	private String name;
	private Long internalId;
	private Algorithm algo;
	

	public  AlgorithmWorkflowItem(String name, Long id, Algorithm algo)
	{
		this.name = name;
		this.internalId = id;
		this.algo = algo;		
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
	
	public Algorithm getAlgorithm()
	{
		return algo;
	}

}
