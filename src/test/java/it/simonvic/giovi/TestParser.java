package it.simonvic.giovi;

import static it.simonvic.giovi.GioviVisitorUtils.parserOf;
import static org.assertj.core.api.Assertions.assertThatNoException;

import org.junit.jupiter.api.Test;

import giovi.GioviParserVisitResult;
import it.simonvic.giovi.antlr.GioviParserBaseVisitor;

public class TestParser {

	@Test
	void parse() {
		assertThatNoException()
			.isThrownBy(() -> new GioviParserBaseVisitor<GioviParserVisitResult>().visit(parserOf("""
				=> foo()
				=> foo.foo.foo()
				=> foo().foo()
				=> foo()()
				=> {foo}()

				foo()
				foo.foo.foo()
				foo().foo()
				foo()()
				{foo}()

				=> ::foo.foo.foo
				=> ::Foo.foo.foo
				=> ::Foo.foo().foo
				""")
				.begin()));
	}

}
