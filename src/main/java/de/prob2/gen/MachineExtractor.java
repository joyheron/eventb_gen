package de.prob2.gen;

import de.be4.eventb.core.parser.analysis.DepthFirstAdapter;
import de.be4.eventb.core.parser.node.AAnticipatedConvergence;
import de.be4.eventb.core.parser.node.AConvergentConvergence;
import de.be4.eventb.core.parser.node.ADerivedInvariant;
import de.be4.eventb.core.parser.node.AEvent;
import de.be4.eventb.core.parser.node.AInvariant;
import de.be4.eventb.core.parser.node.AOrdinaryConvergence;
import de.be4.eventb.core.parser.node.AVariable;
import de.be4.eventb.core.parser.node.AVariant;
import de.be4.eventb.core.parser.node.PConvergence;
import de.prob.model.eventb.Event;
import de.prob.model.eventb.Event.EventType;
import de.prob.model.eventb.EventBMachine;
import de.prob.model.eventb.MachineModifier;
import de.prob.model.representation.BEvent;

public class MachineExtractor extends DepthFirstAdapter {

	MachineModifier machineM;

	public MachineExtractor(final EventBMachine machine) {
		machineM = new MachineModifier(machine);
	}

	public EventBMachine getMachine() {
		return machineM.getMachine();
	}

	@Override
	public void caseAVariable(final AVariable node) {
		machineM = machineM.variable(node.getName().getText());
	}

	@Override
	public void caseAInvariant(final AInvariant node) {
		machineM = machineM.invariant(node.getName().getText(), node
				.getPredicate().getText());
	}

	@Override
	public void caseADerivedInvariant(final ADerivedInvariant node) {
		machineM = machineM.invariant(node.getName().getText(), node
				.getPredicate().getText(), true);
	}

	@Override
	public void caseAVariant(final AVariant node) {
		machineM = machineM.variant(node.getExpression().getText());
	}

	@Override
	public void caseAEvent(final AEvent node) {
		Event event = new Event(node.getName().getText(),
				extractEventType(node.getConvergence()), false);

		EventExtractor eE = new EventExtractor(event);
		node.apply(eE);
		machineM = new MachineModifier(machineM.getMachine().addTo(
				BEvent.class, event));
	}

	public EventType extractEventType(final PConvergence convergence) {
		if (convergence instanceof AOrdinaryConvergence) {
			return EventType.ORDINARY;
		}
		if (convergence instanceof AAnticipatedConvergence) {
			return EventType.ANTICIPATED;
		}
		if (convergence instanceof AConvergentConvergence) {
			return EventType.CONVERGENT;
		}
		throw new IllegalArgumentException("Unknown convergence "
				+ convergence.toString());
	}

}
