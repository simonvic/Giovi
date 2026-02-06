package it.simonvic.giovi;

import static it.simonvic.giovi.GioviVisitorUtils.visit;
import static it.simonvic.giovi.GioviVisitorUtils.visitExpression;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import giovi.Natural;
import giovi.lang.*;
import it.simonvic.giovi.util.GioviLists;
import it.simonvic.giovi.util.GioviMaps;
import it.simonvic.giovi.util.Structs;

public class TestDefaults {

	@Nested
	class TestDefaultOverride {

		@Test
		void testDefaultOverrideNatural() {
			assertThat(visit("""
				x Natural default 69n
				""")
				.getCurrentScope()
				.getVariable("x")
				.orElseThrow()
				.defaultValue()
				.value())
				.isEqualTo(new Natural(69));
		}

		@Test
		void testDefaultOverrideInt() {
			assertThat(visit("""
				x Int default 69
				""")
				.getCurrentScope()
				.getVariable("x")
				.orElseThrow()
				.defaultValue()
				.value())
				.isEqualTo(69);
		}

		@Test
		void testDefaultOverrideReal() {
			assertThat(visit("""
				x Real default 6.9
				""")
				.getCurrentScope()
				.getVariable("x")
				.orElseThrow()
				.defaultValue()
				.value())
				.isEqualTo(6.9);
		}

		@Test
		void testDefaultOverrideBool() {
			assertThat(visit("""
				x Bool default true
				""")
				.getCurrentScope()
				.getVariable("x")
				.orElseThrow()
				.defaultValue()
				.value())
				.isEqualTo(true);
		}

		@Test
		void testDefaultOverrideText() {
			assertThat(visit("""
				x Text default "foo"
				""")
				.getCurrentScope()
				.getVariable("x")
				.orElseThrow()
				.defaultValue()
				.value())
				.isEqualTo("foo");
		}

		@Test
		void testDefaultOverrideList() {
			assertThat(visit("""
				x [Int] default [1 2 3]
				""")
				.getCurrentScope()
				.getVariable("x")
				.orElseThrow()
				.defaultValue()
				.value())
				.isEqualTo(GioviLists.of(1, 2, 3).value());
		}

		@Test
		void testDefaultOverrideMap() {
			assertThat(visit("""
				x [Int:Bool] default [1:true 2:false]
				""")
				.getCurrentScope()
				.getVariable("x")
				.orElseThrow()
				.defaultValue()
				.value())
				.isEqualTo(Map.of(
					Var.of(1), Var.of(true),
					Var.of(2), Var.of(false)));
		}

		@Test
		void testDefaultOverrideStruct() {
			var def = visit("""
				Bar <
					baz Text
					foobar [Int]
				>
				Foo <
					foo Text
					bar Bar
				>
				x Foo default Foo.<
					foo: "foo"
					bar: Bar.<
						baz: "baz"
						foobar: [1 2 3]
					>
				>
				""")
				.getCurrentScope()
				.getVariable("x")
				.orElseThrow()
				.defaultValue()
				.value();
			assertThat(def)
				.isEqualTo(Structs.of(
					"Foo",
					"foo", VarD.of("foo"),
					"bar", VarD.of(
						Structs.of("Bar",
							"baz", VarD.of("baz"),
							"foobar", new VarD<>(
								GioviLists.of(1, 2, 3),
								new GioviList(GioviInt.TYPE))),
						Structs.of("Bar",
							"baz", VarD.of(""),
							"foobar", new VarD<>(
								new GioviList(GioviInt.TYPE),
								new GioviList(GioviInt.TYPE))))));
		}

		@Test
		void testNestedDefaultOverrideStruct() {
			var def = visit("""
				Bar <
					baz Text default "bazdefault"
					foobar [Int] default [69]
				>
				Foo <
					foo Text default "foodefault"
					bar Bar default Bar.<
						baz: "overriden bazdefault"
						foobar: [420]
					>
				>
				x Foo default Foo.<
					foo: "foo"
					bar: Bar.<
						baz: "baz"
						foobar: [1 2 3]
					>
				>
				""")
				.getCurrentScope()
				.getVariable("x")
				.orElseThrow()
				.defaultValue()
				.value();
			assertThat(def)
				.isEqualTo(Structs.of(
					"Foo",
					"foo", VarD.of("foo", "foodefault"),
					"bar", VarD.of(
						Structs.of("Bar",
							"baz", VarD.of("baz", "bazdefault"),
							"foobar", VarD.of(List.of(1, 2, 3), List.of(69))),
						Structs.of("Bar",
							"baz", VarD.of("overriden bazdefault", "bazdefault"),
							"foobar", VarD.of(List.of(420), List.of(69))))));
		}

	}

