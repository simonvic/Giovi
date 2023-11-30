package it.simonvic.giovi;

import static it.simonvic.giovi.Giovi.runExpression;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.simonvic.giovi.lang.Scope;
import it.simonvic.giovi.lang.primitives.Struct;
import it.simonvic.giovi.lang.values.*;

public class TestTypeCheckExpression {

	GioviVisitor visitor;
	Scope scope;

	@BeforeEach
	void setup() {
		visitor = new GioviVisitor();
		scope = visitor.getCurrentScope();
	}

	@Test
	void testNatural() {
		scope.declare("x", new GioviNatural());
		assertTrue(runExpression(visitor, "x is Natural").asGioviBool().unbox());
	}

	@Test
	void testInt() {
		scope.declare("x", new GioviInt());
		assertTrue(runExpression(visitor, "x is Int").asGioviBool().unbox());
	}

	@Test
	void testReal() {
		scope.declare("x", new GioviInt());
		assertTrue(runExpression(visitor, "x is Int").asGioviBool().unbox());
	}

	@Test
	void testBool() {
		scope.declare("x", new GioviBool());
		assertTrue(runExpression(visitor, "x is Bool").asGioviBool().unbox());
	}

	@Test
	void testText() {
		scope.declare("x", new GioviText());
		assertTrue(runExpression(visitor, "x is Text").asGioviBool().unbox());
	}

	@Test
	void testStruct() {
		var definitionStruct = new GioviStruct("Foobar", new Struct.Named());
		scope.define("Foobar", definitionStruct);
		scope.declare("x", definitionStruct.copy());
		assertTrue(runExpression(visitor, "x is Foobar").asGioviBool().unbox());
	}

	@Test
	void testListOfPrimitive() {
		scope.declare("x", new GioviList(new GioviTypePrimitive(GioviInt.class)));
		assertTrue(runExpression(visitor, "x is [Int]").asGioviBool().unbox());
	}

	@Test
	void testListOfList() {
		scope.declare("x",
			new GioviList(new GioviTypeList(new GioviTypePrimitive(GioviInt.class))));
		assertTrue(runExpression(visitor, "x is [[Int]]").asGioviBool().unbox());
	}

	@Test
	void testMapOfPrimitive() {
		scope.declare("x", new GioviMap(
			new GioviTypePrimitive(GioviText.class),
			new GioviTypePrimitive(GioviInt.class)));
		assertTrue(runExpression(visitor, "x is [Text : Int]").asGioviBool().unbox());
	}

	@Test
	void testMapOfList() {
		scope.declare("x", new GioviMap(
			new GioviTypePrimitive(GioviText.class),
			new GioviTypeList(new GioviTypePrimitive(GioviInt.class))));
		assertTrue(runExpression(visitor, "x is [Text : [Int]]").asGioviBool().unbox());
	}

	@Test
	void testMapOfStruct() {
		scope.define("Foobar", new GioviStruct("Foobar", new Struct.Named()));
		scope.declare("x", new GioviMap(
			new GioviTypePrimitive(GioviText.class),
			new GioviTypeStruct("Foobar")));
		assertTrue(runExpression(visitor, "x is [Text : Foobar]").asGioviBool().unbox());
	}

}
