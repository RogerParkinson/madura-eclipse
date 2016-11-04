/*******************************************************************************
 * Copyright (c)2014 Prometheus Consulting
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package nz.co.senanque.madura.ruleeditor.editor;

import nz.co.senanque.madura.MaduraPlugin;

import org.eclipse.ui.views.properties.*;

/**
 * This class encapsulates property sheet properties
 * for MarkElement.  This will display properties for
 * the MarkElement when selected in the readme editor's
 * content outline.
 * <p>
 * Not sure this is actually used.
 * @author Roger Parkinson
 */
public class MarkElementProperties implements IPropertySource {
	protected MarkElement element;

	protected static final String PROPERTY_LINECOUNT = "lineno"; //$NON-NLS-1$
	protected static final String PROPERTY_START = "start"; //$NON-NLS-1$
	protected static final String PROPERTY_LENGTH = "length"; //$NON-NLS-1$
/**
 * Creates a new MarkElementProperties.
 *
 * @param element  the element whose properties this instance represents
 */
public MarkElementProperties(MarkElement element) {
	super();
	this.element = element;
}
/* (non-Javadoc)
 * Method declared on IPropertySource
 */
public Object getEditableValue() {
	return this;
}
/* (non-Javadoc)
 * Method declared on IPropertySource
 */
public IPropertyDescriptor[] getPropertyDescriptors() {
	// Create the property vector.
	IPropertyDescriptor[] propertyDescriptors = new IPropertyDescriptor[3];

	// Add each property supported.
	PropertyDescriptor descriptor;

	descriptor = new PropertyDescriptor(PROPERTY_LINECOUNT, MaduraPlugin.getResourceString("Line_count")); //$NON-NLS-1$
	propertyDescriptors[0] = descriptor;
	descriptor = new PropertyDescriptor(PROPERTY_START, MaduraPlugin.getResourceString("Title_start")); //$NON-NLS-1$
	propertyDescriptors[1] = descriptor;
	descriptor = new PropertyDescriptor(PROPERTY_LENGTH, MaduraPlugin.getResourceString("Title_length")); //$NON-NLS-1$
	propertyDescriptors[2] = descriptor;

	// Return it.
	return propertyDescriptors;
}
/* (non-Javadoc)
 * Method declared on IPropertySource
 */
public Object getPropertyValue(Object name) {
	if (name.equals(PROPERTY_LINECOUNT))
		return new Integer(element.getNumberOfLines());
	if (name.equals(PROPERTY_START))
		return new Integer(element.getStart());
	if (name.equals(PROPERTY_LENGTH))
		return new Integer(element.getLength());
	return null;
}
/* (non-Javadoc)
 * Method declared on IPropertySource
 */
public boolean isPropertySet(Object property) {
	return false;
}
/* (non-Javadoc)
 * Method declared on IPropertySource
 */
public void resetPropertyValue(Object property) {
}
/* (non-Javadoc)
 * Method declared on IPropertySource
 */
public void setPropertyValue(Object name, Object value) {
}
}
