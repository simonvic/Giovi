package it.simonvic.giovi;

import it.simonvic.giovi.lang.Block;
import it.simonvic.giovi.exceptions.VariableAlreadyDefinedException;
import it.simonvic.giovi.exceptions.VariableNotDefinedException;
import it.simonvic.giovi.lang.values.GioviInt;
import it.simonvic.giovi.lang.values.GioviValue;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestVariables {

	Block block;

	@BeforeEach
	public void setup() {
		block = new Block(null);
	}

	@Nested
	class TestDeclaration {
		@Test
		public void testDeclaration_string() {
			block.declare("aString", GioviValue.of("hello world"));
			assertTrue(block.isDefinedVariable("aString"));
		}

		@Test
		public void testDeclaration_bool() {
			block.declare("aBoolean", GioviValue.of(true));
			assertTrue(block.isDefinedVariable("aBoolean"));
		}

		@Test
		public void testDeclaration_real() {
			block.declare("aReal", GioviValue.of(0.69));
			assertTrue(block.isDefinedVariable("aReal"));
		}

		@Test
		public void testDeclaration_int() {
			block.declare("anInteger", GioviValue.of(69));
			assertTrue(block.isDefinedVariable("anInteger"));
		}

		@Test
		@Disabled("Null values not implemented yet")
		public void testDeclaration_null() {
			block.declare("aNull", new GioviInt(null));
			assertTrue(block.isDefinedVariable("aNull"));
		}

		@Test
		public void testDeclarationVariableType_string() {
			block.declare("aString", GioviValue.of("hello world"));
			assertEquals(String.class, block.getVariableValue("aString").get().getClass());
		}

		@Test
		public void testDeclarationVariableType_bool() {
			block.declare("aBoolean", GioviValue.of(true));
			assertEquals(Boolean.class, block.getVariableValue("aBoolean").get().getClass());
		}

		@Test
		public void testDeclarationVariableType_real() {
			block.declare("aReal", GioviValue.of(0.69));
			assertEquals(Double.class, block.getVariableValue("aReal").get().getClass());
		}

		@Test
		public void testDeclarationVariableType_int() {
			block.declare("anInteger", GioviValue.of(69));
			assertEquals(Integer.class, block.getVariableValue("anInteger").get().getClass());
		}

		@Test
		public void declare_shouldThrow_VariableAlreadyDefined() {
			block.declare("someVar", GioviValue.of(69));
			assertThrows(VariableAlreadyDefinedException.class, () -> block.declare("someVar", GioviValue.of(69)));
		}

		@Test
		public void getVariable_shouldThrow_VariableNotDefined() {
			assertThrows(VariableNotDefinedException.class, () -> block.getVariableValue("someUndefinedVariable"));
		}

		@Test
		public void update_shouldThrow_VariableNotDefined() {
			assertThrows(VariableNotDefinedException.class, () -> block.setVariableValue("someUndefinedVariable", GioviValue.of(69)));
		}

		@Test
		public void declare_shouldDeclareVariable() {
			block.declare("aString", GioviValue.of("hello world"));
			assertTrue(block.isDefinedVariable("aString"));
		}
	}


	@Test
	public void update_shouldUpdateVariableValue() {
		GioviInt value = new GioviInt();
		block.declare("anInt", value);
		block.setVariableValue("anInt", GioviValue.of(69));
		assertEquals(69, value.get());
	}

	@Test
	public void testGetValue() {
		GioviInt value = new GioviInt(69);
		block.declare("anInt", value);
		assertEquals(69, block.getVariableValue("anInt").get());
	}
}
