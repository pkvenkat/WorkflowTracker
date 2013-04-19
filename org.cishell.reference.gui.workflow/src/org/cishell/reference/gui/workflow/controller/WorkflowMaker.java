package org.cishell.reference.gui.workflow.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFileChooser;

import org.cishell.reference.gui.workflow.model.AlgorithmWorkflowItem;
import org.cishell.reference.gui.workflow.model.NormalWorkflow;
import org.cishell.reference.gui.workflow.model.Workflow;
import org.cishell.reference.gui.workflow.model.WorkflowItem;
import org.cishell.reference.gui.workflow.views.WorkflowView;
import org.eclipse.swt.widgets.Display;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.StaxDriver;

public class WorkflowMaker {
	public WorkflowMaker(){
		
	}

	public void save(){		
	  write();
	}
	
	public synchronized void load(){
		new Thread(
				new Runnable(){
					public void run(){

						JFileChooser fileChooser = new JFileChooser();
						File currentDirectory = null;
						fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
						fileChooser.setCurrentDirectory(currentDirectory);
						fileChooser.showOpenDialog(null);
						File file = fileChooser.getSelectedFile();
						FileReader freader;
						BufferedReader in;
						XStream reader;
						
						if( file != null){
							try {
								freader = new FileReader(file);
								in = new BufferedReader(freader);
								reader = new XStream(new StaxDriver());
								reader.setClassLoader(WorkflowSaver.class.getClassLoader());
								WorkflowSaver saver =(WorkflowSaver)  reader.fromXML(in) ;
		//						System.out.println("name of the current workflow="+saver.getCurrentWorkflow().getName());
								System.out.println("class name" +saver.getClass());
								//print all the VALUES
								WorkflowManager mgr =WorkflowManager.getInstance();
								List<Workflow> list = new ArrayList<Workflow>();
								 for (Map.Entry<Long, Workflow> entry : saver.getMap().entrySet())
								   {
									 System.out.println("\n Load unique id ="+ entry.getKey());
									 Workflow val =  entry.getValue();
									 list.add(val);
									 Long id = mgr.getUniqueInternalId();
									 System.out.println("\n changed unique id ="+ id);
									 val.setInternalId(id);
									 mgr.addWorkflow(id,val);									
								  }	
								 
								 final List<Workflow> wfList = new ArrayList<Workflow>(list);
								 Display.getDefault().asyncExec(new Runnable() {
									    public void run() {
									    	for(Workflow wf :wfList)
									    	{
											 WorkflowView.getDefault().addWorflowtoUI(wf);
									    	}
									    }
									});
								return ;
							} catch (FileNotFoundException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
		
						}
		
						return;
					}
					
				}
				).start();
	}
	
	
	public void write() {
		System.out.println("We are in write");
		
		new Thread(
				new Runnable(){
						public void run() {
		XStream writer = new XStream(new StaxDriver());
		writer.autodetectAnnotations(true);

		//writer.alias("workflowmaker", WorkflowMaker.class );
		String xml = writer.toXML(new WorkflowSaver());
		File currentDirectory = null;
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setCurrentDirectory(currentDirectory);
		fileChooser.showSaveDialog(null);
		System.out.println("After null");
		File file = fileChooser.getSelectedFile();
		//File file = new File(state.getFilename());
		if (file != null) {
			try {
				FileWriter fstream;
				fstream = new FileWriter(file);
				BufferedWriter out = new BufferedWriter(fstream);
				out.write(xml);
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		}
	}).start();

	}

	
}

