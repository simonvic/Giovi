package it.simonvic.giovi;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import giovi.Natural;
import giovi.Struct;
import giovi.Tuple;
import giovi.TupleNamed;
import giovi.lang.*;
import it.simonvic.giovi.exceptions.NonWideneable;
import it.simonvic.giovi.util.*;

public class TestWidening {

	@Nested
	class TestGioviNatural {

		@Test
		void testWideningNaturalToNatural() throws NonWideneable {
			assertThat(new GioviNatural(new Natural(69))
				.widenTo(new GioviTypeNatural()))
					.isEqualTo(new GioviNatural(new Natural(69)));
		}

		@Test
		void testWideningNaturalToInt() throws NonWideneable {
			assertThat(new GioviNatural(new Natural(69))
				.widenTo(new GioviTypeInt()))
					.isEqualTo(new GioviInt(69));
		}

		@Test
		void testWideningNaturalToReal() throws NonWideneable {
			assertThat(new GioviNatural(new Natural(69))
				.widenTo(new GioviTypeReal()))
					.isEqualTo(new GioviReal(69d));
		}

	}

	@Nested
	class TestGioviInt {

		@Test
		void testWideningIntToInt() throws NonWideneable {
			assertThat(new GioviInt(69)
				.widenTo(new GioviTypeInt()))
					.isEqualTo(new GioviInt(69));
		}

		@Test
		void testWideningIntToReal() throws NonWideneable {
			assertThat(new GioviInt(69)
				.widenTo(new GioviTypeReal()))
					.isEqualTo(new GioviReal(69d));
		}

	}

	@Nested
	class TestGioviReal {

		@Test
		void testWideningRealToReal() throws NonWideneable {
			assertThat(new GioviReal(69d)
				.widenTo(new GioviTypeReal()))
					.isEqualTo(new GioviReal(69d));
		}

	}

	@Nested
	class TestGioviText {

		@Test
		void testWideningTextToText() throws NonWideneable {
			assertThat(new GioviText("hello")
				.widenTo(new GioviTypeText()))
					.isEqualTo(new GioviText("hello"));
		}

	}

	@Nested
	class TestGioviBool {

		@Test
		void testWideningBoolToBool() throws NonWideneable {
			assertThat(new GioviBool(true)
				.widenTo(new GioviTypeBool()))
					.isEqualTo(new GioviBool(true));
		}

	}

	@Nested
	class TestGioviList {

		@Test
		void testWideningIntListToRealList() throws NonWideneable {
			assertThat(GioviLists.of(1, 2, 3)
				.widenTo(new GioviTypeList(new GioviTypeReal())))
					.isEqualTo(GioviLists.of(1d, 2d, 3d));
		}

		@Test
		void wideningRealListToIntList_shouldThrow() {
			assertThatExceptionOfType(NonWideneable.class).isThrownBy(
				() -> GioviLists.of(1d, 2d, 3d)
					.widenTo(new GioviTypeList(new GioviTypeInt())));
		}

		@Test
		void testWideningIntMatrixToRealMatrix() throws NonWideneable {
			assertThat(GioviLists.of(
				List.of(1, 2, 3),
				List.of(1, 2, 3))
				.widenTo(new GioviTypeList(new GioviTypeList(new GioviTypeReal()))))
					.isEqualTo(GioviLists.of(
						List.of(1d, 2d, 3d),
						List.of(1d, 2d, 3d)));
		}

		@Test
		void wideningRealMatrixToIntMatrix_shouldThrow() {
			assertThatExceptionOfType(NonWideneable.class).isThrownBy(
				() -> GioviLists.of(
					List.of(1d, 2d, 3d),
					List.of(1d, 2d, 3d))
					.widenTo(new GioviTypeList(new GioviTypeList(new GioviTypeInt()))));
		}

	}

	@Nested
	class TestGioviMap {

		@Test
		void testWidening_TextIntMap_to_TextRealMap() throws NonWideneable {
			assertThat(GioviMaps.of(
				"a", 1,
				"b", 2,
				"c", 3)
				.widenTo(new GioviTypeMap(new GioviTypeText(), new GioviTypeReal())))
					.isEqualTo(GioviMaps.of(
						"a", 1d,
						"b", 2d,
						"c", 3d));
		}

		@Test
		void widening_TextRealMap_to_TextIntMap_shouldThrow() {
			assertThatExceptionOfType(NonWideneable.class).isThrownBy(
				() -> GioviMaps.of(
					"a", 1d,
					"b", 2d,
					"c", 3d)
					.widenTo(new GioviTypeMap(new GioviTypeInt(), new GioviTypeInt())));
		}

	}

	// TODO(test): ...

	@Nested
	class TestGioviTuple {

