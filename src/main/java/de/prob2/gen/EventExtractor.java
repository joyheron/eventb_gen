package de.prob2.gen;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.extension.IFormulaExtension;

import com.google.common.base.Joiner;

import de.be4.eventbalg.core.parser.analysis.DepthFirstAdapter;
import de.be4.eventbalg.core.parser.node.AAction;
import de.be4.eventbalg.core.parser.node.AAnticipatedConvergence;
import de.be4.eventbalg.core.parser.node.AConvergentConvergence;
import de.be4.eventbalg.core.parser.node.ADerivedGuard;
import de.be4.eventbalg.core.parser.node.AExtendedEventRefinement;
import de.be4.eventbalg.core.parser.node.AGuard;
import de.be4.eventbalg.core.parser.node.AOrdinaryConvergence;
import de.be4.eventbalg.core.parser.node.AParameter;
import de.be4.eventbalg.core.parser.node.AWitness;
import de.be4.eventbalg.core.parser.node.TComment;
import de.prob.model.eventb.Event;
import de.prob.model.eventb.Event.EventType;
import de.prob.model.eventb.EventModifier;
import de.prob.model.representation.ModelElementList;

public class EventExtractor extends DepthFirstAdapter {

	private EventModifier eventM;
	private Set<IFormulaExtension> typeEnv;
	private boolean initialisation;

	public EventExtractor(final Event event, Set<IFormulaExtension> typeEnv,
			String comment) {
		this.typeEnv = typeEnv;
		initialisation = event.getName() == "INITIALISATION";
		eventM = new EventModifier(event, initialisation, typeEnv);
		eventM = eventM.addComment(comment);
	}

	public Event getEvent() {
		return eventM.getEvent();
	}

	@Override
	public void caseAParameter(final AParameter node) {
		eventM = eventM.parameter(node.getName().getText(),
				getComment(node.getComments()));
	}

	@Override
	public void caseAGuard(final AGuard node) {
		eventM = eventM.guard(node.getName().getText(), node.getPredicate()
				.getText(), false, getComment(node.getComments()));
	}

	@Override
	public void caseADerivedGuard(final ADerivedGuard node) {
		eventM = eventM.guard(node.getName().getText(), node.getPredicate()
				.getText(), true, getComment(node.getComments()));
	}

	@Override
	public void caseAAction(final AAction node) {
		eventM = eventM.action(node.getName().getText(), node.getAction()
				.getText(), getComment(node.getComments()));
	}

	@Override
	public void caseAWitness(final AWitness node) {
		eventM = eventM.witness(node.getName().getText(), node.getPredicate()
				.getText(), getComment(node.getComments()));
	}

	@Override
	public void caseAExtendedEventRefinement(final AExtendedEventRefinement node) {
		ModelElementList<Event> list = new ModelElementList<Event>();
		Event e = new Event(node.getName().getText(), EventType.ORDINARY, false);
		list = list.addElement(e);
		eventM = new EventModifier(eventM.getEvent().set(Event.class, list),
				initialisation, typeEnv);
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

	public String getComment(List<TComment> comments) {
		List<String> cmts = new ArrayList<String>();
		for (TComment tComment : comments) {
			cmts.add(tComment.getText());
		}
		return Joiner.on("\n").join(cmts);
	}
}
