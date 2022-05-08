package it.simonvic.giovi;

import it.simonvic.giovi.exceptions.VariableAlreadyDefinedException;
import it.simonvic.giovi.lang.Scope;
import it.simonvic.giovi.lang.values.GioviValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestBlocks {

	Scope global;
	Scope scope1;
	Scope scope2;
	Scope scope11;
	Scope scope111;

	@BeforeEach
	public void setup() {
		global = new Scope();
		scope1 = new Scope(global);
		scope2 = new Scope(global);
		scope11 = new Scope(scope1);
		scope111 = new Scope(scope11);
	}

	@Test
	public void test_declareVariable() {
		global.declare("aString", GioviValue.of("hello world"));
		assertTrue(global.isDefinedVariable("aString"));
	}

	@Test
	public void test_getVariableValue() {
		global.declare("aString", GioviValue.of("hello world"));
		assertEquals("hello world", global.getVariableValue("aString").unbox());
	}

	@Test
	public void test_isVariableDeclared() {
		assertFalse(global.isDefinedVariable("aString"));
	}

	@Test
	public void nestedBlocksShouldSeeParentsVariables() {
		global.declare("aString", GioviValue.of("Hello world"));
		assertTrue(scope1.isDefinedVariable("aString"));
	}

	@Test
	public void nestedBlocksShouldSeeParentsVariables2() {
		global.declare("aString", GioviValue.of("Hello world"));
		assertTrue(scope111.isDefinedVariable("aString"));
	}

	@Test
	public void nonNestedBlocksShouldSeeParentsVariables() {
		scope1.declare("aString", GioviValue.of("Hello world"));
		assertFalse(scope2.isDefinedVariable("aString"));
	}

	@Test
	public void nestedBlockShouldThrow_VariableAlreadyDefined() {
		scope11.declare("aString", GioviValue.of("Hello world"));
		assertThrows(VariableAlreadyDefinedException.class, () -> scope111.declare("aString", GioviValue.of("hello_world")));
	}

	@Test
	public void nestedBlockShouldAccessVariablesValue() {
		global.declare("aString", GioviValue.of("Hello world"));
		assertEquals("Hello world", scope11.getVariableValue("aString").unbox());
	}

	@Test
	public void nestedBlockShouldBeAbleToChangeVariablesValue() {
		global.declare("aString", GioviValue.of("Hello world"));
		scope2.setVariableValue("aString", GioviValue.of("ciao mondo"));
		assertEquals("ciao mondo", global.getVariableValue("aString").unbox());
	}


}
