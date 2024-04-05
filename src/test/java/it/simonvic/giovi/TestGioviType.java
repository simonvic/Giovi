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

public class TestGioviType {

	static Stream<Arguments> produceEqualTypes() {
		return Stream.of(
			Arguments.of(
				new GioviTypeNatural(),
				new GioviTypeNatural()),
			Arguments.of(
				new GioviTypeInt(),
				new GioviTypeInt()),
			Arguments.of(
				new GioviTypeReal(),
				new GioviTypeReal()),
			Arguments.of(
				new GioviTypeText(),
				new GioviTypeText()),
			Arguments.of(
				new GioviTypeBool(),
				new GioviTypeBool()),
			Arguments.of(
				new GioviTypeList(new GioviTypeInt()),
				new GioviTypeList(new GioviTypeInt())),
			Arguments.of(
				new GioviTypeMap(
					new GioviTypeInt(),
					new GioviTypeText()),
				new GioviTypeMap(
					new GioviTypeInt(),
					new GioviTypeText())));
	}

	static Stream<Arguments> produceNotEqualTypes() {
		return Stream.of(
			Arguments.of(
				new GioviTypeNatural(),
				new GioviTypeInt()),
			Arguments.of(
				new GioviTypeInt(),
				new GioviTypeReal()),
			Arguments.of(
				new GioviTypeReal(),
				new GioviTypeBool()),
			Arguments.of(
				new GioviTypeBool(),
				new GioviTypeText()),
			Arguments.of(
				new GioviTypeText(),
				new GioviTypeList(new GioviTypeInt())),
			Arguments.of(
				new GioviTypeList(new GioviTypeInt()),
				new GioviTypeMap(
					new GioviTypeInt(),
					new GioviTypeText())));
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
					.as(new GioviTypeNatural())
					.asGioviNatural();
				assertEquals(expected, actual);
			}

			@Test
			void toInt() {
				var expected = new GioviInt(69);
				var actual = new GioviNatural(new Natural(69))
					.as(new GioviTypeInt())
					.asGioviInt();
				assertEquals(expected, actual);
			}

			@Test
			void toReal() {
				var expected = new GioviReal(69d);
				var actual = new GioviNatural(new Natural(69))
					.as(new GioviTypeReal())
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
					.as(new GioviTypeNatural())
					.asGioviNatural();
				assertEquals(expected, actual);
			}

			@Test
			void toInt() {
				var expected = new GioviInt(69);
				var actual = new GioviInt(69)
					.as(new GioviTypeInt())
					.asGioviInt();
				assertEquals(expected, actual);
			}

			@Test
			void toReal() {
				var expected = new GioviReal(69d);
				var actual = new GioviInt(69)
					.as(new GioviTypeReal())
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
					.as(new GioviTypeNatural())
					.asGioviNatural();
				assertEquals(expected, actual);
			}

			@Test
			void toInt() {
				var expected = new GioviInt(69);
				var actual = new GioviReal(69d)
					.as(new GioviTypeInt())
					.asGioviInt();
				assertEquals(expected, actual);
			}

			@Test
			void toReal() {
				var expected = new GioviReal(69d);
				var actual = new GioviReal(69d)
					.as(new GioviTypeReal())
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
					.as(new GioviTypeText())
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
					.as(new GioviTypeBool())
					.asGioviBool();
				assertEquals(expected, actual);
			}

		}

	}

}
