/*******************************************************************************
 * Copyright (c)2014 Prometheus Consulting
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package nz.co.senanque.madura.ruleeditor.editor.scanners;

import nz.co.senanque.madura.MaduraPlugin;
import nz.co.senanque.madura.TokenManager;

import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;

/**
 * 
 * @author Roger Parkinson
 */
public class DefaultScanner extends RuleBasedScanner
{
	public DefaultScanner(TokenManager tokenManager)
	{
		IToken importToken = tokenManager.getToken(MaduraPlugin.PREF_KEYWORD_COLOR);	
		IToken propertyToken =
			tokenManager.getToken(MaduraPlugin.PREF_DEFAULT_COLOR);
		setDefaultReturnToken(propertyToken);
		WordRule keywordRule = new WordRule(new WordDetector());
		keywordRule.addWord("import",importToken);
		setRules(new IRule[] {keywordRule,new WhitespaceRule(new WhitespaceDetector())});
	}
}
