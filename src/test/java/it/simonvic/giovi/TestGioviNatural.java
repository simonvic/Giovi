package it.simonvic.giovi;

import static it.simonvic.giovi.GioviVisitorUtils.visitExpression;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import it.simonvic.giovi.lang.values.natives.Natural;

public class TestGioviNatural {


	//                                                                                              Creation
	@Nested
	class Creation {

		@ParameterizedTest
		@CsvSource(textBlock = """
			69n, 69
			420n, 420
			69_420n, 69_420
			1_000_000n, 1_000_000
			0b1000101n, 69
			0b0000_0001_1010_0100n, 420
			0o105n, 69
			0o207_454n, 69420
			0x10f2cn, 0x10f2c
			0xFF_FF_FFn, 0xFFFFFF
			""")
		void testLiteral(String code, Integer expected) {
			assertThat(visitExpression(code).asNativeNatural()).isEqualTo(new Natural(expected));
		}

		@Test
		void testMaxValue() {
			assertThat(visitExpression("2147483647").asNativeNatural())
				.isEqualTo(new Natural(Integer.MAX_VALUE));
		}

		@Test
		void naturalGreaterThanMaxValue_shouldFail() {
			assertThatExceptionOfType(NumberFormatException.class)
				.isThrownBy(() -> visitExpression("2147483648n"));
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
