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

public class TestValue {

	static Stream<Arguments> produceValuesWithValueType() {
		return Stream.of(
			Arguments.of(Value.of(69), IntValue.class),
			Arguments.of(Value.of(4.20), RealValue.class),
			Arguments.of(Value.of(true), BoolValue.class),
			Arguments.of(Value.of("hello"), TextValue.class),
			Arguments.of(Value.of(List.of()), ListValue.class),
			Arguments.of(Value.of(Map.of()), MapValue.class)
		);
	}

	@ParameterizedTest
	@MethodSource("produceValuesWithValueType")
	void test_Value_of_valueType(Value<?> value, Class<?> valueType) {
		assertEquals(value.getClass(), valueType);
	}

	static Stream<Arguments> produceValuesWithPrimitiveValueType() {
		return Stream.of(
			Arguments.of(Value.of(69), Integer.class),
			Arguments.of(Value.of(4.20), Double.class),
			Arguments.of(Value.of(true), Boolean.class),
			Arguments.of(Value.of("hello"), String.class),
			Arguments.of(Value.of(List.of()), ArrayList.class),
			Arguments.of(Value.of(Map.of()), LinkedHashMap.class)
		);
	}

	@ParameterizedTest
	@MethodSource("produceValuesWithPrimitiveValueType")
	void test_Value_of_primitiveValueType(Value<?> value, Class<?> primitiveValueType) {
		assertEquals(value.getType(), primitiveValueType);
	}


	static Stream<Arguments> produceEquals() {
		return Stream.of(
			Arguments.of(Value.of(69), Value.of(69)),
			Arguments.of(Value.of(4.20), Value.of(4.20)),
			Arguments.of(Value.of(true), Value.of(true)),
			Arguments.of(Value.of("hello"), Value.of("hello")),
			Arguments.of(new NaturalValue(new Natural(69)), new NaturalValue(new Natural(69))),
			Arguments.of(new IntValue(69), new IntValue(69)),
			Arguments.of(new RealValue(4.20), new RealValue(4.20)),
			Arguments.of(new BoolValue(true), new BoolValue(true)),
			Arguments.of(new TextValue("hello"), new TextValue("hello"))
		);
	}

	static Stream<Arguments> produceNotEquals() {
		return Stream.of(
			Arguments.of(Value.of(69), Value.of(420)),
			Arguments.of(Value.of(4.20), Value.of(6.9)),
			Arguments.of(Value.of(true), Value.of(false)),
			Arguments.of(Value.of("hello"), Value.of("hELlo")),
			Arguments.of(new NaturalValue(new Natural(69)), new NaturalValue(new Natural(420))),
			Arguments.of(new IntValue(420), new IntValue(69)),
			Arguments.of(new RealValue(4.20), new RealValue(6.9)),
			Arguments.of(new BoolValue(true), new BoolValue(false)),
			Arguments.of(new TextValue("hello"), new TextValue("heLLo"))
		);
	}

	@ParameterizedTest
	@MethodSource("produceEquals")
	void shouldBeEquals(Value<?> v1, Value<?> v2) {
		assertEquals(v1, v2);
	}

	@ParameterizedTest
	@MethodSource("produceNotEquals")
	void shouldNotBeEquals(Value<?> v1, Value<?> v2) {
		assertNotEquals(v1, v2);
	}

	@Nested
	class TestCollections {

		@Nested
		class TestLists {
			static Stream<Arguments> produceEquals() {
				return Stream.of(
					Arguments.of(
						Value.of(List.of(1, 2, 3, 4)),
						Value.of(List.of(1, 2, 3, 4))
					),
					Arguments.of(
						Value.of(List.of(1d, 2d, 3d, 4d)),
						Value.of(List.of(1d, 2d, 3d, 4d))
					),
					Arguments.of(
						Value.of(List.of(true, false, true, true)),
						Value.of(List.of(true, false, true, true))
					),
					Arguments.of(
						Value.of(List.of("simone", "mario", "luigi")),
						Value.of(List.of("simone", "mario", "luigi"))
					)
				);
			}

			static Stream<Arguments> produceNotEquals() {
				return Stream.of(
					Arguments.of(
						Value.of(List.of(1, 2, 3, 69)),
						Value.of(List.of(1, 2, 3, 4))
					),
					Arguments.of(
						Value.of(List.of(1, 2, 3)),
						Value.of(List.of(1, 2, 3, 4))
					),
					Arguments.of(
						Value.of(List.of(1, 2, 3, 4)),
						Value.of(List.of(1, 2, 3))
					)
				);
			}

			@ParameterizedTest
			@MethodSource("produceEquals")
			void shouldBeEquals(Value<?> v1, Value<?> v2) {
				assertEquals(v1, v2);
			}

			@ParameterizedTest
			@MethodSource("produceNotEquals")
			void shouldNotBeEquals(Value<?> v1, Value<?> v2) {
				assertNotEquals(v1, v2);
			}
		}

		@Nested
		class TestMaps {
			static Stream<Arguments> produceEquals() {
				return Stream.of(
					Arguments.of(
						Value.of(Map.of(1, "a", 2, "b", 3, "c")),
						Value.of(Map.of(1, "a", 2, "b", 3, "c"))
					),
					Arguments.of(
						Value.of(Map.of(1, "a", 2, "b", 3, "c")),
						Value.of(Map.of(2, "b", 1, "a", 3, "c"))
					)
				);
			}

			static Stream<Arguments> produceNotEquals() {
				return Stream.of(
					Arguments.of(
						Value.of(Map.of(1, "A", 2, "b", 3, "c")),
						Value.of(Map.of(1, "a", 2, "b", 3, "c"))
					),
					Arguments.of(
						Value.of(Map.of(69, "a", 2, "b", 3, "c")),
						Value.of(Map.of(1, "a", 2, "b", 3, "c"))
					),
					Arguments.of(
						Value.of(Map.of(1, "a", 2, "b", 3, "c", 4, "d")),
						Value.of(Map.of(1, "a", 2, "b", 3, "c"))
					),
					Arguments.of(
						Value.of(Map.of("1", "a", "2", "b", "3", "c", "4", "d")),
						Value.of(Map.of(1, "a", 2, "b", 3, "c"))
					)
				);
			}

			@ParameterizedTest
			@MethodSource("produceEquals")
			void shouldBeEquals(Value<?> v1, Value<?> v2) {
				assertEquals(v1, v2);
			}

			@ParameterizedTest
			@MethodSource("produceNotEquals")
			void shouldNotBeEquals(Value<?> v1, Value<?> v2) {
				assertNotEquals(v1, v2);
			}
		}
	}
}
