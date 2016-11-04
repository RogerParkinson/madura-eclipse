/*******************************************************************************
 * Copyright (c)2014 Prometheus Consulting
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package nz.co.senanque.madura.workfloweditor.editor;
import java.io.ByteArrayInputStream;

import nz.co.senanque.madura.MaduraPlugin;
import nz.co.senanque.madura.ProjectPropertyPage;
import nz.co.senanque.madura.Utils;
import nz.co.senanque.parser.InputStreamParserSource;
import nz.co.senanque.parser.ParserException;
import nz.co.senanque.parser.ParserSource;
import nz.co.senanque.parser.TOCInterface;
import nz.co.senanque.process.parser.ParsePackage;
import nz.co.senanque.process.parser.ProcessTextProvider;
import nz.co.senanque.schemaparser.SchemaParser;
import nz.co.senanque.workflow.WorkflowManagerMock;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 
 * This handles the outline control for the rule editor
 * It generates the table of contents by parsing the rule file
 * and sets up markers to indicate any errors.
 * 
 * @author Roger Parkinson
 */
public class WorkflowOutlinePage extends ContentOutlinePage implements TOCInterface
{
    private static final Logger log = LoggerFactory.getLogger(WorkflowOutlinePage.class);

	private boolean m_timerStarted;
	private long m_parserOffset=0;
	private IFile m_input;
	private final AdaptableList m_toc = new AdaptableList();
	private final TextEditor m_textEditor;

	public WorkflowOutlinePage(IFile input, TextEditor textEditor)
	{
		super();
		m_input = input;
		m_textEditor = textEditor;
		MaduraPlugin.registerBuilder(m_input.getProject(),WorkflowBuilder.getId());
	}

