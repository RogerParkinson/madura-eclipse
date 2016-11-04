/*******************************************************************************
 * Copyright (c)2014 Prometheus Consulting
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package nz.co.senanque.madura.ruleeditor.editor;

import nz.co.senanque.madura.TokenManager;
import nz.co.senanque.madura.ruleeditor.editor.contentassist.RuleAssistant;
import nz.co.senanque.madura.ruleeditor.editor.scanners.CodeScanner;
import nz.co.senanque.madura.ruleeditor.editor.scanners.CommentScanner;
import nz.co.senanque.madura.ruleeditor.editor.scanners.DefaultScanner;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.ui.IEditorInput;

/**
 * 
 * @author Roger Parkinson
 */
public class RuleConfiguration
   extends SourceViewerConfiguration
{
   private final TokenManager tokenManager;
   private IEditorInput m_input;

   public RuleConfiguration(TokenManager tokenManager)
   {
	  this.tokenManager = tokenManager;
   }

   public String[] getConfiguredContentTypes(
	  ISourceViewer sourceViewer)
   {
	  return RulePartitionScanner.getLegalContentTypes();
   }

   public IPresentationReconciler getPresentationReconciler(
	  ISourceViewer sourceViewer)
   {
	  PresentationReconciler reconciler =
		 new PresentationReconciler();
	  DefaultDamagerRepairer dr;

	  dr = new DefaultDamagerRepairer(
			  new DefaultScanner(tokenManager));
	  reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
	  reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

	dr = new DefaultDamagerRepairer(
			new CommentScanner(tokenManager));
	reconciler.setDamager(dr,
	   RulePartitionScanner.MADURA_COMMENT);
	reconciler.setRepairer(dr,
	   RulePartitionScanner.MADURA_COMMENT);

	dr = new DefaultDamagerRepairer(
			new CodeScanner(tokenManager));
	reconciler.setDamager(dr,
	   RulePartitionScanner.MADURA_CODE);
	reconciler.setRepairer(dr,
	   RulePartitionScanner.MADURA_CODE);

	  return reconciler;
   }

   public IContentAssistant getContentAssistant(
	  ISourceViewer sourceViewer)
   {
	  ContentAssistant assistant = new ContentAssistant();
	  assistant.setContentAssistProcessor(
		 new RuleAssistant(tokenManager,this),
		RulePartitionScanner.MADURA_CODE);
	  assistant.enableAutoActivation(true);
	  assistant.enableAutoInsert(true);
	  return assistant;
   }

	public void setInput(IEditorInput input) {
		m_input = input;
	}

	public IEditorInput getEditorInput() {
		return m_input;
	}
}
