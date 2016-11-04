/*******************************************************************************
 * Copyright (c)2014 Prometheus Consulting
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package nz.co.senanque.madura.workfloweditor.editor;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.editors.text.FileDocumentProvider;

/**
 * 
 * @author Roger Parkinson
 */
public class WorkflowDocumentProvider
   extends FileDocumentProvider
{
   protected IDocument createDocument(Object element)
	  throws CoreException
   {
	  IDocument document = super.createDocument(element);
	  if (document != null)
	  {
		 IDocumentPartitioner partitioner =
			new FastPartitioner(
			   new WorkflowPartitionScanner(),
			   WorkflowPartitionScanner.getLegalContentTypes());
		 partitioner.connect(document);
		 document.setDocumentPartitioner(partitioner);
	  }
	  return document;
   }
   protected void handleElementContentChanged(IFileEditorInput fileEditorInput)
   {
   	super.handleElementContentChanged(fileEditorInput);
   }
   public void aboutToChange(Object element)
   {
   	super.aboutToChange(element);
   }
 }