		@Test
		void testWidening_emptyTuple_to_emptyTuple() throws NonWideneable {
			assertThat(new GioviTuple().widenTo(new GioviTypeTuple(new Tuple())))
				.isEqualTo(new GioviTuple());
		}

		@Test
		void testWidening_toTuple_with_sameFields() throws NonWideneable {
			assertThat(GioviTuples.of(
				69,
				true,
				"foo")
				.widenTo(new GioviTypeTuple(new Tuple(
					new GioviInt(),
					new GioviBool(),
					new GioviText()))))
						.isEqualTo(GioviTuples.of(
							69,
							true,
							"foo"));
		}

		@Test
		void testWidening_toTuple_with_widerFields() throws NonWideneable {
			assertThat(GioviTuples.of(
				new Natural(69),
				69,
				List.of(69, 420))
				.widenTo(new GioviTypeTuple(new Tuple(
					new GioviInt(),
					new GioviReal(),
					new GioviList(new GioviTypeReal())))))
						.isEqualTo(GioviTuples.of(
							69,
							69d,
							List.of(69d, 420d)));
		}

		@Test
		void widening_toTuple_with_narrowerFields_shouldThrow() throws NonWideneable {
			assertThatExceptionOfType(NonWideneable.class).isThrownBy(() -> GioviTuples.of(
				69,
				69d,
				List.of(69d, 420d))
				.widenTo(new GioviTypeTuple(new Tuple(
					new GioviNatural(),
					new GioviInt(),
					new GioviList(new GioviTypeInt())))));
		}

		@Test
		void testWidening_toNamedTuple_with_sameFields() throws NonWideneable {
			assertThat(GioviTuples.of(
				new Natural(69),
				69,
				List.of(69, 420))
				.widenTo(new GioviTypeTupleNamed(Tuples.ofNamed(
					"n", new Natural(0),
					"i", 0,
					"li", List.of(0)))))
						.isEqualTo(GioviTuples.ofNamed(
							"n", new Natural(69),
							"i", 69,
							"li", List.of(69, 420)));
		}

		@Test
		void testWidening_toNamedTuple_with_widerFields() throws NonWideneable {
			assertThat(GioviTuples.of(
				new Natural(69),
				69,
				List.of(69, 420))
				.widenTo(new GioviTypeTupleNamed(Tuples.ofNamed(
					"i", 0,
					"r", 0d,
					"lr", List.of(0d)))))
						.isEqualTo(GioviTuples.ofNamed(
							"i", 69,
							"r", 69d,
							"lr", List.of(69d, 420d)));
		}

		@Test
		void widening_toNamedTuple_with_narrowerFields_shouldThrow() throws NonWideneable {
			assertThatExceptionOfType(NonWideneable.class).isThrownBy(() -> GioviTuples.of(
				69,
				69d,
				List.of(69d, 420d))
				.widenTo(new GioviTypeTupleNamed(Tuples.ofNamed(
					"n", new Natural(),
					"i", 0,
					"li", List.of(0)))));
		}


		@Test
		void testWidening_toStruct_with_sameFields() throws NonWideneable {
			assertThat(GioviTuples.of(
				new Natural(69),
				69,
				List.of(69, 420))
				.widenTo(new GioviTypeStruct(Structs.ofNative(
					"Foo",
					"n", new Natural(0),
					"i", 0,
					"li", List.of(0)))))
						.isEqualTo(GioviStructs.ofNative(
							"Foo",
							"n", new Natural(69),
							"i", 69,
							"li", List.of(69, 420)));
		}

		@Test
		void testWidening_toStruct_with_widerFields() throws NonWideneable {
			assertThat(GioviTuples.of(
				new Natural(69),
				69,
				List.of(69, 420))
				.widenTo(new GioviTypeStruct(Structs.ofNative(
					"Foo",
					"i", 0,
					"r", 0d,
					"lr", List.of(0d)))))
						.isEqualTo(GioviStructs.ofNative(
							"Foo",
							"i", 69,
							"r", 69d,
							"lr", List.of(69d, 420d)));
		}

		@Test
		void widening_toStruct_with_narrowerFields_shouldThrow() throws NonWideneable {
			assertThatExceptionOfType(NonWideneable.class).isThrownBy(() -> GioviTuples.of(
				69,
				69d,
				List.of(69d, 420d))
				.widenTo(new GioviTypeStruct(Structs.ofNative(
					"Foo",
					"n", new Natural(),
					"i", 0,
					"li", List.of(0)))));
		}


	}

	@Nested
	class TestGioviTupleNamed {

		@Test
		void testWidening_emptyNamedTuple_to_emptyNamedTuple() throws NonWideneable {
			assertThat(new GioviTupleNamed().widenTo(new GioviTypeTupleNamed(new TupleNamed())))
				.isEqualTo(new GioviTupleNamed());
		}

