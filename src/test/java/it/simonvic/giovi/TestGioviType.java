package it.simonvic.giovi;

import static it.simonvic.giovi.GioviVisitorUtils.visitExpression;
import static org.assertj.core.api.Assertions.assertThat;
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
				GioviType.NAT,
				GioviType.NAT
			),
			Arguments.of(
				GioviType.INT,
				GioviType.INT
			),
			Arguments.of(
				GioviType.REAL,
				GioviType.REAL
			),
			Arguments.of(
				GioviType.TEXT,
				GioviType.TEXT
			),
			Arguments.of(
				GioviType.BOOL,
				GioviType.BOOL
			),
			Arguments.of(
				GioviType.LIST_OF_INT,
				GioviType.LIST_OF_INT
			),
			Arguments.of(
				new GioviTypeMap(GioviType.INT, GioviType.TEXT),
				new GioviTypeMap(GioviType.INT, GioviType.TEXT)
			)
		);
	}

	static Stream<Arguments> produceNotEqualTypes() {
		return Stream.of(
			Arguments.of(
				GioviType.NAT,
				GioviType.INT
			),
			Arguments.of(
				GioviType.INT,
				GioviType.REAL
			),
			Arguments.of(
				GioviType.REAL,
				GioviType.BOOL
			),
			Arguments.of(
				GioviType.BOOL,
				GioviType.TEXT
			),
			Arguments.of(
				GioviType.TEXT,
				GioviType.LIST_OF_INT
			),
			Arguments.of(
				GioviType.LIST_OF_INT,
				new GioviTypeMap(GioviType.INT, GioviType.TEXT)
			)
		);
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
					.as(GioviType.NAT)
					.asGioviNatural();
				assertEquals(expected, actual);
			}

			@Test
			void toInt() {
				var expected = new GioviInt(69);
				var actual = new GioviNatural(new Natural(69))
					.as(GioviType.INT)
					.asGioviInt();
				assertEquals(expected, actual);
			}

			@Test
			void toReal() {
				var expected = new GioviReal(69d);
				var actual = new GioviNatural(new Natural(69))
					.as(GioviType.REAL)
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
					.as(GioviType.NAT)
					.asGioviNatural();
				assertEquals(expected, actual);
			}

			@Test
			void toInt() {
				var expected = new GioviInt(69);
				var actual = new GioviInt(69)
					.as(GioviType.INT)
					.asGioviInt();
				assertEquals(expected, actual);
			}

			@Test
			void toReal() {
				var expected = new GioviReal(69d);
				var actual = new GioviInt(69)
					.as(GioviType.REAL)
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
					.as(GioviType.NAT)
					.asGioviNatural();
				assertEquals(expected, actual);
			}

			@Test
			void toInt() {
				var expected = new GioviInt(69);
				var actual = new GioviReal(69d)
					.as(GioviType.INT)
					.asGioviInt();
				assertEquals(expected, actual);
			}

			@Test
			void toReal() {
				var expected = new GioviReal(69d);
				var actual = new GioviReal(69d)
					.as(GioviType.REAL)
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
					.as(GioviType.TEXT)
					.asGioviText();
				assertEquals(expected, actual);
			}

		}

		@Nested
		class TestBool {

			@Test
			void toBool() {
				var expected = GioviBool.TRUE;
				var actual = GioviBool.TRUE
					.as(GioviType.BOOL)
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

	@Nested
	class Creation {

		@Test
		void testPrimitiveTypeCreation() {
			assertThat(visitExpression("""
					type.Int
					"""))
				.isEqualTo(GioviType.INT);
		}

		@Test
		void testListTypeCreation() {
			assertThat(visitExpression("""
					type.[Int]
					"""))
				.isEqualTo(GioviType.LIST_OF_INT);
		}

		@Test
		void testListOfListTypeCreation() {
			assertThat(visitExpression("""
					type.[[Int]]
					"""))
				.isEqualTo(new GioviTypeList(GioviType.LIST_OF_INT));
		}

		@Test
		void testMapTypeCreation() {
			assertThat(visitExpression("""
					type.[Text:Int]
					"""))
				.isEqualTo(new GioviTypeMap(GioviType.TEXT, GioviType.INT));
		}

	}

}
