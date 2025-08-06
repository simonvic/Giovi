package it.simonvic.giovi;

import static it.simonvic.giovi.GioviVisitorUtils.visit;

import org.junit.jupiter.api.Test;

public class TestModules {

	// TODO(test): ...

	@Test
	void test_declaration() {

	}

	@Test
	void modules() {
		visit(
			"""
			module Foo {
				x Text <- "foo"
				"Hello from foo" -> _out
				module Bar {
					x Text <- "bar"
					"Hello from foo.bar" -> _out
					module Qux {
						x Text <- "qux"
						"Hello from foo.bar.qux" -> _out
					}
				}
			}

			Foo -> _out
			import Foo
			Foo.x -> _out

			import giovi.util.Math as Math
			Math -> _out

			import giovi.util.Color as Color
			Color.Rgb.<> -> _out

			""");
	}

}
