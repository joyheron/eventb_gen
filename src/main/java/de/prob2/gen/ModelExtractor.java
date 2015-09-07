package de.prob2.gen;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Joiner;

import de.be4.eventbalg.core.parser.analysis.DepthFirstAdapter;
import de.be4.eventbalg.core.parser.node.AContextParseUnit;
import de.be4.eventbalg.core.parser.node.AMachineParseUnit;
import de.be4.eventbalg.core.parser.node.TComment;
import de.be4.eventbalg.core.parser.node.TIdentifierLiteral;
import de.prob.model.eventb.Context;
import de.prob.model.eventb.EventBMachine;
import de.prob.model.eventb.EventBModel;
import de.prob.model.representation.DependencyGraph.ERefType;
import de.prob.model.representation.Machine;
import de.prob.model.representation.ModelElementList;

public class ModelExtractor extends DepthFirstAdapter {

	private EventBModel model;

	public ModelExtractor(final EventBModel model) {
		this.model = model;
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
			seen = seen.addElement(new Context(cName));
			model = model.addRelationship(name, cName, ERefType.SEES);
		}
		ModelElementList<EventBMachine> refines = new ModelElementList<EventBMachine>();
		for (TIdentifierLiteral mchName : node.getRefinesNames()) {
			String mName = mchName.getText();
			refines = refines.addElement(new EventBMachine(mName));
			model = model.addRelationship(name, mName, ERefType.REFINES);
		}
		machine = machine.set(Machine.class, refines);
		machine = machine.set(Context.class, seen);
		MachineExtractor mE = new MachineExtractor(machine,
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
			extended = extended.addElement(new Context(cName));
			model = model.addRelationship(name, cName, ERefType.EXTENDS);
		}
		context = context.set(Context.class, extended);
		ContextExtractor cE = new ContextExtractor(context,
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
