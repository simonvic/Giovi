package it.simonvic.giovi;

import static it.simonvic.giovi.GioviVisitorUtils.visit;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import it.simonvic.giovi.exceptions.GioviException;

public class TestModules {

	// TODO(test): ...

	@Test
	void test_declaration() {

	}

	@Nested
	class Import {

		@Test
		void import_nativeModules() {
			visit("""
				import [
					giovi.util.Math as Math
					giovi.util.Random as Random
				]
				Math.PI -> _out
				Random.integer() -> _out
				""");
		}

		@Test
		void import_innerModules() {
			visit("""

				module Foo {
					"hello from Foo" -> _out
					name:Text <- "foo"
					module Bar {
						"hello from Foo.Bar" -> _out
						name:Text <- "bar"
						module Qux {
							"hello from Foo.Bar.Qux" -> _out
							name:Text <- "qux"
						}
					}
					module Baz {
						"hello from Foo.Baz" -> _out
						name:Text <- "baz"
					}
				}

				import Foo
				Foo -> _out
				Foo.name -> _out

				import Foo.Bar
				Foo.Bar -> _out
				Foo.Bar.name -> _out

				import Foo.Bar.Qux
				Foo.Bar.Qux -> _out
				Foo.Bar.Qux.name -> _out

				moduleToImport:Text <- "Baz"
				import Foo."$moduleToImport"
				Foo.Baz -> _out
				Foo.Baz.name -> _out

				""");
		}

		@Test
		void import_fileModules() {
			visit("""
				import "file://src/test/resources/modules_test/a.giovi" as a
				a -> _out
				a.name -> _out
				a.b -> _out
				""");
		}


		@Test
		void import_innerModule_in_fileModule() {
			visit("""
				import "file://src/test/resources/modules_test/a.giovi" . b as b
				b -> _out
				b.name -> _out
				""");
		}


		@Test
		void import_deepInnerModule_in_fileModule() {
			visit("""
				import "file://src/test/resources/modules_test/a.giovi".b.c as c
				c -> _out
				c.name -> _out
				""");
		}


		@Test
		void importing_nestedFileModule_shouldThrow() {
			assertThatExceptionOfType(GioviException.class).isThrownBy(() -> visit("""
				import "file://src/test/resources/modules_test/a.giovi" . "file://foo"
				"""));
		}


	}

}
