package it.simonvic.giovi;

import static it.simonvic.giovi.Giovi.run;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
		var expected = Structs.of(
			"foo", 69,
			"bar", true,
			"foobar", Structs.of(
				"x", 0,
				"y", 0));
		var actual = Structs.of(
			"foo", 69,
			"bar", true,
			"foobar", Structs.of(
				"x", 0,
				"y", 0));
		assertEquals(expected, actual);
	}

	@Test
	void sandbox() {
		run("""
			"hello world" / " " > \\out
			""");
	}

}
