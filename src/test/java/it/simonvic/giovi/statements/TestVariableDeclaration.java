package it.simonvic.giovi.statements;

import static it.simonvic.giovi.GioviVisitorUtils.visit;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import it.simonvic.giovi.utils.Vars;

public class TestVariableDeclaration {

	@Test
	void testThat_variableDeclaration_withNoType_shouldInferType() {
		assertThat(
			visit("""
				var x <- 69
				""")
				.lookup("x")
		).contains(Vars.of(69));
	}

	@Test
	void testThat_variableDeclaration_withNoType_withDefault_shouldInferType() {
		assertThat(
			visit("""
				var x default 420 <- 69
				""")
				.lookup("x")
		).contains(Vars.of(69, 420));
	}

}
