package it.simonvic.giovi;

import it.simonvic.giovi.exceptions.VariableAlreadyDeclaredException;
import it.simonvic.giovi.exceptions.VariableNotDeclaredException;
import it.simonvic.giovi.lang.Scope;
import it.simonvic.giovi.lang.values.GioviInt;
import it.simonvic.giovi.lang.values.GioviValue;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;


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
			scope.declare("aString", GioviValue.of("hello world"));
			assertTrue(scope.isDeclaredVariable("aString"));
		}

		@Test
		void testDeclaration_bool() {
			scope.declare("aBoolean", GioviValue.of(true));
			assertTrue(scope.isDeclaredVariable("aBoolean"));
		}

		@Test
		void testDeclaration_real() {
			scope.declare("aReal", GioviValue.of(0.69));
			assertTrue(scope.isDeclaredVariable("aReal"));
		}

		@Test
		void testDeclaration_int() {
			scope.declare("anInteger", GioviValue.of(69));
			assertTrue(scope.isDeclaredVariable("anInteger"));
		}

		@Test
		@Disabled("Null values not implemented yet")
		void testDeclaration_null() {
			scope.declare("aNull", new GioviInt(null));
			assertTrue(scope.isDeclaredVariable("aNull"));
		}

		@Test
		void testDeclarationVariableType_string() {
			scope.declare("aString", GioviValue.of("hello world"));
			assertEquals(String.class, scope.getVariableValue("aString").unbox().getClass());
		}

		@Test
		void testDeclarationVariableType_bool() {
			scope.declare("aBoolean", GioviValue.of(true));
			assertEquals(Boolean.class, scope.getVariableValue("aBoolean").unbox().getClass());
		}

		@Test
		void testDeclarationVariableType_real() {
			scope.declare("aReal", GioviValue.of(0.69));
			assertEquals(Double.class, scope.getVariableValue("aReal").unbox().getClass());
		}

		@Test
		void testDeclarationVariableType_int() {
			scope.declare("anInteger", GioviValue.of(69));
			assertEquals(Integer.class, scope.getVariableValue("anInteger").unbox().getClass());
		}

		@Test
		void declare_shouldThrow_VariableAlreadyDefined() {
			scope.declare("someVar", GioviValue.of(69));
			assertThrows(VariableAlreadyDeclaredException.class, () -> scope.declare("someVar", GioviValue.of(69)));
		}

		@Test
		void getVariable_shouldThrow_VariableNotDefined() {
			assertThrows(VariableNotDeclaredException.class, () -> scope.getVariableValue("someUndefinedVariable"));
		}

		@Test
		void update_shouldThrow_VariableNotDefined() {
			assertThrows(VariableNotDeclaredException.class, () -> scope.setVariableValue("someUndefinedVariable", GioviValue.of(69)));
		}

		@Test
		void declare_shouldDeclareVariable() {
			scope.declare("aString", GioviValue.of("hello world"));
			assertTrue(scope.isDeclaredVariable("aString"));
		}
	}


	@Test
	void update_shouldUpdateVariableValue() {
		GioviInt value = new GioviInt();
		scope.declare("anInt", value);
		scope.setVariableValue("anInt", GioviValue.of(69));
		assertEquals(69, value.unbox());
	}

	@Test
	void testValueUnbox() {
		GioviInt value = new GioviInt(69);
		scope.declare("anInt", value);
		assertEquals(69, scope.getVariableValue("anInt").unbox());
	}
}
