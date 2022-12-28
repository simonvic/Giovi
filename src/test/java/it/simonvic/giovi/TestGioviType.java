package it.simonvic.giovi;

import it.simonvic.giovi.lang.type.GioviType;
import it.simonvic.giovi.lang.type.Natural;
import it.simonvic.giovi.lang.values.*;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class TestGioviType {

	static Stream<Arguments> produceEqualTypes() {
		return Stream.of(
			Arguments.of(new GioviType(null), new GioviType(null)),
			Arguments.of(new GioviType(GioviNatural.class), new GioviType(GioviNatural.class)),
			Arguments.of(new GioviType(GioviInt.class), new GioviType(GioviInt.class)),
			Arguments.of(new GioviType(GioviReal.class), new GioviType(GioviReal.class)),
			Arguments.of(new GioviType(GioviText.class), new GioviType(GioviText.class)),
			Arguments.of(new GioviType(GioviBool.class), new GioviType(GioviBool.class)),
			Arguments.of(new GioviType(GioviList.class), new GioviType(GioviList.class)),
			Arguments.of(new GioviType(GioviMap.class), new GioviType(GioviMap.class)),
			Arguments.of(new GioviType(GioviStruct.class), new GioviType(GioviStruct.class))
		);
	}

	static Stream<Arguments> produceNotEqualTypes() {
		return Stream.of(
			Arguments.of(new GioviType(null), new GioviType(GioviNatural.class)),
			Arguments.of(new GioviType(GioviInt.class), new GioviType(GioviReal.class)),
			Arguments.of(new GioviType(GioviReal.class), new GioviType(GioviText.class)),
			Arguments.of(new GioviType(GioviText.class), new GioviType(GioviBool.class)),
			Arguments.of(new GioviType(GioviBool.class), new GioviType(GioviList.class)),
			Arguments.of(new GioviType(GioviList.class), new GioviType(GioviMap.class)),
			Arguments.of(new GioviType(GioviMap.class), new GioviType(GioviStruct.class)),
			Arguments.of(new GioviType(GioviStruct.class), new GioviType(null))
		);
	}

	@ParameterizedTest
	@MethodSource("produceEqualTypes")
	void testEquality(GioviType type1, GioviType type2) {
		assertEquals(type1, type2);
	}

	@ParameterizedTest
	@MethodSource("produceNotEqualTypes")
	void testNotEquality(GioviType type1, GioviType type2) {
		assertNotEquals(type1, type2);
	}

	@Nested
	class TestCasts {

		@Nested
		class TestNatural {

			@Test
			void toNatural() {
				GioviNatural expected = new GioviNatural(new Natural(69));
				GioviNatural actual = new GioviNatural(new Natural(69))
					.castTo(GioviNatural.class)
					.asGioviNatural();
				assertEquals(expected, actual);
			}

			@Test
			void toInt() {
				GioviInt expected = new GioviInt(69);
				GioviInt actual = new GioviNatural(new Natural(69))
					.castTo(GioviInt.class)
					.asGioviInt();
				assertEquals(expected, actual);
			}

			@Test
			void toReal() {
				GioviReal expected = new GioviReal(69d);
				GioviReal actual = new GioviNatural(new Natural(69))
					.castTo(GioviReal.class)
					.asGioviReal();
				assertEquals(expected, actual);
			}
		}

		@Nested
		class TestInt {

			@Test
			void toNatural() {
				GioviNatural expected = new GioviNatural(new Natural(69));
				GioviNatural actual = new GioviInt(69)
					.castTo(GioviNatural.class)
					.asGioviNatural();
				assertEquals(expected, actual);
			}

			@Test
			void toInt() {
				GioviInt expected = new GioviInt(69);
				GioviInt actual = new GioviInt(69)
					.castTo(GioviInt.class)
					.asGioviInt();
				assertEquals(expected, actual);
			}

			@Test
			void toReal() {
				GioviReal expected = new GioviReal(69d);
				GioviReal actual = new GioviInt(69)
					.castTo(GioviReal.class)
					.asGioviReal();
				assertEquals(expected, actual);
			}
		}

		@Nested
		class TestReal {

			@Test
			void toNatural() {
				GioviNatural expected = new GioviNatural(new Natural(69));
				GioviNatural actual = new GioviReal(69d)
					.castTo(GioviNatural.class)
					.asGioviNatural();
				assertEquals(expected, actual);
			}

			@Test
			void toInt() {
				GioviInt expected = new GioviInt(69);
				GioviInt actual = new GioviReal(69d)
					.castTo(GioviInt.class)
					.asGioviInt();
				assertEquals(expected, actual);
			}

			@Test
			void toReal() {
				GioviReal expected = new GioviReal(69d);
				GioviReal actual = new GioviReal(69d)
					.castTo(GioviReal.class)
					.asGioviReal();
				assertEquals(expected, actual);
			}
		}

		@Nested
		class TestText {

			@Test
			void toText() {
				GioviText expected = new GioviText("hello world");
				GioviText actual = new GioviText("hello world")
					.castTo(GioviText.class)
					.asGioviText();
				assertEquals(expected, actual);
			}

		}

		@Nested
		class TestBool {

			@Test
			void toBool() {
				GioviBool expected = new GioviBool(true);
				GioviBool actual = new GioviBool(true)
					.castTo(GioviBool.class)
					.asGioviBool();
				assertEquals(expected, actual);
			}

		}

	}

}
