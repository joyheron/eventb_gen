package de.prob2.gen;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Joiner;

import de.be4.eventbalg.core.parser.analysis.DepthFirstAdapter;
import de.be4.eventbalg.core.parser.node.ADerivedInvariant;
import de.be4.eventbalg.core.parser.node.AEvent;
import de.be4.eventbalg.core.parser.node.AInvariant;
import de.be4.eventbalg.core.parser.node.AVariable;
import de.be4.eventbalg.core.parser.node.AVariant;
import de.be4.eventbalg.core.parser.node.TComment;
import de.prob.model.eventb.Event;
import de.prob.model.eventb.Event.EventType;
import de.prob.model.eventb.EventBMachine;
import de.prob.model.eventb.MachineModifier;
import de.prob.model.representation.BEvent;

public class MachineExtractor extends DepthFirstAdapter {

	MachineModifier machineM;

	public MachineExtractor(final EventBMachine machine, String comment) {
		machineM = new MachineModifier(machine);
		machineM = machineM.addComment(comment);
	}

	public EventBMachine getMachine() {
		return machineM.getMachine();
	}

	@Override
	public void caseAVariable(final AVariable node) {
		machineM = machineM.variable(node.getName().getText(),
				getComment(node.getComments()));
	}

	@Override
	public void caseAInvariant(final AInvariant node) {
		machineM = machineM.invariant(node.getName().getText(), node
				.getPredicate().getText(), false,
				getComment(node.getComments()));
	}

	@Override
	public void caseADerivedInvariant(final ADerivedInvariant node) {
		machineM = machineM
				.invariant(node.getName().getText(), node.getPredicate()
						.getText(), true, getComment(node.getComments()));
	}

	@Override
	public void caseAVariant(final AVariant node) {
		machineM = machineM.variant(node.getExpression().getText(),
				getComment(node.getComments()));
	}

	@Override
	public void caseAEvent(final AEvent node) {
		EventExtractor eE = new EventExtractor(new Event(node.getName()
				.getText(), EventType.ORDINARY, false),
				getComment(node.getComments()));
		node.apply(eE);

		machineM = new MachineModifier(machineM.getMachine().addTo(
				BEvent.class, eE.getEvent()));
	}

	public String getComment(List<TComment> comments) {
		List<String> cmts = new ArrayList<String>();
		for (TComment tComment : comments) {
			cmts.add(tComment.getText());
		}
		return Joiner.on("\n").join(cmts);
	}
}
