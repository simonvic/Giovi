package it.simonvic.giovi;

import static it.simonvic.giovi.Giovi.runExpression;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import it.simonvic.giovi.util.GioviStructs;

import java.util.List;

public class TestGioviStruct {

	@Test
	void shouldBeEquals() {
		assertEquals(
			GioviStructs.of(
				"foo", 69,
				"bar", true,
				"foobar", "hello"
			),
			GioviStructs.of(
				"foo", 69,
				"bar", true,
				"foobar", "hello"
			)
		);
	}

	@Test
	void structsWithDifferentFields_shouldNotBeEquals() {
		assertNotEquals(
			GioviStructs.of(
				"foo", 69,
				"bar", true
			),
			GioviStructs.of(
				"foo", 69
			)
		);
	}

	@Test
	void structsWithDifferentFieldsType_shouldNotBeEquals() {
		assertNotEquals(
			GioviStructs.of(
				"foo", 69,
				"bar", true
			),
			GioviStructs.of(
				"foo", 69,
				"bar", 420
			)
		);
	}

	@Nested
	class Creation {

		@Test
		void anonStruct_unnamedAttributes() {
			assertEquals(GioviStructs.of(List.of(
				69,
				true,
				"foo"
			)), runExpression("""
				(69 true "foo")
				"""));
		}

		@Test
		void anonStruct_namedAttributes() {
			assertEquals(GioviStructs.of(
				"foo", 69,
				"bar", true,
				"foobar", "hello"
			), runExpression("""
				(
					foo:69
					bar:true
					foobar:"hello"
				)
				"""));
		}

		@Disabled("Not implemented yet")
		@Test
		void typedStruct() {
			/*
			assertEquals(new GioviType("Color"), runExpression("""
				Color.(255 69 42)
				""").asGioviStruct().getGioviType());
			 */
		}


	}

}
