package it.simonvic.giovi;

import static it.simonvic.giovi.GioviVisitorUtils.visit;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import giovi.GioviParserVisitResult;
import it.simonvic.giovi.antlr.GioviParser.BlockContext;
import it.simonvic.giovi.antlr.GioviParser.IfStatementContext;
import it.simonvic.giovi.utils.TestsVisitor;

public class TestExpressionPatternMatching {

	// TODO(test): mockito?

	@Test
	void testThat_inIf_matchedPattern_isDeclared_onlyInTrueBody() {
		visit(
			"""
			if 69 is i Int do { }
			""",
			new TestsVisitor() {

				@Override
				public GioviParserVisitResult visitBlock(BlockContext ctx) {
					assertThat(lookup("i"))
						.hasValue(VarD.of(69));
					return super.visitBlock(ctx);
				}

				@Override
				public GioviParserVisitResult visitIfStatement(IfStatementContext ctx) {
					var ret = super.visitIfStatement(ctx);
					assertThat(lookup("i"))
						.isEmpty();
					return ret;
				}

			}
		);
	}

	@Test
	void testThat_inIf_negatedMatchedPattern_isNotDeclared() {
		visit(
			"""
			if 69 is not i Int or true do { }
			""",
			new TestsVisitor() {

				@Override
				public GioviParserVisitResult visitBlock(BlockContext ctx) {
					assertThat(lookup("i"))
						.isEmpty();
					return super.visitBlock(ctx);
				}

				@Override
				public GioviParserVisitResult visitIfStatement(IfStatementContext ctx) {
					var ret = super.visitIfStatement(ctx);
					assertThat(lookup("i"))
						.isEmpty();
					return ret;
				}

			}
		);
	}

	@Test
	void testThat_inIf_negatedPattern_isNotDeclared() {
		visit(
			"""
			if 69 is not i Text or true do { }
			""",
			new TestsVisitor() {

				@Override
				public GioviParserVisitResult visitBlock(BlockContext ctx) {
					assertThat(lookup("i"))
						.isEmpty();
					return super.visitBlock(ctx);
				}

				@Override
				public GioviParserVisitResult visitIfStatement(IfStatementContext ctx) {
					var ret = super.visitIfStatement(ctx);
					assertThat(lookup("i"))
						.isEmpty();
					return ret;
				}

			}
		);
	}

	@Test
	void testThat_inIf_matchedProductPattern_isDeclared() {
		visit(
			"""
			if <69, "foo"> is <a Int, b Text> do { }
			""",
			new TestsVisitor() {

				@Override
				public GioviParserVisitResult visitBlock(BlockContext ctx) {
					assertAll(
						() -> assertThat(lookup("a")).hasValue(VarD.of(69)),
						() -> assertThat(lookup("b")).hasValue(VarD.of("foo"))
					);
					return super.visitBlock(ctx);
				}

			}
		);
	}

	@Test
	void testThat_inIf_partiallyMatchedPattern_isNotDeclared() {
		visit(
			"""
			if <69, "foo"> is <a Int, b Bool> or true do { }
			""",
			new TestsVisitor() {

				@Override
				public GioviParserVisitResult visitBlock(BlockContext ctx) {
					assertAll(
						() -> assertThat(lookup("a")).isEmpty(),
						() -> assertThat(lookup("b")).isEmpty()
					);
					return super.visitBlock(ctx);
				}

				@Override
				public GioviParserVisitResult visitIfStatement(IfStatementContext ctx) {
					assertAll(
						() -> assertThat(lookup("a")).isEmpty(),
						() -> assertThat(lookup("b")).isEmpty()
					);
					return super.visitIfStatement(ctx);
				}

			}
		);
	}

	@Test
	void testThat_inIf_negatedPartiallyMatchedPattern_isNotDeclared() {
		visit(
			"""
			if <69, "foo"> is not <a Int, b Bool> or true do { }
			""",
			new TestsVisitor() {

				@Override
				public GioviParserVisitResult visitBlock(BlockContext ctx) {
					assertAll(
						() -> assertThat(lookup("a")).isEmpty(),
						() -> assertThat(lookup("b")).isEmpty()
					);
					return super.visitBlock(ctx);
				}

				@Override
				public GioviParserVisitResult visitIfStatement(IfStatementContext ctx) {
					assertAll(
						() -> assertThat(lookup("a")).isEmpty(),
						() -> assertThat(lookup("b")).isEmpty()
					);
					return super.visitIfStatement(ctx);
				}

			}
		);
	}

	@Test
	void testThat_inIf_shortCircuited_pattern_isNotDeclared() {
		visit(
			"""
			if true or 69 is i Int do { }
			""",
			new TestsVisitor() {

				@Override
				public GioviParserVisitResult visitBlock(BlockContext ctx) {
					assertThat(lookup("i"))
						.isEmpty();
					return super.visitBlock(ctx);
				}

			}
		);
	}

	@Disabled("Candidate not implemented yet")
	@Test
	void testThat_inIf_negatedPattern_isDeclared_inFalseBody() {
		assertAll(
			() -> assertThatNoException().isThrownBy(() -> visit("""
				if 69 is not i Int do {
				}
				i -> _
				""")),
			() -> assertThatNoException().isThrownBy(() -> visit("""
				if 69 is not i Int do {
				} else {
					i -> _
				}
				"""))
		);
	}

}
