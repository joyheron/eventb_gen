package de.prob2.gen;

import de.be4.eventb.core.parser.analysis.DepthFirstAdapter;
import de.be4.eventb.core.parser.node.AAxiom;
import de.be4.eventb.core.parser.node.ACarrierSet;
import de.be4.eventb.core.parser.node.AConstant;
import de.be4.eventb.core.parser.node.ADerivedAxiom;
import de.prob.model.eventb.Context;
import de.prob.model.eventb.ContextModifier;

public class ContextExtractor extends DepthFirstAdapter {

	private ContextModifier contextM;

	public ContextExtractor(final Context context) {
		contextM = new ContextModifier(context);
	}

	public Context getContext() {
		return contextM.getContext();
	}

	@Override
	public void caseAAxiom(final AAxiom node) {
		contextM = contextM.axiom(node.getName().getText(), node.getPredicate()
				.getText());
	}

	@Override
	public void caseADerivedAxiom(final ADerivedAxiom node) {
		contextM = contextM.axiom(node.getName().getText(), node.getPredicate()
				.getText(), true);
	}

	@Override
	public void caseACarrierSet(final ACarrierSet node) {
		contextM = contextM.set(node.getName().getText());
	}

	@Override
	public void caseAConstant(final AConstant node) {
		contextM = contextM.constant(node.getName().getText());
	}

}
