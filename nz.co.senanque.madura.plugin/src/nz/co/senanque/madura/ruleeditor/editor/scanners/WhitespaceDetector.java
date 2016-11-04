/*******************************************************************************
 * Copyright (c)2014 Prometheus Consulting
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package nz.co.senanque.madura.ruleeditor.editor.scanners;

import org.eclipse.jface.text.rules.IWhitespaceDetector;

/**
 * @author Roger Parkinson
 */
public class WhitespaceDetector implements IWhitespaceDetector {

	public boolean isWhitespace(char c) {
		return Character.isWhitespace(c);
	}
}
