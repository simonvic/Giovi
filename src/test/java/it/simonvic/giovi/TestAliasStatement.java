package it.simonvic.giovi;

import static it.simonvic.giovi.GioviVisitorUtils.visit;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import it.simonvic.giovi.utils.Consts;

public class TestAliasStatement {

	@Test
	void aliasing_constants() {
		var scope = visit("""
			const PI <- 3.1415926535
			alias PI as pi
			pi -> _out
			""")
			.getCurrentScope();
		assertThat(scope.getConstantValue("PI").orElseThrow())
			.isSameAs(scope.getConstantValue("pi").orElseThrow());
	}

	@Test
	// TODO: clarify requirements: this effectively allows to declare new constants
	void aliasing_value_shouldDeclareNewConstant() {
		var scope = visit("""
			alias 180 * 2 as NO_SCOPE
			""")
			.getCurrentScope();
		assertThat(scope.getConstantValue("NO_SCOPE"))
			.contains(Consts.of(360));
	}

	@Test
	void aliasing_function() {
		var scope = visit("""
			someFun() {}
			alias someFun as someAliasToFun
			""")
			.getCurrentScope();
		assertThat(scope.getFunction("someFun").orElseThrow())
			.isSameAs(scope.getFunction("someAliasToFun").orElseThrow());
	}

	@Test
	void aliasing_module() {
		var scope = visit("""
			module Foo {}
			alias Foo as AliasOfFoo
			""")
			.getCurrentScope();
		assertThat(scope.getModule("Foo").orElseThrow())
			.isSameAs(scope.getModule("AliasOfFoo").orElseThrow());
	}

	@Test
	void aliasing_nativeModule() {
		var scope = visit("""
			alias giovi.util.Random as Random
			""")
			.getCurrentScope();
		assertThat(scope.getModule("Random").orElseThrow())
			.isSameAs(
				scope.getModule("giovi").orElseThrow()
					.scope().getModule("util").orElseThrow()
					.scope().getModule("Random").orElseThrow()
			);
	}

	@Disabled("Clarify requirements")
	@Test
	void aliasing_variable() {
		var scope = visit("""
			i Int <- 69
			alias i as j
			j -> _out
			j <- 420
			j -> _out
			i -> _out
			""")
			.getCurrentScope();
		assertThat(scope.getVariable("i").orElseThrow())
			.isSameAs(scope.getVariable("j").orElseThrow());
	}

	@Test
	void test_invokingSelfFunction_fromAlias() {
		visit("""
			Color <
				r Int
				g Int
				b Int

				self toText() => Text {
					=> "$self : ${self.r} ${self.g} ${self.b}"
				}
			>
			alias Color as Rgb
			Rgb -> _out
			Rgb.<1,2,3>.toText() -> _out
			""");
	}

}
