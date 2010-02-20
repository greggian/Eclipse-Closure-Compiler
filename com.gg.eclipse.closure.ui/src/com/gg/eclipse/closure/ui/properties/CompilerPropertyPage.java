package com.gg.eclipse.closure.ui.properties;


import java.util.ArrayList;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.views.navigator.ResourceComparator;

import com.gg.eclipse.closure.settings.ClosureSettings;
import com.gg.eclipse.closure.settings.ClosureSettingsManager;
import com.gg.eclipse.closure.settings.IClosureSettings;


public class CompilerPropertyPage extends PropertyPage {
	
	private Text outputPathText;

	/**
	 * Constructor for SamplePropertyPage.
	 */
	public CompilerPropertyPage() {
		super();
	}

	private void addCompileOptionsGroup(Composite parent) {		
		
		Group group = new Group(parent, SWT.NONE);
		group.setText("Compiler Output");
		group.setLayout(new GridLayout(3, false));
		group.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		
		// Label for output folder field
		Label outputPathLabel = new Label(group, SWT.NONE);
		outputPathLabel.setText("Default output folder: ");

		// output folder text field
		outputPathText = new Text(group, SWT.SINGLE | SWT.BORDER);
		outputPathText.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		
		Button outputPathButton = new Button(group, SWT.NONE);
		outputPathButton.setText("Browse...");
		outputPathButton.addSelectionListener(new SelectionListener(){
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
			public void widgetSelected(SelectionEvent arg0) {
				String folder = chooseFolder();
				if(folder != null){
					outputPathText.setText(folder);
				}
			}
			
		});

		
	}
	
	private void populateCompileOptionsGroup(final IClosureSettings settings){
		IResource rsrc = (IResource)getElement();
		
		// Populate output folder text field
		final IPath path = settings.getOutputFolder();
		final IPath workspaceRelativePath = rsrc.getFullPath().append(path).makeRelative();
		outputPathText.setText(workspaceRelativePath.toOSString());
		
	}
	
	private String chooseFolder() {		
		IProject curProj = (IProject)getElement();
		
		IWorkspaceRoot root = curProj.getWorkspace().getRoot();
		
		final Class<?>[] acceptedClasses= new Class<?>[] { IProject.class, IFolder.class };
		IProject[] allProjects= root.getProjects();
		ArrayList<IProject> rejectedElements= new ArrayList<IProject>(allProjects.length);
		for (int i= 0; i < allProjects.length; i++) {
			if (!allProjects[i].equals(curProj)) {
				rejectedElements.add(allProjects[i]);
			}
		}
		ViewerFilter filter= new TypedViewerFilter(acceptedClasses, rejectedElements.toArray());

		ILabelProvider lp= new WorkbenchLabelProvider();
		ITreeContentProvider cp= new WorkbenchContentProvider();


		FolderSelectionDialog dialog= new FolderSelectionDialog(getShell(), lp, cp);
		dialog.setTitle("title");
		dialog.setMessage("message");
		dialog.addFilter(filter);
		dialog.setInput(curProj.getProject().getParent());

		IResource initSelection= null;
		initSelection= curProj.findMember("bin");
		dialog.setInitialSelection(initSelection);
		
		dialog.setAllowMultiple(false);
		dialog.setComparator(new ResourceComparator(ResourceComparator.NAME));
		dialog.open();
		
		IContainer result = (IContainer)dialog.getFirstResult();
		if (result != null) {
			try {
				return result.getFullPath().makeRelative().toOSString();
			} catch (Throwable t){
				t.printStackTrace();
			}
		}
		return null;
	}
	
	
	/**
	 * @see PreferencePage#createContents(Composite)
	 */
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		composite.setLayout(layout);
		GridData data = new GridData(GridData.FILL);
		data.grabExcessHorizontalSpace = true;
		composite.setLayoutData(data);

		addCompileOptionsGroup(composite);
		populateCompileOptionsGroup(getPersistedSettings());
		
		return composite;
	}

	protected IClosureSettings getPersistedSettings(){
		final IResource rsrc = (IResource)getElement();
		final ClosureSettingsManager manager = new ClosureSettingsManager(rsrc);
		
		IClosureSettings settings = null;
		try {
			settings = manager.getSettings();
		} catch (CoreException e) {
			settings = ClosureSettingsManager.getDefaultSettings();
		}
		return settings;
	}

	protected void performDefaults() {
		final IClosureSettings defaultSettings = ClosureSettingsManager.getDefaultSettings();
		populateCompileOptionsGroup(defaultSettings);
	}
	
	public boolean performOk() {
		// store the value in the owner text field
		try {
			final IPath specifiedPath = Path.fromOSString(outputPathText.getText());
			final IPath relativePath = specifiedPath.removeFirstSegments(1).makeRelative();
			final IClosureSettings settings = new ClosureSettings(relativePath, "unused");
			
			final IResource rsrc = ((IResource) getElement());
			final ClosureSettingsManager manager = new ClosureSettingsManager(rsrc);
			manager.storeSettings(settings);
		} catch (CoreException e) {
			return false;
		}
		return true;
	}

}