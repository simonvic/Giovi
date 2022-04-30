package it.simonvic.giovi;

import it.simonvic.giovi.Variable;
import it.simonvic.giovi.exceptions.VariableAlreadyDefinedException;
import it.simonvic.giovi.exceptions.VariableNotDefinedException;
import it.simonvic.giovi.lang.Variables;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.*;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestVariables {

	Variables vars;

	@BeforeEach
	public void setup() {
		vars = new Variables();
	}

	@Test
	public void testDeclaration_string() {
		vars.declare(new Variable<>("aString", "hello world"));
		assertTrue(vars.isDefined("aString"));
	}

	@Test
	public void testDeclaration_bool() {
		vars.declare(new Variable<>("aBoolean", true));
		assertTrue(vars.isDefined("aBoolean"));
	}

	@Test
	public void testDeclaration_real() {
		vars.declare(new Variable<>("aReal", 0.69));
		assertTrue(vars.isDefined("aReal"));
	}

	@Test
	public void testDeclaration_int() {
		vars.declare(new Variable<>("anInteger", 69));
		assertTrue(vars.isDefined("anInteger"));
	}

	@Test
	public void testDeclaration_null() {
		vars.declare(new Variable<>("aNull", null));
		assertTrue(vars.isDefined("aNull"));
	}

	@Test
	public void testDeclarationVariableType_string() {
		vars.declare(new Variable<>("aString", "hello world"));
		assertEquals(String.class, vars.getVariable("aString").getValue().getClass());
	}

	@Test
	public void testDeclarationVariableType_bool() {
		vars.declare(new Variable<>("aBoolean", true));
		assertEquals(Boolean.class, vars.getVariable("aBoolean").getValue().getClass());
	}

	@Test
	public void testDeclarationVariableType_real() {
		vars.declare(new Variable<>("aReal", 0.69));
		assertEquals(Double.class, vars.getVariable("aReal").getValue().getClass());
	}

	@Test
	public void testDeclarationVariableType_int() {
		vars.declare(new Variable<>("anInteger", 69));
		assertEquals(Integer.class, vars.getVariable("anInteger").getValue().getClass());
	}

	@Test
	public void declare_shouldThrow_VariableAlreadyDefined() {
		vars.declare(new Variable<>("someVar", 69));
		assertThrows(VariableAlreadyDefinedException.class, () -> vars.declare(new Variable<>("someVar", 69)));
	}

	@Test
	public void getVariable_shouldThrow_VariableNotDefined() {
		assertThrows(VariableNotDefinedException.class, () -> vars.getVariable("someUndefinedVariable"));
	}

	@Test
	public void update_shouldThrow_VariableNotDefined() {
		assertThrows(VariableNotDefinedException.class, () -> vars.update(new Variable<>("someUndefinedVariable", 69)));
	}

	@Test
	public void declare_shouldDeclareVariable() {
		vars.declare(new Variable<>("aString", "hello world"));
		assertTrue(vars.isDefined("aString"));
	}


	@Test
	public void update_shouldUpdateVariableValue() {
		Variable<Integer> variable = new Variable<>("anInt");
		vars.declare(variable);
		variable.setValue(69);
		vars.update(variable);
		assertEquals(69, variable.getValue());
	}

	@Test
	public void testGetValue() {
		Variable<Integer> variable = new Variable<>("anInt", 69);
		vars.declare(variable);
		assertEquals(69, vars.getValue("anInt"));
	}
}
