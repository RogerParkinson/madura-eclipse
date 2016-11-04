/*******************************************************************************
 * Copyright (c)2014 Prometheus Consulting
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package nz.co.senanque.madura.ruleeditor.preferences;
import nz.co.senanque.madura.MaduraPlugin;

import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
/**
 * This class represents a preference page that
 * is contributed to the Preferences dialog. By 
 * subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows
 * us to create a page that is small and knows how to 
 * save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They
 * are stored in the preference store that belongs to
 * the main plug-in class. That way, preferences can
 * be accessed directly via the preference store.
 * @author Roger Parkinson
 */
public class RulePreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage
{
	public static final String P_PATH = "pathPreference";
	public static final String P_BOOLEAN = "booleanPreference";
	public static final String P_CHOICE = "choicePreference";
	public static final String P_STRING = "stringPreference";
	public RulePreferencePage()
	{
		super(GRID);
		setPreferenceStore(MaduraPlugin.getDefault().getPreferenceStore());
		setDescription("Madura preferences");
		initializeDefaults();
	}
	/**
	 * Sets the default values of the preferences.
	 */
	private void initializeDefaults()
	{
		IPreferenceStore store = getPreferenceStore();
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
		/*store.setDefault(
			PageWidget.FOREGROUND,
			StringConverter.asString(new RGB(255, 255, 255)));
		store.setDefault(
			PageWidget.BACKGROUND,
			StringConverter.asString(new RGB(0, 0, 255)));
		store.setDefault(
			ActionWidget.FOREGROUND,
			StringConverter.asString(new RGB(0, 0, 0)));
		store.setDefault(
			ActionWidget.BACKGROUND,
			StringConverter.asString(new RGB(255, 255, 0)));
		store.setDefault(
			EventWidget.FOREGROUND,
			StringConverter.asString(new RGB(0, 0, 0)));
		store.setDefault(
			EventWidget.BACKGROUND,
			StringConverter.asString(new RGB(192, 192, 192)));
		*/
	}
	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	public void createFieldEditors()
	{
		addField(
			new ColorFieldEditor(
				MaduraPlugin.PREF_COMMENT_COLOR,
				"&Comment color:",
				getFieldEditorParent()));
		addField(
			new ColorFieldEditor(
				MaduraPlugin.PREF_TEXT_COLOR,
				"&Text color:",
				getFieldEditorParent()));
		addField(
			new ColorFieldEditor(
				MaduraPlugin.PREF_CODE_COLOR,
				"Co&de color:",
				getFieldEditorParent()));
		addField(
			new ColorFieldEditor(
				MaduraPlugin.PREF_KEYWORD_COLOR,
				"&Keyword color:",
				getFieldEditorParent()));
		addField(
			new ColorFieldEditor(
				MaduraPlugin.PREF_DEFAULT_COLOR,
				"&Default color:",
				getFieldEditorParent()));
		/*addField(
			new ColorFieldEditor(
				PageWidget.BACKGROUND,
				"Page Background:",
				getFieldEditorParent()));
		addField(
			new ColorFieldEditor(
				PageWidget.FOREGROUND,
				"Page Foreground:",
				getFieldEditorParent()));
		addField(
			new ColorFieldEditor(
				ActionWidget.BACKGROUND,
				"Action Background:",
				getFieldEditorParent()));
		addField(
			new ColorFieldEditor(
				ActionWidget.FOREGROUND,
				"Action Foreground",
				getFieldEditorParent()));
		addField(
			new ColorFieldEditor(
				EventWidget.BACKGROUND,
				"Event Background",
				getFieldEditorParent()));
		addField(
			new ColorFieldEditor(
				EventWidget.FOREGROUND,
				"Event Foreground",
				getFieldEditorParent()));
		*/
	}
	public void init(IWorkbench workbench)
	{
	}
}