package it.simonvic.giovi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.stream.Stream;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import giovi.Natural;
import giovi.lang.*;
import it.simonvic.giovi.util.GioviTypes;

public class TestGioviType {

	static Stream<Arguments> produceEqualTypes() {
		return Stream.of(
			Arguments.of(
				GioviTypes.of(GioviNatural.class),
				GioviTypes.of(GioviNatural.class)),
			Arguments.of(
				GioviTypes.of(GioviInt.class),
				GioviTypes.of(GioviInt.class)),
			Arguments.of(
				GioviTypes.of(GioviReal.class),
				GioviTypes.of(GioviReal.class)),
			Arguments.of(
				GioviTypes.of(GioviText.class),
				GioviTypes.of(GioviText.class)),
			Arguments.of(
				GioviTypes.of(GioviBool.class),
				GioviTypes.of(GioviBool.class)),
			Arguments.of(
				new GioviTypeList(GioviTypes.of(GioviInt.class)),
				new GioviTypeList(GioviTypes.of(GioviInt.class))),
			Arguments.of(
				new GioviTypeMap(
					GioviTypes.of(GioviInt.class),
					GioviTypes.of(GioviText.class)),
				new GioviTypeMap(
					GioviTypes.of(GioviInt.class),
					GioviTypes.of(GioviText.class))));
	}

	static Stream<Arguments> produceNotEqualTypes() {
		return Stream.of(
			Arguments.of(
				GioviTypes.of(GioviNatural.class),
				GioviTypes.of(GioviInt.class)),
			Arguments.of(
				GioviTypes.of(GioviInt.class),
				GioviTypes.of(GioviReal.class)),
			Arguments.of(
				GioviTypes.of(GioviReal.class),
				GioviTypes.of(GioviBool.class)),
			Arguments.of(
				GioviTypes.of(GioviBool.class),
				GioviTypes.of(GioviText.class)),
			Arguments.of(
				GioviTypes.of(GioviText.class),
				new GioviTypeList(GioviTypes.of(GioviInt.class))),
			Arguments.of(
				new GioviTypeList(GioviTypes.of(GioviInt.class)),
				new GioviTypeMap(
					GioviTypes.of(GioviInt.class),
					GioviTypes.of(GioviText.class))));
	}

	@ParameterizedTest
	@MethodSource("produceEqualTypes")
	void testEquality(GioviType<?> type1, GioviType<?> type2) {
		assertEquals(type1, type2);
	}

	@ParameterizedTest
	@MethodSource("produceNotEqualTypes")
	void testNotEquality(GioviType<?> type1, GioviType<?> type2) {
		assertNotEquals(type1, type2);
	}

	@Nested
	class TestCasts {

		@Nested
		class TestNatural {

			@Test
			void toNatural() {
				var expected = new GioviNatural(new Natural(69));
				var actual = new GioviNatural(new Natural(69))
					.as(GioviTypes.of(GioviNatural.class))
					.asGioviNatural();
				assertEquals(expected, actual);
			}

			@Test
			void toInt() {
				var expected = new GioviInt(69);
				var actual = new GioviNatural(new Natural(69))
					.as(GioviTypes.of(GioviInt.class))
					.asGioviInt();
				assertEquals(expected, actual);
			}

			@Test
			void toReal() {
				var expected = new GioviReal(69d);
				var actual = new GioviNatural(new Natural(69))
					.as(GioviTypes.of(GioviReal.class))
					.asGioviReal();
				assertEquals(expected, actual);
			}
		}

		@Nested
		class TestInt {

			@Test
			void toNatural() {
				var expected = new GioviNatural(new Natural(69));
				var actual = new GioviInt(69)
					.as(GioviTypes.of(GioviNatural.class))
					.asGioviNatural();
				assertEquals(expected, actual);
			}

			@Test
			void toInt() {
				var expected = new GioviInt(69);
				var actual = new GioviInt(69)
					.as(GioviTypes.of(GioviInt.class))
					.asGioviInt();
				assertEquals(expected, actual);
			}

			@Test
			void toReal() {
				var expected = new GioviReal(69d);
				var actual = new GioviInt(69)
					.as(GioviTypes.of(GioviReal.class))
					.asGioviReal();
				assertEquals(expected, actual);
			}
		}

		@Nested
		class TestReal {

			@Test
			void toNatural() {
				var expected = new GioviNatural(new Natural(69));
				var actual = new GioviReal(69d)
					.as(GioviTypes.of(GioviNatural.class))
					.asGioviNatural();
				assertEquals(expected, actual);
			}

			@Test
			void toInt() {
				var expected = new GioviInt(69);
				var actual = new GioviReal(69d)
					.as(GioviTypes.of(GioviInt.class))
					.asGioviInt();
				assertEquals(expected, actual);
			}

			@Test
			void toReal() {
				var expected = new GioviReal(69d);
				var actual = new GioviReal(69d)
					.as(GioviTypes.of(GioviReal.class))
					.asGioviReal();
				assertEquals(expected, actual);
			}
		}

		@Nested
		class TestText {

			@Test
			void toText() {
				var expected = new GioviText("hello world");
				var actual = new GioviText("hello world")
					.as(GioviTypes.of(GioviText.class))
					.asGioviText();
				assertEquals(expected, actual);
			}

		}

		@Nested
		class TestBool {

			@Test
			void toBool() {
				var expected = new GioviBool(true);
				var actual = new GioviBool(true)
					.as(GioviTypes.of(GioviBool.class))
					.asGioviBool();
				assertEquals(expected, actual);
			}

		}

	}

}
