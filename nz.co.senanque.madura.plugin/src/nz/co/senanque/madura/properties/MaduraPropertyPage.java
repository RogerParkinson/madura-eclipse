/*******************************************************************************
 * Copyright (c)2014 Prometheus Consulting
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package nz.co.senanque.madura.properties;

import nz.co.senanque.madura.Utils;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PropertyPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the file property page. It establishes the xsd file that is used to compile the current file.
 * 
 * @author Roger Parkinson
 *
 */
public class MaduraPropertyPage extends PropertyPage {
	
//	private static final Logger log = LoggerFactory.getLogger(MaduraPropertyPage.class);

	private static final String XSD_FILE_LABEL = "&XSD File:";
	public static final String XSD_FILE = "XSD_FILE";
	private static final int TEXT_FIELD_WIDTH = 30;
	
	private Text xsdFilePathText;

	public MaduraPropertyPage() {
		super();
	}

	private void addSeparator(Composite parent) {
		Label separator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		separator.setLayoutData(gridData);
	}

	/**
	 * Uses the standard container selection dialog to
	 * choose the new value for the container field.
	 */
	private void handleBrowse(Text t)
	{
		IResource element = (IResource)getElement();
		IPath elementPath = element.getProjectRelativePath();
		IProject project = ((IResource)getElement()).getProject();
		String projectPath = Utils.getProjectPath(element);
		IPath currentFile = project.getFile(t.getText()).getProjectRelativePath();
		if (currentFile == null) {
			elementPath = elementPath.removeFileExtension().removeFirstSegments(1).removeLastSegments(1);
		} else {
			elementPath = currentFile.removeFileExtension().removeFirstSegments(1).removeLastSegments(1);
		}
		String path = projectPath+elementPath.toOSString();
		FileDialog d = new FileDialog(getShell(),SWT.OPEN);
		d.setFilterPath(path);
		d.setFilterExtensions(new String[]{"*.xsd"});
		String filePath = d.open();
		if (filePath.startsWith(projectPath)) {
			filePath = filePath.substring(projectPath.length());
		}
		if (filePath != null) {
			t.setText(filePath);
		}
	}
	private void addSecondSection(Composite parent) {
		Composite composite = createDefaultComposite(parent);

		// Label for xsdFile field
		Label xsdFileLabel = new Label(composite, SWT.NONE);
		xsdFileLabel.setText(XSD_FILE_LABEL);

		// xsd field
		xsdFilePathText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		GridData gd = new GridData();
		gd.widthHint = convertWidthInCharsToPixels(TEXT_FIELD_WIDTH);
		xsdFilePathText.setLayoutData(gd);

		// Populate xsd field
		try {
			String xsdFilePath = ((IResource) getElement())
					.getPersistentProperty(new QualifiedName("", XSD_FILE));
			xsdFilePathText.setText((xsdFilePath != null) ? xsdFilePath : Utils
					.getDefaultXSDPath((IResource) getElement()));
		} catch (CoreException e) {
			xsdFilePathText.setText(Utils
					.getDefaultXSDPath((IResource) getElement()));
		}
		Button button = new Button(composite, SWT.PUSH);
		button.setText("Browse...");
		MaduraPropertyPage me = this;
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Utils.handleBrowse(xsdFilePathText, (IResource)me.getElement() , me.getShell());
			}
		});
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

//		addFirstSection(composite);
//		addSeparator(composite);
		addSecondSection(composite);
		addSeparator(composite);
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
		super.performDefaults();
		// Populate the owner text field with the default value
		xsdFilePathText.setText(Utils.getDefaultXSDPath((IResource) getElement()));
	}
	
	public boolean performOk() {
		// store the value in the owner text field
		Utils.saveXSDReference((IFile) getElement(), xsdFilePathText.getText());
//		IResource resource = (IResource) getElement();
//		xsdFilePathText.getText()
//		try {
//			((IResource) getElement()).setPersistentProperty(
//					new QualifiedName("", XSD_FILE),
//					xsdFilePathText.getText());
//		} catch (CoreException e) {
//			return false;
//		}
		return true;
	}


}