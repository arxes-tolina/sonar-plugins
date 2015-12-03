/*
 *  (c) tolina GmbH, 2015
 */
package de.tolina.sonar.plugins.vft.checks;

import java.util.function.Function;
import java.util.function.Predicate;

import org.sonar.check.Rule;
import org.sonar.plugins.java.api.JavaFileScanner;
import org.sonar.plugins.java.api.JavaFileScannerContext;
import org.sonar.plugins.java.api.semantic.Symbol;
import org.sonar.plugins.java.api.tree.BaseTreeVisitor;
import org.sonar.plugins.java.api.tree.MemberSelectExpressionTree;
import org.sonar.plugins.java.api.tree.NewClassTree;
import org.sonar.plugins.java.api.tree.Tree;

import com.google.common.annotations.VisibleForTesting;

import de.tolina.sonar.plugins.Tags;


/**
 * Checks accessing fields and methods marked as {@link VisibleForTesting}.
 * Only accesses form test code or the same class are legal. 
 * 
 */
@Rule(key = UnexpectedAccessCheck.RULE_KEY, // 
name = UnexpectedAccessCheck.RULE_NAME, // 
description = UnexpectedAccessCheck.RULE_NAME, //
tags = { Tags.BAD_PRACTICE, Tags.DESIGN })
public class UnexpectedAccessCheck extends BaseTreeVisitor implements JavaFileScanner {

	//private final Logger logger = LoggerFactory.getLogger(this.getClass());

	static final String RULE_KEY = "UnexpectedAccessToVisibleForTesting";
	static final String RULE_NAME = "You must not access to package-private method or field which is annotated by @VisibleForTesting.";
	static final String RULE_DESCRIPTION = "<p>You must not access to package-private method or field which is annotated by @VisibleForTesting.</p>"//
			+ "<p>This annotation means that visibility was widened only for test code, so your implementation code</p>"//
			+ "<p>shouldn't access to method which is annotated by this annotation.</p>";

	private JavaFileScannerContext context;

	private final Predicate<Symbol> hasVisibleForTestingPredicate = new HasVisibleForTesting();
	private Function<Tree, Symbol> getSymbol = new GetSymbol();

	@Override
	public void scanFile(final JavaFileScannerContext ctx) {
		context = ctx;
		scan(ctx.getTree());
	}


	@Override
	public void visitNewClass(NewClassTree tree) {
		final Symbol symbol = getSymbol.apply(tree);
		addIssueIfNeeded(symbol, tree);
		super.visitNewClass(tree);
	}


	@Override
	public void visitMemberSelectExpression(final MemberSelectExpressionTree tree) {
		final Symbol symbol = tree.identifier().symbol();
		addIssueIfNeeded(symbol, tree);
		super.visitMemberSelectExpression(tree);
	}

	private void addIssueIfNeeded(final Symbol symbol, final Tree tree) {
		boolean hasVisibleForTesting = hasVisibleForTestingPredicate.test(symbol);
		if (hasVisibleForTesting) {
			context.addIssue(tree, this, String.format(RULE_NAME));
		}
	}

}
