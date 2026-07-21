package it.simonvic.giovi;

import static it.simonvic.giovi.GioviVisitorUtils.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import giovi.Natural;
import giovi.Range;
import giovi.lang.GioviRecord;
import it.simonvic.giovi.util.*;

public class TestGioviRecord {

	//////////////////////////////////////////////////////////////////////////////////////////////// CREATIONS

	@Nested
	class Creation {

		@Test
		void testCreation_record() {
			assertThat(visitExpression("""
				<
					a: 69
					b: true
					c: "foo"
				>
				""")).isEqualTo(
				GioviRecords.of(
					"a", 69,
					"b", true,
					"c", "foo"
				)
			);
		}

		@Test
		void testCreation_withNestedRecord() {
			assertThat(visitExpression("""
				<
					a: 69
					b: true
					c: <
						foo: "foo"
						bar: "bar"
					>
				>
				""")).isEqualTo(
				GioviRecords.of(
					"a", 69,
					"b", true,
					"c", GRecords.of(
						"foo", VarD.of("foo"),
						"bar", VarD.of("bar")
					)
				)
			);
		}

		@Test
		void testCreation_withNestedMixedTuples() {
			assertThat(visitExpression("""
				<
					a: 69
					b: true
					c: <
						foo: "foo"
						bar: "bar"
					>
					d: <1 2 3>
				>
				""")).isEqualTo(
				GioviRecords.of(
					"a", 69,
					"b", true,
					"c", GRecords.of(
						"foo", VarD.of("foo"),
						"bar", VarD.of("bar")
					),
					"d", GTuples.ofNative(1, 2, 3)
				)
			);
		}

	}

	//////////////////////////////////////////////////////////////////////////////////////////////// OPERATIONS

	@Nested
	class Operations {

		//////////////////////////////////////////////////////////////////////////////////////////// casting

		@Nested
		class Casting {

			@Test
			void casting_toTuple_withEqualTypes() {
				assertThat(
					visitExpression("""
						record.<a:69n b:"hello" c:true> as tuple<Natural, Text, Bool>
						""")
				).isEqualTo(
					GioviTuples.of(new Natural(69), "hello", true)
				);
			}

			@Test
			void casting_toTuple_withWiderTypes() {
				assertThat(
					visitExpression("""
						record.<a:69n b:"hello" c:420i> as tuple<Int, Text, Real>
						""")
				).isEqualTo(
					GioviTuples.of(69, "hello", 420d)
				);
			}

			@Test
			void casting_toTuple_withWiderSignature() {
				assertThat(
					visitExpression("""
						record.<a:69n b:420i> as tuple<Real, Real, Real>
						""")
				).isEqualTo(
					GioviTuples.of(69d, 420d, 0d)
				);
			}

			@Test
			void casting_toTuple_withNarrowerTypes() {
				assertThat(
					visitExpression("""
						record.<a:1i b:2r> as tuple<Natural, Int>
						""")
				).isEqualTo(
					GioviTuples.of(new Natural(1), 2)
				);
			}

			@Test
			void casting_toTuple_withNarrowerSignature() {
				assertThat(
					visitExpression("""
						record.<a:1r b:2r c:3r> as tuple<Natural, Int>
						""")
				).isEqualTo(
					GioviTuples.of(new Natural(1), 2)
				);
			}

			@Test
			void casting_toRecord_withEqualSignature() {
				assertThat(
					visitExpression("""
						record.<a:69n b:"hello" c:true> as record<a Natural, b Text, c Bool>
						""")
				).isEqualTo(
					GioviRecords.of(
						"a", new Natural(69),
						"b", "hello",
						"c", true
					)
				);
			}

			@Test
			void casting_toRecord_withWiderSignature() {
				assertThat(
					visitExpression("""
						record.<a:69n b:420i> as record<a Int, b Real, c Real>
						""")
				).isEqualTo(
					GioviRecords.of(
						"a", 69,
						"b", 420d,
						"c", 0d
					)
				);
			}

			@Test
			void casting_toRecord_withNarrowerSignature() {
				assertThat(
					visitExpression("""
						record.<a:69n b:420i c:6.9> as record<b Natural, c Int>
						""")
				).isEqualTo(
					GioviRecords.of(
						"b", new Natural(420),
						"c", 6
					)
				);
			}

			@Test
			void casting_toStruct_withEqualSignature() {
				assertThat(
					visitExpression(
						visit("""
							struct Foo <a Natural, b Text, c Bool>
							"""),
						"""
						record.<a:69n b:"hello" c:true> as Foo
						"""
					)
				).isEqualTo(
					GioviStructs.ofNative(
						"Foo",
						"a", new Natural(69),
						"b", "hello",
						"c", true
					)
				);
			}

			@Test
			void casting_toStruct_withWiderSignature() {
				assertThat(
					visitExpression(
						visit("""
							struct Foo <a Real, b Real, c Real>
							"""),
						"""
						record.<a:69n b:420i> as Foo
						"""
					)
				).isEqualTo(
					GioviStructs.ofNative(
						"Foo",
						"a", 69d,
						"b", 420d,
						"c", 0d
					)
				);
			}


			@Test
			void casting_toStruct_withNarrowerSignature() {
				assertThat(
					visitExpression(
						visit("""
							struct Foo <a Natural, b Int>
							"""),
						"""
						record.<a:1r b:2r c:3r> as Foo
						"""
					)
				).isEqualTo(
					GioviStructs.ofNative(
						"Foo",
						"a", new Natural(1),
						"b", 2
					)
				);
			}

		}

		//////////////////////////////////////////////////////////////////////////////////////////// redirection

		@Nested
		class Redirection {

