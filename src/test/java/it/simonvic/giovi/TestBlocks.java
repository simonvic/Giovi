package it.simonvic.giovi;

import it.simonvic.giovi.exceptions.VariableAlreadyDefinedException;
import it.simonvic.giovi.lang.Block;
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
		global.declareVariable("aString", "hello world");
		assertTrue(global.isVariableDefined("aString"));
	}

	@Test
	public void test_getVariableValue() {
		global.declareVariable("aString", "hello world");
		assertEquals("hello world", global.getVariableValue("aString"));
	}

	@Test
	public void test_isVariableDeclared() {
		assertFalse(global.isVariableDefined("aString"));
	}

	@Test
	public void nestedBlocksShouldSeeParentsVariables() {
		global.declareVariable("aString", "Hello world");
		assertTrue(block1.isVariableDefined("aString"));
	}

	@Test
	public void nestedBlocksShouldSeeParentsVariables2() {
		global.declareVariable("aString", "Hello world");
		assertTrue(block111.isVariableDefined("aString"));
	}

	@Test
	public void nonNestedBlocksShouldSeeParentsVariables() {
		block1.declareVariable("aString", "Hello world");
		assertFalse(block2.isVariableDefined("aString"));
	}

	@Test
	public void nestedBlockShouldThrow_VariableAlreadyDefined() {
		block11.declareVariable("aString", "Hello world");
		assertThrows(VariableAlreadyDefinedException.class, () -> block111.declareVariable("aString", "hello_world"));
	}

	@Test
	public void nestedBlockShouldAccessVariablesValue() {
		global.declareVariable("aString", "Hello world");
		assertEquals("Hello world", block11.getVariableValue("aString"));
	}

	@Test
	public void nestedBlockShouldBeAbleToChangeVariablesValue() {
		global.declareVariable("aString", "Hello world");
		block2.updateVariableValue("aString", "ciao mondo");
		assertEquals("ciao mondo", global.getVariableValue("aString"));
	}


}