	public void createControl(Composite parent)
	{
		super.createControl(parent);
		// Configure the context menu.
		MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
		menuMgr.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		menuMgr.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS + "-end")); //$NON-NLS-1$
		final TreeViewer viewer = getTreeViewer();
		IDocumentProvider d = m_textEditor.getDocumentProvider();
		IDocument dd = d.getDocument(m_textEditor.getEditorInput());
		viewer.setContentProvider(new WorkbenchContentProvider());
		viewer.setLabelProvider(new WorkflowLabelProvider());
		String s = dd.get();
		SchemaParser schemaParser = Utils.getXSDFile(m_input);
		if (schemaParser == null) {
			return;
		}

		try
		{
			ITypedRegion r[] = dd.computePartitioning(0,s.length());
			viewer.setInput(getContentOutline(r,s,m_input.getName(),m_input.getLocation().toOSString(),schemaParser));
		}
		catch (BadLocationException e)
		{
			e.printStackTrace();
		}

		Menu menu = menuMgr.createContextMenu(viewer.getTree());
		viewer.getTree().setMenu(menu);
		// Be sure to register it so that other plug-ins can add actions.
		getSite().registerContextMenu("nz.co.senanque.madura.workflow.outline", menuMgr, viewer); //$NON-NLS-1$
		dd.addDocumentListener(new IDocumentListener()
		{
			public void documentAboutToBeChanged(DocumentEvent event)
			{
			}
			public void documentChanged(DocumentEvent event)
			{
				Document doc = (Document) event.getDocument();
				processChange(doc);
			}
		});
	}
	public void processChange(final Document doc)
	{
		if (doc == null)
		{
			WorkflowBuilder.buildFile(m_input, this);
			log.debug("Just invoked full build");
			return;
		}
		final Tree tree = (Tree)getControl();
		final TreeViewer treeView = getTreeViewer();
		try
		{
			Runnable runnable = new Runnable()
			{
				public void run()
				{
					getControl().setRedraw(false);
					try
					{
						String s = doc.get();
						Object expanded[] = treeView.getExpandedElements();
						tree.removeAll();
						SchemaParser schemaParser = Utils.getXSDFile(m_input);
						if (schemaParser == null) {
							return;
						}

						ITypedRegion r[] = doc.computePartitioning(0,s.length());
						AdaptableList o = ((AdaptableList)getContentOutline(r,s,m_input.getName(),m_input.getLocation().toOSString(),schemaParser));
						treeView.setInput(o);
						for (int i=0;i<expanded.length;i++)
						{
							int j = o.children.indexOf(expanded[i]);
							if (j > -1)
								treeView.setExpandedState(o.children.get(j),true);
						}
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
					getControl().setRedraw(true);
					getControl().redraw();
					m_timerStarted = false;
				}
			};
			if (!m_timerStarted)
			{
				m_timerStarted = true;
				Display.getDefault().timerExec(1000,runnable);
			}
			else
			{
				getControl().setRedraw(true);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * Build the table of contents for the package
	 * To do this we call the rules parser and pass it this class
	 * which implements the TOCInterface. The TOCInterface is passed the
	 * table of contents by the parser.
	 * 
	 * @param input
	 * @return
	 */
	private Object getContentOutline(
		ITypedRegion r[],
		String input,
		String name,
		String path,
		SchemaParser schemaParser)
	{
		try
		{
			m_input.deleteMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
		}
		catch (CoreException e1)
		{
		}
		m_toc.clear();
		int lineCount = 0;
		m_parserOffset = 0;
		String messageNames = ProjectPropertyPage.getMessageNames(m_input.getProject());
		String computeNames = ProjectPropertyPage.getComputeNames(m_input.getProject());
		for (int i = 0; i < r.length; i++)
		{
			ITypedRegion t = r[i];
			String section =
				input.substring(t.getOffset(), t.getOffset() + t.getLength());
			int sectionLineCount = 0;
			for (int i1=0;i1<section.length();i1++) {
				if (section.charAt(i1) == '\n') {
					sectionLineCount++;
				}
			}
			log.debug("sectionLineCount {}",sectionLineCount);
			try
			{
				ParserSource parserSource = new InputStreamParserSource(new ByteArrayInputStream(section.getBytes()), m_input.getName(), Utils.getBufferSize());
				ProcessTextProvider textProvider = new ProcessTextProvider(parserSource,schemaParser, new WorkflowManagerMock(messageNames,computeNames));
		        textProvider.setToc(this);
				ParsePackage parsePackage = new ParsePackage();
				parsePackage.parse(textProvider);
//				log.debug("Parser for {} {}",m_parserOffset,section);

				m_parserOffset += parserSource.getPos();
			}
			catch (ParserException f)
			{
				Utils.addErrorElement(Utils.cleanupMessage(f.getMessage()), lineCount+f.getLineCount()+1,m_input);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			lineCount += sectionLineCount;
			log.debug("lineCount {}",lineCount);
		}
		return m_toc;
	}
	public Object addTOCElement(
		Object parent,
		String name,
		long start,
		long end,
		int type)
	{
		long adjustedStart = start + m_parserOffset;
		MarkElement element =
			new MarkElement(
				(IAdaptable) parent,
				name,
				(int) adjustedStart,
				(int) (end - start),
				type);
//		if (parent == m_input)
			m_toc.add(element);
			log.debug("Adding TOC name {} start {} end {} type {}",name,adjustedStart,end-start,type);
		return element;
	}
	public void addErrorElement(String name, int line)
	{
		final String n = name;
		final int l = line;
		try
		{
			MaduraPlugin.getWorkspace().run(new IWorkspaceRunnable()
			{
				public void run(IProgressMonitor monitor)
				{
					try
					{
						IEditorInput input = m_textEditor.getEditorInput();
						if (input instanceof IFileEditorInput)
						{
							IResource resource =
								((IFileEditorInput) input).getFile();
							IMarker marker =
								resource.createMarker(IMarker.PROBLEM);
							if (marker.exists())
							{
								marker.setAttribute(IMarker.LOCATION, l);
								marker.setAttribute(IMarker.LINE_NUMBER, l);
								marker.setAttribute(IMarker.MESSAGE, n);
								marker.setAttribute(
									IMarker.SEVERITY,
									IMarker.SEVERITY_ERROR);
							}
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
	public Object getTOCElement()
	{
		return m_input;
	}
	public void selectionChanged(SelectionChangedEvent event)
	{
		super.selectionChanged(event);
		ISelection selection = event.getSelection();
		if (selection.isEmpty())
			m_textEditor.resetHighlightRange();
		else
		{
			MarkElement segment =
				(MarkElement) ((IStructuredSelection) selection)
					.getFirstElement();
			int start = segment.getStart();
			int length = segment.getLength();
			try
			{
				m_textEditor.setHighlightRange(start, length, true);
			}
			catch (IllegalArgumentException x)
			{
				m_textEditor.resetHighlightRange();
			}
		}
	}
	/**
	 * @param file
	 */
	public void setInput(IFile file)
	{
		m_input = file;
	}
}
