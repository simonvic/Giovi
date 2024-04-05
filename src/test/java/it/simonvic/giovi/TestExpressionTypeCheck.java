package it.simonvic.giovi;

import static it.simonvic.giovi.GioviVisitorUtils.visitExpression;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import giovi.Struct;
import giovi.lang.*;
import it.simonvic.giovi.lang.Scope;

public class TestExpressionTypeCheck {

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
		assertTrue(visitExpression(visitor, "x is Natural").asGioviBool().unbox());
	}

	@Test
	void testInt() {
		scope.declare("x", new GioviInt());
		assertTrue(visitExpression(visitor, "x is Int").asGioviBool().unbox());
	}

	@Test
	void testReal() {
		scope.declare("x", new GioviInt());
		assertTrue(visitExpression(visitor, "x is Int").asGioviBool().unbox());
	}

	@Test
	void testBool() {
		scope.declare("x", new GioviBool());
		assertTrue(visitExpression(visitor, "x is Bool").asGioviBool().unbox());
	}

	@Test
	void testText() {
		scope.declare("x", new GioviText());
		assertTrue(visitExpression(visitor, "x is Text").asGioviBool().unbox());
	}

	@Test
	void testStruct() {
		var definition = new Struct("Foobar");
		scope.define(definition);
		scope.declare("x", new GioviStruct(definition.copy()));
		assertTrue(visitExpression(visitor, "x is Foobar").asGioviBool().unbox());
	}

	@Test
	void testListOfPrimitive() {
		scope.declare("x", new GioviList(new GioviTypeInt()));
		assertTrue(visitExpression(visitor, "x is [Int]").asGioviBool().unbox());
	}

	@Test
	void testListOfList() {
		scope.declare("x", new GioviList(new GioviTypeList(new GioviTypeInt())));
		assertTrue(visitExpression(visitor, "x is [[Int]]").asGioviBool().unbox());
	}

	@Test
	void testMapOfPrimitive() {
		scope.declare("x", new GioviMap(new GioviTypeText(), new GioviTypeInt()));
		assertTrue(visitExpression(visitor, "x is [Text : Int]").asGioviBool().unbox());
	}

	@Test
	void testMapOfList() {
		scope.declare("x", new GioviMap(
			new GioviTypeText(),
			new GioviTypeList(new GioviTypeInt())));
		assertTrue(visitExpression(visitor, "x is [Text : [Int]]").asGioviBool().unbox());
	}

	@Test
	void testMapOfStruct() {
		var definition = new Struct("Foobar");
		scope.define(definition);
		scope.declare("x", new GioviMap(new GioviTypeText(), new GioviTypeStruct(definition)));
		assertTrue(visitExpression(visitor, "x is [Text : Foobar]").asGioviBool().unbox());
	}

}
