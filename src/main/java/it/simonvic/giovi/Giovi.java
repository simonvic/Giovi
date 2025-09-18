package it.simonvic.giovi;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.apache.commons.cli.*;

import giovi.Module;
import giovi.Module.FileModule;
import giovi.Module.NativeModule;
import it.simonvic.giovi.antlr.GioviLexer;
import it.simonvic.giovi.antlr.GioviParser;
import it.simonvic.giovi.exceptions.GioviException;
import it.simonvic.giovi.lsp.GioviLS;
import it.simonvic.giovi.lsp.proxy.GioviLSProxy;

public class Giovi {

	private static final Option OPT_HELP = Option.builder()
		.option("h")
		.longOpt("help")
		.desc("Show this help")
		.get();

	private static final Option OPT_VERBOSE = Option.builder()
		.option("v")
		.longOpt("verbose")
		.desc("")
		.get();

	private static final Option OPT_LS = Option.builder()
		.longOpt("ls")
		.desc("Launch Giovi language server")
		.build();

	private static final Option OPT_LS_PROXY = Option.builder()
		.longOpt("ls-proxy")
		.desc("Launch Giovi language server proxy")
		.build();

	private static final Option OPT_SHELL = Option.builder()
		.longOpt("shell")
		.desc("Launch interactive REPL Giovi shell")
		.get();

	private static final Option OPT_FROM_ARG = Option.builder()
		.option("f")
		.longOpt("from")
		.hasArg()
		.argName("code")
		.desc("Interpret code from argument")
		.get();

	private static final Option OPT_FORMAT = Option.builder()
		.longOpt("format")
		.hasArg()
		.argName("file")
		.desc("Format a giovi file")
		.get();

	private static final Option OPT_FORMAT_OUTPUT = Option.builder()
		.longOpt("format-output")
		.hasArg()
		.argName("output-file")
		.desc("Choose where to output formatted Giovi file")
		.get();

	private static final Option OPT_DEBUG_AST = Option.builder()
		.longOpt("debug-ast")
		.desc("Debug AST")
		.get();

	private static final Options OPTIONS = optionsOf(
		OPT_HELP,
		OPT_VERBOSE,
		OPT_LS,
		OPT_LS_PROXY,
		OPT_SHELL,
		OPT_FROM_ARG,
		OPT_FORMAT,
		OPT_FORMAT_OUTPUT,
		OPT_DEBUG_AST
	);

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

	public static void main(String[] args)
		throws IOException, ParseException, InterruptedException, ExecutionException {
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

		if (cli.hasOption(OPT_LS)) {
			GioviLS.main(new String[] {});
			return;
		}

		if (cli.hasOption(OPT_LS_PROXY)) {
			new GioviLSProxy().run();
			return;
		}

		Module module;
		CharStream charStream;
		if (cli.hasOption(OPT_FROM_ARG)) {
			charStream = CharStreams.fromString(cli.getOptionValue(OPT_FROM_ARG));
			module = new NativeModule("--", GioviVisitor.ROOT_MODULE);
		} else {
			if (cli.getArgs().length < 1) {
				printHelp();
				return;
			}
			String arg = cli.getArgs()[0];
			if (arg.equals("-")) {
				charStream = CharStreams.fromStream(System.in);
				module = new NativeModule("-", GioviVisitor.ROOT_MODULE);
			} else {
				charStream = CharStreams.fromStream(new FileInputStream(arg));
				module = new FileModule(URI.create("file://" + arg));
			}
		}

		var lexer = new GioviLexer(charStream);
		var parser = new GioviParser(new CommonTokenStream(lexer));
		var tree = parser.begin();
		var visitor = new GioviVisitor(module);
		try {
			visitor.visit(tree);
		} catch (GioviException ex) {
			if (verbosity >= 3) {
				ex.printStackTrace();
			} else if (verbosity >= 1) {
				Throwable cause = ex.getCause();
				while (cause != null) {
					if (verbosity >= 2) {
						System.err.println("=> " + cause);
					} else {
						System.err.println("=> " + cause.getMessage());
					}
					cause = cause.getCause();
				}
			} else {
				System.err.println(ex.getMessage());
			}
		}

	}

}
