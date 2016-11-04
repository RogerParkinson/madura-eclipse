/*******************************************************************************
 * Copyright (c)2014 Prometheus Consulting
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package nz.co.senanque.madura;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import nz.co.senanque.rules.annotations.Function;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PropertyPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Roger Parkinson
 *
 */
public class ProjectPropertyPage extends PropertyPage {

	private static final Logger log = LoggerFactory.getLogger(ProjectPropertyPage.class);
	private static final String CLASSES_PATH_LABEL = "Classes path:";
	public static final String CLASSES_PATH = "CLASSES_PATH";

	private static final String CLASS_NAMES_PROPERTY = "CLASS_NAMES_PROPERTY";
	private static final String CLASS_NAMES_LABEL = "Class names";
	
	private static final String MESSAGE_NAMES_PROPERTY = "MESSAGE_NAMES_PROPERTY";
	private static final String MESSAGE_NAMES_LABEL = "Message names";
	
	private static final String COMPUTE_NAMES_PROPERTY = "Compute_NAMES_PROPERTY";
	private static final String COMPUTE_NAMES_LABEL = "Compute names";
	
	private static final int TEXT_FIELD_WIDTH40 = 40;
	private static final int TEXT_FIELD_WIDTH = 80;
	private static final int TEXT_FIELD_HEIGHT = 5;
	
	private Text classNamesText;
	private Text classesPathText;
	private Text messageNamesText;
	private Text computeNamesText;

