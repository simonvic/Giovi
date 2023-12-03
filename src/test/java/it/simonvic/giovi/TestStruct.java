package it.simonvic.giovi;

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

}
