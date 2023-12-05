package it.simonvic.giovi;

import static it.simonvic.giovi.GioviVisitor.visit;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import it.simonvic.giovi.exceptions.GioviException;

public class TestFunctions {

	@Test
	void notReturningInFunctionWithReturn_shouldThrow() {
		assertThatExceptionOfType(GioviException.class).isThrownBy(() -> visit("""
			someFunction() => Int {}
			someFunction()
			"""));
	}

	@Test
	void returningInFunctionWithNoReturn_shouldThrow() {
		assertThatExceptionOfType(GioviException.class).isThrownBy(() -> visit("""
			someFunction() {
				=> 69
			}
			someFunction()
			"""));
	}

	@Test
	void returningWrongType_shouldThrow() {
		assertThatExceptionOfType(GioviException.class).isThrownBy(() -> visit("""
			someFunction() => Text {
				=> 69
			}
			someFunction()
			"""));
	}

	@ParameterizedTest
	@ValueSource(
		strings = {
				"fun(){} fun(69)",
				"fun(x Int){} fun()",
				"fun(x Int, y Int){} fun(69)"
		})
	void functionCall_withWrongParametersNumber_shouldThrow(String code) {
		assertThatExceptionOfType(GioviException.class).isThrownBy(() -> visit(code));
	}

	@Test
	void testDeclaration_noParams_noReturn() {
		var functionSignature = visit("""
			sayHelloWorld() {}
			""").getCurrentScope().getFunctionSignature("sayHelloWorld");
		assertAll(
			() -> assertThat(functionSignature.getReturnType()).isEmpty(),
			() -> assertThat(functionSignature.getParameters()).isEmpty());
	}

	@Test
	void nonDefaultParameterAfterDefaultParameter_shouldThrowException() {
		assertThatExceptionOfType(GioviException.class).isThrownBy(() -> visit("""
			fun(x Int default 69, y Int) {
			}
			"""));
	}

	@Test
	void correctDefaultParameters_shouldNotThrowException() {
		assertDoesNotThrow(() -> visit("""
			fun(x Int, y Int default 69, z Int default 420) {
			}
			"""));
	}

	@Disabled("Function overload not implemented yet")
	@Nested
	class TestOverload {

		@Test
		void differentParameterCount_shouldNotThrow() {
			assertThatExceptionOfType(GioviException.class).isThrownBy(() -> visit("""
				fun(x Int, y Int) {}
				fun(x Int, y Int, z Int) {}
				"""));
		}

		@Test
		void sameSignature_shouldThrow() {
			assertThatExceptionOfType(GioviException.class).isThrownBy(() -> visit("""
				fun(x Int, y Int) {}
				fun(x Int, y Int) {}
				"""));
		}
	}

	@Nested
	class TestNamedParametersCall {

		@Test
		void missingParameter_shouldThrow() {
			assertThatExceptionOfType(GioviException.class).isThrownBy(() -> visit("""
				fun(a Int, b Int) {}
				fun(b: 0)
				"""));
		}

		@Test
		void duplicateNamedParameter_shouldThrow() {
			assertThatExceptionOfType(GioviException.class).isThrownBy(() -> visit("""
				fun(a Int, b Int) {}
				fun(a: 0, a: 1)
				"""));
		}

		@Test
		void unknownNamedParameter_shouldThrow() {
			assertThatExceptionOfType(GioviException.class).isThrownBy(() -> visit("""
				fun(a Int, b Int) {}
				fun(a: 0, b: 1, z: 69)
				"""));
		}

	}
}
