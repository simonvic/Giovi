package it.simonvic.giovi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.assertj.core.api.Assertions.assertThat;

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
				GioviNatural.TYPE,
				GioviNatural.TYPE),
			Arguments.of(
				GioviInt.TYPE,
				GioviInt.TYPE),
			Arguments.of(
				GioviReal.TYPE,
				GioviReal.TYPE),
			Arguments.of(
				GioviText.TYPE,
				GioviText.TYPE),
			Arguments.of(
				GioviBool.TYPE,
				GioviBool.TYPE),
			Arguments.of(
				GioviList.TYPE_OF_INT,
				GioviList.TYPE_OF_INT),
			Arguments.of(
				new GioviTypeMap(
					GioviInt.TYPE,
					GioviText.TYPE),
				new GioviTypeMap(
					GioviInt.TYPE,
					GioviText.TYPE)));
	}

	static Stream<Arguments> produceNotEqualTypes() {
		return Stream.of(
			Arguments.of(
				GioviNatural.TYPE,
				GioviInt.TYPE),
			Arguments.of(
				GioviInt.TYPE,
				GioviReal.TYPE),
			Arguments.of(
				GioviReal.TYPE,
				GioviBool.TYPE),
			Arguments.of(
				GioviBool.TYPE,
				GioviText.TYPE),
			Arguments.of(
				GioviText.TYPE,
				GioviList.TYPE_OF_INT),
			Arguments.of(
				GioviList.TYPE_OF_INT,
				new GioviTypeMap(
					GioviInt.TYPE,
					GioviText.TYPE)));
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
					.as(GioviNatural.TYPE)
					.asGioviNatural();
				assertEquals(expected, actual);
			}

			@Test
			void toInt() {
				var expected = new GioviInt(69);
				var actual = new GioviNatural(new Natural(69))
					.as(GioviInt.TYPE)
					.asGioviInt();
				assertEquals(expected, actual);
			}

			@Test
			void toReal() {
				var expected = new GioviReal(69d);
				var actual = new GioviNatural(new Natural(69))
					.as(GioviReal.TYPE)
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
					.as(GioviNatural.TYPE)
					.asGioviNatural();
				assertEquals(expected, actual);
			}

			@Test
			void toInt() {
				var expected = new GioviInt(69);
				var actual = new GioviInt(69)
					.as(GioviInt.TYPE)
					.asGioviInt();
				assertEquals(expected, actual);
			}

			@Test
			void toReal() {
				var expected = new GioviReal(69d);
				var actual = new GioviInt(69)
					.as(GioviReal.TYPE)
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
					.as(GioviNatural.TYPE)
					.asGioviNatural();
				assertEquals(expected, actual);
			}

			@Test
			void toInt() {
				var expected = new GioviInt(69);
				var actual = new GioviReal(69d)
					.as(GioviInt.TYPE)
					.asGioviInt();
				assertEquals(expected, actual);
			}

			@Test
			void toReal() {
				var expected = new GioviReal(69d);
				var actual = new GioviReal(69d)
					.as(GioviReal.TYPE)
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
					.as(GioviText.TYPE)
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
					.as(GioviBool.TYPE)
					.asGioviBool();
				assertEquals(expected, actual);
			}

		}

	}

	@Nested
	class TestTypeType {

		@Test
		void testTypeOfTypePreservation() {
			var someInt = new GioviInt();
			assertThat(someInt.getGioviType().getGioviType())
				.isEqualTo(new GioviTypeType<>(new GioviTypeInt()));
		}

		@Test
		void testSpawnOfTypeType() {
			var someInt = new GioviInt();
			assertThat(someInt.getGioviType().getGioviType().spawn())
				.isEqualTo(new GioviTypeInt());
		}

		// INFO: fun fact, spawn() and getGioviType() are one the opposite of the other

		@Test
		void testSpawnOfTypeTypeSpawn() { // lmao
			var someInt = new GioviInt();
			assertThat(someInt.getGioviType().getGioviType().spawn().spawn())
				.isEqualTo(new GioviInt());
		}

	}

}
