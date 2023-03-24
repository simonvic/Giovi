package it.simonvic.giovi;

import static it.simonvic.giovi.Giovi.runExpression;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import it.simonvic.giovi.lang.values.GioviBool;
import it.simonvic.giovi.lang.values.GioviInt;
import it.simonvic.giovi.lang.values.GioviMap;
import it.simonvic.giovi.lang.values.GioviReal;
import it.simonvic.giovi.lang.values.GioviText;
import it.simonvic.giovi.util.GioviMaps;

public class TestMaps {

	@Nested
	class TestTypeInference {

		@Test
		void mapOfIntAndText() {
			String code = """
				[ 1:"a" 2:"b" 3:"c" ]
				""";
			GioviMap map = runExpression(code).asGioviMap();
			assertEquals(GioviInt.class, map.getKeyType().unbox());
			assertEquals(GioviText.class, map.getElementType().unbox());
		}

		@Test
		void mapOfRealAndBool() {
			String code = """
				[ 1:true 2.0:true 3:false ]
				""";
			GioviMap map = runExpression(code).asGioviMap();
			assertEquals(GioviReal.class, map.getKeyType().unbox());
			assertEquals(GioviBool.class, map.getElementType().unbox());
		}

		@Test
		void mapOfIntAndReal() {
			String code = """
				[ 1:1 2:0.2 3:3 ]
				""";
			GioviMap map = runExpression(code).asGioviMap();
			assertEquals(GioviInt.class, map.getKeyType().unbox());
			assertEquals(GioviReal.class, map.getElementType().unbox());
		}

	}

	@Test
	void mapsShouldBeEqual() {
		GioviMap map1 = new GioviMap(GioviInt.class, GioviText.class);
		map1.put(new GioviInt(0), new GioviText("a"));
		map1.put(new GioviInt(1), new GioviText("b"));
		map1.put(new GioviInt(2), new GioviText("c"));

		GioviMap map2 = new GioviMap(GioviInt.class, GioviText.class);
		map2.put(new GioviInt(0), new GioviText("a"));
		map2.put(new GioviInt(1), new GioviText("b"));
		map2.put(new GioviInt(2), new GioviText("c"));

		assertEquals(map1, map2);
	}

	private static Stream<Arguments> produceEqualMaps() {
		return Stream.of(
			Arguments.of("""
				[ 1:"a" 2:"b" 3:"c" ]
				""", GioviMaps.of(1, "a", 2, "b", 3, "c")
			)
		);
	}

	@ParameterizedTest
	@MethodSource("produceEqualMaps")
	void testMapsEquality(String code, GioviMap expected) {
		assertEquals(expected, runExpression(code));
	}

}
