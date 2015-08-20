package de.prob2.gen;

import de.be4.eventb.core.parser.analysis.DepthFirstAdapter;
import de.be4.eventb.core.parser.node.AAction;
import de.be4.eventb.core.parser.node.AAnticipatedConvergence;
import de.be4.eventb.core.parser.node.AConvergentConvergence;
import de.be4.eventb.core.parser.node.ADerivedGuard;
import de.be4.eventb.core.parser.node.AExtendedEventRefinement;
import de.be4.eventb.core.parser.node.AGuard;
import de.be4.eventb.core.parser.node.AOrdinaryConvergence;
import de.be4.eventb.core.parser.node.AParameter;
import de.be4.eventb.core.parser.node.AWitness;
import de.prob.model.eventb.Event;
import de.prob.model.eventb.Event.EventType;
import de.prob.model.eventb.EventModifier;
import de.prob.model.representation.ModelElementList;

public class EventExtractor extends DepthFirstAdapter {

	private EventModifier eventM;

	public EventExtractor(final Event event) {
		eventM = new EventModifier(event);
	}

	public Event getEvent() {
		return eventM.getEvent();
	}

	@Override
	public void caseAParameter(final AParameter node) {
		eventM = eventM.parameter(node.getName().getText());
	}

	@Override
	public void caseAGuard(final AGuard node) {
		eventM = eventM.guard(node.getName().getText(), node.getPredicate()
				.getText());
	}

	@Override
	public void caseADerivedGuard(final ADerivedGuard node) {
		eventM = eventM.guard(node.getName().getText(), node.getPredicate()
				.getText(), true);
	}

	@Override
	public void caseAAction(final AAction node) {
		eventM = eventM.action(node.getName().getText(), node.getAction()
				.getText());
	}

	@Override
	public void caseAWitness(final AWitness node) {
		eventM = eventM.witness(node.getName().getText(), node.getPredicate()
				.getText());
	}

	@Override
	public void caseAExtendedEventRefinement(final AExtendedEventRefinement node) {
		ModelElementList<Event> list = new ModelElementList<Event>();
		list = list.addElement(new Event(node.getName().getText(),
				EventType.ORDINARY, false));
		eventM = new EventModifier(eventM.getEvent().set(Event.class, list));
	}

	@Override
	public void caseAConvergentConvergence(AConvergentConvergence node) {
		eventM = eventM.setType(EventType.CONVERGENT);
	}

	@Override
	public void caseAOrdinaryConvergence(AOrdinaryConvergence node) {
		eventM = eventM.setType(EventType.ORDINARY);
	}

	@Override
	public void caseAAnticipatedConvergence(AAnticipatedConvergence node) {
		eventM = eventM.setType(EventType.ANTICIPATED);
	}
}
