/*******************************************************************************
 * Copyright (c)2014 Prometheus Consulting
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package nz.co.senanque.madura.workfloweditor.editor;

import nz.co.senanque.madura.Images;
import nz.co.senanque.process.parser.ParsePackage;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * Provides labels for our scopes and rules
 * 
 * @author Roger Parkinson
 */
public class WorkflowLabelProvider extends LabelProvider
{
	public WorkflowLabelProvider()
	{
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
	 */
	public Image getImage(Object element)
	{
		//return null;
		if (element instanceof MarkElement)
		{
			 switch (((MarkElement)element).getType())
			 {
				case ParsePackage.TYPE_QUEUE:
					return Images.QUEUE_IMAGE.createImage();
				case ParsePackage.TYPE_PROCESS:
					return Images.PROCESS_IMAGE.createImage();
			 }			 	
		}
		return Images.TAG_IMAGE.createImage();
	}
	public String getText(Object element)
	{
		if (element instanceof MarkElement)
		{
			 return ((MarkElement)element).getLabel(null);
		}
		return element.toString();
	}
	public void dispose()
	{
		super.dispose();
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
	 */
	public boolean isLabelProperty(Object element, String property)
	{
		return true;
	}
}
