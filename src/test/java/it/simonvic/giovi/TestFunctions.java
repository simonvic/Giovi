package it.simonvic.giovi;

import static it.simonvic.giovi.GioviVisitorUtils.visit;
import static it.simonvic.giovi.GioviVisitorUtils.visitFunctionDeclaration;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.LinkedHashMap;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import giovi.lang.*;
import it.simonvic.giovi.exceptions.GioviException;
import it.simonvic.giovi.lang.GioviFunctionSignature;
import it.simonvic.giovi.lang.GioviFunctionSignatureParameter;
import it.simonvic.giovi.util.GioviTypes;

public class TestFunctions {

	private static GioviFunctionSignature signatureOf(
		String identifier,
		GioviFunctionSignatureParameter... parameters) {
		var params = new LinkedHashMap<String, GioviFunctionSignatureParameter>();
		for (var param : parameters) {
			params.put(param.identifier(), param);
		}
		return new GioviFunctionSignature(identifier, params, null);
	}

	private static GioviFunctionSignatureParameter paramOf(
		String identifier,
		Class<? extends GioviValue<?>> type,
		GioviValue<?> defaultValue) {
		return new GioviFunctionSignatureParameter(identifier, GioviTypes.of(type), defaultValue);
	}

	private static GioviFunctionSignatureParameter paramOf(
		String identifier,
		Class<? extends GioviValue<?>> type) {
		return new GioviFunctionSignatureParameter(identifier, GioviTypes.of(type));
	}

	@Nested
	class Declaration {

		@Test
		void testFunctionDeclaration_withNoParamsAndNoReturn() {
			var functionSignature = visitFunctionDeclaration("""
				someFunction() {}
				""");
			assertAll(
				() -> assertThat(functionSignature.parameters())
					.isEmpty(),
				() -> assertThat(functionSignature.returnType())
					.isEqualTo(GioviTypes.of(GioviVoid.class)));
		}

		@Test
		void testFunctionDeclaration_withNoParamsAndReturn() {
			var functionSignature = visitFunctionDeclaration("""
				someFunction() => Int {}
				""");
			assertAll(
				() -> assertThat(functionSignature.parameters())
					.isEmpty(),
				() -> assertThat(functionSignature.returnType())
					.isEqualTo(GioviTypes.of(GioviInt.class)));
		}

		@Test
		void testFunctionDeclaration_withRequiredParameter() {
			assertThat(visitFunctionDeclaration("""
				someFunction(
					a Int
				) {}
				"""))
				.isEqualTo(signatureOf(
					"someFunction",
					paramOf("a", GioviInt.class)));

		}

		@Test
		void testFunctionDeclaration_withRequiredParameters() {
			assertThat(visitFunctionDeclaration("""
				someFunction(
					a Int,
					b Real
				) {}
				"""))
				.isEqualTo(signatureOf(
					"someFunction",
					paramOf("a", GioviInt.class),
					paramOf("b", GioviReal.class)));
		}

		@Test
		void testFunctionDeclaration_withOptionalParameter() {
			assertThat(visitFunctionDeclaration("""
				someFunction(
					a Int default 69,
				) {}
				"""))
				.isEqualTo(signatureOf(
					"someFunction",
					paramOf("a", GioviInt.class, GioviValue.of(69))));
		}

		@Test
		void functionDeclaration_withOptionalParameterOfWrongType_shouldThrow() {
			assertThatExceptionOfType(GioviException.class).isThrownBy(() -> visit("""
				someFunction(
					a Int default "hello",
				) {}
				"""));
		}

		@Test
		void functionDeclaration_withOptionalParameterOfDifferentButCompatibleType_shouldNotThrow() {
			assertThatNoException().isThrownBy(() -> visit("""
				someFunction(
					a Real default 69n,
				) {}
				"""));
		}

		@Test
		void testFunctionDeclaration_withOptionalParameters() {
			assertThat(visitFunctionDeclaration("""
				someFunction(
					a Int default 69,
					b Real default 6.9,
				) {}
				"""))
				.isEqualTo(signatureOf(
					"someFunction",
					paramOf("a", GioviInt.class, GioviValue.of(69)),
					paramOf("b", GioviReal.class, GioviValue.of(6.9))));
		}

