package com.gg.eclipse.closure.ui.properties;


import java.util.ArrayList;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.views.navigator.ResourceComparator;


public class SamplePropertyPage extends PropertyPage {


	private static final int TEXT_FIELD_WIDTH = 50;
	private static final String OUTPUT_PATH_PROPERTY = "CLOSURE_OUTPUT_PATH";
	private static final String DEFAULT_OUTPUT_PATH = "bin";

	private Text outputPathText;

	/**
	 * Constructor for SamplePropertyPage.
	 */
	public SamplePropertyPage() {
		super();
	}

	/*
	private void addSeparator(Composite parent) {
		Label separator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		separator.setLayoutData(gridData);
	}
	*/

	private void addSection(Composite parent) {
		Composite composite = createDefaultComposite(parent);

		// Label for owner field
		Label outputPathLabel = new Label(composite, SWT.NONE);
		outputPathLabel.setText("Output Path: ");

		// Owner text field
		outputPathText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		GridData gd = new GridData();
		gd.widthHint = convertWidthInCharsToPixels(TEXT_FIELD_WIDTH);
		outputPathText.setLayoutData(gd);

		
		Button outputPathButton = new Button(composite, SWT.NONE);
		outputPathButton.addSelectionListener(new SelectionListener(){

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				String folder = chooseFolder();
				if(folder != null){
					outputPathText.setText(folder);
				}
			}
			
		});
		
		// Populate owner text field
		try {
			String owner =
				((IResource) getElement()).getPersistentProperty(
					new QualifiedName("", OUTPUT_PATH_PROPERTY));
			outputPathText.setText((owner != null) ? owner : DEFAULT_OUTPUT_PATH);
		} catch (CoreException e) {
			outputPathText.setText(DEFAULT_OUTPUT_PATH);
		}
	}
	
	
	private String chooseFolder() {
		String initPath= ""; //$NON-NLS-1$
		//if (fURLResult != null && "file".equals(fURLResult.getProtocol())) { //$NON-NLS-1$
		//	initPath= (new File(fURLResult.getFile())).getPath();
		//}
		
		//OutputLocationDialog dl;
		
		IProject curProj = (IProject)getElement();
		
		IWorkspaceRoot root = curProj.getWorkspace().getRoot();
		
		final Class[] acceptedClasses= new Class[] { IProject.class, IFolder.class };
		IProject[] allProjects= root.getProjects();
		ArrayList rejectedElements= new ArrayList(allProjects.length);
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
		//dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());
		dialog.setInput(curProj.getProject().getParent());
		dialog.setInitialSelection(curProj.getProject());

		IResource initSelection= null;
		initSelection= curProj.findMember("bin");
		dialog.setInitialSelection(initSelection);
		
		dialog.setAllowMultiple(false);
		dialog.setComparator(new ResourceComparator(ResourceComparator.NAME));
		dialog.open();
		
		IContainer result = (IContainer)dialog.getFirstResult();
		if (result != null) {
			try {
				//return result.getProjectRelativePath().toString();
				return result.getFullPath().makeRelative().toOSString();
				//URL url= new File(result).toURL();
				//return url.toExternalForm();
			//} catch (MalformedURLException e) {
				//e.printStackTrace();
			} catch (Throwable t){
				t.printStackTrace();
			}
		}
		return null;
	}
	
	/*
	private void addSecondSection(Composite parent) {
		Composite composite = createDefaultComposite(parent);

		// Label for owner field
		Label ownerLabel = new Label(composite, SWT.NONE);
		ownerLabel.setText("");

		// Owner text field
		ownerText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		GridData gd = new GridData();
		gd.widthHint = convertWidthInCharsToPixels(TEXT_FIELD_WIDTH);
		ownerText.setLayoutData(gd);

		
		
		// Populate owner text field
		try {
			String owner =
				((IResource) getElement()).getPersistentProperty(
					new QualifiedName("", OWNER_PROPERTY));
			ownerText.setText((owner != null) ? owner : DEFAULT_OWNER);
		} catch (CoreException e) {
			ownerText.setText(DEFAULT_OWNER);
		}
	}
	*/
	
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

		addSection(composite);
		return composite;
	}

	private Composite createDefaultComposite(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);

		GridData data = new GridData();
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.FILL;
		composite.setLayoutData(data);

		return composite;
	}

	protected void performDefaults() {
		// Populate the owner text field with the default value
		outputPathText.setText(DEFAULT_OUTPUT_PATH);
	}
	
	public boolean performOk() {
		// store the value in the owner text field
		try {
			((IResource) getElement()).setPersistentProperty(
				new QualifiedName("", OUTPUT_PATH_PROPERTY),
				outputPathText.getText());
		} catch (CoreException e) {
			return false;
		}
		return true;
	}

}