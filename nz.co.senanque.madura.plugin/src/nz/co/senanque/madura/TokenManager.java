/*******************************************************************************
 * Copyright (c)2014 Prometheus Consulting
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package nz.co.senanque.madura;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * 
 * @author Roger Parkinson
 */
public class TokenManager
{
	private Map<RGB, Color> colorTable = new HashMap<>(10);
	private Map<String,Token> tokenTable = new HashMap<>(10);
	private final IPreferenceStore preferenceStore;

	public TokenManager(IPreferenceStore preferenceStore)
	{
		this.preferenceStore = preferenceStore;
	}
	public IToken getToken(String prefKey)
	{
		Token token = (Token) tokenTable.get(prefKey);
		if (token == null)
		{
			String colorName = preferenceStore.getString(prefKey);
			RGB rgb = StringConverter.asRGB(colorName);
			token = new Token(new TextAttribute(getColor(rgb)));
			tokenTable.put(prefKey, token);
		}
		return token;
	}
	public void dispose()
	{
		Iterator<Color> e = colorTable.values().iterator();
		while (e.hasNext())
			((Color)e.next()).dispose();
	}
	private Color getColor(RGB rgb)
	{
		Color color = (Color) colorTable.get(rgb);
		if (color == null || color.isDisposed())
		{
			color = new Color(Display.getCurrent(),rgb);
			colorTable.put(rgb, color);
		}
		return color;
	}
	public boolean affectsTextPresentation(PropertyChangeEvent event)
	{
		Token token = (Token) tokenTable.get(event.getProperty());
		return (token != null);
	}
	public void handlePreferenceStoreChanged(PropertyChangeEvent event)
	{
		String prefKey = event.getProperty();
		Token token = (Token) tokenTable.get(prefKey);
		if (token != null)
		{
			String colorName = preferenceStore.getString(prefKey);
			RGB rgb = StringConverter.asRGB(colorName);
			token.setData(new TextAttribute(getColor(rgb)));
		}
	}
	/* (non-Javadoc)
	 * @see nz.co.senanque.madura.storyboard.IColourProvider#getColour(java.lang.String)
	 */
	public Color getColour(String id)
	{
		String colorName = preferenceStore.getString(id);
		RGB rgb = StringConverter.asRGB(colorName);
		return getColor(rgb);
	}
	public String getPreference(String key)
	{
		return preferenceStore.getString(key);
	}
}