	public ProjectPropertyPage() {
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
		if (t.getText() == null || t.getText().equals("")) {
			
		} else {
			IPath currentFile = project.getFile(t.getText()).getProjectRelativePath();
			if (currentFile == null) {
				elementPath = elementPath.removeFileExtension().removeFirstSegments(1).removeLastSegments(1);
			} else {
				elementPath = currentFile.removeFileExtension().removeFirstSegments(1).removeLastSegments(1);
			}
		}
		String path = projectPath+elementPath.toOSString();
		DirectoryDialog d = new DirectoryDialog(getShell(),SWT.OPEN);
		d.setFilterPath(path);
		String filePath = d.open();
		if (filePath.startsWith(projectPath)) {
			filePath = filePath.substring(projectPath.length());
		}
		if (filePath != null) {
			t.setText(filePath);
		}
	}
	private void addFirstSection(Composite parent) {
		Composite composite = createDefaultComposite(parent);

		// Label for classes path field
		Label classesPathLabel = new Label(composite, SWT.TOP);
		classesPathLabel.setText(CLASSES_PATH_LABEL);

		// classes path field
		composite = createDefaultComposite(parent);

		classesPathText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		GridData gd = new GridData();
		gd.widthHint = convertWidthInCharsToPixels(TEXT_FIELD_WIDTH40);
		classesPathText.setLayoutData(gd);

		// Populate classes path field
		try {
			String classesFilePath = ((IResource) getElement())
					.getPersistentProperty(new QualifiedName("", CLASSES_PATH));
			classesPathText.setText((classesFilePath != null) ? classesFilePath : getClassesPathDefault(((IResource)getElement()).getProject()));
		} catch (CoreException e) {
			classesPathText.setText(Utils
					.getDefaultXSDPath((IResource) getElement()));
		}
		Button button = new Button(composite, SWT.PUSH);
		button.setText("Browse...");
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleBrowse(classesPathText);
			}
		});
	}
	private void addSecondSection(Composite parent) {
		Composite composite = createDefaultComposite(parent);

		Label ownerLabel = new Label(composite, SWT.TOP);
		ownerLabel.setText(CLASS_NAMES_LABEL);

		composite = createDefaultComposite(parent);
		classNamesText = new Text(composite, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		GridData gd = new GridData();
		gd.widthHint = convertWidthInCharsToPixels(TEXT_FIELD_WIDTH);
		gd.heightHint = convertHeightInCharsToPixels(TEXT_FIELD_HEIGHT);
		classNamesText.setLayoutData(gd);

		try {
			String classNames = ((IResource) getElement())
					.getPersistentProperty(new QualifiedName("",
							CLASS_NAMES_PROPERTY));
			classNamesText.setText((classNames != null) ? classNames : "");
		} catch (CoreException e) {
			classNamesText.setText("");
		}
	}
	private void addThirdSection(Composite parent) {
		Composite composite = createDefaultComposite(parent);
    

		Label ownerLabel = new Label(composite, SWT.BOLD);
		ownerLabel.setText("Workflow");
		FontData[] fontData = ownerLabel.getFont().getFontData();
	    Font font = new Font(composite.getDisplay(), fontData[0].name, fontData[0].getHeight(), SWT.BOLD);  
	    ownerLabel.setFont(font); 
		composite = createDefaultComposite(parent);

		ownerLabel = new Label(composite, SWT.TOP);
		ownerLabel.setText(MESSAGE_NAMES_LABEL);

		composite = createDefaultComposite(parent);
		messageNamesText = new Text(composite, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		GridData gd = new GridData();
		gd.widthHint = convertWidthInCharsToPixels(TEXT_FIELD_WIDTH);
		gd.heightHint = convertHeightInCharsToPixels(TEXT_FIELD_HEIGHT);
		messageNamesText.setLayoutData(gd);

		try {
			String classNames = ((IResource) getElement())
					.getPersistentProperty(new QualifiedName("",
							MESSAGE_NAMES_PROPERTY));
			messageNamesText.setText((classNames != null) ? classNames : "");
		} catch (CoreException e) {
			messageNamesText.setText("");
		}
		composite = createDefaultComposite(parent);

		ownerLabel = new Label(composite, SWT.TOP);
		ownerLabel.setText(COMPUTE_NAMES_LABEL);

		composite = createDefaultComposite(parent);
		computeNamesText = new Text(composite, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		gd = new GridData();
		gd.widthHint = convertWidthInCharsToPixels(TEXT_FIELD_WIDTH);
		gd.heightHint = convertHeightInCharsToPixels(TEXT_FIELD_HEIGHT);
		computeNamesText.setLayoutData(gd);

		try {
			String classNames = ((IResource) getElement())
					.getPersistentProperty(new QualifiedName("",
							COMPUTE_NAMES_PROPERTY));
			computeNamesText.setText((classNames != null) ? classNames : "");
		} catch (CoreException e) {
			computeNamesText.setText("");
		}
	}

	/**
	 * @see PreferencePage#createContents(Composite)
	 */
	protected Control createContents(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		IResource resource = (IResource) getElement();
		if (resource.getType() == IResource.PROJECT) {

			GridLayout layout = new GridLayout();
			composite.setLayout(layout);
			GridData data = new GridData(GridData.FILL);
			data.grabExcessHorizontalSpace = true;
			composite.setLayoutData(data);
	
			addFirstSection(composite);
			addSeparator(composite);
			addSecondSection(composite);
			addSeparator(composite);
			addThirdSection(composite);
		}
		return composite;
	}

	private Composite createDefaultComposite(Composite parent)
	{
		return createDefaultComposite(parent, 2);
	}
	private Composite createDefaultComposite(Composite parent, int columns) {
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = columns;
		composite.setLayout(layout);

		GridData data = new GridData();
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.FILL;
		composite.setLayoutData(data);

		return composite;
	}

	protected void performDefaults() {
		try {
			((IResource) getElement()).setPersistentProperty(
					new QualifiedName("", CLASSES_PATH),
					getClassesPathDefault(((IResource) getElement()).getProject()));
		} catch (CoreException e) {
			log.warn("{}",e.getMessage());
		}
	}
	
	private static String getClassesPathDefault(IProject project) {
		IFolder folderBin = project.getFolder("bin");
		IFolder folderMaven = project.getFolder("target/classes");
		String ret = null;
		if (folderBin.exists()) {
				ret = folderBin.getProjectRelativePath().toOSString();
		} else if (folderMaven.exists()) {
				ret = folderMaven.getProjectRelativePath().toOSString();
		}
		return ret;
	}
	
	public boolean performOk() {
		// store the value in the owner text field
		try {
			((IResource) getElement()).setPersistentProperty(
					new QualifiedName("", CLASS_NAMES_PROPERTY),
					classNamesText.getText());
			((IResource) getElement()).setPersistentProperty(
					new QualifiedName("", CLASSES_PATH),
					classesPathText.getText());
			((IResource) getElement()).setPersistentProperty(
					new QualifiedName("", MESSAGE_NAMES_PROPERTY),
					messageNamesText.getText());
			((IResource) getElement()).setPersistentProperty(
					new QualifiedName("", COMPUTE_NAMES_PROPERTY),
					computeNamesText.getText());
		} catch (CoreException e) {
			return false;
		}
		return true;
	}
	public static String getMessageNames(IProject project) {
		try {
			return ((IResource) project).getPersistentProperty(new QualifiedName("", MESSAGE_NAMES_PROPERTY));
		} catch (CoreException e) {
			return "";
		}
	}
	public static String getComputeNames(IProject project) {
		try {
			return ((IResource) project).getPersistentProperty(new QualifiedName("", COMPUTE_NAMES_PROPERTY));
		} catch (CoreException e) {
			return "";
		}
	}
	
	public static List<Class<?>> getExternalFunctions(IProject project) {
		List<Class<?>> ret = new ArrayList<>();
		String classes=null;
		String classPath=null;
		try {
			classes = ((IResource) project).getPersistentProperty(
					new QualifiedName("", CLASS_NAMES_PROPERTY));
			classPath = ((IResource) project).getPersistentProperty(
					new QualifiedName("", CLASSES_PATH));
		} catch (CoreException e) {
			log.warn("{}",e.getMessage());
			return ret;
		}
		if (classPath == null) {
			classPath =  getClassesPathDefault(project);
		}

		if (classes != null && classPath != null) {
			String classesDir = Utils.getProjectPath(project)+classPath;
			if (!classesDir.endsWith("/")) {
				classesDir += "/";
			}
			URL classesURL;
			try {
				classesURL = new URL("file","",classesDir);
			} catch (MalformedURLException e) {
				log.warn("{}",e.getMessage());
				return ret;
			}
			URLClassLoader classLoader = new URLClassLoader(new URL[]{classesURL},Function.class.getClassLoader());
			
			StringTokenizer st = new StringTokenizer(classes,",");
			while (st.hasMoreTokens()) {
				String className = st.nextToken();
				Class<?> clazz=null;
				try {
					clazz = classLoader.loadClass(className);
					ret.add(clazz);
				} catch (ClassNotFoundException e) {
					log.warn("{} {}",className,e.getMessage());
				}
			}
			try {
				classLoader.close();
			} catch (IOException e) {
				log.warn("{}",e.getMessage());
			}
		}
		return ret;
	}


}