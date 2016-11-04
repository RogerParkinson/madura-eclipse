/*******************************************************************************
 * Copyright (c)2014 Prometheus Consulting
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package nz.co.senanque.madura;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.graphics.RGB;

/**
 * @author Roger Parkinson
 *
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = MaduraPlugin.getDefault().getPreferenceStore();
		store.setDefault(
				MaduraPlugin.PREF_COMMENT_COLOR,
				StringConverter.asString(new RGB(0, 127, 0)));
			store.setDefault(
				MaduraPlugin.PREF_CODE_COLOR,
				StringConverter.asString(new RGB(0, 0, 0)));
			store.setDefault(
				MaduraPlugin.PREF_DEFAULT_COLOR,
				StringConverter.asString(new RGB(0, 0, 0)));
			store.setDefault(
				MaduraPlugin.PREF_KEYWORD_COLOR,
				StringConverter.asString(new RGB(127, 0, 85)));
			store.setDefault(
				MaduraPlugin.PREF_TEXT_COLOR,
				StringConverter.asString(new RGB(85, 85, 127)));

	}

}