			@Test
			void redir() {
				visit("""
					<a:1 b:true c:"hello"> -> foo record<a Int, b Bool, c Text>
					""");
			}

		}

		//////////////////////////////////////////////////////////////////////////////////////////// arithmetic

		//////////////////////////////////////////////////////////////////////////////////////////// boolean

		//////////////////////////////////////////////////////////////////////////////////////////// relational

		@Nested
		class Equality {

			@Test
			void records_withEqualValues_shouldBeTrue() {
				assertThat(visitNativeBool("""
					<a:1 b:true c:"hello"> = <a:1 b:true c:"hello">
					"""))
					.isTrue();
			}

			@Test
			void records_withEqualValues_butDifferentFields_shouldBeFalse() {
				assertThat(visitNativeBool("""
					<a:1 b:true c:"hello"> = <x:1 y:true z:"hello">
					"""))
					.isFalse();
			}

			@Test
			void records_withEqualValuesOfDifferentButCompatibleTypes_shouldBeTrue() {
				assertThat(visitNativeBool("""
					<a:1n b:true c:"hello"> = <a:1r b:true c:"hello">
					"""))
					.isTrue();
			}

			@Test
			void records_withAtLeastOneDifferentValue_shouldBeFalse() {
				assertThat(visitNativeBool("""
					<a:1 b:true c:"hello"> = <a:1 b:true c:"HELLO">
					"""))
					.isFalse();
			}

			@Test
			void records_withDifferentValueTypes_shouldBeFalse() {
				assertThat(visitNativeBool("""
					<a:1 b:true c:"hello"> = <a:1 b:true c:69>
					"""))
					.isFalse();
			}

			@Test
			void records_withEqualValuesButInDifferentOrder_shouldBeTrue() {
				assertThat(visitNativeBool("""
					<a:1 b:true c:"hello"> = <b:true a:1 c:"hello">
					"""))
					.isTrue();
			}

		}

		//////////////////////////////////////////////////////////////////////////////////////////// access

		@Nested
		class Access {

			@ParameterizedTest
			@CsvSource(textBlock = """
				<a:0 b:1 c:2 d:3>@0, 0
				<a:0 b:1 c:2 d:3>@1, 1
				<a:0 b:1 c:2 d:3>@2, 2
				<a:0 b:1 c:2 d:3>@3, 3
				""")
			void access_withInt_shouldReturnFieldAtIndex(String code, Integer expected) {
				assertThat(visitVar(code).unwrap().value()).isEqualTo(expected);
			}

			@ParameterizedTest
			@CsvSource(textBlock = """
				<a:0 b:1 c:2 d:3>@{-1}, 3
				<a:0 b:1 c:2 d:3>@{-2}, 2
				<a:0 b:1 c:2 d:3>@{-3}, 1
				<a:0 b:1 c:2 d:3>@{-4}, 0
				""")
			void access_withNegativeInt_shouldReturnFieldAtRearIndex(
				String code,
				Integer expected
			) {
				assertThat(visitVar(code).unwrap().value()).isEqualTo(expected);
			}


			static Stream<Arguments> produceArgs_access_withRange() {
				return Stream.of(
					Arguments.of(
						"<a:0 b:1 c:2 d:3 e:4>@0..4",
						GioviRecords.of("a", 0, "b", 1, "c", 2, "d", 3, "e", 4)
					),
					Arguments.of(
						"<a:0 b:1 c:2 d:3 e:4>@0..3",
						GioviRecords.of("a", 0, "b", 1, "c", 2, "d", 3)
					),
					Arguments.of(
						"<a:0 b:1 c:2 d:3 e:4>@0..2",
						GioviRecords.of("a", 0, "b", 1, "c", 2)
					),
					Arguments.of(
						"<a:0 b:1 c:2 d:3 e:4>@0..1",
						GioviRecords.of("a", 0, "b", 1)
					),
					Arguments.of(
						"<a:0 b:1 c:2 d:3 e:4>@0..0",
						GioviRecords.of("a", 0)
					),
					Arguments.of(
						"<a:0 b:1 c:2 d:3 e:4>@1..4",
						GioviRecords.of("b", 1, "c", 2, "d", 3, "e", 4)
					),
					Arguments.of(
						"<a:0 b:1 c:2 d:3 e:4>@2..4",
						GioviRecords.of("c", 2, "d", 3, "e", 4)
					),
					Arguments.of(
						"<a:0 b:1 c:2 d:3 e:4>@3..4",
						GioviRecords.of("d", 3, "e", 4)
					),
					Arguments.of(
						"<a:0 b:1 c:2 d:3 e:4>@4..4",
						GioviRecords.of("e", 4)
					),
					Arguments.of(
						"<a:0 b:1 c:2 d:3 e:4>@1..3",
						GioviRecords.of("b", 1, "c", 2, "d", 3)
					),
					Arguments.of(
						"<a:0 b:1 c:2 d:3 e:4>@2..4",
						GioviRecords.of("c", 2, "d", 3, "e", 4)
					)
				);
			}

			@ParameterizedTest
			@MethodSource("produceArgs_access_withRange")
			void access_withRange_shouldReturnSubset(String code, GioviRecord expected) {
				assertThat(visitValue(code)).isEqualTo(expected);
			}

			@Test
			void access_withLengthOperator_shouldReturnFieldsCount() {
				assertThat(
					visitValue("""
						<a:0 b:1 c:2 d:3 e:4>@%
						""")
						.value()
				).isEqualTo(5);
			}

			@Test
			void accessWithIndexesOperator_shouldReturnRange() {
				assertThat(visitExpression("""
					<a:0 b:1 c:2 d:3 e:4>@@
					"""))
					.isEqualTo(Range.of(0, 4, 1));
			}


		}

	}

}

