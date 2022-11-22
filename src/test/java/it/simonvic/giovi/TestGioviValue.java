package it.simonvic.giovi;

import it.simonvic.giovi.lang.type.Natural;
import it.simonvic.giovi.lang.values.*;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class TestGioviValue {

	static Stream<Arguments> produceValuesWithValueType() {
		return Stream.of(
			Arguments.of(GioviValue.of(69), GioviInt.class),
			Arguments.of(GioviValue.of(4.20), GioviReal.class),
			Arguments.of(GioviValue.of(true), GioviBool.class),
			Arguments.of(GioviValue.of("hello"), GioviText.class),
			Arguments.of(GioviValue.of(List.of(69)), GioviList.class),
			Arguments.of(GioviValue.of(Map.of(0, "a")), GioviMap.class)
		);
	}

	@ParameterizedTest
	@MethodSource("produceValuesWithValueType")
	void test_Value_of_valueType(GioviValue<?> value, Class<?> valueType) {
		assertEquals(value.getClass(), valueType);
	}

	static Stream<Arguments> produceValuesWithPrimitiveValueType() {
		return Stream.of(
			Arguments.of(GioviValue.of(69), Integer.class),
			Arguments.of(GioviValue.of(4.20), Double.class),
			Arguments.of(GioviValue.of(true), Boolean.class),
			Arguments.of(GioviValue.of("hello"), String.class),
			Arguments.of(GioviValue.of(List.of(69)), ArrayList.class),
			Arguments.of(GioviValue.of(Map.of(0, "a")), LinkedHashMap.class)
		);
	}

	@ParameterizedTest
	@MethodSource("produceValuesWithPrimitiveValueType")
	void test_Value_of_primitiveValueType(GioviValue<?> value, Class<?> primitiveValueType) {
		assertEquals(value.getValueType(), primitiveValueType);
	}


	static Stream<Arguments> produceEquals() {
		return Stream.of(
			Arguments.of(GioviValue.of(69), GioviValue.of(69)),
			Arguments.of(GioviValue.of(4.20), GioviValue.of(4.20)),
			Arguments.of(GioviValue.of(true), GioviValue.of(true)),
			Arguments.of(GioviValue.of("hello"), GioviValue.of("hello")),
			Arguments.of(new GioviNatural(new Natural(69)), new GioviNatural(new Natural(69))),
			Arguments.of(new GioviInt(69), new GioviInt(69)),
			Arguments.of(new GioviReal(4.20), new GioviReal(4.20)),
			Arguments.of(new GioviBool(true), new GioviBool(true)),
			Arguments.of(new GioviText("hello"), new GioviText("hello"))
		);
	}

	static Stream<Arguments> produceNotEquals() {
		return Stream.of(
			Arguments.of(GioviValue.of(69), GioviValue.of(420)),
			Arguments.of(GioviValue.of(4.20), GioviValue.of(6.9)),
			Arguments.of(GioviValue.of(true), GioviValue.of(false)),
			Arguments.of(GioviValue.of("hello"), GioviValue.of("hELlo")),
			Arguments.of(new GioviNatural(new Natural(69)), new GioviNatural(new Natural(420))),
			Arguments.of(new GioviInt(420), new GioviInt(69)),
			Arguments.of(new GioviReal(4.20), new GioviReal(6.9)),
			Arguments.of(new GioviBool(true), new GioviBool(false)),
			Arguments.of(new GioviText("hello"), new GioviText("heLLo"))
		);
	}

	@ParameterizedTest
	@MethodSource("produceEquals")
	void shouldBeEquals(GioviValue<?> v1, GioviValue<?> v2) {
		assertEquals(v1, v2);
	}

	@ParameterizedTest
	@MethodSource("produceNotEquals")
	void shouldNotBeEquals(GioviValue<?> v1, GioviValue<?> v2) {
		assertNotEquals(v1, v2);
	}

	@Nested
	class TestCollections {

		@Nested
		class TestLists {
			static Stream<Arguments> produceEquals() {
				return Stream.of(
					Arguments.of(
						GioviValue.of(List.of(1, 2, 3, 4)),
						GioviValue.of(List.of(1, 2, 3, 4))
					),
					Arguments.of(
						GioviValue.of(List.of(1d, 2d, 3d, 4d)),
						GioviValue.of(List.of(1d, 2d, 3d, 4d))
					),
					Arguments.of(
						GioviValue.of(List.of(true, false, true, true)),
						GioviValue.of(List.of(true, false, true, true))
					),
					Arguments.of(
						GioviValue.of(List.of("simone", "mario", "luigi")),
						GioviValue.of(List.of("simone", "mario", "luigi"))
					)
				);
			}

			static Stream<Arguments> produceNotEquals() {
				return Stream.of(
					Arguments.of(
						GioviValue.of(List.of(1, 2, 3, 69)),
						GioviValue.of(List.of(1, 2, 3, 4))
					),
					Arguments.of(
						GioviValue.of(List.of(1, 2, 3)),
						GioviValue.of(List.of(1, 2, 3, 4))
					),
					Arguments.of(
						GioviValue.of(List.of(1, 2, 3, 4)),
						GioviValue.of(List.of(1, 2, 3))
					)
				);
			}

			@ParameterizedTest
			@MethodSource("produceEquals")
			void shouldBeEquals(GioviValue<?> v1, GioviValue<?> v2) {
				assertEquals(v1, v2);
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
						GioviValue.of(Map.of(1, "a", 2, "b", 3, "c"))
					),
					Arguments.of(
						GioviValue.of(Map.of(1, "a", 2, "b", 3, "c")),
						GioviValue.of(Map.of(2, "b", 1, "a", 3, "c"))
					)
				);
			}

			static Stream<Arguments> produceNotEquals() {
				return Stream.of(
					Arguments.of(
						GioviValue.of(Map.of(1, "A", 2, "b", 3, "c")),
						GioviValue.of(Map.of(1, "a", 2, "b", 3, "c"))
					),
					Arguments.of(
						GioviValue.of(Map.of(69, "a", 2, "b", 3, "c")),
						GioviValue.of(Map.of(1, "a", 2, "b", 3, "c"))
					),
					Arguments.of(
						GioviValue.of(Map.of(1, "a", 2, "b", 3, "c", 4, "d")),
						GioviValue.of(Map.of(1, "a", 2, "b", 3, "c"))
					),
					Arguments.of(
						GioviValue.of(Map.of("1", "a", "2", "b", "3", "c", "4", "d")),
						GioviValue.of(Map.of(1, "a", 2, "b", 3, "c"))
					)
				);
			}

			@ParameterizedTest
			@MethodSource("produceEquals")
			void shouldBeEquals(GioviValue<?> v1, GioviValue<?> v2) {
				assertEquals(v1, v2);
			}

			@ParameterizedTest
			@MethodSource("produceNotEquals")
			void shouldNotBeEquals(GioviValue<?> v1, GioviValue<?> v2) {
				assertNotEquals(v1, v2);
			}
		}
	}
}
