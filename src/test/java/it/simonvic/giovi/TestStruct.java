package it.simonvic.giovi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static it.simonvic.giovi.Giovi.visit;
import org.junit.jupiter.api.Test;

import it.simonvic.giovi.util.Structs;

public class TestStruct {

	@Test
	void testEquals() {
		assertEquals(
			Structs.of(
				"foo", "lol",
				"bar", true),
			Structs.of(
				"foo", "lol",
				"bar", true));
	}

	@Test
	void testEqualsNested() {
		assertEquals(
			Structs.of(
				"foo", 69,
				"bar", true,
				"foobar", Structs.of(
					"x", 0,
					"y", 0)),
			Structs.of(
				"foo", 69,
				"bar", true,
				"foobar", Structs.of(
					"x", 0,
					"y", 0)));
	}

	@Test
	void sandbox() {
		visit("""
			factorial(n Int) => Int {
			#" " * n ->+ \\out
				n -> \\out
				if n <= 1 do => 1
				factorial(n - 1) -> p Int
				p -> \\out
				=> n * p
			}

			factorial(4) -> \\out
			""");

	}

}
