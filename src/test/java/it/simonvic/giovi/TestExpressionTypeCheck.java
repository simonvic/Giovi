package it.simonvic.giovi;

import static it.simonvic.giovi.GioviVisitorUtils.visit;
import static it.simonvic.giovi.GioviVisitorUtils.visitExpression;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class TestExpressionTypeCheck {

	@Test
	void testNatural() {
		var visitor = visit("""
			x Natural
			""");
		assertTrue(visitExpression(visitor, """
			x is Natural
			""").asNativeBool());
	}

	@Test
	void testInt() {
		var visitor = visit("""
			x Int
			""");
		assertTrue(visitExpression(visitor, """
			x is Int
			""").asNativeBool());
	}

	@Test
	void testReal() {
		var visitor = visit("""
			x Int
			""");
		assertTrue(visitExpression(visitor, """
			x is Int
			""").asNativeBool());
	}

	@Test
	void testBool() {
		var visitor = visit("""
			x Bool
			""");
		assertTrue(visitExpression(visitor, """
			x is Bool
			""").asNativeBool());
	}

	@Test
	void testText() {
		var visitor = visit("""
			x Text
			""");
		assertTrue(visitExpression(visitor, """
			x is Text
			""").asNativeBool());
	}

	@Test
	void testStruct() {
		var visitor = visit("""
			Foo <i Int, t Text, b Bool>
			x Foo
			""");
		assertTrue(visitExpression(visitor, """
			x is Foo
			""").asNativeBool());
	}

	@Test
	void testListOfPrimitive() {
		var visitor = visit("""
			x [Int]
			""");
		assertTrue(visitExpression(visitor, """
			x is [Int]
			""").asNativeBool());
	}

	@Test
	void testListOfList() {
		var visitor = visit("""
			x [[Int]]
			""");
		assertTrue(visitExpression(visitor, """
			x is [[Int]]
			""").asNativeBool());
	}

	@Test
	void testMapOfPrimitive() {
		var visitor = visit("""
			x [Text : Int]
			""");
		assertTrue(visitExpression(visitor, """
			x is [Text : Int]
			""").asNativeBool());
	}

	@Test
	void testMapOfList() {
		var visitor = visit("""
			x [Text : [Int]]
			""");
		assertTrue(visitExpression(visitor, """
			x is [Text : [Int]]
			""").asNativeBool());
	}

	@Test
	void testMapOfStruct() {
		var visitor = visit("""
			Foo <i Int, t Text, b Bool>
			x [Text : Foo]
			""");
		assertTrue(visitExpression(visitor, """
			x is [Text : Foo]
			""").asNativeBool());
	}

	@Test
	@Disabled("Needs requirements clarification")
	void testTupleIsStruct() {
		var visitor = visit("""
			Foo <i Int, t Text, b Bool>
			""");
		assertTrue(visitExpression(visitor, """
			<69 "hello" true> is Foo
			""").asNativeBool());
	}

	@Test
	@Disabled("Needs requirements clarification")
	void testEqualStructs() {
		var visitor = visit("""
			Foo <i Int, t Text, b Bool>
			Bar <i Int, t Text, b Bool>
			""");
		assertTrue(visitExpression(visitor, """
			Bar.<> is Foo
			""").asNativeBool());
	}

	@Test
	@Disabled("Needs requirements clarification")
	void testStructIsTuple() {
		var visitor = visit("""
			Foo <i Int, t Text, b Bool>
			x Foo
			""");
		assertTrue(visitExpression(visitor, """
			x is <Int, Text, Bool>
			""").asNativeBool());
	}

}
