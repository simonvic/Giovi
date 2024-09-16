package it.simonvic.giovi;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;

import giovi.lang.*;
import it.simonvic.giovi.exceptions.GioviException;
import it.simonvic.giovi.exceptions.VariableAlreadyDeclared;
import it.simonvic.giovi.exceptions.VariableNotDeclared;
import it.simonvic.giovi.lang.LoopScope;
import it.simonvic.giovi.lang.Scope;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestScope {

	Scope scope;

	@BeforeEach
	void setup() {
		scope = new Scope(null);
	}

	@Nested
	class TestDeclaration {
		@Test
		void testDeclaration_string() {
			scope.declare("aString", new GioviText("hello world"));
			assertTrue(scope.isDeclaredVariable("aString"));
		}

		@Test
		void testDeclaration_bool() {
			scope.declare("aBoolean", new GioviBool(true));
			assertTrue(scope.isDeclaredVariable("aBoolean"));
		}

		@Test
		void testDeclaration_real() {
			scope.declare("aReal", new GioviReal(0.69));
			assertTrue(scope.isDeclaredVariable("aReal"));
		}

		@Test
		void testDeclaration_int() {
			scope.declare("anInteger", new GioviInt(69));
			assertTrue(scope.isDeclaredVariable("anInteger"));
		}

		@Test
		void testDeclarationVariableType_string() {
			scope.declare("aString", new GioviText("hello world"));
			assertEquals(String.class, scope.getVariableValue("aString").value().getClass());
		}

		@Test
		void testDeclarationVariableType_bool() {
			scope.declare("aBoolean", new GioviBool(true));
			assertEquals(Boolean.class, scope.getVariableValue("aBoolean").value().getClass());
		}

		@Test
		void testDeclarationVariableType_real() {
			scope.declare("aReal", new GioviReal(0.69));
			assertEquals(Double.class, scope.getVariableValue("aReal").value().getClass());
		}

		@Test
		void testDeclarationVariableType_int() {
			scope.declare("anInteger", new GioviInt(69));
			assertEquals(Integer.class, scope.getVariableValue("anInteger").value().getClass());
		}

		@Test
		void declare_shouldThrow_VariableAlreadyDefined() {
			scope.declare("someVar", new GioviInt(69));
			assertThrows(VariableAlreadyDeclared.class,
				() -> scope.declare("someVar", new GioviInt(69)));
		}

		@Test
		void getVariable_shouldThrow_VariableNotDefined() {
			assertThrows(VariableNotDeclared.class,
				() -> scope.getVariableValue("someUndefinedVariable"));
		}

		@Test
		void update_shouldThrow_VariableNotDefined() {
			assertThrows(VariableNotDeclared.class,
				() -> scope.setVariableValue("someUndefinedVariable", new GioviInt(69)));
		}

		@Test
		void declare_shouldDeclareVariable() {
			scope.declare("aString", new GioviText("hello world"));
			assertTrue(scope.isDeclaredVariable("aString"));
		}
	}


	@Test
	void update_shouldUpdateVariableValue() {
		GioviInt value = new GioviInt();
		scope.declare("anInt", value);
		scope.setVariableValue("anInt", new GioviInt(69));
		assertEquals(69, value.value());
	}

	@Test
	void testValueUnbox() {
		GioviInt value = new GioviInt(69);
		scope.declare("anInt", value);
		assertEquals(69, scope.getVariableValue("anInt").value());
	}

	@Nested
	class TestVisibility {
		Scope global;
		Scope scope1;
		Scope scope2;
		Scope scope11;
		Scope scope111;

		@BeforeEach
		void setup() {
			global = new Scope();
			scope1 = new Scope(global);
			scope2 = new Scope(global);
			scope11 = new Scope(scope1);
			scope111 = new Scope(scope11);
		}

		@Test
		void test_declareVariable() {
			global.declare("aString", new GioviText("hello world"));
			assertTrue(global.isDeclaredVariable("aString"));
		}

		@Test
		void test_getVariableValue() {
			global.declare("aString", new GioviText("hello world"));
			assertEquals("hello world", global.getVariableValue("aString").value());
		}

		@Test
		void test_isVariableDeclared() {
			assertFalse(global.isDeclaredVariable("aString"));
		}

		@Test
		void nestedBlocksShouldSeeParentsVariables() {
			global.declare("aString", new GioviText("Hello world"));
			assertTrue(scope1.isDeclaredVariable("aString"));
		}

		@Test
		void nestedBlocksShouldSeeParentsVariables2() {
			global.declare("aString", new GioviText("Hello world"));
			assertTrue(scope111.isDeclaredVariable("aString"));
		}

		@Test
		void nonNestedBlocksShouldSeeParentsVariables() {
			scope1.declare("aString", new GioviText("Hello world"));
			assertFalse(scope2.isDeclaredVariable("aString"));
		}

		@Test
		void sameBlockShouldThrow_VariableAlreadyDeclared() {
			scope1.declare("aString", new GioviText("Hello world"));
			assertThrows(VariableAlreadyDeclared.class,
				() -> scope1.declare("aString", new GioviText("hello_world")));
		}

		@Test
		void nestedBlockShouldNotThrow_VariableAlreadyDeclared() {
			scope11.declare("aString", new GioviText("Hello world"));
			assertThatNoException()
				.isThrownBy(() -> scope111.declare("aString", new GioviText("hello_world")));
		}

		@Test
		void nestedBlockShouldAccessVariablesValue() {
			global.declare("aString", new GioviText("Hello world"));
			assertEquals("Hello world", scope11.getVariableValue("aString").value());
		}

		@Test
		void nestedBlockShouldBeAbleToChangeVariablesValue() {
			global.declare("aString", new GioviText("Hello world"));
			scope2.setVariableValue("aString", new GioviText("ciao mondo"));
			assertEquals("ciao mondo", global.getVariableValue("aString").value());
		}
	}

	@Nested
	class Loop {

		@Test
		void skipping_inNonLoopScope_shouldThrow() {
			assertThatExceptionOfType(GioviException.class)
				.isThrownBy(() -> {
					var global = new Scope(null);
					var scope = new Scope(global);
					scope.doSkip();
				});
		}

		@Test
		void skipping_inLoopScope_shouldNotThrow() {
			assertThatNoException().isThrownBy(() -> {
				var global = new Scope(null);
				var scope = new Scope(global);
				var scope1 = new LoopScope(scope);
				scope1.doSkip();
			});
		}

		@Test
		void skipping_inScopeNestedInLoopScope_shouldNotThrow() {
			assertThatNoException().isThrownBy(() -> {
				var global = new Scope(null);
				var scope = new Scope(global);
				var scope1 = new LoopScope(scope);
				var scope11 = new LoopScope(scope1);
				scope11.doSkip();
			});
		}

		@Test
		void breaking_inNonLoopScope_shouldThrow() {
			assertThatExceptionOfType(GioviException.class)
				.isThrownBy(() -> {
					var global = new Scope(null);
					var scope = new Scope(global);
					scope.doBreak();
				});
		}

		@Test
		void breaking_inLoopScope_shouldNotThrow() {
			assertThatNoException().isThrownBy(() -> {
				var global = new Scope(null);
				var scope = new Scope(global);
				var scope1 = new LoopScope(scope);
				scope1.doBreak();
			});
		}

		@Test
		void breaking_inScopeNestedInLoopScope_shouldNotThrow() {
			assertThatNoException().isThrownBy(() -> {
				var global = new Scope(null);
				var scope = new Scope(global);
				var scope1 = new LoopScope(scope);
				var scope11 = new LoopScope(scope1);
				scope11.doBreak();
			});
		}

	}

}
