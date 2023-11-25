package it.simonvic.giovi;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.simonvic.giovi.exceptions.VariableAlreadyDeclaredException;
import it.simonvic.giovi.lang.Scope;
import it.simonvic.giovi.lang.values.GioviValue;

public class TestBlocks {

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
		global.declare("aString", GioviValue.of("hello world"));
		assertTrue(global.isDeclaredVariable("aString"));
	}

	@Test
	void test_getVariableValue() {
		global.declare("aString", GioviValue.of("hello world"));
		assertEquals("hello world", global.getVariableValue("aString").unbox());
	}

	@Test
	void test_isVariableDeclared() {
		assertFalse(global.isDeclaredVariable("aString"));
	}

	@Test
	void nestedBlocksShouldSeeParentsVariables() {
		global.declare("aString", GioviValue.of("Hello world"));
		assertTrue(scope1.isDeclaredVariable("aString"));
	}

	@Test
	void nestedBlocksShouldSeeParentsVariables2() {
		global.declare("aString", GioviValue.of("Hello world"));
		assertTrue(scope111.isDeclaredVariable("aString"));
	}

	@Test
	void nonNestedBlocksShouldSeeParentsVariables() {
		scope1.declare("aString", GioviValue.of("Hello world"));
		assertFalse(scope2.isDeclaredVariable("aString"));
	}

	@Test
	void sameBlockShouldThrow_VariableAlreadyDeclared() {
		scope1.declare("aString", GioviValue.of("Hello world"));
		assertThrows(VariableAlreadyDeclaredException.class,
			() -> scope1.declare("aString", GioviValue.of("hello_world")));
	}

	@Test
	void nestedBlockShouldNotThrow_VariableAlreadyDeclared() {
		scope11.declare("aString", GioviValue.of("Hello world"));
		assertDoesNotThrow(() -> scope111.declare("aString", GioviValue.of("hello_world")));
	}

	@Test
	void nestedBlockShouldAccessVariablesValue() {
		global.declare("aString", GioviValue.of("Hello world"));
		assertEquals("Hello world", scope11.getVariableValue("aString").unbox());
	}

	@Test
	void nestedBlockShouldBeAbleToChangeVariablesValue() {
		global.declare("aString", GioviValue.of("Hello world"));
		scope2.setVariableValue("aString", GioviValue.of("ciao mondo"));
		assertEquals("ciao mondo", global.getVariableValue("aString").unbox());
	}

}