		@Test
		void testFunctionDeclaration_withRequiredParametersAndTerminalOptionalParameters() {
			assertThat(visitFunctionDeclaration("""
				someFunction(
					a Int,
					b Real,
					c Bool default true,
				) {}
				"""))
				.isEqualTo(signatureOf(
					"someFunction",
					paramOf("a", GioviInt.class),
					paramOf("b", GioviReal.class),
					paramOf("c", GioviBool.class, GioviValue.of(true))));
		}

		@Test
		void testFunctionDeclaration_withMixedRequiredAndOptionalParameters() {
			assertThat(visitFunctionDeclaration("""
				someFunction(
					a Int default 69,
					b Real,
					c Bool default true,
				) {}
				"""))
				.isEqualTo(signatureOf(
					"someFunction",
					paramOf("a", GioviInt.class, GioviValue.of(69)),
					paramOf("b", GioviReal.class),
					paramOf("c", GioviBool.class, GioviValue.of(true))));
		}

		@Disabled("Function overload not implemented yet")
		@Nested
		class TestOverload {

			@Test
			void overloading_withDifferentParameterCount_shouldNotThrow() {
				assertThatExceptionOfType(GioviException.class).isThrownBy(() -> visit("""
					fun(x Int, y Int) {}
					fun(x Int, y Int, z Int) {}
					"""));
			}

			@Test
			void overloading_withSameSignature_shouldThrow() {
				assertThatExceptionOfType(GioviException.class).isThrownBy(() -> visit("""
					fun(x Int, y Int) {}
					fun(x Int, y Int) {}
					"""));
			}

		}

	}

	@Nested
	class Return {

		@Test
		void notReturningInFunctionWithReturn_shouldNotThrow() {
			assertThatNoException().isThrownBy(() -> visit("""
				someFunction() {}
				someFunction()
				"""));
		}

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

		@Test
		void returningDifferentButCompatibleType_shouldNotThrow() {
			assertThatNoException().isThrownBy(() -> visit("""
				someFunction() => Real {
					=> 69n
				}
				someFunction()
				"""));
		}

	}

	@Nested
	class Invocation {

		@Test
		void invokingNonExistingFunction_shouldThrow() {
			assertThatExceptionOfType(GioviException.class).isThrownBy(() -> visit("""
				someFunction() {}
				someNonExistingFunction()
				"""));
		}

		@Test
		void invokingFunction_withExtraPositionalParam_shouldThrow() {
			assertThatExceptionOfType(GioviException.class).isThrownBy(() -> visit("""
				someFunction() {}
				someFunction(69)
				"""));
		}

		@Test
		void invokingFunction_withExtraPositionalParams_shouldThrow() {
			assertThatExceptionOfType(GioviException.class).isThrownBy(() -> visit("""
				someFunction(x Int) {}
				someFunction(69, 6.9)
				"""));
		}

		@Test
		void invokingFunction_withMissingRequiredParam_shouldThrow() {
			assertThatExceptionOfType(GioviException.class).isThrownBy(() -> visit("""
				someFunction(a Int) {}
				someFunction()
				"""));
		}

		@Test
		void invokingFunction_withMissingRequiredParams_shouldThrow() {
			assertThatExceptionOfType(GioviException.class).isThrownBy(() -> visit("""
				someFunction(a Int, b Real) {}
				someFunction(69)
				"""));
		}

		@Test
		void invokingFunction_withNamedParams_shouldNotThrow() {
			assertThatNoException().isThrownBy(() -> visit("""
				someFunction(a Int, b Real) {}
				someFunction(a: 69, b: 6.9)
				"""));
		}

		@Test
		void invokingFunction_usingNamedParam_withMissingRequiredParam_shouldThrow() {
			assertThatExceptionOfType(GioviException.class).isThrownBy(() -> visit("""
				fun(a Int, b Int) {}
				fun(b: 0)
				"""));
		}

		@Test
		void invokingFunction_usingDuplicateNamedParams_shouldThrow() {
			assertThatExceptionOfType(GioviException.class).isThrownBy(() -> visit("""
				fun(a Int, b Int) {}
				fun(a: 0, a: 1)
				"""));
		}

		@Test
		void invokingFunction_usingUnkownNamedParam_shouldThrow() {
			assertThatExceptionOfType(GioviException.class).isThrownBy(() -> visit("""
				fun(a Int, b Int) {}
				fun(a: 0, b: 1, z: 69)
				"""));
		}

	}

}
