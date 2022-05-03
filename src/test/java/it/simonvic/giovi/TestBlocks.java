package it.simonvic.giovi;

import it.simonvic.giovi.exceptions.VariableAlreadyDefinedException;
import it.simonvic.giovi.lang.Block;
import it.simonvic.giovi.lang.values.Value;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestBlocks {

	Block global;
	Block block1;
	Block block2;
	Block block11;
	Block block111;

	@BeforeEach
	public void setup() {
		global = new Block(null);
		block1 = new Block(global);
		block2 = new Block(global);
		block11 = new Block(block1);
		block111 = new Block(block11);
	}

	@Test
	public void test_declareVariable() {
		global.declare("aString", Value.of("hello world"));
		assertTrue(global.isDefinedVariable("aString"));
	}

	@Test
	public void test_getVariableValue() {
		global.declare("aString", Value.of("hello world"));
		assertEquals("hello world", global.getVariableValue("aString").get());
	}

	@Test
	public void test_isVariableDeclared() {
		assertFalse(global.isDefinedVariable("aString"));
	}

	@Test
	public void nestedBlocksShouldSeeParentsVariables() {
		global.declare("aString", Value.of("Hello world"));
		assertTrue(block1.isDefinedVariable("aString"));
	}

	@Test
	public void nestedBlocksShouldSeeParentsVariables2() {
		global.declare("aString", Value.of("Hello world"));
		assertTrue(block111.isDefinedVariable("aString"));
	}

	@Test
	public void nonNestedBlocksShouldSeeParentsVariables() {
		block1.declare("aString", Value.of("Hello world"));
		assertFalse(block2.isDefinedVariable("aString"));
	}

	@Test
	public void nestedBlockShouldThrow_VariableAlreadyDefined() {
		block11.declare("aString", Value.of("Hello world"));
		assertThrows(VariableAlreadyDefinedException.class, () -> block111.declare("aString", Value.of("hello_world")));
	}

	@Test
	public void nestedBlockShouldAccessVariablesValue() {
		global.declare("aString", Value.of("Hello world"));
		assertEquals("Hello world", block11.getVariableValue("aString").get());
	}

	@Test
	public void nestedBlockShouldBeAbleToChangeVariablesValue() {
		global.declare("aString", Value.of("Hello world"));
		block2.setVariableValue("aString", Value.of("ciao mondo"));
		assertEquals("ciao mondo", global.getVariableValue("aString").get());
	}


}
