/*******************************************************************************
 * Copyright (c)2014 Prometheus Consulting
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package nz.co.senanque.madura.ruleeditor.editor.scanners;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

/**
 * @author Roger Parkinson
 */
public class FormatRule implements IRule
{
	private final IToken token;

	public FormatRule(IToken token)
	{
		this.token = token;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.IRule#evaluate(org.eclipse.jface.text.rules.ICharacterScanner)
	 */
	public IToken evaluate(ICharacterScanner scanner)
	{
		int c = scanner.read();
		if (c == '%')
		{
			do
			{
				c = scanner.read();
			}
			while (c != ICharacterScanner.EOF 
				&& (Character.isLetterOrDigit((char) c)
				|| c == '~' || c == '.'));
			scanner.unread();
			return token;
		}
		scanner.unread();
		return Token.UNDEFINED;
	}
}
