package org.cishell.reference.gui.workflow;
import java.io.File;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;
import org.cishell.app.service.scheduler.SchedulerService;
import org.cishell.reference.gui.workflow.views.WorkflowView;
import org.cishell.reference.gui.workspace.CIShellApplication;



public class Activator extends AbstractUIPlugin implements IStartup{

public static final String PLUGIN_ID = "org.cishell.reference.gui.workflow";
	private static Activator plugin;
	private static BundleContext context;
	private boolean waitForBundleContext;
    private LogService logger;

	private static final int ATTEMPTS_TO_FIND_TOOLBAR = 15;
	private static final int SLEEP_TIME = 100;
	
	public Activator() {
		plugin = this;
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		Activator.context = context;
		if (waitForBundleContext) {
			//System.out.println( "Inside Start");
			earlyStartup();
		}
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public static Activator getDefault() {
		return plugin;
	}
	
	public static BundleContext getContext() {
		return context;
	}
	
  public static SchedulerService getSchedulerService() {
		ServiceReference serviceReference = context.getServiceReference(SchedulerService.class.getName());
		SchedulerService manager = null;
		
		if (serviceReference != null) {
			manager = (SchedulerService) context.getService(serviceReference);
		}
		
		return manager;
	}
	
    public static Image createImage(String name){
       if(Platform.isRunning()){
            return AbstractUIPlugin.
            	imageDescriptorFromPlugin(PLUGIN_ID, 
            	        File.separator + "icons" + File.separator + name).
            	        createImage();
        }
        else {
            return null;
        }
                    
    }

	public void earlyStartup() {
		System.err.println( "Inside early Start");

		if (context != null) {
				Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					System.err.println( "Running");
					 
					Action scheduler = new WorkflowAction();
					IMenuManager manager = CIShellApplication.getMenuManager();
					
					
					IMenuManager newManager = null;
					for (int i = 0; i < ATTEMPTS_TO_FIND_TOOLBAR && newManager == null; i++) {
						try {
						Thread.sleep(SLEEP_TIME);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						newManager = manager.findMenuUsingPath("File");
					}
					
					manager = manager.findMenuUsingPath("File");
					
					if (manager == null) {
						System.err.println( "Unable to add workflow to File menu, since File menu does not exist.");
					} else {
						manager.appendToGroup("start", scheduler);
					}
					System.err.println( "Running2");
					WorkflowView view = WorkflowView.getDefault();
					boolean visible = view != null
							&& PlatformUI.getWorkbench()
									.getActiveWorkbenchWindow().getActivePage()
									.isPartVisible(view);
					scheduler.setChecked(visible);
					IMenuManager otherManagerReference = CIShellApplication.getMenuManager();
					if(otherManagerReference == null) {
						System.err.println("The menu manager is still null. Surprise.");
					} else {
						otherManagerReference.update(true);
					}
				}
			});
			waitForBundleContext = false;
		}
		else {
			waitForBundleContext = true;
		}
	}
	
    private class WorkflowAction extends Action {
        public WorkflowAction(){
            super("Workflow", IAction.AS_CHECK_BOX);
            setId("workflow");
        }
        
        public void run(){
            if(this.isChecked()){
				System.err.println( "Checked");
	            try {	            	
                    IWorkbench bench = PlatformUI.getWorkbench();
                    if(bench != null)
                    {
                    	IWorkbenchWindow window =	bench.getActiveWorkbenchWindow();
                    if(window!= null)	{
                    	window.getActivePage().showView(WorkflowView.ID_VIEW);
                         }
                    }
                    
                } catch (PartInitException e) {
                    e.printStackTrace();
                }
            }
            else{
            	IWorkbench bench = PlatformUI.getWorkbench();
                if(bench != null)
                {
                	IWorkbenchWindow window =	bench.getActiveWorkbenchWindow();
                    if(window!= null)	{
                	window.getActivePage().hideView(WorkflowView.getDefault());
                    }
                }
				System.err.println( "UnChecked");

           }
        }	    
    }
	
	
}