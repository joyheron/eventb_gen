package de.prob2.gen;

import de.be4.eventb.core.parser.analysis.DepthFirstAdapter;
import de.be4.eventb.core.parser.node.ADerivedInvariant;
import de.be4.eventb.core.parser.node.AEvent;
import de.be4.eventb.core.parser.node.AInvariant;
import de.be4.eventb.core.parser.node.AVariable;
import de.be4.eventb.core.parser.node.AVariant;
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
		Event event = new Event(node.getName().getText(), EventType.ORDINARY,
				false);

		EventExtractor eE = new EventExtractor(event);
		node.apply(eE);
		machineM = new MachineModifier(machineM.getMachine().addTo(
				BEvent.class, event));
	}

}