		@Test
		void widening_toTuple_shouldThrow() throws NonWideneable {
			assertThatExceptionOfType(NonWideneable.class).isThrownBy(() -> GioviTuples.ofNamed(
				"n", new Natural(69),
				"i", 69,
				"li", List.of(69, 420))
				.widenTo(new GioviTypeTuple(new Tuple())));
		}

		@Test
		void testWidening_toNamedTuple_with_sameFields() throws NonWideneable {
			assertThat(GioviTuples.ofNamed(
				"a", new Natural(69),
				"b", 69,
				"c", List.of(69, 420))
				.widenTo(new GioviTypeTupleNamed(Tuples.ofNamed(
					"a", new Natural(0),
					"b", 0,
					"c", List.of(0)))))
						.isEqualTo(GioviTuples.ofNamed(
							"a", new Natural(69),
							"b", 69,
							"c", List.of(69, 420)));
		}

		@Test
		void widening_toNamedTuple_with_differentNames_shouldThrow() {
			assertThatExceptionOfType(NonWideneable.class).isThrownBy(() -> GioviTuples.ofNamed(
				"a", new Natural(69),
				"b", 69,
				"c", List.of(69, 420))
				.widenTo(new GioviTypeTupleNamed(Tuples.ofNamed(
					"x", new Natural(0),
					"y", 0,
					"z", List.of(0)))));
		}

		@Test
		void testWidening_toStruct_with_sameFields() throws NonWideneable {
			assertThat(GioviTuples.ofNamed(
				"a", new Natural(69),
				"b", 69,
				"c", List.of(69, 420))
				.widenTo(new GioviTypeStruct(Structs.ofNative(
					"Foo",
					"a", 0,
					"b", 0d,
					"c", List.of(0d)))))
						.isEqualTo(GioviStructs.ofNative(
							"Foo",
							"a", 69,
							"b", 69d,
							"c", List.of(69d, 420d)));
		}

		@Test
		void widening_toStruct_with_sameFields_but_differentName_shouldThrow() {
			assertThatExceptionOfType(NonWideneable.class).isThrownBy(() -> GioviTuples.ofNamed(
				"a", new Natural(69),
				"b", 69,
				"c", List.of(69, 420))
				.widenTo(new GioviTypeStruct(Structs.ofNative(
					"Foo",
					"x", new Natural(0),
					"y", 0,
					"z", List.of(0)))));
		}

	}

	@Nested
	class TestGioviStruct {

		@Test
		void testWidening_emptyStruct_to_emptyStruct() throws NonWideneable {
			assertThat(new GioviStruct(new Struct("Foo"))
				.widenTo(new GioviTypeStruct(new Struct("Foo"))))
					.isEqualTo(new GioviStruct(new Struct("Foo")));
		}

		@Test
		void widening_toTuple_shouldThrow() throws NonWideneable {
			assertThatExceptionOfType(NonWideneable.class).isThrownBy(() -> GioviStructs.ofNative(
				"Foo",
				"n", new Natural(69),
				"i", 69,
				"li", List.of(69, 420))
				.widenTo(new GioviTypeTuple(new Tuple())));
		}

		@Test
		void widening_toNamedTuple_shouldThrow() throws NonWideneable {
			assertThatExceptionOfType(NonWideneable.class).isThrownBy(() -> GioviStructs.ofNative(
				"Foo",
				"n", new Natural(69),
				"i", 69,
				"li", List.of(69, 420))
				.widenTo(new GioviTypeTupleNamed(new TupleNamed())));
		}


		@Test
		void testWidening_toStruct_of_sameType() throws NonWideneable {
			assertThat(GioviStructs.ofNative(
				"Foo",
				"a", new Natural(69),
				"b", 69,
				"c", List.of(69, 420))
				.widenTo(new GioviTypeStruct(Structs.ofNative(
					"Foo",
					"a", new Natural(0),
					"b", 0,
					"c", List.of(0, 0)))))
						.isEqualTo(GioviStructs.ofNative(
							"Foo",
							"a", new Natural(69),
							"b", 69,
							"c", List.of(69, 420)));
		}

		// TODO: clarify requirements about widening between different struct types

		@Test
		void testWidening_toStruct_of_differentType() throws NonWideneable {
			assertThatExceptionOfType(NonWideneable.class).isThrownBy(() -> GioviStructs.ofNative(
				"Foo",
				"a", new Natural(69),
				"b", 69,
				"c", List.of(69, 420))
				.widenTo(new GioviTypeStruct(Structs.ofNative(
					"Bar",
					"a", new Natural(0),
					"b", 0,
					"c", List.of(0, 0)))));
		}


	}

	@Nested
	class TestGioviFunction {
	}

	@Nested
	class TestGioviType {
	}

	@Nested
	class TestGioviVoid {
	}

}
