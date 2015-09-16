package de.prob2.gen;

import java.io.File;
import java.io.IOException;

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
import de.prob.model.eventb.algorithm.AlgorithmTranslator;
import de.prob.model.eventb.algorithm.NaiveAlgorithmTranslator;
import de.prob.model.eventb.algorithm.NaiveTerminationAnalysis;
import de.prob.model.eventb.translate.ModelToXML;

public class Main {
	public final static String NAME = "name";
	public final static String DEBUG = "debug";
	public final static String PATH = "path";
	public final static String GENERATE = "generate";
	public final static String NAIVE = "naive";
	public final static String TERMINATION = "termination";

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
			boolean debug = false;
			if (line.hasOption(DEBUG)) {
				debug = true;
			}

			String path = line.getOptionValue(PATH);
			EventBModel model = new ModelGenerator(path, name, debug)
			.getModel();

			if (line.hasOption(GENERATE)) {
				if (debug) {
					System.out.println("running model generation algorithm");
				}
				model = new AlgorithmTranslator(model).run();
			} else if (line.hasOption(NAIVE)) {
				if (debug) {
					System.out
					.println("running naive model generation algorithm");
				}
				model = new NaiveAlgorithmTranslator(model).run();
				if (line.hasOption(TERMINATION)) {
					if (debug) {
						System.out.println("running termination analysis");
					}
					model = new NaiveTerminationAnalysis(model).run();
				}
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

		Option naive = new Option(
				NAIVE,
				"naive algorithm for the generate of Event-B models based an algorithm description");

		Option termination = new Option(
				TERMINATION,
				"use in connection with 'naive' algorithm to generate specifications including a framework to help with termination proofs");

		OptionGroup required = new OptionGroup();
		required.setRequired(true);
		required.addOption(path);
		options.addOptionGroup(required);
		options.addOption(name);
		options.addOption(debug);
		options.addOption(generate);
		options.addOption(naive);
		options.addOption(termination);
		return options;
	}
}
