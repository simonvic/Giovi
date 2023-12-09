package it.simonvic.giovi;

import static it.simonvic.giovi.GioviVisitorUtils.visitExpression;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class TestGioviReal {

	//                                                                                              Creation
	@Nested
	class Creation {

		@ParameterizedTest
		@CsvSource(textBlock = """
			6.9, 6.9
			69r, 69.0
			69_420r, 69_420.0
			1_000_000r, 1_000_000.0
			0b1000101r, 69.0
			0b0000_0001_1010_0100r, 420.0
			0o105r, 69.0
			0o207_454r, 69420.0
			1e10, 1e10
			0x1p69, 0x1p69
			""")
		void testLiteral(String code, Double expected) {
			assertThat(visitExpression(code).asNativeReal()).isEqualTo(expected);
		}

		@Test
		void testMaxValue() {
			assertThat(visitExpression("" + Double.MAX_VALUE).asNativeReal())
				.isEqualTo(Double.MAX_VALUE);
		}

		@Test
		void testMinValue() {
			assertThat(visitExpression("" + Double.MIN_VALUE).asNativeReal())
				.isEqualTo(Double.MIN_VALUE);
		}

	}

	//                                                                                              Operations
	@Nested
	class Operations {

		//                                                                                          redirection
		@Nested
		class Redirection {
		}

		//                                                                                          arithmetic
		@Nested
		class Arithmetic {
		}

		//                                                                                          boolean
		@Nested
		class Boolean {
		}

		//                                                                                          relational
		@Nested
		class Relational {
		}

		//                                                                                          access
		@Nested
		class Access {
		}

	}


}
