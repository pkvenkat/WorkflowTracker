package org.cishell.reference.gui.workflow.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.cishell.app.service.datamanager.DataManagerService;
import org.cishell.framework.algorithm.AlgorithmProperty;
import org.cishell.framework.algorithm.ProgressMonitor;
import org.cishell.framework.data.Data;
import org.cishell.framework.data.DataProperty;
import org.cishell.service.conversion.Converter;
import org.cishell.service.conversion.DataConversionService;
import org.eclipse.core.internal.registry.osgi.Activator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Table;
import org.osgi.framework.BundleContext;

public class NormalWorkflow implements Workflow {
	private String name;
	private Long id;
	private LinkedHashMap<Long, WorkflowItem> map;

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
		System.out.println("Running workflow");
		BundleContext bundleContext = Activator.getContext();
		DataManagerService dataManager = (DataManagerService)
				bundleContext.getService(
						bundleContext.getServiceReference(
								DataManagerService.class.getName()));
		System.out.println("data manager for workflow");
	  Data[] data = dataManager.getSelectedData();
		System.out.println("data manager is not null"+ "map size="+map.size());

	 // if(data[0]!= null)
	   // System.out.println(data[0].getMetadata().get(DataProperty.LABEL));

		for(Map.Entry<Long, WorkflowItem> entry: map.entrySet())
		{
			System.out.println("Inside for loop for ");
			WorkflowItem item = entry.getValue();
			if(item instanceof AlgorithmWorkflowItem)
			{
				AlgorithmWorkflowItem algo = (AlgorithmWorkflowItem)item;				
				algo.setInputData(data);
				System.out.println("Running Algorithm" + algo.getName());
				data = (Data[])algo.run();		
				System.out.println("Completed Running Algorithm" + algo.getName());

			}			
		}
	}

	@Override
	public void add(WorkflowItem item) {
        map.put(item.getIternalId(), item);		
	}

<<<<<<< HEAD
	@Override
	public void remove(WorkflowItem item) {
		map.remove(item.getIternalId());
		
	}
=======
	
>>>>>>> aa7ebf91539eeb77d860d50426e957a5969914cb
	
    	
}
