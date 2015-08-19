package de.prob2.gen;

public class Main {
	private static Thread prob;

	public static void runProB(final de.prob.Main m, final String[] args) {
		prob = new Thread(new Runnable() {
			@Override
			public void run() {
				m.main(args);
			}
		});
		prob.start();
	}

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		de.prob.Main m = de.prob.Main.getInjector().getInstance(
				de.prob.Main.class);
		runProB(m, args);
	}
}
