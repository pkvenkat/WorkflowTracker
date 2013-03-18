/*
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 *
 * Created on Feb 19, 2005 at Indiana University.
 */
package org.cishell.reference.gui.workflow.views;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.algorithm.AlgorithmProperty;
import org.cishell.framework.data.Data;
import org.cishell.framework.data.DataProperty;
import org.cishell.reference.gui.workflow.model.Workflow;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.ServiceReference;

import org.cishell.reference.gui.workflow.Activator;


/**
 * DataModelGUIItem is a wrapper of a DataModel which is used by the
 * DataModelTreeView to hold the items in the TreeView. It adds to the
 * DataModel the notion of having parent and children DataModelTreeItems
 * and keeps track of this information for usage by the TreeView.
 *
 * @author Team IVC
 */
public class DataGUIItem {
	private String brandPluginID;
	
    //images for the defined types
//    private Image matrixIcon;
    private Image treeIcon;
    private Image networkIcon;
    private Image unknownIcon;
    private Image textIcon;
    private Image plotIcon;
    private Image tableIcon;
    private Image databaseIcon;
    private Image rasterImageIcon;
    private Image vectorImageIcon;
    private Image modelIcon;
    private Image rIcon;
    
    private Map<String, Image> typeToImage = new HashMap<String, Image>();
    private Collection<DataGUIItem> children = new ArrayList<DataGUIItem>();
    private Algorithm algorithm;
    private String label;
    private DataGUIItem parent;


    /**
     * Creates a new DataModelGUIItem object.
     *
     * @param model the DataModel this DataModelGUIItem is using
     * @param parent the parent DataModelGUIItem of this DataModelGUIItem
     */
    public DataGUIItem(Algorithm algorithm, DataGUIItem parent) {
        this.algorithm = algorithm;
        
        ServiceReference serviceReference = Activator
				.getSchedulerService().getServiceReference(algorithm);
	
		if (serviceReference != null) {
			label = (String) serviceReference
					.getProperty(AlgorithmProperty.LABEL);
		}
        
        this.parent = parent;
        
        this.brandPluginID = "org.cishell.reference.gui.workflow";
//        matrixIcon      = getImage("matrix.png", this.brandPluginID);
        modelIcon        = getImage("model.jpg", this.brandPluginID);
        registerImage(DataProperty.OTHER_TYPE, unknownIcon);
        
        /********************************************
         * This is a temporary work around solution.
         * Since many algs claims the output data type is MATRIX_TYPE,
         * but in fact it should be TABLE_TYPE. 
         * Should associate MATRIX_TYPE with matrixIcon and clean up
         * related algs.
         * ******************************************/      
        registerImage(DataProperty.MODEL_TYPE, modelIcon);
    }
    
    public DataGUIItem(Workflow workflow, DataGUIItem parent,int type) {
    	label = workflow.getName();    	
    	this.brandPluginID = "org.cishell.reference.gui.workflow";
//      matrixIcon      = getImage("matrix.png", this.brandPluginID);
      modelIcon       = getImage("tree.png", this.brandPluginID);
  

      registerImage(DataProperty.OTHER_TYPE, unknownIcon);
      
      /********************************************
       * This is a temporary work around solution.
       * Since many algs claims the output data type is MATRIX_TYPE,
       * but in fact it should be TABLE_TYPE. 
       * Should associate MATRIX_TYPE with matrixIcon and clean up
       * related algs.
       * ******************************************/      
          registerImage(DataProperty.MODEL_TYPE, modelIcon);  	
    	
    }
    

    public String getLabel() {
		return label;
	}

	public void setAlgorithmLabel(String algorithmLabel) {
		this.label = algorithmLabel;
	}

 
    /**
     * Returns the parent DataModelGUIItem of this DataModelGUIItem, or
     * null if this is the root item.
     *
     * @return the parent DataModelGUIItem of this DataModelGUIItem, or
     * null if this is the root item
     */
    public DataGUIItem getParent() {
        return parent;
    }

    /**
     * Adds the given DataModelGUIItem as a child of this DataModelGUIItem
     *
     * @param item the new child of this DataModelGUIItem
     */
    public void addChild(DataGUIItem item) {
        this.children.add(item);
    }

    /**
     * Returns an array of all of the children of this DataModelGUIItem
     *
     * @return an array of all of the children of this DataModelGUIItem
     */
    public Object[] getChildren() {
        return this.children.toArray();
    }

    /**
     * Removes the given DataModelGUIItem from the collection of children
     * of this DataModelGUIItem.
     *
     * @param item the child of this DataModelGUIItem to remove
     */
    public void removeChild(DataGUIItem item) {
        this.children.remove(item);
    }
    
//    /**
//     * Returns the icon associated with this DataModel for display in IVC.
//     * 
//     * @return the icon associated with this DataModel for display in IVC
//     */
//    public Image getIcon() {
//        Image icon = this.typeToImage.get(dataProperty);
//
//        if (icon == null) {
//        	icon = unknownIcon;
//        }
//
//        return icon;
//    }
    
    public Image getIcon() {
     
        return this.modelIcon;
    }
    
    
    public void registerImage(String type, Image image) {
        this.typeToImage.put(type, image);
    }

    public static Image getImage(String name, String brandPluginID) {
        if (Platform.isRunning()) {
        	String imageLocation =
        		String.format("%sicons%s%s", File.separator, File.separator, name);
            ImageDescriptor imageDescriptor =
            	AbstractUIPlugin.imageDescriptorFromPlugin(brandPluginID, imageLocation);

            if (imageDescriptor != null) {
            	return imageDescriptor.createImage(); 
            } else {
            	String errorMessage = String.format(
            		"Could not find the icon '%s' in '%s'. Using the default image instead.",
            		imageLocation,
            		brandPluginID);
            	System.err.println(errorMessage);

            	return getDefaultImage();
            }
   
        } else {
        	String format =
        		"Could not obtain the image '%s' in '%s', since the platform was not " +
        		"running (?). Using the default image instead.";
        	String errorMessage = String.format(format, name, brandPluginID);
        	System.err.println(errorMessage);

        	return getDefaultImage();
        }            
    }
    
    private static final String DEFAULT_IMAGE_LOCATION = File.separator + "unknown.png";
    
    private static Image getDefaultImage() {
    	String thisPluginID = "org.cishell.reference.gui.datamanager"; //TODO: don't hardcode this
    	ImageDescriptor imageDescriptor = AbstractUIPlugin.imageDescriptorFromPlugin(
    			thisPluginID, DEFAULT_IMAGE_LOCATION);
    	return imageDescriptor.createImage();
    }
}
