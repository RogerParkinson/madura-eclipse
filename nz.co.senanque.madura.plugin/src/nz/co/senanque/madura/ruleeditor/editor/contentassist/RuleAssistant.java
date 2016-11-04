/*******************************************************************************
 * Copyright (c)2014 Prometheus Consulting
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package nz.co.senanque.madura.ruleeditor.editor.contentassist;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;

import nz.co.senanque.madura.ProjectPropertyPage;
import nz.co.senanque.madura.TokenManager;
import nz.co.senanque.madura.Utils;
import nz.co.senanque.madura.ruleeditor.editor.RuleConfiguration;
import nz.co.senanque.madura.ruleeditor.editor.RulePartitionScanner;
import nz.co.senanque.rulesparser.FunctionDescriptor;
import nz.co.senanque.rulesparser.FunctionDescriptorFactory;
import nz.co.senanque.rulesparser.FunctionDescriptorHolder;
import nz.co.senanque.rulesparser.FunctionDescriptorHolderImpl;
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
public class RuleAssistant implements IContentAssistProcessor
{
	private static final Logger log = LoggerFactory.getLogger(RuleAssistant.class);
	private final Object[] m_functionArray;
	private final SchemaParser schemaParser;

	public RuleAssistant(TokenManager tokenManager, RuleConfiguration ruleConfiguration)
	{
		super();
		IFile file = (IFile)ruleConfiguration.getEditorInput().getAdapter(IFile.class);
		schemaParser = Utils.getXSDFile(file);
		
		List<Class<?>> externalFunctions = ProjectPropertyPage.getExternalFunctions(file.getProject());
		FunctionDescriptorHolder fdh = new FunctionDescriptorHolderImpl(externalFunctions);
        new FunctionDescriptorFactory().loadOperators(fdh);
		List<String> functions = new ArrayList<>();
		for (Entry<String, FunctionDescriptor> entry: fdh.getFunctions()) {
			FunctionDescriptor o = entry.getValue();
			if (o.getOperator() != null && !o.getOperator().equals("x")) {
//				functions.add(o.getOperator());
				continue;
			}
			int c = o.getParameterTypes().length;
			Class<?> argTypes[] = o.getParameterTypes();
			String n = o.getName()+"(";
			for (int i = 0; i < c ; i++)
			{
				if (i > 0) n+=",";
				n += getParameterName(argTypes[i]);
			}
			n+= ")";
			functions.add(n);
		}

		m_functionArray = functions.toArray();
	}
	private String getParameterName(Class<?> c)
	{
		String name = c.getName();
		int i = name.lastIndexOf('.');
		name = name.substring(i+1).toLowerCase();
		if (name.equals("listeningarray")) name = "list";
		if (name.equals("ruleproxyfield")) name = "field";
		return name;
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
			if (!region.getType().equals(RulePartitionScanner.MADURA_CODE)) return null;
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
