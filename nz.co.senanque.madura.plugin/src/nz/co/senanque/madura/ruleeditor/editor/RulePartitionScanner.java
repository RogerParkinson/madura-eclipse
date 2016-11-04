/*******************************************************************************
 * Copyright (c)2014 Prometheus Consulting
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package nz.co.senanque.madura.ruleeditor.editor;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;

/**
 * 
 * This decides what the different partitions are, and how they are identified.
 * 
 * @author Roger Parkinson
 */
public class RulePartitionScanner extends RuleBasedPartitionScanner
{
	public final static String MADURA_DEFAULT = "__madura_default";
	public final static String MADURA_COMMENT = "__madura_comment";
	public final static String MADURA_TEXT = "__madura_text";
	public final static String MADURA_KEYWORD = "__madura_keyword";
	public final static String MADURA_CODE = "__madura_code";

	public RulePartitionScanner()
	{
		super();
		
		Token commentPartition = new Token(MADURA_COMMENT);
		Token codePartition = new Token(MADURA_CODE);
//		Token textPartition = new Token(MADURA_TEXT);
//		Token keyword= new Token(MADURA_KEYWORD);
//		Token other= new Token(IDocument.DEFAULT_CONTENT_TYPE);

		SingleLineRule commentRule =
			new SingleLineRule(
				"//",
				null,
				commentPartition,
				(char) 0,
				true);

		MultiLineRule commentRule2 =
			new MultiLineRule(
				"/*",
				"*/",
				commentPartition,
				(char) 0,
				true);
		MultiLineRule codeRule1 =
			new MultiLineRule(
				"rule",
				"\n}\n",
				codePartition,
				(char) 0,
				true);
		MultiLineRule codeRule2 =
			new MultiLineRule(
				"constraint",
				"\n}\n",
				codePartition,
				(char) 0,
				true);
		MultiLineRule codeRule3 =
			new MultiLineRule(
				"formula",
				"\n}\n",
				codePartition,
				(char) 0,
				true);

		setPredicateRules(new IPredicateRule[] {commentRule2,commentRule,/*textRule,*/codeRule1,codeRule2,codeRule3});
	}
	public static String[] getLegalContentTypes()
	{
		return new String[]
		{
			IDocument.DEFAULT_CONTENT_TYPE,
			RulePartitionScanner.MADURA_COMMENT,
			RulePartitionScanner.MADURA_CODE
		};
	}
}
