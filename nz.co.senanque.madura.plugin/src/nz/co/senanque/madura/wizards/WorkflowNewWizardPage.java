/*******************************************************************************
 * Copyright (c)2014 Prometheus Consulting
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package nz.co.senanque.madura.wizards;

import nz.co.senanque.madura.Utils;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;

/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (wrk).
 * @author Roger Parkinson
 */

public class WorkflowNewWizardPage extends WizardPage {
	private Text containerText;

	private Text fileText;
	private Text xsdFileText;

	private ISelection selection;
	private IResource resource;

	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param pageName
	 */
	public WorkflowNewWizardPage(ISelection selection) {
		super("wizardPage");
		setTitle("Workflow Editor File");
		setDescription("This wizard creates a new file with *.wrk extension that can be opened by the workflow editor.");
		this.selection = selection;
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 3;
		layout.verticalSpacing = 9;
		Label label = new Label(container, SWT.NULL);
		label.setText("&Container:");

		containerText = new Text(container, SWT.BORDER | SWT.SINGLE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		containerText.setLayoutData(gd);
		containerText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});

		Button button = new Button(container, SWT.PUSH);
		button.setText("Browse...");
		WizardPage me = this;
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleBrowse();
			}
		});
		label = new Label(container, SWT.NULL);
		label.setText("&File name:");

		fileText = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		fileText.setLayoutData(gd);
		fileText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});
		label = new Label(container, SWT.NULL);
		label.setText("");//dummy label to hack layout
		label = new Label(container, SWT.NULL);
		label.setText("&XSD File name:");

		xsdFileText = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		xsdFileText.setLayoutData(gd);
		xsdFileText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});
		Button xsdbutton = new Button(container, SWT.PUSH);
		xsdbutton.setText("Browse...");
		xsdbutton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Utils.handleBrowse(xsdFileText, resource , me.getShell());
			}
		});
		initialize();
		dialogChanged();
		setControl(container);
	}

	/**
	 * Tests if the current workbench selection is a suitable container to use.
	 */

	private void initialize() {
		if (selection != null && selection.isEmpty() == false
				&& selection instanceof IStructuredSelection) {
			IStructuredSelection ssel = (IStructuredSelection) selection;
			if (ssel.size() > 1)
				return;
			Object obj = ssel.getFirstElement();
	        if (obj instanceof IJavaElement) {
				IJavaElement ijp = (IJavaElement)obj;
				containerText.setText(ijp.getPath().toPortableString());
				resource = ijp.getJavaProject().getProject();
				
			}
			if (obj instanceof IFolder) {
				IFolder ijp = (IFolder)obj;
				containerText.setText(ijp.getFullPath().toPortableString());
				resource = ijp;
			}
			if (obj instanceof IFile) {
				IFile ijp = (IFile)obj;
				IContainer container = ijp.getParent();
				if (container instanceof IFolder) {
					IFolder f = (IFolder)container;
					containerText.setText(f.getFullPath().toPortableString());
					resource = f;
				} else if (container instanceof IProject) {
					IProject p = (IProject)container;
					containerText.setText(p.getFullPath().toPortableString());
					resource = p;
					
				}			
			}
		}
		fileText.setText("new_file.wrk");
	}

	/**
	 * Uses the standard container selection dialog to choose the new value for
	 * the container field.
	 */

	private void handleBrowse() {
		ContainerSelectionDialog dialog = new ContainerSelectionDialog(
				getShell(), ResourcesPlugin.getWorkspace().getRoot(), false,
				"Select new file container");
		if (dialog.open() == ContainerSelectionDialog.OK) {
			Object[] result = dialog.getResult();
			if (result.length == 1) {
				containerText.setText(((Path) result[0]).toString());
			}
		}
	}

	/**
	 * Ensures that both text fields are set.
	 */

	private void dialogChanged() {
		IResource container = ResourcesPlugin.getWorkspace().getRoot()
				.findMember(new Path(getContainerName()));
		String fileName = getFileName();
		String xsdFileName = getXSDFileName();

		if (getContainerName().length() == 0) {
			updateStatus("File container must be specified");
			return;
		}
		if (container == null
				|| (container.getType() & (IResource.PROJECT | IResource.FOLDER)) == 0) {
			updateStatus("File container must exist");
			return;
		}
		if (!container.isAccessible()) {
			updateStatus("Project must be writable");
			return;
		}
		if (fileName.length() == 0) {
			updateStatus("File name must be specified");
			return;
		}
		if (fileName.replace('\\', '/').indexOf('/', 1) > 0) {
			updateStatus("File name must be valid");
			return;
		}
		int dotLoc = fileName.lastIndexOf('.');
		if (dotLoc != -1) {
			String ext = fileName.substring(dotLoc + 1);
			if (ext.equalsIgnoreCase("wrk") == false) {
				updateStatus("File extension must be \"wrk\"");
				return;
			}
		}
		if (xsdFileName.length() == 0) {
			updateStatus("XSD File name must be specified");
			return;
		}
		if (xsdFileName.replace('\\', '/').indexOf('/', 1) > 0) {
			updateStatus("XSD File name must be valid");
			return;
		}
		dotLoc = xsdFileName.lastIndexOf('.');
		if (dotLoc != -1) {
			String ext = xsdFileName.substring(dotLoc + 1);
			if (ext.equalsIgnoreCase("xsd") == false) {
				updateStatus("XSD File extension must be \"xsd\"");
				return;
			}
		}
		updateStatus(null);
	}

	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}

	public String getContainerName() {
		return containerText.getText();
	}

	public String getFileName() {
		return fileText.getText();
	}
	public String getXSDFileName() {
		return xsdFileText.getText();
	}
}