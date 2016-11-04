/*******************************************************************************
 * Copyright (c)2014 Prometheus Consulting
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package nz.co.senanque.madura.ruleeditor.editor;

import nz.co.senanque.madura.Images;
import nz.co.senanque.rulesparser.ParsePackage;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * Provides labels for our scopes and rules
 * 
 * @author Roger Parkinson
 */
public class RuleLabelProvider extends LabelProvider
{
	public RuleLabelProvider()
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
				case ParsePackage.TYPE_CONSTRAINT:
					return Images.CONSTRAINT_IMAGE.createImage();
				case ParsePackage.TYPE_FORMULA:
					return Images.FORMULA_IMAGE.createImage();
				case ParsePackage.TYPE_RULE:
					return Images.RULE_IMAGE.createImage();
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
