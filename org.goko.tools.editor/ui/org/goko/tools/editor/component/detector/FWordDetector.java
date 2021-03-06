/**
 * 
 */
package org.goko.tools.editor.component.detector;

import org.eclipse.jface.text.rules.IWordDetector;

/**
 * @author Psyko
 * @date 22 mai 2016
 */
public class FWordDetector implements IWordDetector {

	/** (inheritDoc)
	 * @see org.eclipse.jface.text.rules.IWordDetector#isWordStart(char)
	 */
	@Override
	public boolean isWordStart(char c) {
		return c == 'F' || c == 'f';
	}

	/** (inheritDoc)
	 * @see org.eclipse.jface.text.rules.IWordDetector#isWordPart(char)
	 */
	@Override
	public boolean isWordPart(char c) {		
		return false; //Character.isDigit(c) || c == '.';
	}

}
