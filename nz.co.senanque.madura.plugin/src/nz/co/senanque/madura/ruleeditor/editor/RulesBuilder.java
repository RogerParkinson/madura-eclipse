/*******************************************************************************
 * Copyright (c)2014 Prometheus Consulting
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package nz.co.senanque.madura.ruleeditor.editor;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import nz.co.senanque.madura.MaduraPlugin;
import nz.co.senanque.madura.ProjectPropertyPage;
import nz.co.senanque.madura.Utils;
import nz.co.senanque.parser.InputStreamParserSource;
import nz.co.senanque.parser.ParserException;
import nz.co.senanque.parser.ParserSource;
import nz.co.senanque.parser.TOCInterface;
import nz.co.senanque.rulesparser.FunctionDescriptorFactory;
import nz.co.senanque.rulesparser.ParsePackage;
import nz.co.senanque.rulesparser.RulesTextProvider;
import nz.co.senanque.schemaparser.SchemaParser;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * The builder parses the rules files 
 * 
 * @author Roger Parkinson
 */
public class RulesBuilder extends IncrementalProjectBuilder
{
	private static final Logger log = LoggerFactory.getLogger(RulesBuilder.class);
	
	private static String ID = MaduraPlugin.PLUGIN_ID+"."+Utils.fixCase(RulesBuilder.class.getSimpleName());
	
	public static void buildFile(IFile file, TOCInterface toc)
	{
		try
		{
			file.deleteMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
		}
		catch (CoreException e1)
		{
		}
		
        List<Class<?>> externalFunctions = ProjectPropertyPage.getExternalFunctions(file.getProject());
        ParserSource parserSource;
        String name = "";
        SchemaParser schemaParser;
		try {
			schemaParser = Utils.getXSDFile(file);
			if (schemaParser == null) {
				return;
			}
			name = file.getLocation().toOSString();
			InputStream is = new FileInputStream(name);
			parserSource = new InputStreamParserSource(is,file.getName(),Utils.getBufferSize());
		} catch (Exception e3) {
			Utils.addErrorElement("Failed to load rules file: "+e3.getMessage(), 0,file);
	        return;
		}

        // parse the rules
        RulesTextProvider textProvider = new RulesTextProvider(parserSource, schemaParser,externalFunctions);
        textProvider.setToc(toc);
        new FunctionDescriptorFactory().loadOperators(textProvider);
        ParsePackage parsePackage = new ParsePackage();
        try
        {
            parsePackage.parse(textProvider);
        }
        catch (ParserException f)
        {
        	Utils.addErrorElement(Utils.cleanupMessage(f.getMessage()), f.getLineCount()+1,file);
        }
	}

	public class MyBuildDeltaVisitor implements IResourceDeltaVisitor
	{
		/* (non-Javadoc)
		 * @see org.eclipse.core.resources.IResourceDeltaVisitor#visit(org.eclipse.core.resources.IResourceDelta)
		 */
		public boolean visit(IResourceDelta delta) throws CoreException
		{
			IResource resource = delta.getResource();
			if (resource != null && resource instanceof IFile && ((IFile)resource).getFileExtension().equals("rul"))
			{
				switch (delta.getKind() & (IResourceDelta.ADDED | IResourceDelta.CHANGED | IResourceDelta.REMOVED))
				{
					case IResourceDelta.ADDED:
					case IResourceDelta.CHANGED:
						buildFile((IFile) resource,null);
						break;
					case IResourceDelta.REMOVED:
				}
			}
			return true;
		}
	}
	public class MyBuildVisitor implements IResourceVisitor
	{
		/* (non-Javadoc)
		 * @see org.eclipse.core.resources.IResourceVisitor#visit(org.eclipse.core.resources.IResource)
		 */
		public boolean visit(IResource resource) throws CoreException
		{
			if (resource != null && resource instanceof IFile && ((IFile)resource).getFileExtension().equals("rul"))
			{
				buildFile((IFile) resource,null);
			}
			return true;
		}
	}

	public RulesBuilder()
	{
		super();
	}
	/* (non-Javadoc)
	 * @see org.eclipse.core.internal.events.InternalBuilder#build(int, java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor)
		  throws CoreException {
	   if (kind == IncrementalProjectBuilder.FULL_BUILD) {
		   log.debug("invoked build (Full)");
		  fullBuild(monitor);
	   } else {
		  IResourceDelta delta = getDelta(getProject());
		  if (delta == null) {
			   log.debug("invoked build (Full)");
			 fullBuild(monitor);
		  } else {
			   log.debug("invoked build (incremental)");
			 incrementalBuild(delta, monitor);
		  }
	   }
	   return null;
	}
	protected void clean(IProgressMonitor monitor) throws CoreException {
		log.debug("invoked clean");
	}
	protected void fullBuild(final IProgressMonitor monitor) throws CoreException {
	   try {
		  getProject().accept(new MyBuildVisitor());
	   } catch (CoreException e) { }
	}

	protected void incrementalBuild(IResourceDelta delta, 
		  IProgressMonitor monitor) throws CoreException {
	   // the visitor does the work.
	   delta.accept(new MyBuildDeltaVisitor());
	}
	public static String getId() {
		return ID;
	}
}
