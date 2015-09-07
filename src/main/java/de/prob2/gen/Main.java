package de.prob2.gen;

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

public class Main {
	public final static String NAME = "name";
	public final static String DEBUG = "debug";
	public final static String PATH = "path";

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

			new ModelGenerator(line.getOptionValue(PATH), name, debug);
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

		OptionGroup required = new OptionGroup();
		required.setRequired(true);
		required.addOption(path);
		options.addOptionGroup(required);
		options.addOption(name);
		options.addOption(debug);
		return options;
	}
}