	@Nested
	class TestDefaultReset {

		@Test
		void testDefaultResetNatural() {
			assertThat(visit("""
				x Natural default 69n
				420n -> x
				default -> x
				""")
				.getCurrentScope()
				.getVariable("x")
				.orElseThrow())
				.isEqualTo(VarD.of(new Natural(69), new Natural(69)));
		}

		@Test
		void testDefaultResetInt() {
			assertThat(visit("""
				x Int default 69
				420 -> x
				default -> x
				""")
				.getCurrentScope()
				.getVariable("x")
				.orElseThrow())
				.isEqualTo(VarD.of(69, 69));
		}

		@Test
		void testDefaultResetReal() {
			assertThat(visit("""
				x Real default 6.9
				420 -> x
				default -> x
				""")
				.getCurrentScope()
				.getVariable("x")
				.orElseThrow())
				.isEqualTo(VarD.of(6.9, 6.9));
		}

		@Test
		void testDefaultResetBool() {
			assertThat(visit("""
				x Bool default true
				false -> x
				default -> x
				""")
				.getCurrentScope()
				.getVariable("x")
				.orElseThrow())
				.isEqualTo(VarD.of(true, true));
		}

		@Test
		void testDefaultResetText() {
			assertThat(visit("""
				x Text default "foo"
				"bar" -> x
				default -> x
				""")
				.getCurrentScope()
				.getVariable("x")
				.orElseThrow())
				.isEqualTo(VarD.of("foo", "foo"));
		}

		@Test
		void testDefaultResetList() {
			assertThat(visit("""
				x [Int] default [1 2 3]
				[69 420] -> x
				default -> x
				""")
				.getCurrentScope()
				.getVariable("x")
				.orElseThrow()
				.unwrap())
				.isEqualTo(GioviLists.of(1, 2, 3));
		}

		@Test
		void testDefaultResetMap() {
			assertThat(visit("""
				x [Int:Bool] default [1:true 2:false]
				[69:true 420:true] -> x
				default -> x
				""")
				.getCurrentScope()
				.getVariable("x")
				.orElseThrow())
				.isEqualTo(VarD.of(
					Map.of(
						1, true,
						2, false),
					Map.of(
						1, true,
						2, false)));
		}

		@Test
		void testDefaultResetStruct() {
			assertThat(visit("""
				Bar <
					baz Text
					foobar [Int]
				>
				Foo <
					foo Text
					bar Bar
				>
				x Foo default Foo.<
					foo: "foo"
					bar: Bar.<
						baz: "baz"
						foobar: [1 2 3]
					>
				>
				x <- Foo.<
					foo: "hello"
					bar: Bar.<
						baz: "world"
						foobar: [69 420]
					>
				>
				default -> x
				""")
				.getCurrentScope()
				.getVariable("x")
				.orElseThrow()
				.unwrap()
				.value())
				.isEqualTo(Structs.of(
					"Foo",
					"foo", VarD.of("foo"),
					"bar", VarD.of(
						Structs.of("Bar",
							"baz", VarD.of("baz"),
							"foobar", new VarD<>(
								GioviLists.of(1, 2, 3),
								new GioviList(GioviInt.TYPE))),
						Structs.of("Bar",
							"baz", VarD.of(""),
							"foobar", new VarD<>(
								new GioviList(GioviInt.TYPE),
								new GioviList(GioviInt.TYPE))))));
		}

	}

	@Nested
	class TestDotDefault {

		@Test
		void testDotDefaultNatural() {
			var visitor = visit("""
				x Natural default 69n
				""");
			assertThat(visitExpression(visitor, """
				x.default
				"""))
				.isEqualTo(new GioviNatural(new Natural(69)));
		}

		@Test
		void testDotDefaultInt() {
			var visitor = visit("""
				x Int default 69
				""");
			assertThat(visitExpression(visitor, """
				x.default
				"""))
				.isEqualTo(new GioviInt(69));
		}

		@Test
		void testDotDefaultReal() {
			var visitor = visit("""
				x Real default 6.9
				""");
			assertThat(visitExpression(visitor, """
				x.default
				"""))
				.isEqualTo(new GioviReal(6.9));
		}

		@Test
		void testDotDefaultBool() {
			var visitor = visit("""
				x Bool default true
				""");
			assertThat(visitExpression(visitor, """
				x.default
				"""))
				.isEqualTo(new GioviBool(true));
		}

