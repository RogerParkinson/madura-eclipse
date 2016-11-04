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

import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedScanner;

/**
 * 
 * @author Roger Parkinson
 */
public class CommentScanner extends RuleBasedScanner
{
	public CommentScanner(TokenManager tokenManager)
	{
		IToken commentToken =
			tokenManager.getToken(MaduraPlugin.PREF_COMMENT_COLOR);
		setDefaultReturnToken(commentToken);
	}
}
