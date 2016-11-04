/*******************************************************************************
 * Copyright (c)2014 Prometheus Consulting
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package nz.co.senanque.madura.workfloweditor.editor;

import java.util.*;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;

/**
 * A list of adaptable objects.  This is a generic list that can
 * be used to display an arbitrary set of adaptable objects in the workbench.
 * Also implements the IWorkbenchAdapter interface for simple display
 * and navigation.
 * @author Roger Parkinson
 */
public class AdaptableList implements IWorkbenchAdapter, IAdaptable {
	public List<IAdaptable> children = new ArrayList<>();
/**
 * Creates a new adaptable list with the given children.
 */
public AdaptableList() {
}
/**
 * Creates a new adaptable list with the given children.
 */
public AdaptableList(IAdaptable[] newChildren) {
	for (int i = 0; i < newChildren.length; i++) {
		children.add(newChildren[i]);
	}
}
/**
 * Adds all the adaptable objects in the given enumeration to this list.
 * Returns this list.
 */
public AdaptableList add(Iterator<IAdaptable> iterator) {
	while (iterator.hasNext()) {
		add((IAdaptable)iterator.next());
	}
	return this;
}
/**
 * Adds the given adaptable object to this list.
 * Returns this list.
 */
public AdaptableList add(IAdaptable adaptable) {
	children.add(adaptable);
	return this;
}
/* (non-Javadoc)
 * Method declared on IAdaptable
 */
public Object getAdapter(Class adapter) {
	if (adapter == IWorkbenchAdapter.class) return this;
	return null;
}
/**
 * Returns the elements in this list.
 */
public Object[] getChildren() {
	return children.toArray();
}
/* (non-Javadoc)
 * Method declared on IWorkbenchAdapter
 */
public Object[] getChildren(Object o) {
	return children.toArray();
}
/* (non-Javadoc)
 * Method declared on IWorkbenchAdapter
 */
public ImageDescriptor getImageDescriptor(Object object) {
	return null;
}
/* (non-Javadoc)
 * Method declared on IWorkbenchAdapter
 */
public String getLabel(Object object) {
	return object == null ? "" : object.toString(); //$NON-NLS-1$
}
/* (non-Javadoc)
 * Method declared on IWorkbenchAdapter
 */
 public Object getParent(Object object) {
	 return null;
 }
public Object getParent() {
	return children.get(0);
}
public void clear()
{
	children.clear();
}
/**
 * Removes the given adaptable object from this list.
 */
public void remove(IAdaptable adaptable) {
	children.remove(adaptable);
}
/**
 * Returns the number of items in the list
 */
public int size() {
	return children.size();
}
}
