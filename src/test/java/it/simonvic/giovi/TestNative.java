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

import giovi.JFunction;
import giovi.RecordFieldSignature;
import giovi.StructSignature;
import giovi.lang.*;
import giovi.util.Color.Rgb;
import giovi.util.TestModule.TestStruct;
import it.simonvic.giovi.lang.FormalParameter;
import it.simonvic.giovi.lang.FormalParameters;
import it.simonvic.giovi.lang.FunctionSignature;

public class TestNative {

	@Test
	void foo() {
		visit("""
			_out <- giovi.util.TestModule.TestStruct.<>
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

	@Test
	void test_struct() throws NoSuchMethodException {
		var expectedSignature = new StructSignature(
			"TestStruct",
			new RecordFieldSignature("i", new GioviTypeInt()),
			new RecordFieldSignature("t", new GioviTypeText(), new GioviText("some default")),
			new RecordFieldSignature("l", new GioviTypeList(new GioviTypeInt())),
			new RecordFieldSignature("m", new GioviTypeMap(
				new GioviTypeText(),
				new GioviTypeInt())));
		expectedSignature.constants().put("SOME_CONST", new GioviInt(69));
		expectedSignature.functions().put(
			"someFunc",
			new GioviFunction(new JFunction(TestStruct.class.getMethod("someFunc"))));
		var actualSignature = visitExpression("giovi.util.TestModule.TestStruct");
		assertThat(actualSignature)
			.isEqualTo(new GioviTypeStruct(expectedSignature));
	}

	// TODO: clarify requirements. Should functions and constants be inherited?

	@Test
	void test_struct_extended() {
		var expectedSignature = new StructSignature(
			"TestStructExtends",
			new RecordFieldSignature("i", new GioviTypeInt()),
			new RecordFieldSignature("t", new GioviTypeText(), new GioviText("some default")),
			new RecordFieldSignature("l", new GioviTypeList(new GioviTypeInt())),
			new RecordFieldSignature("m", new GioviTypeMap(
				new GioviTypeText(),
				new GioviTypeInt())),
			new RecordFieldSignature("i2", new GioviTypeInt(), new GioviInt(420)));
		assertThat(visitExpression("giovi.util.TestModule.TestSubModule.TestStructExtends"))
			.isEqualTo(new GioviTypeStruct(expectedSignature));
	}

}
