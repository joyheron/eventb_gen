package de.prob2.gen;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import de.be4.eventbalg.core.parser.BException;
import de.prob.model.eventb.EventBModel;
import de.prob.model.eventb.algorithm.AlgorithmGenerationOptions;
import de.prob.model.eventb.algorithm.AlgorithmTranslator;
import de.prob.model.eventb.algorithm.graph.GraphMerge;
import de.prob.model.eventb.algorithm.graph.IGraphTransformer;
import de.prob.model.eventb.translate.ModelToXML;

public class Main {
	public final static String NAME = "name";
	public final static String DEBUG = "debug";
	public final static String PATH = "path";
	public final static String GENERATE = "generate";
	public final static String MERGE = "mergeBranches";
	public final static String OPTIMIZE = "optimize";
	public final static String ASSERTIONS = "propagateAssertions";

	public static boolean debug = false;

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		PosixParser parser = new PosixParser();
		Options options = getCommandlineOptions();
		try {
			CommandLine line = parser.parse(options, args);
			String name = "modelgen";
			if (line.hasOption(NAME)) {
				name = line.getOptionValue(NAME);
			}
			if (line.hasOption(DEBUG)) {
				debug = true;
			}

			String path = line.getOptionValue(PATH);
			EventBModel model = new ModelGenerator(path, name).getModel();

			List<IGraphTransformer> transformers = new ArrayList<IGraphTransformer>();
			if (line.hasOption(MERGE)) {
				transformers.add(new GraphMerge());
			}

			if (line.hasOption(GENERATE)) {
				if (debug) {
					System.out.println("running model generation algorithm");
				}
				AlgorithmGenerationOptions opts = new AlgorithmGenerationOptions()
						.optimize(line.hasOption(OPTIMIZE))
						.mergeBranches(line.hasOption(MERGE))
						.propagateAssertions(line.hasOption(ASSERTIONS));

				model = new AlgorithmTranslator(model, opts).run();
			}

			if (debug) {
				System.out.println("writing to Rodin");
			}
			new ModelToXML().writeToRodin(model, name, path);
			System.out.println("Rodin project written to: " + path + name
					+ File.separator);
		} catch (ParseException exp) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("java -jar probcli.jar", options);
			System.exit(-1);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (BException e) {
			e.printStackTrace();
		}

	}

	@SuppressWarnings("static-access")
	public static Options getCommandlineOptions() {
		Options options = new Options();

		Option path = OptionBuilder
				.withArgName("path/to/dir")
				.hasArg()
				.withDescription(
						"specify the directory which contains the model description files (.emch for machines, .ctx for contexts)")
						.create(PATH);

		Option name = OptionBuilder.withArgName("name").hasArg()
				.withDescription("specify the name for the generated project")
				.create(NAME);

		Option debug = new Option(DEBUG,
				"print debug information during project generation");

		Option generate = new Option(GENERATE,
				"run an algorithm to generate Event-B models based on an algorithm description");

		Option optimize = new Option(
				OPTIMIZE,
				"optimize algorithm generation to combine assignments with preceding boolean choices.");

		Option merge = new Option(
				MERGE,
				"merge branches within the control flow graph during algorithm translation in order to optimize the result");

		Option assertions = new Option(ASSERTIONS,
				"propage assertions throughout an algorithm to help with automatic proving");

		OptionGroup required = new OptionGroup();
		required.setRequired(true);
		required.addOption(path);
		options.addOptionGroup(required);
		options.addOption(name);
		options.addOption(debug);
		options.addOption(generate);
		options.addOption(optimize);
		options.addOption(merge);
		options.addOption(assertions);
		return options;
	}
}
