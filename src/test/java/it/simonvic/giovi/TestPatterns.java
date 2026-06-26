package it.simonvic.giovi;

import static it.simonvic.giovi.GioviVisitorUtils.visit;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.Test;

import giovi.Natural;
import giovi.lang.GioviInt;
import giovi.lang.GioviNatural;
import giovi.lang.GioviReal;
import it.simonvic.giovi.util.GioviRecords;
import it.simonvic.giovi.util.GioviTuples;


public class TestPatterns {


	@Test
	void tuple_toPositionalPattern() {
		var scope = visit("""
			<1n, <2, 3, 4>, 5r> -> <x, y <y1, _, y3>, z>
			""")
			.getCurrentScope();
		assertAll(
			() -> assertThat(scope.getVariable("x").orElseThrow().unwrap())
				.isEqualTo(new GioviNatural(new Natural(1))),
			() -> assertThat(scope.getVariable("y").orElseThrow().unwrap())
				.isEqualTo(GioviTuples.of(2, 3, 4)),
			() -> assertThat(scope.getVariable("y1").orElseThrow().unwrap())
				.isEqualTo(new GioviInt(2)),
			() -> assertThat(scope.getVariable("y3").orElseThrow().unwrap())
				.isEqualTo(new GioviInt(4)),
			() -> assertThat(scope.getVariable("z").orElseThrow().unwrap())
				.isEqualTo(new GioviReal(5d))
		);
	}

	@Test
	void tuple_toPositionalPattern_withWiderTypes() {
		var scope = visit("""
			<1n, <2, 3, 4>, 5r> -> <x Int, y <y1 Int, _, y3 Real>, z Real>
			""")
			.getCurrentScope();
		assertAll(
			() -> assertThat(scope.getVariable("x").orElseThrow().unwrap())
				.isEqualTo(new GioviInt(1)),
			() -> assertThat(scope.getVariable("y").orElseThrow().unwrap())
				.isEqualTo(GioviTuples.of(2, 3, 4)),
			() -> assertThat(scope.getVariable("y1").orElseThrow().unwrap())
				.isEqualTo(new GioviInt(2)),
			() -> assertThat(scope.getVariable("y3").orElseThrow().unwrap())
				.isEqualTo(new GioviReal(4d)),
			() -> assertThat(scope.getVariable("z").orElseThrow().unwrap())
				.isEqualTo(new GioviReal(5d))
		);
	}

	@Test
	void tuple_toNarrowerPositionalPattern() {
		var scope = visit("""
			<1n, <2, 3, 4>, 5r> -> <_, <_, y2>>
			""")
			.getCurrentScope();
		assertThat(scope.getVariable("y2").orElseThrow().unwrap())
			.isEqualTo(new GioviInt(3));
	}

	@Test
	void record_toPositionalPattern() {
		var scope = visit("""
			<a:1n, b:<b1:2, b2:3, b3:4>, c:5r> -> <x, <y1, _, y3>, z>
			""")
			.getCurrentScope();
		assertAll(
			() -> assertThat(scope.getVariable("x").orElseThrow().unwrap())
				.isEqualTo(new GioviNatural(new Natural(1))),
			() -> assertThat(scope.getVariable("y1").orElseThrow().unwrap())
				.isEqualTo(new GioviInt(2)),
			() -> assertThat(scope.getVariable("y3").orElseThrow().unwrap())
				.isEqualTo(new GioviInt(4)),
			() -> assertThat(scope.getVariable("z").orElseThrow().unwrap())
				.isEqualTo(new GioviReal(5d))
		);
	}

	@Test
	void record_toPositionalPattern_withWiderTypes() {
		var scope = visit("""
			<a:1n, b:<b1:2, b2:3, b3:4>, c:5r> -> <x Int, y <y1 Int, _, y3 Real>, z Real>
			""")
			.getCurrentScope();
		assertAll(
			() -> assertThat(scope.getVariable("x").orElseThrow().unwrap())
				.isEqualTo(new GioviInt(1)),
			() -> assertThat(scope.getVariable("y").orElseThrow().unwrap())
				.isEqualTo(
					GioviRecords.of(
						"b1", 2,
						"b2", 3,
						"b3", 4
					)
				),
			() -> assertThat(scope.getVariable("y1").orElseThrow().unwrap())
				.isEqualTo(new GioviInt(2)),
			() -> assertThat(scope.getVariable("y3").orElseThrow().unwrap())
				.isEqualTo(new GioviReal(4d)),
			() -> assertThat(scope.getVariable("z").orElseThrow().unwrap())
				.isEqualTo(new GioviReal(5d))
		);
	}

	@Test
	void record_toNarrowerExplicitNamedPattern() {
		var scope = visit("""
			<a:1n, b:<b1:2, b2:3, b3:4>, c:5r> -> <b:<b2:y2>>
			""")
			.getCurrentScope();
		assertThat(scope.getVariable("y2").orElseThrow().unwrap())
			.isEqualTo(new GioviInt(3));
	}


	@Test
	void record_toExplicitNamedPattern() {
		var scope = visit("""
			<a:1n, b:<b1:2, b2:3, b3:4>, c:5r> -> <a:x, b:y<b1:y1, b2:_, b3:y3>, c:z>
			""")
			.getCurrentScope();
		assertAll(
			() -> assertThat(scope.getVariable("x").orElseThrow().unwrap())
				.isEqualTo(new GioviNatural(new Natural(1))),
			() -> assertThat(scope.getVariable("y").orElseThrow().unwrap())
				.isEqualTo(
					GioviRecords.of(
						"b1", 2,
						"b2", 3,
						"b3", 4
					)
				),
			() -> assertThat(scope.getVariable("y1").orElseThrow().unwrap())
				.isEqualTo(new GioviInt(2)),
			() -> assertThat(scope.getVariable("y3").orElseThrow().unwrap())
				.isEqualTo(new GioviInt(4)),
			() -> assertThat(scope.getVariable("z").orElseThrow().unwrap())
				.isEqualTo(new GioviReal(5d))
		);
	}

	@Test
	void record_toImplicitNamedPattern() {
		var scope = visit("""
			<a:1n, b:<b1:2, b2:3, b3:4>, c:5r> -> <:a, b:b<:b1, b2:_, :b3>, :c>
			""")
			.getCurrentScope();
		assertAll(
			() -> assertThat(scope.getVariable("a").orElseThrow().unwrap())
				.isEqualTo(new GioviNatural(new Natural(1))),
			() -> assertThat(scope.getVariable("b").orElseThrow().unwrap())
				.isEqualTo(
					GioviRecords.of(
						"b1", 2,
						"b2", 3,
						"b3", 4
					)
				),
			() -> assertThat(scope.getVariable("b1").orElseThrow().unwrap())
				.isEqualTo(new GioviInt(2)),
			() -> assertThat(scope.getVariable("b3").orElseThrow().unwrap())
				.isEqualTo(new GioviInt(4)),
			() -> assertThat(scope.getVariable("c").orElseThrow().unwrap())
				.isEqualTo(new GioviReal(5d))
		);
	}

}
