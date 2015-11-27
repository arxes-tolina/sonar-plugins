/*
 *  (c) tolina GmbH, 2015
 */
package de.tolina.sonar.plugins.vft.checks;

import java.util.Objects;

import com.google.common.annotations.VisibleForTesting;

class UnexpectedAccessCheckTestClassCallee {
	//	/** commons-logging Logger für diese Klasse. Per Default auskommentiert */
	//	private final transient Log log = LogFactory.getLog(this.getClass());

	@VisibleForTesting
	Object visibleForTesting = new Object();

	Object visibleForAll = new Object();

	@VisibleForTesting
	void methodeCallee() {
		// NOOP
	}

	void legalCallInsideSameClass() {
		methodeCallee();
	}

	void legalAccesInsideSameClass() {
		Objects.requireNonNull(visibleForTesting);
	}
}