		@Test
		void testDotDefaultText() {
			var visitor = visit("""
				x Text default 'hello'
				""");
			assertThat(visitExpression(visitor, """
				x.default
				"""))
				.isEqualTo(new GioviText("hello"));
		}

		@Test
		void testDotDefaultList() {
			var visitor = visit("""
				x [Int] default [1 2 3]
				""");
			assertThat(visitExpression(visitor, """
				x.default
				"""))
				.isEqualTo(GioviLists.of(1, 2, 3));
		}

		@Test
		void testDotDefaultMaps() {
			var visitor = visit("""
				x [Int:Bool] default [1:true 2:false]
				""");
			assertThat(visitExpression(visitor, """
				x.default
				"""))
				.isEqualTo(GioviMaps.of(1, true, 2, false));
		}

		@Test
		void testDotDefaultStruct() {
			var visitor = visit("""
				Bar <
					baz Text
					foobar [Int]
				>
				Foo <
					foo Text
					bar Bar
				>
				x Foo default Foo.<
					foo: "foo"
					bar: Bar.<
						baz: "baz"
						foobar: [1 2 3]
					>
				>
				""");
			assertThat(visitExpression(visitor, """
				x.default
				""")
				.asGioviValue()
				.value())
				.isEqualTo(Structs.of(
					"Foo",
					"foo", VarD.of("foo"),
					"bar", VarD.of(
						Structs.of("Bar",
							"baz", VarD.of("baz"),
							"foobar", new VarD<>(
								GioviLists.of(1, 2, 3),
								new GioviList(GioviInt.TYPE))),
						Structs.of("Bar",
							"baz", VarD.of(""),
							"foobar", new VarD<>(
								new GioviList(GioviInt.TYPE),
								new GioviList(GioviInt.TYPE))))));
		}

	}

	@Nested
	class TestTypesDefault {

		@Test
		void testTypeDefaultNatural() {
			assertThat(visitExpression("""
				Natural.default
				"""))
				.isEqualTo(new GioviNatural());
		}

		@Test
		void testTypeDefaultInt() {
			assertThat(visitExpression("""
				Int.default
				"""))
				.isEqualTo(new GioviInt());
		}

		@Test
		void testTypeDefaultReal() {
			assertThat(visitExpression("""
				Real.default
				"""))
				.isEqualTo(new GioviReal());
		}

		@Test
		void testTypeDefaultBool() {
			assertThat(visitExpression("""
				Bool.default
				"""))
				.isEqualTo(new GioviBool());
		}

		@Test
		void testTypeDefaultText() {
			assertThat(visitExpression("""
				Text.default
				"""))
				.isEqualTo(new GioviText());
		}

		// FIXME: [Int] not parsed as list Type, but as a list of Types

		@Test
		@Disabled
		void testTypeDefaultList() {
			assertThat(visitExpression("""
				[Int].default
				"""))
				.isEqualTo(new GioviList(GioviInt.TYPE));
		}

		@Test
		@Disabled
		void testTypeDefaultMaps() {
			assertThat(visitExpression("""
				[Text:Int].default
				"""))
				.isEqualTo(new GioviMap(new GioviTypeMap(
					GioviText.TYPE,
					GioviInt.TYPE)));
		}

		@Test
		void testTypeDefaultStruct() {
			var visitor = visit("""
				Bar <
					baz Text default "baz"
					foobar [Int] default [1 2 3]
				>
				Foo <
					foo Text default "foo"
					bar Bar
					foobar Bar default Bar.<
						baz: "BAZ"
						foobar: [69 420]
					>
				>
				""");
			assertThat(visitExpression(visitor, """
				Foo.default
				""")
				.asGioviValue()
				.value())
				.isEqualTo(Structs.of(
					"Foo",
					"foo", VarD.of("foo", "foo"),
					"bar", VarD.of(Structs.of("Bar",
						"baz", VarD.of("baz", "baz"),
						"foobar", VarD.of(List.of(1, 2, 3), List.of(1, 2, 3)))),
					"foobar", VarD.of(
						Structs.of("Bar",
							"baz", VarD.of("BAZ", "baz"),
							"foobar", VarD.of(List.of(69, 420), List.of(1, 2, 3))),
						Structs.of("Bar",
							"baz", VarD.of("BAZ", "baz"),
							"foobar", VarD.of(List.of(69, 420), List.of(1, 2, 3))))));
		}


	}

}
