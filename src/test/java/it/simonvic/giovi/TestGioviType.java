package it.simonvic.giovi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.stream.Stream;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import it.simonvic.giovi.lang.primitives.Natural;
import it.simonvic.giovi.lang.values.*;

public class TestGioviType {

	static Stream<Arguments> produceEqualTypes() {
		return Stream.of(
			Arguments.of(
				new GioviTypePrimitive(null),
				new GioviTypePrimitive(null)),
			Arguments.of(
				new GioviTypePrimitive(GioviNatural.class),
				new GioviTypePrimitive(GioviNatural.class)),
			Arguments.of(
				new GioviTypePrimitive(GioviInt.class),
				new GioviTypePrimitive(GioviInt.class)),
			Arguments.of(
				new GioviTypePrimitive(GioviReal.class),
				new GioviTypePrimitive(GioviReal.class)),
			Arguments.of(
				new GioviTypePrimitive(GioviText.class),
				new GioviTypePrimitive(GioviText.class)),
			Arguments.of(
				new GioviTypePrimitive(GioviBool.class),
				new GioviTypePrimitive(GioviBool.class)),
			Arguments.of(
				new GioviTypeList(new GioviTypePrimitive(GioviInt.class)),
				new GioviTypeList(new GioviTypePrimitive(GioviInt.class))),
			Arguments.of(
				new GioviTypeMap(
					new GioviTypePrimitive(GioviInt.class),
					new GioviTypePrimitive(GioviText.class)),
				new GioviTypeMap(
					new GioviTypePrimitive(GioviInt.class),
					new GioviTypePrimitive(GioviText.class))));
	}

	static Stream<Arguments> produceNotEqualTypes() {
		return Stream.of(
			Arguments.of(
				new GioviTypePrimitive(null),
				new GioviTypePrimitive(GioviNatural.class)),
			Arguments.of(
				new GioviTypePrimitive(GioviNatural.class),
				new GioviTypePrimitive(GioviInt.class)),
			Arguments.of(
				new GioviTypePrimitive(GioviInt.class),
				new GioviTypePrimitive(GioviReal.class)),
			Arguments.of(
				new GioviTypePrimitive(GioviReal.class),
				new GioviTypePrimitive(GioviBool.class)),
			Arguments.of(
				new GioviTypePrimitive(GioviBool.class),
				new GioviTypePrimitive(GioviText.class)),
			Arguments.of(
				new GioviTypePrimitive(GioviText.class),
				new GioviTypeList(new GioviTypePrimitive(GioviInt.class))),
			Arguments.of(
				new GioviTypeList(new GioviTypePrimitive(GioviInt.class)),
				new GioviTypeMap(
					new GioviTypePrimitive(GioviInt.class),
					new GioviTypePrimitive(GioviText.class))));
	}

	@ParameterizedTest @MethodSource("produceEqualTypes")
	void testEquality(GioviType<?> type1, GioviType<?> type2) {
		assertEquals(type1, type2);
	}

	@ParameterizedTest @MethodSource("produceNotEqualTypes")
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
					.castTo(new GioviTypePrimitive(GioviNatural.class))
					.asGioviNatural();
				assertEquals(expected, actual);
			}

			@Test
			void toInt() {
				var expected = new GioviInt(69);
				var actual = new GioviNatural(new Natural(69))
					.castTo(new GioviTypePrimitive(GioviInt.class))
					.asGioviInt();
				assertEquals(expected, actual);
			}

			@Test
			void toReal() {
				var expected = new GioviReal(69d);
				var actual = new GioviNatural(new Natural(69))
					.castTo(new GioviTypePrimitive(GioviReal.class))
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
					.castTo(new GioviTypePrimitive(GioviNatural.class))
					.asGioviNatural();
				assertEquals(expected, actual);
			}

			@Test
			void toInt() {
				var expected = new GioviInt(69);
				var actual = new GioviInt(69)
					.castTo(new GioviTypePrimitive(GioviInt.class))
					.asGioviInt();
				assertEquals(expected, actual);
			}

			@Test
			void toReal() {
				var expected = new GioviReal(69d);
				var actual = new GioviInt(69)
					.castTo(new GioviTypePrimitive(GioviReal.class))
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
					.castTo(new GioviTypePrimitive(GioviNatural.class))
					.asGioviNatural();
				assertEquals(expected, actual);
			}

			@Test
			void toInt() {
				var expected = new GioviInt(69);
				var actual = new GioviReal(69d)
					.castTo(new GioviTypePrimitive(GioviInt.class))
					.asGioviInt();
				assertEquals(expected, actual);
			}

			@Test
			void toReal() {
				var expected = new GioviReal(69d);
				var actual = new GioviReal(69d)
					.castTo(new GioviTypePrimitive(GioviReal.class))
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
					.castTo(new GioviTypePrimitive(GioviText.class))
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
					.castTo(new GioviTypePrimitive(GioviBool.class))
					.asGioviBool();
				assertEquals(expected, actual);
			}

		}

	}

}
