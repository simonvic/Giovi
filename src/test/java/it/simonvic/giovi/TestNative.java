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
				new FunctionSignature("onlyReturn", GioviType.TEXT)),
			Arguments.of(
				"giovi.util.TestModule.onlyReturn",
				new FunctionSignature("onlyReturn", GioviType.TEXT)),
			Arguments.of(
				"giovi.util.TestModule.manyParams",
				new FunctionSignature(
					"manyParams",
					new FormalParameters(
						new FormalParameter("t", GioviType.TEXT),
						new FormalParameter("i", GioviType.INT),
						new FormalParameter("r", GioviType.REAL)))),
			Arguments.of(
				"giovi.util.TestModule.paramWithLabel",
				new FunctionSignature(
					"paramWithLabel",
					new FormalParameters(
						new FormalParameter("label", "t", GioviType.TEXT)))),
			Arguments.of(
				"giovi.util.TestModule.oneParamWithReturn",
				new FunctionSignature(
					"oneParamWithReturn",
					new FormalParameters(
						new FormalParameter("t", GioviType.TEXT)),
					GioviType.TEXT)),
			Arguments.of(
				"giovi.util.TestModule.complex",
				new FunctionSignature(
					"complex",
					new FormalParameters(
						new FormalParameter("i", GioviType.INT),
						new FormalParameter("s", new GioviTypeStruct(structSignatureOf(Rgb.class))),
						new FormalParameter("l", GioviType.LIST_OF_INT),
						new FormalParameter("m",
							new GioviTypeMap(
								GioviType.TEXT,
								new GioviTypeStruct(structSignatureOf(Rgb.class))))),
					GioviType.INT)),
			Arguments.of(
				"giovi.util.TestModule.getListOfPrimitive",
				new FunctionSignature(
					"getListOfPrimitive",
					GioviType.LIST_OF_INT
				)
			),
			Arguments.of(
				"giovi.util.TestModule.getMapOfPrimitive",
				new FunctionSignature(
					"getMapOfPrimitive",
					new GioviTypeMap(GioviType.TEXT, GioviType.INT)
				)
			)
		);
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
			new RecordFieldSignature("i", GioviType.INT),
			new RecordFieldSignature("t", GioviType.TEXT, new GioviText("some default")),
			new RecordFieldSignature("l", GioviType.LIST_OF_INT),
			new RecordFieldSignature("m", new GioviTypeMap(
				GioviType.TEXT,
				GioviType.INT)));
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
			new RecordFieldSignature("i", GioviType.INT),
			new RecordFieldSignature("t", GioviType.TEXT, new GioviText("some default")),
			new RecordFieldSignature("l", GioviType.LIST_OF_INT),
			new RecordFieldSignature("m", new GioviTypeMap(
				GioviType.TEXT,
				GioviType.INT)),
			new RecordFieldSignature("i2", GioviType.INT, new GioviInt(420)));
		assertThat(visitExpression("giovi.util.TestModule.TestSubModule.TestStructExtends"))
			.isEqualTo(new GioviTypeStruct(expectedSignature));
	}

	@Test
	void test_struct_recursive() {
		visit(
			"""
			root giovi.util.TestModule.Node
			root.data <- "root"

			root.next.next.next -> _out
			root -> _out
			root.toText -> _out
			root.toText() -> _out
			giovi.util.TestModule.Node.newSelf -> _out
			giovi.util.TestModule.Node.newSelf() -> _out
			root.capitalize -> _out
			root.capitalize() -> _out

			""");
	}

}
