/*******************************************************************************
 * Copyright (c)2014 Prometheus Consulting
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package nz.co.senanque.madura.workfloweditor.editor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import nz.co.senanque.madura.TokenManager;
import nz.co.senanque.madura.Utils;
import nz.co.senanque.schemaparser.SchemaParser;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Roger Parkinson
 */
public class WorkflowAssistant implements IContentAssistProcessor
{
	private static final Logger log = LoggerFactory.getLogger(WorkflowAssistant.class);
	private final Object[] m_functionArray;
	private final SchemaParser schemaParser;

	public WorkflowAssistant(TokenManager tokenManager, WorkflowConfiguration workflowConfiguration)
	{
		super();
		IFile file = (IFile)workflowConfiguration.getEditorInput().getAdapter(IFile.class);
		schemaParser = Utils.getXSDFile(file);
		
		List<String> functions = new ArrayList<>();

		m_functionArray = functions.toArray();
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#computeCompletionProposals(org.eclipse.jface.text.ITextViewer, int)
	 */
	public ICompletionProposal[] computeCompletionProposals(
		ITextViewer viewer,
		int documentOffset)
	{
		String context = null;
		
		try {
			IDocument document = viewer.getDocument();
			ITypedRegion region = document.getDocumentPartitioner().getPartition(documentOffset);
			if (!region.getType().equals(WorkflowPartitionScanner.MADURA_CODE)) return null;
			String regionContent = document.get(region.getOffset(),region.getLength());
			StringTokenizer st = new StringTokenizer(regionContent);
			context = st.nextToken();
			context = st.nextToken();// set the context object here
		} catch (BadLocationException e) {
			log.warn("{}",e.getMessage());
		}
		
		List<ICompletionProposal> proposals = new ArrayList<>();
		if (context != null) {
			Set<String> operands = schemaParser.findOperandsInScope(context, "");
			for (String operand: operands) {
				proposals.add(new CompletionProposal(operand,documentOffset,0,operand.length()));
			}
		}
		for (int i=0;i<m_functionArray.length;i++)
		{
			proposals.add(new CompletionProposal((String)m_functionArray[i],documentOffset,0,((String)m_functionArray[i]).length()));
		}
		ICompletionProposal[] ret = new ICompletionProposal[proposals.size()];
		int i = 0;
		for (ICompletionProposal p:proposals) {
			ret[i++] = p;
		}
		return ret;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#computeContextInformation(org.eclipse.jface.text.ITextViewer, int)
	 */
	public IContextInformation[] computeContextInformation(
		ITextViewer viewer,
		int documentOffset)
	{
		return null;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getCompletionProposalAutoActivationCharacters()
	 */
	public char[] getCompletionProposalAutoActivationCharacters()
	{
		return new char[] {'<','>','<','>','=','*','%','*','-','/','+'};
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getContextInformationAutoActivationCharacters()
	 */
	public char[] getContextInformationAutoActivationCharacters()
	{
		return null;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getErrorMessage()
	 */
	public String getErrorMessage()
	{
		return "No completions available.";
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getContextInformationValidator()
	 */
	public IContextInformationValidator getContextInformationValidator()
	{
		return null;
	}
}
