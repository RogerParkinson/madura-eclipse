/*******************************************************************************
 * Copyright (c)2014 Prometheus Consulting
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package nz.co.senanque.madura.workfloweditor.editor;

import nz.co.senanque.madura.MaduraPlugin;
import nz.co.senanque.madura.TokenManager;
import nz.co.senanque.madura.ruleeditor.editor.scanners.WhitespaceDetector;
import nz.co.senanque.madura.ruleeditor.editor.scanners.WordDetector;

import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;

/**
 * 
 * @author Roger Parkinson
 */
public class CodeScanner extends RuleBasedScanner
{
	private static String[] s_keywords= { "package", "queue", "process", "if", "else", "do", "for",
		"try", "name", "permission", "catch", "read-permission", "fork", "until", "while",
		"compute", "message", "retry", "continue", "abort", "error", "timeout", "form",
		"arg1","arg2","arg3","arg4","arg5","arg6",};

	public CodeScanner(TokenManager tokenManager)
	{
		IToken defaultToken = tokenManager.getToken(MaduraPlugin.PREF_CODE_COLOR);	
		IToken keywordToken = tokenManager.getToken(MaduraPlugin.PREF_KEYWORD_COLOR);	
		IToken commentToken = tokenManager.getToken(MaduraPlugin.PREF_COMMENT_COLOR);	
		IToken textToken = tokenManager.getToken(MaduraPlugin.PREF_TEXT_COLOR);	

		SingleLineRule commentRule =
			new SingleLineRule(
				"//",
				null,
				commentToken,
				(char) 0,
				true);

		MultiLineRule commentRule2 =
			new MultiLineRule(
				"/*",
				"*/",
				commentToken,
				(char) 0,
				true);
		MultiLineRule textRule =
			new MultiLineRule(
				"\"",
				"\"",
				textToken,
				(char) 0,
				true);
		WordRule keywordRule = new WordRule(new WordDetector());
		for (int i = 0; i < s_keywords.length; i++)
		{
			keywordRule.addWord(s_keywords[i],keywordToken);
		}
		IRule whitespaceRule = new WhitespaceRule(new WhitespaceDetector());
		setDefaultReturnToken(defaultToken);
		setRules( new IRule[] {commentRule,commentRule2,textRule,keywordRule,whitespaceRule});
	}
}
