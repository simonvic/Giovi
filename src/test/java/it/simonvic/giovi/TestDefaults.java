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
import it.simonvic.giovi.util.GioviTypes;

public class TestDefaults {

	// TODO(test): structs

	@Nested
	class TestDefaultOverride {

		@Test
		void testDefaultOverrideNatural() {
			assertThat(visit("""
				x Natural default 69n
				""")
				.getCurrentScope()
				.getVariableValue("x")
				.unboxDefault())
					.isEqualTo(new Natural(69));
		}

		@Test
		void testDefaultOverrideInt() {
			assertThat(visit("""
				x Int default 69
				""")
				.getCurrentScope()
				.getVariableValue("x")
				.unboxDefault())
					.isEqualTo(69);
		}

		@Test
		void testDefaultOverrideReal() {
			assertThat(visit("""
				x Real default 6.9
				""")
				.getCurrentScope()
				.getVariableValue("x")
				.unboxDefault())
					.isEqualTo(6.9);
		}

		@Test
		void testDefaultOverrideBool() {
			assertThat(visit("""
				x Bool default true
				""")
				.getCurrentScope()
				.getVariableValue("x")
				.unboxDefault())
					.isEqualTo(true);
		}

		@Test
		void testDefaultOverrideText() {
			assertThat(visit("""
				x Text default "foo"
				""")
				.getCurrentScope()
				.getVariableValue("x")
				.unboxDefault())
					.isEqualTo("foo");
		}

		@Test
		void testDefaultOverrideList() {
			assertThat(visit("""
				x [Int] default [1 2 3]
				""")
				.getCurrentScope()
				.getVariableValue("x")
				.unboxDefault())
					.isEqualTo(List.of(new GioviInt(1), new GioviInt(2), new GioviInt(3)));
		}

		@Test
		void testDefaultOverrideMap() {
			assertThat(visit("""
				x [Int:Bool] default [1:true 2:false]
				""")
				.getCurrentScope()
				.getVariableValue("x")
				.unboxDefault())
					.isEqualTo(Map.of(
						new GioviInt(1), new GioviBool(true),
						new GioviInt(2), new GioviBool(false)));
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
				.getVariableValue("x"))
					.isEqualTo(new GioviNatural(new Natural(69)));
		}

		@Test
		void testDefaultResetInt() {
			assertThat(visit("""
				x Int default 69
				420 -> x
				default -> x
				""")
				.getCurrentScope()
				.getVariableValue("x"))
					.isEqualTo(new GioviInt(69));
		}

		@Test
		void testDefaultResetReal() {
			assertThat(visit("""
				x Real default 6.9
				420 -> x
				default -> x
				""")
				.getCurrentScope()
				.getVariableValue("x"))
					.isEqualTo(new GioviReal(6.9));
		}

		@Test
		void testDefaultResetBool() {
			assertThat(visit("""
				x Bool default true
				false -> x
				default -> x
				""")
				.getCurrentScope()
				.getVariableValue("x"))
					.isEqualTo(new GioviBool(true));
		}

		@Test
		void testDefaultResetText() {
			assertThat(visit("""
				x Text default "foo"
				"bar" -> x
				default -> x
				""")
				.getCurrentScope()
				.getVariableValue("x"))
					.isEqualTo(new GioviText("foo"));
		}

		@Test
		void testDefaultResetList() {
			assertThat(visit("""
				x [Int] default [1 2 3]
				[69 420] -> x
				default -> x
				""")
				.getCurrentScope()
				.getVariableValue("x"))
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
				.getVariableValue("x"))
					.isEqualTo(GioviMaps.of(
						1, true,
						2, false));
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
				.isEqualTo(new GioviList(GioviTypes.of(GioviInt.class)));
		}

		@Test
		@Disabled
		void testTypeDefaultMaps() {
			assertThat(visitExpression("""
				[Text:Int].default
				"""))
				.isEqualTo(new GioviMap(
					GioviTypes.of(GioviText.class),
					GioviTypes.of(GioviInt.class)));
		}

	}

}
