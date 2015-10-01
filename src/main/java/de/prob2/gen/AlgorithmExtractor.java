package de.prob2.gen;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Set;

import org.eventb.core.ast.extension.IFormulaExtension;

import de.be4.eventbalg.core.parser.node.AAlgorithm;
import de.be4.eventbalg.core.parser.node.AAssertStmt;
import de.be4.eventbalg.core.parser.node.AAssignStmt;
import de.be4.eventbalg.core.parser.node.AAssumeStmt;
import de.be4.eventbalg.core.parser.node.AIfStmt;
import de.be4.eventbalg.core.parser.node.ALoopInvariant;
import de.be4.eventbalg.core.parser.node.ALoopVariant;
import de.be4.eventbalg.core.parser.node.AWhileStmt;
import de.be4.eventbalg.core.parser.node.PLoopInvariant;
import de.be4.eventbalg.core.parser.node.PLoopVariant;
import de.be4.eventbalg.core.parser.node.PStmt;
import de.prob.model.eventb.ModelGenerationException;
import de.prob.model.eventb.algorithm.Block;
import de.prob.model.eventb.algorithm.Statement;

public class AlgorithmExtractor extends ElementExtractor {

	public AlgorithmExtractor(final Set<IFormulaExtension> typeEnv) {
		super(typeEnv);
	}

	public Block extract(final AAlgorithm node) {
		LinkedList<PStmt> block = node.getBlock();
		return extractStmts(block);
	}

	private Block extractStmts(final LinkedList<PStmt> block) {
		Block b = new Block(new ArrayList<Statement>(), typeEnv);
		for (PStmt pStmt : block) {
			b = extractStmt(b, pStmt);
		}
		return b;
	}

	private Block extractStmt(final Block b, final PStmt pStmt) {
		if (pStmt instanceof AWhileStmt) {
			AWhileStmt whileStmt = (AWhileStmt) pStmt;
			try {
				return b.While(whileStmt.getCondition().getText(),
						extractStmts(whileStmt.getStatements()),
						extractInvariant(b, whileStmt.getInvariant()),
						extractVariant(b, whileStmt.getVariant()));
			} catch (ModelGenerationException e) {
				handleException(e, whileStmt);
			}
		}
		if (pStmt instanceof AIfStmt) {
			AIfStmt ifStmt = (AIfStmt) pStmt;
			Block thenBlock = extractStmts(ifStmt.getThen());
			Block elseBlock = extractStmts(ifStmt.getElse());
			try {
				return b.If(ifStmt.getCondition().getText(), thenBlock,
						elseBlock);
			} catch (ModelGenerationException e) {
				handleException(e, ifStmt);
			}
		}
		if (pStmt instanceof AAssertStmt) {
			try {
				return b.Assert(((AAssertStmt) pStmt).getPredicate().getText());
			} catch (ModelGenerationException e) {
				handleException(e, pStmt);
			}
		}
		if (pStmt instanceof AAssumeStmt) {
			try {
				return b.Assume(((AAssumeStmt) pStmt).getPredicate().getText());
			} catch (ModelGenerationException e) {
				handleException(e, pStmt);
			}
		}
		if (pStmt instanceof AAssignStmt) {
			try {
				return b.Assign(((AAssignStmt) pStmt).getAction().getText());
			} catch (ModelGenerationException e) {
				handleException(e, pStmt);
			}
		}
		return null;
	}

	private String extractVariant(final Block b, final PLoopVariant variant) {
		if (variant instanceof ALoopVariant) {
			try {
				String text = ((ALoopVariant) variant).getExpression()
						.getText();
				b.parseExpression(text);
				return text;
			} catch (ModelGenerationException e) {
				handleException(e, variant);
			}
		}
		return null;
	}

	private String extractInvariant(final Block b, final PLoopInvariant variant) {
		if (variant instanceof ALoopInvariant) {
			try {
				String text = ((ALoopInvariant) variant).getPredicate()
						.getText();
				b.parsePredicate(text);
				return text;
			} catch (ModelGenerationException e) {
				handleException(e, variant);
			}
		}
		return null;
	}
}
