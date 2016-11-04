/*******************************************************************************
 * Copyright (c)2014 Prometheus Consulting
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package nz.co.senanque.madura;

import java.io.FileInputStream;

import nz.co.senanque.madura.preferences.PreferenceConstants;
import nz.co.senanque.madura.properties.MaduraPropertyPage;
import nz.co.senanque.schemaparser.SchemaParser;
import nz.co.senanque.schemaparser.SchemaParserImpl;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.preference.IPreferenceStore;
import org.jdom.Document;
import org.jdom.input.SAXBuilder;

/**
 * @author Roger Parkinson
 *
 */
public class Utils {
	
	private Utils() {
		// No construction, just static methods
	}
	public static String fixCase(String string) {
		return Character.toLowerCase(string.charAt(0))+string.substring(1);
	}
	public static String getDefaultXSDPath(IResource resource)
	{
		IPath currentFile = resource.getProjectRelativePath().removeFileExtension().addFileExtension("xsd");
		return currentFile.toOSString();
	}
	public static String getProjectPath(IResource resource)
	{
		IProject project = resource.getProject();
		return project.getLocation().toOSString()+java.io.File.separatorChar;
	}
	public static String getProjectName(IResource resource)
	{
		IProject project = resource.getProject();
		return project.getName().toString();
	}
	public static String cleanupMessage(String message) {
		int j = message.indexOf(" in scope");
		if (j > 0)
			message = message.substring(0, j);
		j = message.indexOf(" at line");
		if (j > 0)
			message = message.substring(0, j);
		j = message.indexOf(":");
		if (j > 0)
			message = message.substring(0, j);
		return message;
	}
	public static SchemaParser getXSDFile(IFile file) {
		String localXSDFile = null;
		try {
			String f = file.getPersistentProperty(new QualifiedName("",MaduraPropertyPage.XSD_FILE));
			if (f == null) {
				f = Utils.getDefaultXSDPath(file);
			}
			localXSDFile = Utils.getProjectPath(file)+f;
		} catch (CoreException e1) {
			e1.printStackTrace();
			localXSDFile = Utils.getProjectPath(file)+Utils.getDefaultXSDPath(file);
		}
        // parse the schema
        SAXBuilder builder = new SAXBuilder();
//        builder.setValidation(true);
//        builder.setIgnoringElementContentWhitespace(true);
        Document doc=null;
        try
        {
            doc = builder.build(new FileInputStream(localXSDFile));
        }
        catch (Exception e)
        {
        	addErrorElement("Failed to load schema file : "+e.getMessage(), 0,file);
           return null;
        }
        SchemaParserImpl schemaParser = new SchemaParserImpl();
        schemaParser.parse(doc,localXSDFile);
        return schemaParser;
	}
	public static int getBufferSize() {
		IPreferenceStore store = MaduraPlugin.getDefault().getPreferenceStore();
		return store.getInt(PreferenceConstants.P_BUFFER_SIZE);
	}
	public static void addErrorElement(final String name, final int line, final IResource resource)
	{
		try
		{
			MaduraPlugin.getWorkspace().run(new IWorkspaceRunnable()
			{
				public void run(IProgressMonitor monitor)
				{
					try
					{
						IMarker marker =
							resource.createMarker(IMarker.PROBLEM);
						if (marker.exists())
						{
							marker.setAttribute(IMarker.LOCATION, line);
							marker.setAttribute(IMarker.LINE_NUMBER, line);
							marker.setAttribute(IMarker.MESSAGE, name);
							marker.setAttribute(
								IMarker.SEVERITY,
								IMarker.SEVERITY_ERROR);
//							marker.setAttribute(IMarker.TEXT, name);
						}
					}
					catch (CoreException e)
					{
						e.printStackTrace();
					}
				}
			}, null);
		}
		catch (CoreException e)
		{
			e.printStackTrace();
		}
	}
	public static void addWarningElement(final String name, final int line, final IResource resource)
	{
		try
		{
			MaduraPlugin.getWorkspace().run(new IWorkspaceRunnable()
			{
				public void run(IProgressMonitor monitor)
				{
					try
					{
						IMarker marker =
							resource.createMarker(IMarker.PROBLEM);
						if (marker.exists())
						{
							marker.setAttribute(IMarker.LOCATION, line);
							marker.setAttribute(IMarker.LINE_NUMBER, line);
							marker.setAttribute(IMarker.MESSAGE, name);
							marker.setAttribute(
								IMarker.SEVERITY,
								IMarker.SEVERITY_WARNING);
						}
					}
					catch (CoreException e)
					{
						e.printStackTrace();
					}
				}
			}, null);
		}
		catch (CoreException e)
		{
			e.printStackTrace();
		}
	}

}
