package it.simonvic.giovi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import it.simonvic.giovi.lang.GioviStructDefinition;
import it.simonvic.giovi.lang.primitives.Natural;
import it.simonvic.giovi.lang.primitives.Struct;
import it.simonvic.giovi.lang.values.*;
import it.simonvic.giovi.util.GioviLists;
import it.simonvic.giovi.util.Structs;

public class TestUtils {

	@Nested
	class GioviValuesUtils {

		static Stream<Arguments> produceValuesWithValueType() {
			return Stream.of(
				Arguments.of(GioviNatural.class, GioviValue.of(new Natural(69))),
				Arguments.of(GioviInt.class, GioviValue.of(69)),
				Arguments.of(GioviReal.class, GioviValue.of(4.20)),
				Arguments.of(GioviBool.class, GioviValue.of(true)),
				Arguments.of(GioviText.class, GioviValue.of("hello")),
				Arguments.of(GioviList.class, GioviValue.of(List.of(69))),
				Arguments.of(GioviMap.class, GioviValue.of(Map.of(0, "a"))),
				Arguments.of(GioviStructTuple.class, GioviValue.of(Structs.of("a", "a"))),
				Arguments.of(
					GioviStructTyped.class,
					GioviValue.of(new GioviStructDefinition("Foo", Structs.of("a", "a")))));
		}

		@ParameterizedTest
		@MethodSource("produceValuesWithValueType")
		void test_Value_of_valueType(Class<?> valueType, GioviValue<?> value) {
			assertEquals(valueType, value.getClass());
		}

		static Stream<Arguments> produceValuesWithPrimitiveValueType() {
			return Stream.of(
				Arguments.of(Natural.class, GioviValue.of(new Natural(69))),
				Arguments.of(Integer.class, GioviValue.of(69)),
				Arguments.of(Double.class, GioviValue.of(4.20)),
				Arguments.of(Boolean.class, GioviValue.of(true)),
				Arguments.of(String.class, GioviValue.of("hello")),
				Arguments.of(ArrayList.class, GioviValue.of(List.of(69))),
				Arguments.of(LinkedHashMap.class, GioviValue.of(Map.of(0, "a"))),
				Arguments.of(Struct.Named.class, GioviValue.of(Structs.of("a", "a"))));
		}

		@ParameterizedTest
		@MethodSource("produceValuesWithPrimitiveValueType")
		void test_Value_of_primitiveValueType(Class<?> primitiveValueType, GioviValue<?> value) {
			assertEquals(primitiveValueType, value.getValueType());
		}


		static Stream<Arguments> produceEquals() {
			return Stream.of(
				Arguments.of(GioviValue.of(new Natural(69)), GioviValue.of(new Natural(69))),
				Arguments.of(GioviValue.of(69), GioviValue.of(69)),
				Arguments.of(GioviValue.of(4.20), GioviValue.of(4.20)),
				Arguments.of(GioviValue.of(true), GioviValue.of(true)),
				Arguments.of(GioviValue.of("hello"), GioviValue.of("hello")),
				Arguments.of(new GioviNatural(new Natural(69)), new GioviNatural(new Natural(69))),
				Arguments.of(new GioviInt(69), new GioviInt(69)),
				Arguments.of(new GioviReal(4.20), new GioviReal(4.20)),
				Arguments.of(new GioviBool(true), new GioviBool(true)),
				Arguments.of(new GioviText("hello"), new GioviText("hello")));
		}

		@ParameterizedTest
		@MethodSource("produceEquals")
		void shouldBeEquals(GioviValue<?> v1, GioviValue<?> v2) {
			assertEquals(v1, v2);
		}

		static Stream<Arguments> produceNotEquals() {
			return Stream.of(
				Arguments.of(GioviValue.of(new Natural(69)), GioviValue.of(new Natural(420))),
				Arguments.of(GioviValue.of(69), GioviValue.of(420)),
				Arguments.of(GioviValue.of(4.20), GioviValue.of(6.9)),
				Arguments.of(GioviValue.of(true), GioviValue.of(false)),
				Arguments.of(GioviValue.of("hello"), GioviValue.of("hELlo")),
				Arguments.of(new GioviNatural(new Natural(69)), new GioviNatural(new Natural(420))),
				Arguments.of(new GioviInt(420), new GioviInt(69)),
				Arguments.of(new GioviReal(4.20), new GioviReal(6.9)),
				Arguments.of(new GioviBool(true), new GioviBool(false)),
				Arguments.of(new GioviText("hello"), new GioviText("heLLo")));
		}

