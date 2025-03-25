package it.simonvic.giovi;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import giovi.Natural;
import giovi.Record;
import giovi.Struct;
import giovi.Tuple;
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
			var narrow = GioviTuples.of(
				69,
				true,
				"foo");
			var type = new GioviTypeTuple(new Tuple(
				new VarD<>(new GioviInt()),
				new VarD<>(new GioviBool()),
				new VarD<>(new GioviText())));
			assertThat(narrow.widenTo(type)).isEqualTo(GioviTuples.of(
				69,
				true,
				"foo"));
		}

		@Test
		void testWidening_toTuple_with_widerFields() throws NonWideneable {
			var narrow = GioviTuples.of(
				new Natural(69),
				69,
				List.of(69, 420));
			var type = new GioviTypeTuple(new Tuple(
				new VarD<>(new GioviInt()),
				new VarD<>(new GioviReal()),
				new VarD<>(
					new GioviList(new GioviTypeReal()),
					new GioviList(new GioviTypeReal()))));
			assertThat(narrow.widenTo(type).value())
				.isEqualTo(new Tuple(
					VarD.of(69),
					VarD.of(69d),
					new VarD<>(GioviLists.of(69d, 420d), new GioviList(new GioviTypeReal()))));
		}

		@Test
		void testWidening_toTuple_with_moreFields() throws NonWideneable {
			var narrow = GioviTuples.of(
				new Natural(69),
				69,
				List.of(69, 420));
			var type = new GioviTypeTuple(new Tuple(
				new VarD<>(new GioviInt()),
				new VarD<>(new GioviReal()),
				new VarD<>(
					new GioviList(new GioviTypeReal()),
					new GioviList(new GioviTypeReal())),
				new VarD<>(new GioviBool())));
			assertThat(narrow.widenTo(type).value())
				.isEqualTo(new Tuple(
					VarD.of(69),
					VarD.of(69d),
					new VarD<>(GioviLists.of(69d, 420d), new GioviList(new GioviTypeReal())),
					VarD.of(false)));
		}

		@Test
		void widening_toTuple_with_narrowerFields_shouldThrow() throws NonWideneable {
			assertThatExceptionOfType(NonWideneable.class).isThrownBy(() -> GioviTuples.of(
				69,
				69d,
				List.of(69d, 420d))
				.widenTo(new GioviTypeTuple(new Tuple(
					new VarD<>(new GioviNatural()),
					new VarD<>(new GioviInt()),
					new VarD<>(new GioviList(new GioviTypeInt()))))));
		}

		@Test
		void testWidening_toRecord_with_sameFields() throws NonWideneable {
			var narrow = GioviTuples.of(
				new Natural(69),
				69,
				List.of(69, 420));
			var type = new GioviTypeRecord(Records.of(
				"n", new VarD<>(new GioviNatural()),
				"i", new VarD<>(new GioviInt()),
				"li", new VarD<>(new GioviList(new GioviTypeInt()))));
			assertThat(narrow.widenTo(type).value()).isEqualTo(Records.of(
				"n", new VarD<>(new GioviNatural(new Natural(69))),
				"i", new VarD<>(new GioviInt(69)),
				"li", new VarD<>(GioviLists.of(69, 420), new GioviList(new GioviTypeInt()))));
		}

		@Test
		void testWidening_toRecord_with_widerFields() throws NonWideneable {
			var narrow = GioviTuples.of(
				new Natural(69),
				69,
				List.of(69, 420));
			var signature = Records.of(
				"i", new VarD<>(new GioviInt()),
				"r", new VarD<>(new GioviReal()),
				"lr", new VarD<>(new GioviList(new GioviTypeReal())));
			var type = new GioviTypeRecord(signature);
			assertThat(narrow.widenTo(type)).isEqualTo(new GioviRecord(signature, Records.of(
				"i", new VarD<>(new GioviInt(69)),
				"r", new VarD<>(new GioviReal(69d)),
				"lr", new VarD<>(GioviLists.of(69d, 420d), new GioviList(new GioviTypeReal())))));
		}

		@Test
		void testWidening_toRecord_with_moreFields() throws NonWideneable {
			var narrow = GioviTuples.of(
				new Natural(69),
				69,
				List.of(69, 420));
			var signature = Records.of(
				"i", new VarD<>(new GioviInt()),
				"r", new VarD<>(new GioviReal()),
				"lr", new VarD<>(new GioviList(new GioviTypeReal())),
				"b", new VarD<>(new GioviBool()));
			var type = new GioviTypeRecord(signature);
			assertThat(narrow.widenTo(type)).isEqualTo(new GioviRecord(signature, Records.of(
				"i", new VarD<>(new GioviInt(69)),
				"r", new VarD<>(new GioviReal(69d)),
				"lr", new VarD<>(GioviLists.of(69d, 420d), new GioviList(new GioviTypeReal())),
				"b", new VarD<>(new GioviBool()))));
		}

		@Test
		void widening_toRecord_with_narrowerFields_shouldThrow() throws NonWideneable {
			assertThatExceptionOfType(NonWideneable.class).isThrownBy(() -> GioviTuples.of(
				69,
				69d,
				List.of(69d, 420d))
				.widenTo(new GioviTypeRecord(Records.of(
					"n", new VarD<>(new GioviNatural()),
					"i", new VarD<>(new GioviInt()),
					"li", new VarD<>(new GioviList(new GioviTypeInt()))))));
		}


		@Test
		void testWidening_toStruct_with_sameFields() throws NonWideneable {
			var narrow = GioviTuples.of(
				new Natural(69),
				69,
				List.of(69, 420));
			var signature = Structs.of(
				"Foo",
				"n", new VarD<>(new GioviNatural()),
				"i", new VarD<>(new GioviInt()),
				"li", new VarD<>(new GioviList(new GioviTypeInt())));
			var type = new GioviTypeStruct(signature);
			assertThat(narrow.widenTo(type)).isEqualTo(new GioviStruct(signature, Structs.of(
				"Foo",
				"n", new VarD<>(new GioviNatural(new Natural(69))),
				"i", new VarD<>(new GioviInt(69)),
				"li", new VarD<>(GioviLists.of(69, 420), new GioviList(new GioviTypeInt())))));
		}

		@Test
		void testWidening_toStruct_with_widerFields() throws NonWideneable {
			var narrow = GioviTuples.of(
				new Natural(69),
				69,
				List.of(69, 420));
			var signature = Structs.of(
				"Foo",
				"i", new VarD<>(new GioviInt()),
				"r", new VarD<>(new GioviReal()),
				"lr", new VarD<>(new GioviList(new GioviTypeReal())));
			var type = new GioviTypeStruct(signature);
			assertThat(narrow.widenTo(type)).isEqualTo(new GioviStruct(signature, Structs.of(
				"Foo",
				"i", new VarD<>(new GioviInt(69)),
				"r", new VarD<>(new GioviReal(69d)),
				"lr", new VarD<>(GioviLists.of(69d, 420d), new GioviList(new GioviTypeReal())))));
		}

		@Test
		void testWidening_toStruct_with_moreFields() throws NonWideneable {
			var narrow = GioviTuples.of(
				new Natural(69),
				69,
				List.of(69, 420));
			var signature = Structs.of(
				"Foo",
				"i", new VarD<>(new GioviInt()),
				"r", new VarD<>(new GioviReal()),
				"lr", new VarD<>(new GioviList(new GioviTypeReal())),
				"b", new VarD<>(new GioviBool()));
			var type = new GioviTypeStruct(signature);
			assertThat(narrow.widenTo(type)).isEqualTo(new GioviStruct(signature, Structs.of(
				"Foo",
				"i", new VarD<>(new GioviInt(69)),
				"r", new VarD<>(new GioviReal(69d)),
				"lr", new VarD<>(GioviLists.of(69d, 420d), new GioviList(new GioviTypeReal())),
				"b", new VarD<>(new GioviBool()))));
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
	class TestGioviRecord {

		@Test
		void testWidening_emptyRecord_to_emptyRecord() throws NonWideneable {
			assertThat(new GioviRecord().widenTo(new GioviTypeRecord(new Record())))
				.isEqualTo(new GioviRecord());
		}

		@Test
		void widening_toTuple_shouldThrow() throws NonWideneable {
			assertThatExceptionOfType(NonWideneable.class).isThrownBy(() -> GioviRecords.of(
				"n", new Natural(69),
				"i", 69,
				"li", List.of(69, 420))
				.widenTo(new GioviTypeTuple(new Tuple())));
		}

		@Test
		void testWidening_toRecord_with_sameFields() throws NonWideneable {
			var narrow = GioviRecords.of(
				"a", new Natural(69),
				"b", 69,
				"c", List.of(69, 420));
			var signature = Records.of(
				"a", new VarD<>(new GioviNatural()),
				"b", new VarD<>(new GioviInt()),
				"c", new VarD<>(new GioviList(new GioviTypeInt())));
			var type = new GioviTypeRecord(signature);
			assertThat(narrow.widenTo(type)).isEqualTo(new GioviRecord(signature, Records.of(
				"a", new VarD<>(new GioviNatural(new Natural(69))),
				"b", new VarD<>(new GioviInt(69)),
				"c", new VarD<>(GioviLists.of(69, 420), new GioviList(new GioviTypeInt())))));
		}

		@Test
		void widening_toRecord_with_differentNames_shouldThrow() {
			assertThatExceptionOfType(NonWideneable.class).isThrownBy(() -> GioviRecords.of(
				"a", new Natural(69),
				"b", 69,
				"c", List.of(69, 420))
				.widenTo(new GioviTypeRecord(Records.of(
					"x", new VarD<>(new GioviNatural()),
					"y", new VarD<>(new GioviInt()),
					"z", new VarD<>(new GioviList(new GioviTypeInt()))))));
		}

		@Test
		void testWidening_toRecord_with_moreFields() throws NonWideneable {
			var narrow = GioviRecords.of(
				"a", new Natural(69),
				"b", 69,
				"c", List.of(69, 420));
			var signature = Records.of(
				"a", new VarD<>(new GioviNatural()),
				"b", new VarD<>(new GioviInt()),
				"c", new VarD<>(new GioviList(new GioviTypeInt())),
				"d", new VarD<>(new GioviBool()));
			var type = new GioviTypeRecord(signature);
			assertThat(narrow.widenTo(type)).isEqualTo(new GioviRecord(signature, Records.of(
				"a", new VarD<>(new GioviNatural(new Natural(69))),
				"b", new VarD<>(new GioviInt(69)),
				"c", new VarD<>(GioviLists.of(69, 420), new GioviList(new GioviTypeInt())),
				"d", new VarD<>(new GioviBool()))));
		}

		@Test
		void testWidening_toStruct_with_sameFields() throws NonWideneable {
			var narrow = GioviRecords.of(
				"a", new Natural(69),
				"b", 69,
				"c", List.of(69, 420));
			var signature = Structs.of(
				"Foo",
				"a", new VarD<>(new GioviInt()),
				"b", new VarD<>(new GioviReal()),
				"c", new VarD<>(new GioviList(new GioviTypeReal())));
			var type = new GioviTypeStruct(signature);
			assertThat(narrow.widenTo(type)).isEqualTo(new GioviStruct(signature, Structs.of(
				"Foo",
				"a", new VarD<>(new GioviInt(69)),
				"b", new VarD<>(new GioviReal(69d)),
				"c", new VarD<>(GioviLists.of(69d, 420d), new GioviList(new GioviTypeReal())))));
		}

		@Test
		void widening_toStruct_with_sameFields_but_differentName_shouldThrow() {
			assertThatExceptionOfType(NonWideneable.class).isThrownBy(() -> GioviRecords.of(
				"a", new Natural(69),
				"b", 69,
				"c", List.of(69, 420))
				.widenTo(new GioviTypeStruct(Structs.ofNative(
					"Foo",
					"x", new Natural(0),
					"y", 0,
					"z", List.of(0)))));
		}

		@Test
		void testWidening_toStruct_with_moreFields() throws NonWideneable {
			var narrow = GioviRecords.of(
				"a", new Natural(69),
				"b", 69,
				"c", List.of(69, 420));
			var signature = Structs.of(
				"Foo",
				"a", new VarD<>(new GioviInt()),
				"b", new VarD<>(new GioviReal()),
				"c", new VarD<>(new GioviList(new GioviTypeReal())),
				"d", new VarD<>(new GioviBool()));
			var type = new GioviTypeRecord(signature);
			assertThat(narrow.widenTo(type)).isEqualTo(new GioviRecord(signature, Records.of(
				"a", new VarD<>(new GioviInt(69)),
				"b", new VarD<>(new GioviReal(69d)),
				"c", new VarD<>(GioviLists.of(69d, 420d), new GioviList(new GioviTypeReal())),
				"d", new VarD<>(new GioviBool()))));
		}

	}

	@Nested
	class TestGioviStruct {

		@Test
		void testWidening_emptyStruct_to_emptyStruct() throws NonWideneable {
			assertThat(GioviStructs.of("Foo")
				.widenTo(new GioviTypeStruct(new Struct("Foo"))))
				.isEqualTo(GioviStructs.of("Foo"));
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
		void widening_toRecord_shouldThrow() throws NonWideneable {
			assertThatExceptionOfType(NonWideneable.class).isThrownBy(() -> GioviStructs.ofNative(
				"Foo",
				"n", new Natural(69),
				"i", 69,
				"li", List.of(69, 420))
				.widenTo(new GioviTypeRecord(new Record())));
		}


		@Test
		void testWidening_toStruct_of_sameType() throws NonWideneable {
			var narrow = GioviStructs.ofNative(
				"Foo",
				"a", new Natural(69),
				"b", 69,
				"c", List.of(69, 420));
			var signature = Structs.of(
				"Foo",
				"a", new VarD<>(new GioviNatural()),
				"b", new VarD<>(new GioviInt()),
				"c", new VarD<>(new GioviList(new GioviTypeInt())));
			var type = new GioviTypeStruct(signature);
			assertThat(narrow.widenTo(type)).isEqualTo(new GioviStruct(signature, Structs.of(
				"Foo",
				"a", new VarD<>(new GioviNatural(new Natural(69))),
				"b", new VarD<>(new GioviInt(69)),
				"c", new VarD<>(GioviLists.of(69, 420), new GioviList(new GioviTypeInt())))));
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
