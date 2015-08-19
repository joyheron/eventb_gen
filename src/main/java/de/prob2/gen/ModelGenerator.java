package de.prob2.gen;

import de.be4.eventb.core.parser.BException;
import de.be4.eventb.core.parser.EventBParser;
import de.be4.eventb.core.parser.analysis.ASTPrinter;
import de.be4.eventb.core.parser.node.Start;
import de.prob.Main;
import de.prob.model.eventb.EventBModel;
import de.prob.scripting.StateSpaceProvider;

public class ModelGenerator {

	private EventBModel model;

	public ModelGenerator() {
		model = new EventBModel(Main.getInjector().getInstance(
				StateSpaceProvider.class));
	}

	public EventBModel getModel() {
		return model;
	}

	public EventBModel addComponent(final String componentDesc)
			throws BException {
		EventBParser parser = new EventBParser();
		Start ast = parser.parse(componentDesc, true);
		ast.apply(new ASTPrinter());
		ModelExtractor modelE = new ModelExtractor(model);
		ast.apply(modelE);
		model = modelE.getModel();
		return model;
	}

}
