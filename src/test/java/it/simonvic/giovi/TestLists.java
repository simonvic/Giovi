package it.simonvic.giovi;

import static it.simonvic.giovi.Giovi.runExpression;
import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Stream;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import it.simonvic.giovi.exceptions.GioviException;
import it.simonvic.giovi.lang.values.*;
import it.simonvic.giovi.util.GioviLists;
import it.simonvic.giovi.util.GioviTypes;

public class TestLists {

	@Test
	void test_GioviListsOf() {
		assertEquals(GioviLists.of(1, 2, 3), GioviLists.of(1, 2, 3));
		assertEquals(GioviLists.of("Simone", "Francesco", "Mario"),
			GioviLists.of("Simone", "Francesco", "Mario"));
	}

	@Nested
	class TestTypeInference {

		@Test
		void listOfInt_shouldBe_listOfGioviInt() {
			String code = """
				[ 1 2 3 4 ]
				""";
			assertEquals(GioviInt.class,
				runExpression(code).asGioviList().getEntryType().unbox().getClazz());
		}

		@Test
		void listOfReal_shouldBe_listOfGioviReal() {
			String code = """
				[ 1.0 2.0 3.0 4.0 ]
				""";
			assertEquals(GioviReal.class,
				runExpression(code).asGioviList().getEntryType().unbox().getClazz());
		}

		@Test
		void listOfText_shouldBe_listOfGioviText() {
			String code = """
				[ "A" "B" "C" ]
				""";
			assertEquals(GioviText.class,
				runExpression(code).asGioviList().getEntryType().unbox().getClazz());
		}

		@Test
		void listOfBool_shouldBe_listOfGioviBool() {
			String code = """
				[ true false true true ]
				""";
			assertEquals(GioviBool.class,
				runExpression(code).asGioviList().getEntryType().unbox().getClazz());
		}

		@Test
		void listOfIntAndReal_shouldBe_listOfGioviReal() {
			String code = """
				[ 1 2 3.0 4 ]
				""";
			assertEquals(GioviReal.class,
				runExpression(code).asGioviList().getEntryType().unbox().getClazz());
		}

		@Test
		void listOfMixedTypes_shouldThrow() {
			String code = """
				[ 1 2 3.0 4 "hello" 5 ]
				""";
			assertThrows(GioviException.class, () -> runExpression(code));
		}

		@Test
		void listOfList() {
			String code = """
				[ [1 2 3] [1 2 3] ]
				""";
			var expected = new GioviTypeList(GioviTypes.of(GioviInt.class));
			assertEquals(expected, runExpression(code).asGioviList().getEntryType());
		}

		@Test
		void listOfList_shouldInferCorrectType() {
			String code = """
				[ [1 2 3] [1 2.0 3] ]
				""";
			var expected = new GioviTypeList(GioviTypes.of(GioviReal.class));
			var entryType = runExpression(code).asGioviList().getEntryType();
			assertEquals(expected, entryType);
		}

		@Test
		void listOfList_mixedTypes_shouldThrow() {
			String code = """
				[ [1 2 3] ["hello" "world"] ]
				""";
			assertThrows(GioviException.class, () -> runExpression(code));
		}

	}

	private static Stream<Arguments> produceEqualLists() {
		return Stream.of(
			Arguments.of("[1 2 3]", GioviLists.of(1, 2, 3)),
			Arguments.of("[1 2 3 4]", GioviLists.of(1, 2, 3, 4)),
			Arguments.of("['Simone' 'Francesco' 'Mario']",
				GioviLists.of("Simone", "Francesco", "Mario")));

	}

	@ParameterizedTest
	@MethodSource("produceEqualLists")
	void testListEquality(String code, GioviList expected) {
		assertEquals(expected, runExpression(code));
	}


	private static Stream<Arguments> produceNotEqualLists() {
		return Stream.of(
			Arguments.of("[1 2 3]", GioviLists.of(1, 2, 3, 4)),
			Arguments.of("[1 2 3 4]", GioviLists.of(1, 2, 3)),
			Arguments.of("[1 2 3]", GioviLists.of(1, 2, 69)),
			Arguments.of("['Simone' 'Francesco' 'Ma']",
				GioviLists.of("Simone", "Francesco", "Mario")),
			Arguments.of("['Simone' 'Francesco' 'Mario' 'Luigi']",
				GioviLists.of("Simone", "Francesco", "Mario")));
	}

	@ParameterizedTest
	@MethodSource("produceNotEqualLists")
	void testListNonEquality(String code, GioviList expected) {
		assertNotEquals(expected, runExpression(code));
	}


}
