package org.cishell.reference.gui.workflow.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.JFileChooser;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

public class WorkflowSaver {

	public void write() {
		System.out.println("We are in write");
		
		new Thread(
				new Runnable(){
						public void run() {
        //String xml = "Hi how are u??";
		XStream writer = new XStream(new StaxDriver());
		String xml = writer.toXML(WorkflowManager.getInstance());
		File currentDirectory = null;
		JFileChooser fileChooser = new JFileChooser();
		System.out.println("Before try catch");
		//System.out.println("Currentdirectory is:" +currentDirectory );
		/*try {
			URL temp=getClass().getClassLoader().getResource("");
			System.out.println("URL is:" + temp );
			currentDirectory = new File(temp.toURI());
			System.out.println("Currentdirectory is:" +currentDirectory );
		} catch (URISyntaxException e1) {
			System.out.println("An exception! Oops!" + e1);
		}*/
		
		System.out.println("After try catch");
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		//fileChooser.setCurrentDirectory(currentDirectory);
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
	  
//		file.delete();
		}
	}).start();
}

}
