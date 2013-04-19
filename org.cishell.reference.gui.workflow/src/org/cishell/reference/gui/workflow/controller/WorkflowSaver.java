package org.cishell.reference.gui.workflow.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.LinkedHashMap;

import javax.swing.JFileChooser;

import org.cishell.reference.gui.workflow.model.Workflow;

//import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

public class WorkflowSaver {
	
	private	LinkedHashMap<Long, Workflow> map;
	private Workflow currentWorkflow;
	
	public WorkflowSaver()
	{
		this.currentWorkflow =	WorkflowManager.getInstance().getCurrentWorkflow();
		this.map = WorkflowManager.getInstance().getMap();
	}

	public LinkedHashMap<Long, Workflow> getMap() {
		return map;
	}

	public void setMap(LinkedHashMap<Long, Workflow> map) {
		this.map = map;
	}

	public Workflow getCurrentWorkflow() {
		return currentWorkflow;
	}

	public void setCurrentWorkflow(Workflow currentWorkflow) {
		this.currentWorkflow = currentWorkflow;
	}		
				
}
