package de.prob2.gen;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.extension.IFormulaExtension;

import com.google.common.base.Joiner;

import de.be4.eventbalg.core.parser.node.AContextParseUnit;
import de.be4.eventbalg.core.parser.node.AMachineParseUnit;
import de.be4.eventbalg.core.parser.node.TComment;
import de.be4.eventbalg.core.parser.node.TIdentifierLiteral;
import de.prob.model.eventb.Context;
import de.prob.model.eventb.EventBMachine;
import de.prob.model.eventb.EventBModel;
import de.prob.model.eventb.theory.Theory;
import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.DependencyGraph.ERefType;
import de.prob.model.representation.Machine;
import de.prob.model.representation.ModelElementList;

public class ComponentExtractor extends ElementExtractor {

	private EventBModel model;

	public ComponentExtractor(final EventBModel model) {
		super(extractTypeEnvironment(model));
		this.model = model;
	}

	private static Set<IFormulaExtension> extractTypeEnvironment(
			EventBModel model) {
		Set<IFormulaExtension> typeEnv = new HashSet<IFormulaExtension>();
		ModelElementList<Theory> theories = model
				.getChildrenOfType(Theory.class);
		for (Theory theory : theories) {
			typeEnv.addAll(theory.getTypeEnvironment());
		}
		return typeEnv;
	}

	public EventBModel getModel() {
		return model;
	}

	@Override
	public void caseAMachineParseUnit(final AMachineParseUnit node) {
		String name = node.getName().getText();
		EventBMachine machine = new EventBMachine(name);
		ModelElementList<Context> seen = new ModelElementList<Context>();
		for (TIdentifierLiteral contextName : node.getSeenNames()) {
			String cName = contextName.getText();
			AbstractElement context = model.getComponent(cName);
			if (context instanceof Context) {
				seen = seen.addElement((Context) context);
			} else {
				throw new IllegalArgumentException(
						"Tried to find context with name " + cName
						+ ", but found " + context + " instead.");
			}
		}
		ModelElementList<EventBMachine> refines = new ModelElementList<EventBMachine>();
		for (TIdentifierLiteral mchName : node.getRefinesNames()) {
			String mName = mchName.getText();
			AbstractElement m = model.getComponent(mName);
			if (m instanceof EventBMachine) {
				refines = refines.addElement((EventBMachine) m);
			} else {
				throw new IllegalArgumentException(
						"Tried to find machine with name " + mName
						+ ", but found " + m + " instead.");
			}

		}
		machine = machine.set(Machine.class, refines);
		machine = machine.set(Context.class, seen);
		MachineExtractor mE = new MachineExtractor(machine, typeEnv,
				getComment(node.getComments()));
		node.apply(mE);
		model = model.addMachine(mE.getMachine());

	}

	@Override
	public void caseAContextParseUnit(final AContextParseUnit node) {
		String name = node.getName().getText();
		Context context = new Context(name);
		ModelElementList<Context> extended = new ModelElementList<Context>();
		for (TIdentifierLiteral contextName : node.getExtendsNames()) {
			String cName = contextName.getText();
			AbstractElement c = model.getComponent(cName);
			if (c instanceof Context) {
				extended = extended.addElement((Context) c);
			} else {
				throw new IllegalArgumentException(
						"Tried to find context with name " + cName
						+ ", but found " + c + " instead.");
			}
		}
		context = context.set(Context.class, extended);
		ContextExtractor cE = new ContextExtractor(context, typeEnv,
				getComment(node.getComments()));
		node.apply(cE);
		model = model.addContext(cE.getContext());
	}

	public String getComment(List<TComment> comments) {
		List<String> cmts = new ArrayList<String>();
		for (TComment tComment : comments) {
			cmts.add(tComment.getText());
		}
		return Joiner.on("\n").join(cmts);
	}
}
