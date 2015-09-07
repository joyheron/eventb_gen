package de.prob2.gen;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Joiner;

import de.be4.eventbalg.core.parser.analysis.DepthFirstAdapter;
import de.be4.eventbalg.core.parser.node.AAxiom;
import de.be4.eventbalg.core.parser.node.ACarrierSet;
import de.be4.eventbalg.core.parser.node.AConstant;
import de.be4.eventbalg.core.parser.node.ADerivedAxiom;
import de.be4.eventbalg.core.parser.node.TComment;
import de.prob.model.eventb.Context;
import de.prob.model.eventb.ContextModifier;

public class ContextExtractor extends DepthFirstAdapter {

	private ContextModifier contextM;

	public ContextExtractor(final Context context, String comment) {
		contextM = new ContextModifier(context);
		contextM = contextM.addComment(comment);
	}

	public Context getContext() {
		return contextM.getContext();
	}

	@Override
	public void caseAAxiom(final AAxiom node) {
		contextM = contextM.axiom(node.getName().getText(), node.getPredicate()
				.getText(), false, getComment(node.getComments()));
	}

	@Override
	public void caseADerivedAxiom(final ADerivedAxiom node) {
		contextM = contextM.axiom(node.getName().getText(), node.getPredicate()
				.getText(), true, getComment(node.getComments()));
	}

	@Override
	public void caseACarrierSet(final ACarrierSet node) {
		contextM = contextM.set(node.getName().getText(),
				getComment(node.getComments()));
	}

	@Override
	public void caseAConstant(final AConstant node) {
		contextM = contextM.constant(node.getName().getText(),
				getComment(node.getComments()));
	}

	public String getComment(List<TComment> comments) {
		List<String> cmts = new ArrayList<String>();
		for (TComment tComment : comments) {
			cmts.add(tComment.getText());
		}
		return Joiner.on("\n").join(cmts);
	}

}
