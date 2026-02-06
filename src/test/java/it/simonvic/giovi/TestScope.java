package it.simonvic.giovi;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;

import it.simonvic.giovi.exceptions.GioviException;
import it.simonvic.giovi.exceptions.VariableAlreadyDeclared;
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
			scope.declare("aString", VarD.of("hello world"));
			assertTrue(scope.isDeclaredVariable("aString"));
		}

		@Test
		void testDeclaration_bool() {
			scope.declare("aBoolean", VarD.of(true));
			assertTrue(scope.isDeclaredVariable("aBoolean"));
		}

		@Test
		void testDeclaration_real() {
			scope.declare("aReal", VarD.of(0.69));
			assertTrue(scope.isDeclaredVariable("aReal"));
		}

		@Test
		void testDeclaration_int() {
			scope.declare("anInteger", VarD.of(69));
			assertTrue(scope.isDeclaredVariable("anInteger"));
		}

		@Test
		void testDeclarationVariableType_string() {
			scope.declare("aString", VarD.of("hello world"));
			assertEquals(String.class,
				scope.getVariable("aString").orElseThrow().unwrap().value().getClass());
		}

		@Test
		void testDeclarationVariableType_bool() {
			scope.declare("aBoolean", VarD.of(true));
			assertEquals(Boolean.class,
				scope.getVariable("aBoolean").orElseThrow().unwrap().value().getClass());
		}

		@Test
		void testDeclarationVariableType_real() {
			scope.declare("aReal", VarD.of(0.69));
			assertEquals(Double.class, scope.getVariable("aReal").orElseThrow().unwrap().value().getClass());
		}

		@Test
		void testDeclarationVariableType_int() {
			scope.declare("anInteger", VarD.of(69));
			assertEquals(Integer.class,
				scope.getVariable("anInteger").orElseThrow().unwrap().value().getClass());
		}

		@Test
		void declare_shouldThrow_VariableAlreadyDefined() {
			scope.declare("someVar", VarD.of(69));
			assertThrows(VariableAlreadyDeclared.class,
				() -> scope.declare("someVar", VarD.of(69)));
		}

		@Test
		void getUndeclaredVariable_shouldReturnEmpty() {
			assertThat(scope.getVariable("someUndefinedVariable")).isEmpty();
		}

		@Test
		void declare_shouldDeclareVariable() {
			scope.declare("aString", VarD.of("hello world"));
			assertTrue(scope.isDeclaredVariable("aString"));
		}
	}

	@Test
	void testValueUnbox() {
		scope.declare("anInt", VarD.of(69));
		assertEquals(69, scope.getVariable("anInt").orElseThrow().unwrap().value());
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
			global.declare("aString", VarD.of("hello world"));
			assertTrue(global.isDeclaredVariable("aString"));
		}

		@Test
		void test_getVariableValue() {
			global.declare("aString", VarD.of("hello world"));
			assertEquals("hello world", global.getVariable("aString").orElseThrow().unwrap().value());
		}

		@Test
		void test_isVariableDeclared() {
			assertFalse(global.isDeclaredVariable("aString"));
		}

		@Test
		void nestedBlocksShouldSeeParentsVariables() {
			global.declare("aString", VarD.of("Hello world"));
			assertTrue(scope1.isDeclaredVariable("aString"));
		}

		@Test
		void nestedBlocksShouldSeeParentsVariables2() {
			global.declare("aString", VarD.of("Hello world"));
			assertTrue(scope111.isDeclaredVariable("aString"));
		}

		@Test
		void nonNestedBlocksShouldSeeParentsVariables() {
			scope1.declare("aString", VarD.of("Hello world"));
			assertFalse(scope2.isDeclaredVariable("aString"));
		}

		@Test
		void sameBlockShouldThrow_VariableAlreadyDeclared() {
			scope1.declare("aString", VarD.of("Hello world"));
			assertThrows(VariableAlreadyDeclared.class,
				() -> scope1.declare("aString", VarD.of("hello_world")));
		}

		@Test
		void nestedBlockShouldNotThrow_VariableAlreadyDeclared() {
			scope11.declare("aString", VarD.of("Hello world"));
			assertThatNoException()
				.isThrownBy(() -> scope111.declare("aString", VarD.of("hello_world")));
		}

		@Test
		void nestedBlockShouldAccessVariablesValue() {
			global.declare("aString", VarD.of("Hello world"));
			assertEquals("Hello world", scope11.getVariable("aString").orElseThrow().unwrap().value());
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
