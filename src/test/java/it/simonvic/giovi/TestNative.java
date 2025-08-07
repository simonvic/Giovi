package it.simonvic.giovi;

import static it.simonvic.giovi.GioviVisitorUtils.visit;
import static it.simonvic.giovi.GioviVisitorUtils.visitExpression;
import static it.simonvic.giovi.util.Natives.structSignatureOf;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import giovi.lang.*;
import giovi.util.Color.Rgb;
import it.simonvic.giovi.lang.FormalParameter;
import it.simonvic.giovi.lang.FormalParameters;
import it.simonvic.giovi.lang.FunctionSignature;

public class TestNative {

	@Test
	void foo() {
		visit("""
			_out <- giovi.util.Math.PI
			_out <- giovi.util.Math.floor(2.5)
			_out <- giovi.util.Math.floor(2.5)
			_out <- giovi.util.Random.integer()
			_out <- giovi.util.Color.WHITE
			_out <- giovi.util.Color.rgb(1, 2, 3)
			_out <- giovi.util.Color.rgba(1, 2, 3, 255)
			_out <- giovi.util.Color.Rgb.<>
			_out <- giovi.util.TestModule.TEST_CONST
			_out <- giovi.util.TestModule.complex
			_out <- giovi.util.TestModule.complex(
				i: 420,
				s: giovi.util.Color.Rgb.<>,
				l: [1 2 3],
				m : ["a":<1 1 1> "b":<2 2 2> "c":<3 3 3>]
			)
			# _out <- giovi.util.TestModule.getMap
			# _out <- giovi.util.TestModule.getMap()
			_out <- giovi.util.TestModule.getRgb
			_out <- giovi.util.TestModule.getRgb()
			_out <- giovi.util.TestModule.getListOfRgb
			_out <- giovi.util.TestModule.getListOfRgb()
			""");
	}

	static Stream<Arguments> produceArgs_testModuleSignatures() {
		return Stream.of(
			Arguments.of(
				"giovi.util.TestModule.minimal",
				new FunctionSignature("minimal")),
			Arguments.of(
				"giovi.util.TestModule.onlyReturn",
				new FunctionSignature("onlyReturn", new GioviTypeText())),
			Arguments.of(
				"giovi.util.TestModule.onlyReturn",
				new FunctionSignature("onlyReturn", new GioviTypeText())),
			Arguments.of(
				"giovi.util.TestModule.manyParams",
				new FunctionSignature(
					"manyParams",
					new FormalParameters(
						new FormalParameter("t", new GioviTypeText()),
						new FormalParameter("i", new GioviTypeInt()),
						new FormalParameter("r", new GioviTypeReal())))),
			Arguments.of(
				"giovi.util.TestModule.paramWithLabel",
				new FunctionSignature(
					"paramWithLabel",
					new FormalParameters(
						new FormalParameter("label", "t", new GioviTypeText())))),
			Arguments.of(
				"giovi.util.TestModule.oneParamWithReturn",
				new FunctionSignature(
					"oneParamWithReturn",
					new FormalParameters(
						new FormalParameter("t", new GioviTypeText())),
					new GioviTypeText())),
			Arguments.of(
				"giovi.util.TestModule.complex",
				new FunctionSignature(
					"complex",
					new FormalParameters(
						new FormalParameter("i", new GioviTypeInt()),
						new FormalParameter("s", new GioviTypeStruct(structSignatureOf(Rgb.class))),
						new FormalParameter("l", new GioviTypeList(new GioviTypeInt())),
						new FormalParameter("m",
							new GioviTypeMap(
								new GioviTypeText(),
								new GioviTypeStruct(structSignatureOf(Rgb.class))))),
					new GioviTypeInt())));
	}

	@ParameterizedTest
	@MethodSource("produceArgs_testModuleSignatures")
	void testFunctionSignatures(String code, FunctionSignature signature) {
		assertThat(visitExpression(code).asGioviFunction().value().signature())
			.isEqualTo(signature);
	}

}
