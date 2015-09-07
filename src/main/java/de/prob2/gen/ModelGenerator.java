package de.prob2.gen;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;

import de.be4.eventbalg.core.parser.BException;
import de.be4.eventbalg.core.parser.EventBParser;
import de.be4.eventbalg.core.parser.node.Start;
import de.prob.Main;
import de.prob.model.eventb.EventBModel;
import de.prob.model.eventb.translate.ModelToXML;
import de.prob.scripting.StateSpaceProvider;

public class ModelGenerator {

	private EventBModel model;

	public ModelGenerator(String path, String projectName, boolean debug)
			throws IOException, BException {
		EventBModel model = new EventBModel(Main.getInjector().getInstance(
				StateSpaceProvider.class));
		File file = new File(path);
		checkFile(file, true);
		File[] files = file.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				String lowercaseName = name.toLowerCase();
				if (lowercaseName.endsWith(".emch")
						|| lowercaseName.endsWith(".ctx")) {
					return true;
				} else {
					return false;
				}
			}
		});
		for (File f : files) {
			checkFile(f, false);
			if (debug) {
				System.out.println("viewing file " + file.getAbsolutePath());
			}
			String text = readFile(f);
			if (debug) {
				System.out.println("extracting file " + file.getAbsolutePath());
			}
			model = addComponent(model, text);
			if (debug) {
				System.out.println("file extraction sucessful for "
						+ file.getAbsolutePath());
				System.out.println("Model is now: " + model.toString());
			}

		}
		this.model = model;
		if (debug) {
			System.out.println("writing to Rodin");
		}
		new ModelToXML().writeToRodin(model, projectName, path);

		System.out.println("Rodin project written to: " + path + projectName
				+ File.separator);

	}

	public String readFile(File file) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(file));
		try {
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				sb.append(System.lineSeparator());
				line = br.readLine();
			}
			return sb.toString();
		} finally {
			br.close();
		}
	}

	public void checkFile(File file, boolean directory) throws IOException {
		if (!file.exists()) {
			throw new FileNotFoundException(file.getAbsolutePath());
		} else if (file.isDirectory() != directory) {
			String expected = directory ? "directory" : "file";
			String was = directory ? "file" : "directory";
			throw new IOException("Expected " + file.getAbsolutePath()
					+ " to be a " + expected + " but was a " + was);
		}
	}

	public EventBModel getModel() {
		return model;
	}

	public EventBModel addComponent(EventBModel model,
			final String componentDesc) throws BException {
		EventBParser parser = new EventBParser();
		Start ast = parser.parse(componentDesc, false);
		ModelExtractor modelE = new ModelExtractor(model);
		ast.apply(modelE);
		return modelE.getModel();
	}

}
