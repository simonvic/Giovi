package it.simonvic.giovi;

import static it.simonvic.giovi.GioviVisitorUtils.visit;
import static it.simonvic.giovi.GioviVisitorUtils.visitExpression;
import static it.simonvic.giovi.util.Natives.structSignatureOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import giovi.JFunction;
import giovi.RecordFieldSignature;
import giovi.StructSignature;
import giovi.lang.*;
import giovi.util.Color.Rgb;
import giovi.util.NativeTestingModule.TestStruct;
import it.simonvic.giovi.lang.FormalParameter;
import it.simonvic.giovi.lang.FormalParameters;
import it.simonvic.giovi.lang.FunctionSignature;
import it.simonvic.giovi.lang.annotation.Native.Func;
import it.simonvic.giovi.lang.annotation.Native.Func.Param.Optional;
import it.simonvic.giovi.util.Natives;

public class TestNative {

	@Test
	void foo() {
		visit("""
			_out <- giovi.util.NativeTestingModule.TestStruct.<>
			""");
	}

	static Stream<Arguments> produceArgs_testModuleSignatures() {
		return Stream.of(
			Arguments.of(
				"giovi.util.NativeTestingModule.minimal",
				new FunctionSignature("minimal")
			),
			Arguments.of(
				"giovi.util.NativeTestingModule.onlyReturn",
				new FunctionSignature("onlyReturn", GioviType.TEXT)
			),
			Arguments.of(
				"giovi.util.NativeTestingModule.onlyReturn",
				new FunctionSignature("onlyReturn", GioviType.TEXT)
			),
			Arguments.of(
				"giovi.util.NativeTestingModule.manyParams",
				new FunctionSignature(
					"manyParams",
					new FormalParameters(
						new FormalParameter("t", GioviType.TEXT),
						new FormalParameter("i", GioviType.INT),
						new FormalParameter("r", GioviType.REAL)
					)
				)
			),
			Arguments.of(
				"giovi.util.NativeTestingModule.paramWithLabel",
				new FunctionSignature(
					"paramWithLabel",
					new FormalParameters(
						new FormalParameter("label", "t", GioviType.TEXT)
					)
				)
			),
			Arguments.of(
				"giovi.util.NativeTestingModule.oneParamWithReturn",
				new FunctionSignature(
					"oneParamWithReturn",
					new FormalParameters(
						new FormalParameter("t", GioviType.TEXT)
					),
					GioviType.TEXT
				)
			),
			Arguments.of(
				"giovi.util.NativeTestingModule.complex",
				new FunctionSignature(
					"complex",
					new FormalParameters(
						new FormalParameter("i", GioviType.INT),
						new FormalParameter("s", new GioviTypeStruct(structSignatureOf(Rgb.class))),
						new FormalParameter("l", GioviType.LIST_OF_INT),
						new FormalParameter(
							"m",
							new GioviTypeMap(
								GioviType.TEXT,
								new GioviTypeStruct(structSignatureOf(Rgb.class))
							)
						),
						new FormalParameter("t", new GioviTypeType<>(GioviType.ANY))
					),
					GioviType.INT
				)
			),
			Arguments.of(
				"giovi.util.NativeTestingModule.getListOfPrimitive",
				new FunctionSignature(
					"getListOfPrimitive",
					GioviType.LIST_OF_INT
				)
			),
			Arguments.of(
				"giovi.util.NativeTestingModule.getMapOfPrimitive",
				new FunctionSignature(
					"getMapOfPrimitive",
					new GioviTypeMap(GioviType.TEXT, GioviType.INT)
				)
			),
			Arguments.of(
				"giovi.util.NativeTestingModule.getTypeAny",
				new FunctionSignature(
					"getTypeAny",
					new GioviTypeType<>(GioviType.ANY)
				)
			),
			Arguments.of(
				"giovi.util.NativeTestingModule.getTypeInt",
				new FunctionSignature(
					"getTypeInt",
					new GioviTypeType<>(GioviType.INT)
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
			new RecordFieldSignature(
				"m", new GioviTypeMap(
					GioviType.TEXT,
					GioviType.INT
				)
			)
		);
		expectedSignature.constants().put("SOME_CONST", new GioviInt(69));
		expectedSignature.functions().put(
			"someFunc",
			new GioviFunction(new JFunction(TestStruct.class.getMethod("someFunc")))
		);
		var actualSignature = visitExpression("giovi.util.NativeTestingModule.TestStruct");
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
			new RecordFieldSignature(
				"m", new GioviTypeMap(
					GioviType.TEXT,
					GioviType.INT
				)
			),
			new RecordFieldSignature("i2", GioviType.INT, new GioviInt(420))
		);
		assertThat(
			visitExpression("giovi.util.NativeTestingModule.TestSubModule.TestStructExtends")
		)
			.isEqualTo(new GioviTypeStruct(expectedSignature));
	}

	@Test
	void test_struct_recursive() {
		visit(
			"""
			root giovi.util.NativeTestingModule.Node
			root.data <- "root"

			root.next.next.next -> _out
			root -> _out
			root.toText -> _out
			root.toText() -> _out
			giovi.util.NativeTestingModule.Node.newSelf -> _out
			giovi.util.NativeTestingModule.Node.newSelf() -> _out
			root.capitalize -> _out
			root.capitalize() -> _out

			"""
		);
	}

	@Nested
	class TestNativeFunctionsLookupAndInvocation {

		private class Foo {

			@Func
			public static void simple() {
			}

			@Func
			public static GioviInt simpleWithReturn() {
				return new GioviInt();
			}

			@Func
			public static void simpleWithParams(GioviInt a, GioviText b) {
			}

			@Func.Signature
			public static final FunctionSignature overloaded = new FunctionSignature(
				"overloaded",
				new FormalParameters(
					new FormalParameter("a", GioviType.INT, new GioviInt(1)),
					new FormalParameter("b", GioviType.INT, new GioviInt(10)),
					new FormalParameter("c", GioviType.INT, new GioviInt(100))
				),
				GioviType.INT
			);

			@Func
			public static GioviInt overloaded(
				@Optional GioviInt a,
				@Optional GioviInt b,
				@Optional GioviInt c
			) {
				return new GioviInt(a.value() + b.value() + c.value());
			}

			static final Method _simple;
			static final Method _simpleWithReturn;
			static final Method _simpleWithParams;
			static final Method _overloaded;
			static {
				try {
					_simple = Foo.class.getMethod("simple");
					_simpleWithReturn = Foo.class.getMethod("simpleWithReturn");
					_simpleWithParams = Foo.class.getMethod(
						"simpleWithParams",
						GioviInt.class,
						GioviText.class
					);
					_overloaded = Foo.class.getMethod(
						"overloaded",
						GioviInt.class,
						GioviInt.class,
						GioviInt.class
					);
				} catch (NoSuchMethodException e) {
					throw new RuntimeException(e);
				}
			}

		}

		@Nested
		class PositionalActualParams {

			@Test
			void test_lookup_simple() throws NoSuchMethodException {
				assertThat(
					Natives.lookupMethod(
						Foo.class,
						new FunctionSignature("simple")
					)
				).isEqualTo(Foo._simple);
			}

			@Test
			void test_lookup_simple_withExtraParams_shouldFail() throws NoSuchMethodException {
				assertThatExceptionOfType(NoSuchMethodException.class).isThrownBy(
					() -> Natives.lookupMethod(
						Foo.class,
						new FunctionSignature("simple"),
						List.of(VarD.of("foo"))
					)
				);
			}

			@Test
			void test_lookup_simpleWithReturn() throws NoSuchMethodException {
				assertThat(
					Natives.lookupMethod(
						Foo.class,
						new FunctionSignature("simpleWithReturn", GioviType.INT)
					)
				).isEqualTo(Foo._simpleWithReturn);
			}


			@Test
			void test_lookup_simpleWithParams() throws NoSuchMethodException {
				assertThat(
					Natives.lookupMethod(
						Foo.class,
						new FunctionSignature(
							"simpleWithParams",
							new FormalParameters(
								new FormalParameter("a", GioviType.INT),
								new FormalParameter("b", GioviType.TEXT)
							)
						),
						List.of(
							VarD.of(69),
							VarD.of("hello")
						)
					)
				).isEqualTo(Foo._simpleWithParams);
			}

			@Test
			void test_lookup_overloaded_allParams() throws NoSuchMethodException {
				assertThat(
					Natives.lookupMethod(
						Foo.class,
						Foo.overloaded,
						List.of(
							VarD.of(2),
							VarD.of(20),
							VarD.of(200)
						)
					)
				).isEqualTo(Foo._overloaded);
			}

			@Test
			void test_lookup_overloaded_subsetParams() throws NoSuchMethodException {
				assertThat(
					Natives.lookupMethod(
						Foo.class,
						Foo.overloaded,
						List.of(
							VarD.of(2),
							VarD.of(20)
						)
					)
				).isEqualTo(Foo._overloaded);
			}

			@Test
			void test_lookup_overloaded_subsetParams_2() throws NoSuchMethodException {
				assertThat(
					Natives.lookupMethod(
						Foo.class,
						Foo.overloaded,
						List.of()
					)
				).isEqualTo(Foo._overloaded);
			}

		}

		@Nested
		class NamedActualParams {

			@Test
			void test_lookup_simpleWithParams() throws NoSuchMethodException {
				var actualParams = new LinkedHashMap<String, VarD<?>>();
				actualParams.put("a", VarD.of(69));
				actualParams.put("b", VarD.of("hello"));
				assertThat(
					Natives.lookupMethod(
						Foo.class,
						new FunctionSignature(
							"simpleWithParams",
							new FormalParameters(
								new FormalParameter("a", GioviType.INT),
								new FormalParameter("b", GioviType.TEXT)
							)
						),
						actualParams
					)
				).isEqualTo(Foo._simpleWithParams);
			}

			@Test
			void test_lookup_simpleWithParams_disorganizedActual()
				throws NoSuchMethodException {
				var actualParams = new LinkedHashMap<String, VarD<?>>();
				actualParams.put("b", VarD.of("hello"));
				actualParams.put("a", VarD.of(69));
				assertThat(
					Natives.lookupMethod(
						Foo.class,
						new FunctionSignature(
							"simpleWithParams",
							new FormalParameters(
								new FormalParameter("a", GioviType.INT),
								new FormalParameter("b", GioviType.TEXT)
							)
						),
						actualParams
					)
				).isEqualTo(Foo._simpleWithParams);
			}

			@Test
			void test_lookup_simpleWithParams_subsetWithNoDefault_shouldFail()
				throws NoSuchMethodException {
				var actualParams = new LinkedHashMap<String, VarD<?>>();
				actualParams.put("a", VarD.of(69));
				assertThatExceptionOfType(NoSuchMethodException.class).isThrownBy(
					() -> Natives.lookupMethod(
						Foo.class,
						new FunctionSignature(
							"simpleWithParams",
							new FormalParameters(
								new FormalParameter("a", GioviType.INT),
								new FormalParameter("b", GioviType.TEXT)
							)
						),
						actualParams
					)
				);
			}

			@Test
			void test_lookup_overloaded_allParams() throws NoSuchMethodException {
				var actualParams = new LinkedHashMap<String, VarD<?>>();
				actualParams.put("a", VarD.of(2));
				actualParams.put("b", VarD.of(20));
				actualParams.put("c", VarD.of(200));
				assertThat(
					Natives.lookupMethod(
						Foo.class,
						Foo.overloaded,
						actualParams
					)
				).isEqualTo(Foo._overloaded);
			}

			@Test
			void test_lookup_overloaded_allParamsDisorganized() throws NoSuchMethodException {
				var actualParams = new LinkedHashMap<String, VarD<?>>();
				actualParams.put("b", VarD.of(20));
				actualParams.put("a", VarD.of(2));
				actualParams.put("c", VarD.of(200));
				assertThat(
					Natives.lookupMethod(
						Foo.class,
						Foo.overloaded,
						actualParams
					)
				).isEqualTo(Foo._overloaded);
			}

			@Test
			void test_lookup_overloaded_subsetParams() throws NoSuchMethodException {
				var actualParams = new LinkedHashMap<String, VarD<?>>();
				actualParams.put("a", VarD.of(2));
				actualParams.put("b", VarD.of(20));
				assertThat(
					Natives.lookupMethod(
						Foo.class,
						Foo.overloaded,
						actualParams
					)
				).isEqualTo(Foo._overloaded);
			}

			@Test
			void test_lookup_overloaded_subsetAndDisorganizedParams() throws NoSuchMethodException {
				var actualParams = new LinkedHashMap<String, VarD<?>>();
				actualParams.put("c", VarD.of(200));
				actualParams.put("b", VarD.of(20));
				assertThat(
					Natives.lookupMethod(
						Foo.class,
						Foo.overloaded,
						actualParams
					)
				).isEqualTo(Foo._overloaded);
			}

		}

	}

}