		@ParameterizedTest
		@MethodSource("produceNotEquals")
		void shouldNotBeEquals(GioviValue<?> v1, GioviValue<?> v2) {
			assertNotEquals(v1, v2);
		}

		@Nested
		class TestLists {
			static Stream<Arguments> produceEquals() {
				return Stream.of(
					Arguments.of(
						GioviValue.of(List.of(1, 2, 3, 4)),
						GioviValue.of(List.of(1, 2, 3, 4))),
					Arguments.of(
						GioviValue.of(List.of(1d, 2d, 3d, 4d)),
						GioviValue.of(List.of(1d, 2d, 3d, 4d))),
					Arguments.of(
						GioviValue.of(List.of(true, false, true, true)),
						GioviValue.of(List.of(true, false, true, true))),
					Arguments.of(
						GioviValue.of(List.of("simone", "mario", "luigi")),
						GioviValue.of(List.of("simone", "mario", "luigi"))));
			}

			@ParameterizedTest
			@MethodSource("produceEquals")
			void shouldBeEquals(GioviValue<?> v1, GioviValue<?> v2) {
				assertEquals(v1, v2);
			}

			static Stream<Arguments> produceNotEquals() {
				return Stream.of(
					Arguments.of(
						GioviValue.of(List.of(1, 2, 3, 69)),
						GioviValue.of(List.of(1, 2, 3, 4))),
					Arguments.of(
						GioviValue.of(List.of(1, 2, 3)),
						GioviValue.of(List.of(1, 2, 3, 4))),
					Arguments.of(
						GioviValue.of(List.of(1, 2, 3, 4)),
						GioviValue.of(List.of(1, 2, 3))));
			}

			@ParameterizedTest
			@MethodSource("produceNotEquals")
			void shouldNotBeEquals(GioviValue<?> v1, GioviValue<?> v2) {
				assertNotEquals(v1, v2);
			}
		}

		@Nested
		class TestMaps {
			static Stream<Arguments> produceEquals() {
				return Stream.of(
					Arguments.of(
						GioviValue.of(Map.of(1, "a", 2, "b", 3, "c")),
						GioviValue.of(Map.of(1, "a", 2, "b", 3, "c"))),
					Arguments.of(
						GioviValue.of(Map.of(1, "a", 2, "b", 3, "c")),
						GioviValue.of(Map.of(2, "b", 1, "a", 3, "c"))));
			}

			@ParameterizedTest
			@MethodSource("produceEquals")
			void shouldBeEquals(GioviValue<?> v1, GioviValue<?> v2) {
				assertEquals(v1, v2);
			}

			static Stream<Arguments> produceNotEquals() {
				return Stream.of(
					Arguments.of(
						GioviValue.of(Map.of(1, "A", 2, "b", 3, "c")),
						GioviValue.of(Map.of(1, "a", 2, "b", 3, "c"))),
					Arguments.of(
						GioviValue.of(Map.of(69, "a", 2, "b", 3, "c")),
						GioviValue.of(Map.of(1, "a", 2, "b", 3, "c"))),
					Arguments.of(
						GioviValue.of(Map.of(1, "a", 2, "b", 3, "c", 4, "d")),
						GioviValue.of(Map.of(1, "a", 2, "b", 3, "c"))),
					Arguments.of(
						GioviValue.of(Map.of("1", "a", "2", "b", "3", "c", "4", "d")),
						GioviValue.of(Map.of(1, "a", 2, "b", 3, "c"))));
			}

			@ParameterizedTest
			@MethodSource("produceNotEquals")
			void shouldNotBeEquals(GioviValue<?> v1, GioviValue<?> v2) {
				assertNotEquals(v1, v2);
			}

		}

	}


	@Nested
	class GioviListsUtil {

		@Test
		void test_GioviListsOf() {
			assertEquals(GioviLists.of(1, 2, 3), GioviLists.of(1, 2, 3));
			assertEquals(GioviLists.of("Simone", "Francesco", "Mario"),
				GioviLists.of("Simone", "Francesco", "Mario"));
		}

	}

	@Nested
	class GioviMapsUtil {
	}

	@Nested
	class GioviTypesUtil {
	}

	@Nested
	class GioviStructsUtil {
	}

}
