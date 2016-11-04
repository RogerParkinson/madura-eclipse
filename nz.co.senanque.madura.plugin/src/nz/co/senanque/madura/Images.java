/*******************************************************************************
 * Copyright (c)2014 Prometheus Consulting
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package nz.co.senanque.madura;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;

/**
 * Convenience class for storing references to image descriptors
 * @author Roger Parkinson
 */
public class Images {
	static final URL BASE_URL = getBaseURL();
	public static final ImageDescriptor QUEUE_IMAGE;
	public static final ImageDescriptor PROCESS_IMAGE;
	public static final ImageDescriptor RULE_IMAGE;
	public static final ImageDescriptor FORMULA_IMAGE;
	public static final ImageDescriptor CONSTRAINT_IMAGE;
	public static final ImageDescriptor TAG_IMAGE;

	static {
		QUEUE_IMAGE = createImageDescriptor("queue.gif"); //$NON-NLS-1$
		PROCESS_IMAGE = createImageDescriptor("process.gif"); //$NON-NLS-1$
		RULE_IMAGE = createImageDescriptor("rule.gif"); //$NON-NLS-1$
		CONSTRAINT_IMAGE = createImageDescriptor("constraint.gif"); //$NON-NLS-1$
		FORMULA_IMAGE = createImageDescriptor("formula.gif"); //$NON-NLS-1$
		TAG_IMAGE = createImageDescriptor("rules.gif"); //$NON-NLS-1$
	}

	/**
	 * Utility method to create an <code>ImageDescriptor</code> from a path to a
	 * file.
	 */
	private static ImageDescriptor createImageDescriptor(String path) {
		try {
			URL url = new URL(BASE_URL, path);
			return ImageDescriptor.createFromURL(url);
		} catch (MalformedURLException e) {
		}
		return ImageDescriptor.getMissingImageDescriptor();
	}

	private static URL getBaseURL() {
		URL url = FileLocator.find(Platform.getBundle(MaduraPlugin.PLUGIN_ID),
				new Path("icons/"), null);
		return url;
	}
}
