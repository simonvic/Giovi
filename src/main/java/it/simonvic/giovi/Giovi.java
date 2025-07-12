package it.simonvic.giovi;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.stream.Stream;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.apache.commons.cli.*;

import it.simonvic.giovi.antlr.GioviLexer;
import it.simonvic.giovi.antlr.GioviParser;
import it.simonvic.giovi.exceptions.GioviException;

public class Giovi {

	private static final Option OPT_HELP = Option.builder()
		.option("h")
		.longOpt("help")
		.desc("Show this help")
		.build();

	private static final Option OPT_VERBOSE = Option.builder()
		.option("v")
		.longOpt("verbose")
		.desc("")
		.build();

	private static final Option OPT_SHELL = Option.builder()
		.longOpt("shell")
		.desc("Launch interactive REPL Giovi shell")
		.build();

	private static final Option OPT_FROM_ARG = Option.builder()
		.option("f")
		.longOpt("from")
		.hasArg()
		.argName("code")
		.desc("Interpret code from argument")
		.build();

	private static final Option OPT_FORMAT = Option.builder()
		.longOpt("format")
		.hasArg()
		.argName("file")
		.desc("Format a giovi file")
		.build();

	private static final Option OPT_FORMAT_OUTPUT = Option.builder()
		.longOpt("format-output")
		.hasArg()
		.argName("output-file")
		.desc("Choose where to output formatted Giovi file")
		.build();

	private static final Option OPT_DEBUG_AST = Option.builder()
		.longOpt("debug-ast")
		.desc("Debug AST")
		.build();

	private static final Options OPTIONS = optionsOf(
		OPT_HELP,
		OPT_VERBOSE,
		OPT_SHELL,
		OPT_FROM_ARG,
		OPT_FORMAT,
		OPT_FORMAT_OUTPUT,
		OPT_DEBUG_AST);

	private static void printHelp() {
		HelpFormatter f = new HelpFormatter();
		f.setWidth(80);
		f.setOptionComparator(null);
		f.printHelp("Giovi", OPTIONS, true);
	}

	private static Options optionsOf(Option... options) {
		Options opts = new Options();
		for (Option option : options) {
			opts.addOption(option);
		}
		return opts;
	}

	public static void main(String[] args) throws IOException, ParseException {
		CommandLine cli = new DefaultParser().parse(OPTIONS, args);

		long verbosity = Stream.of(cli.getOptions())
			.filter(OPT_VERBOSE::equals)
			.count();

		if (cli.hasOption(OPT_HELP)) {
			printHelp();
			return;
		}

		if (cli.hasOption(OPT_SHELL)) {
			new GioviShell().run();
			return;
		}

		String moduleName;
		CharStream charStream;
		if (cli.hasOption(OPT_FROM_ARG)) {
			charStream = CharStreams.fromString(cli.getOptionValue(OPT_FROM_ARG));
			moduleName = "";
		} else {
			if (cli.getArgs().length < 1) {
				printHelp();
				return;
			}
			String arg = cli.getArgs()[0];
			if (arg.equals("-")) {
				charStream = CharStreams.fromStream(System.in);
				moduleName = "";
			} else {
				charStream = CharStreams.fromStream(new FileInputStream(arg));
				moduleName = arg;
			}
		}

		var lexer = new GioviLexer(charStream);
		var parser = new GioviParser(new CommonTokenStream(lexer));
		var tree = parser.begin();
		var visitor = new GioviVisitor(moduleName);
		try {
			visitor.visit(tree);
		} catch (GioviException ex) {
			System.err.println(ex.getMessage());
			if (verbosity >= 1 && ex.getCause() != null) {
				System.err.println("Caused by: " + ex.getCause());
			}
			if (verbosity >= 2) {
				ex.printStackTrace();
			}
		}

	}

}
