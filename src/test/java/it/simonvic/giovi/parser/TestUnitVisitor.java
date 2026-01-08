package it.simonvic.giovi.parser;

import static it.simonvic.giovi.GioviVisitorUtils.parserOf;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Collectors;

import it.simonvic.giovi.antlr.TestUnitParser.BeginContext;
import it.simonvic.giovi.antlr.TestUnitParser.UnitContext;
import it.simonvic.giovi.antlr.TestUnitParserBaseVisitor;

public class TestUnitVisitor extends TestUnitParserBaseVisitor<Void> {

	public static String lispTreeOf(String code) {
		var parser = parserOf(code);
		return parser.begin().toStringTree(parser);
	}

	public static String formatLispTree(String code) {
		return code.lines()
			.filter(l -> !l.isBlank())
			.map(l -> l.trim().replaceAll("\n", ""))
			.collect(Collectors.joining(" "));
	}


	@Override
	public Void visitBegin(BeginContext ctx) {
		for (var unit : ctx.unit()) {
			visitUnit(unit);
		}
		return null;
	}

	@Override
	public Void visitUnit(UnitContext ctx) {
		String actualLispTree = lispTreeOf(ctx.code.getText());
		String expectedLispTreeFormatted = formatLispTree(ctx.tree.getText());
		assertThat(actualLispTree).isEqualTo(expectedLispTreeFormatted);
		return null;
	}

}

