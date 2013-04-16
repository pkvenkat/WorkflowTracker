package org.cishell.reference.gui.workflow.views;

import org.eclipse.swt.graphics.Image;


public interface WorkflowTreeItem {
	public String getLabel();
	public String getType();
	public WorkflowTreeItem getParent();
	public Image getIcon();
	public Object[] getChildren();
	public void setLabel(String label);

}
