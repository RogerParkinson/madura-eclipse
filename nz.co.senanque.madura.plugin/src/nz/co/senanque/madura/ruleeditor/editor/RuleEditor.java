/*******************************************************************************
 * Copyright (c)2014 Prometheus Consulting
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package nz.co.senanque.madura.ruleeditor.editor;
import java.util.ResourceBundle;

import nz.co.senanque.madura.MaduraPlugin;
import nz.co.senanque.madura.TokenManager;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.texteditor.ContentAssistAction;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
/**
 * 
 * This coordinates the various classes involved in the Rule Editor which is syntax-aware
 * and delivers tagged errors to the Eclipse environment
 * 
 * @author Roger Parkinson
 */
public class RuleEditor extends TextEditor
{
	private final TokenManager tokenManager;
	private final ResourceBundle resourceBundle;
	private RuleOutlinePage m_contentOutlinePage;
	private RuleConfiguration m_ruleConfiguration;
	public RuleEditor()
	{
		super();
		tokenManager = MaduraPlugin.getDefault().getTokenManager();
		resourceBundle = MaduraPlugin.getDefault().getResourceBundle();
		m_ruleConfiguration = new RuleConfiguration(tokenManager);
		setSourceViewerConfiguration(m_ruleConfiguration);
		RuleDocumentProvider provider = new RuleDocumentProvider();
		setDocumentProvider(provider);
		setPreferenceStore(MaduraPlugin.getDefault().getPreferenceStore());
	}
	protected boolean affectsTextPresentation(PropertyChangeEvent event)
	{
		return super.affectsTextPresentation(event)
			|| tokenManager.affectsTextPresentation(event);
	}
	protected void handlePreferenceStoreChanged(PropertyChangeEvent event)
	{
		tokenManager.handlePreferenceStoreChanged(event);
		super.handlePreferenceStoreChanged(event);
	}
	protected void createActions()
	{
		super.createActions();
		ContentAssistAction action =
			new ContentAssistAction(
				resourceBundle,
				"ContentAssistProposal.",
				this);
		action.setActionDefinitionId(
			ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS);
		setAction("ContentAssistProposal", action);
	}
	public void dispose()
	{
		super.dispose();
	}
	/** (non-Javadoc)
		* Method declared on IAdaptable
		*/
	public Object getAdapter(Class key)
	{
		if (key.equals(IContentOutlinePage.class))
		{
			IEditorInput input = getEditorInput();
			if (input instanceof IFileEditorInput)
			{
				m_contentOutlinePage =
					new RuleOutlinePage(
						((IFileEditorInput) input).getFile(),
						this);
				return m_contentOutlinePage;
			}
		}
		return super.getAdapter(key);
	}
	/** The <code>JavaEditor</code> implementation of this 
	 * <code>AbstractTextEditor</code> method performs sets the 
	 * input of the outline page after AbstractTextEditor has set input.
	 */
	public void doSetInput(IEditorInput input) throws CoreException
	{
		super.doSetInput(input);
		m_ruleConfiguration.setInput(input);
	}
	public void doSave(IProgressMonitor monitor)
	{
		super.doSave(monitor);
		m_contentOutlinePage.processChange(null);
	}
	public void doSaveAs()
	{
		super.doSaveAs();
		m_contentOutlinePage.processChange(null);
	}
}
