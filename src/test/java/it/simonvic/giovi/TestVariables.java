package it.simonvic.giovi;

import it.simonvic.giovi.lang.Block;
import it.simonvic.giovi.exceptions.VariableAlreadyDefinedException;
import it.simonvic.giovi.exceptions.VariableNotDefinedException;
import it.simonvic.giovi.lang.values.IntValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.*;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestVariables {

	Block block;

	@BeforeEach
	public void setup() {
		block = new Block(null);
	}

	@Test
	public void testDeclaration_string() {
		block.declare("aString", "hello world");
		assertTrue(block.isDefinedVariable("aString"));
	}

	@Test
	public void testDeclaration_bool() {
		block.declare("aBoolean", true);
		assertTrue(block.isDefinedVariable("aBoolean"));
	}

	@Test
	public void testDeclaration_real() {
		block.declare("aReal", 0.69);
		assertTrue(block.isDefinedVariable("aReal"));
	}

	@Test
	public void testDeclaration_int() {
		block.declare("anInteger", 69);
		assertTrue(block.isDefinedVariable("anInteger"));
	}

	@Test
	public void testDeclaration_null() {
//		block.declare("aNull", new IntValue(null));
//		assertTrue(block.isDefinedVariable("aNull"));
	}

	@Test
	public void testDeclarationVariableType_string() {
		block.declare("aString", "hello world");
		assertEquals(String.class, block.getVariableValue("aString").get().getClass());
	}

	@Test
	public void testDeclarationVariableType_bool() {
		block.declare("aBoolean", true);
		assertEquals(Boolean.class, block.getVariableValue("aBoolean").get().getClass());
	}

	@Test
	public void testDeclarationVariableType_real() {
		block.declare("aReal", 0.69);
		assertEquals(Double.class, block.getVariableValue("aReal").get().getClass());
	}

	@Test
	public void testDeclarationVariableType_int() {
		block.declare("anInteger", 69);
		assertEquals(Integer.class, block.getVariableValue("anInteger").get().getClass());
	}

	@Test
	public void declare_shouldThrow_VariableAlreadyDefined() {
		block.declare("someVar", 69);
		assertThrows(VariableAlreadyDefinedException.class, () -> block.declare("someVar", 69));
	}

	@Test
	public void getVariable_shouldThrow_VariableNotDefined() {
		assertThrows(VariableNotDefinedException.class, () -> block.getVariableValue("someUndefinedVariable"));
	}

	@Test
	public void update_shouldThrow_VariableNotDefined() {
		assertThrows(VariableNotDefinedException.class, () -> block.setVariableValue("someUndefinedVariable", 69));
	}

	@Test
	public void declare_shouldDeclareVariable() {
		block.declare("aString", "hello world");
		assertTrue(block.isDefinedVariable("aString"));
	}


	@Test
	public void update_shouldUpdateVariableValue() {
		IntValue value = new IntValue();
		block.declare("anInt", value);
		block.setVariableValue("anInt", 69);
		assertEquals(69, value.get());
	}

	@Test
	public void testGetValue() {
		IntValue value = new IntValue(69);
		block.declare("anInt", value);
		assertEquals(69, block.getVariableValue("anInt").get());
	}
}
