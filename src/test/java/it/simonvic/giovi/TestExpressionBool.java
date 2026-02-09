package it.simonvic.giovi;

import static it.simonvic.giovi.GioviVisitorUtils.visit;
import static it.simonvic.giovi.GioviVisitorUtils.visitExpression;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

public class TestExpressionBool {

	// TODO: omegalul

	@ParameterizedTest
	@CsvSource(textBlock = """
		false and []@69, false
		true or []@69, true
		""")
	void testShortCircuitEvaluation(String code, boolean expected) {
		assertThat(visitExpression(code).asNativeBool())
			.isEqualTo(expected);
	}

	@ParameterizedTest
	@ValueSource(
		strings = {
			"if true { | []@69 do {}}",
			"if false { & []@69 do {}}"
		}
	)
	void testShortCircuitEvaluationInMultiIf(String code) {
		assertThatNoException().isThrownBy(() -> visit(code));
	}

	@ParameterizedTest
	@ValueSource(
		strings = {
			"if 'hello' { = 'hello' or = []@69 do {} }",
			"if 'hello' { != 'hello' and = []@69 do {} }",
		}
	)
	void testShortCircuitEvaluationInMultiIfCascade(String code) {
		assertThatNoException().isThrownBy(() -> visit(code));
	}

	@ParameterizedTest
	@ValueSource(
		strings = {
			"if 'hello' { = 'hello' when false do []@69() }",
		}
	)
	void testShortCircuitEvaluationInMultiIfGuarded(String code) {
		assertThatNoException().isThrownBy(() -> visit(code));
	}

}
