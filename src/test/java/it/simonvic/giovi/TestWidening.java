package it.simonvic.giovi;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import giovi.*;
import giovi.lang.*;
import it.simonvic.giovi.exceptions.NonWideneable;
import it.simonvic.giovi.lang.FormalParameter;
import it.simonvic.giovi.util.*;

public class TestWidening {

	@Nested
	class TestGioviNatural {

		@Test
		void testWideningNaturalToNatural() throws NonWideneable {
			assertThat(new GioviNatural(new Natural(69))
				.widenTo(GioviNatural.TYPE))
				.isEqualTo(new GioviNatural(new Natural(69)));
		}

		@Test
		void testWideningNaturalToInt() throws NonWideneable {
			assertThat(new GioviNatural(new Natural(69))
				.widenTo(GioviInt.TYPE))
				.isEqualTo(new GioviInt(69));
		}

		@Test
		void testWideningNaturalToReal() throws NonWideneable {
			assertThat(new GioviNatural(new Natural(69))
				.widenTo(GioviReal.TYPE))
				.isEqualTo(new GioviReal(69d));
		}

	}

	@Nested
	class TestGioviInt {

		@Test
		void testWideningIntToInt() throws NonWideneable {
			assertThat(new GioviInt(69)
				.widenTo(GioviInt.TYPE))
				.isEqualTo(new GioviInt(69));
		}

		@Test
		void testWideningIntToReal() throws NonWideneable {
			assertThat(new GioviInt(69)
				.widenTo(GioviReal.TYPE))
				.isEqualTo(new GioviReal(69d));
		}

	}

	@Nested
	class TestGioviReal {

		@Test
		void testWideningRealToReal() throws NonWideneable {
			assertThat(new GioviReal(69d)
				.widenTo(GioviReal.TYPE))
				.isEqualTo(new GioviReal(69d));
		}

	}

	@Nested
	class TestGioviText {

		@Test
		void testWideningTextToText() throws NonWideneable {
			assertThat(new GioviText("hello")
				.widenTo(GioviText.TYPE))
				.isEqualTo(new GioviText("hello"));
		}

	}

	@Nested
	class TestGioviBool {

		@Test
		void testWideningBoolToBool() throws NonWideneable {
			assertThat(new GioviBool(true)
				.widenTo(GioviBool.TYPE))
				.isEqualTo(new GioviBool(true));
		}

	}

	@Nested
	class TestGioviList {

		@Test
		void testWideningIntListToRealList() throws NonWideneable {
			assertThat(GioviLists.of(1, 2, 3)
				.widenTo(new GioviTypeList(GioviReal.TYPE)))
				.isEqualTo(GioviLists.of(1d, 2d, 3d));
		}

		@Test
		void wideningRealListToIntList_shouldThrow() {
			assertThatExceptionOfType(NonWideneable.class).isThrownBy(
				() -> GioviLists.of(1d, 2d, 3d)
					.widenTo(new GioviTypeList(GioviInt.TYPE)));
		}

		@Test
		void testWideningIntMatrixToRealMatrix() throws NonWideneable {
			assertThat(GioviLists.of(
				List.of(1, 2, 3),
				List.of(1, 2, 3))
				.widenTo(new GioviTypeList(new GioviTypeList(GioviReal.TYPE))))
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
					.widenTo(new GioviTypeList(new GioviTypeList(GioviInt.TYPE))));
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
				.widenTo(new GioviTypeMap(GioviText.TYPE, GioviReal.TYPE)))
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
					.widenTo(new GioviTypeMap(GioviInt.TYPE, GioviInt.TYPE)));
		}

	}

	// TODO(test): ...

	@Nested
	class TestGioviTuple {

		@Test
		void testWidening_emptyTuple_to_emptyTuple() throws NonWideneable {
			assertThat(new GioviTuple().widenTo(GioviTuples.typeOf()))
				.isEqualTo(new GioviTuple());
		}

		@Test
		void testWidening_toTuple_with_sameFields() throws NonWideneable {
			var narrow = GioviTuples.of(
				69,
				true,
				"foo");
			var type = GioviTuples.typeOf(
				GioviInt.TYPE,
				GioviBool.TYPE,
				GioviText.TYPE);
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
			var type = GioviTuples.typeOf(
				GioviInt.TYPE,
				GioviReal.TYPE,
				new GioviTypeList(GioviReal.TYPE));
			assertThat(narrow.widenTo(type).value())
				.isEqualTo(new Tuple(
					VarD.of(69),
					VarD.of(69d),
					new VarD<>(GioviLists.of(69d, 420d), new GioviList(GioviReal.TYPE))));
		}

		@Test
		void testWidening_toTuple_with_moreFields() throws NonWideneable {
			var narrow = GioviTuples.of(
				new Natural(69),
				69,
				List.of(69, 420));
			var type = GioviTuples.typeOf(
				GioviInt.TYPE,
				GioviReal.TYPE,
				new GioviTypeList(GioviReal.TYPE),
				GioviBool.TYPE);
			assertThat(narrow.widenTo(type).value())
				.isEqualTo(new Tuple(
					VarD.of(69),
					VarD.of(69d),
					new VarD<>(GioviLists.of(69d, 420d), new GioviList(GioviReal.TYPE)),
					VarD.of(false)));
		}

		@Test
		void widening_toTuple_with_narrowerFields_shouldThrow() throws NonWideneable {
			assertThatExceptionOfType(NonWideneable.class).isThrownBy(() -> GioviTuples.of(
				69,
				69d,
				List.of(69d, 420d))
				.widenTo(GioviTuples.typeOf(
					GioviNatural.TYPE,
					GioviInt.TYPE,
					new GioviTypeList(GioviInt.TYPE))));
		}

		@Test
		void testWidening_toRecord_with_sameFields() throws NonWideneable {
			var narrow = GioviTuples.of(
				new Natural(69),
				69,
				List.of(69, 420));
			var type = new GioviTypeRecord(GioviRecords.signatureOf(
				"n", GioviNatural.TYPE,
				"i", GioviInt.TYPE,
				"li", new GioviTypeList(GioviInt.TYPE)));
			assertThat(narrow.widenTo(type).value()).isEqualTo(Records.of(
				"n", new VarD<>(new GioviNatural(new Natural(69))),
				"i", new VarD<>(new GioviInt(69)),
				"li", new VarD<>(GioviLists.of(69, 420), new GioviList(GioviInt.TYPE))));
		}

		@Test
		void testWidening_toRecord_with_widerFields() throws NonWideneable {
			var narrow = GioviTuples.of(
				new Natural(69),
				69,
				List.of(69, 420));
			var type = new GioviTypeRecord(GioviRecords.signatureOf(
				"i", GioviInt.TYPE,
				"r", GioviReal.TYPE,
				"lr", new GioviTypeList(GioviReal.TYPE)));
			assertThat(narrow.widenTo(type)).isEqualTo(new GioviRecord(type, Records.of(
				"i", new VarD<>(new GioviInt(69)),
				"r", new VarD<>(new GioviReal(69d)),
				"lr", new VarD<>(GioviLists.of(69d, 420d), new GioviList(GioviReal.TYPE)))));
		}

		@Test
		void testWidening_toRecord_with_moreFields() throws NonWideneable {
			var narrow = GioviTuples.of(
				new Natural(69),
				69,
				List.of(69, 420));
			var type = new GioviTypeRecord(GioviRecords.signatureOf(
				"i", GioviInt.TYPE,
				"r", GioviReal.TYPE,
				"lr", new GioviTypeList(GioviReal.TYPE),
				"b", GioviBool.TYPE));
			assertThat(narrow.widenTo(type)).isEqualTo(new GioviRecord(type, Records.of(
				"i", new VarD<>(new GioviInt(69)),
				"r", new VarD<>(new GioviReal(69d)),
				"lr", new VarD<>(GioviLists.of(69d, 420d), new GioviList(GioviReal.TYPE)),
				"b", new VarD<>(new GioviBool()))));
		}

		@Test
		void widening_toRecord_with_narrowerFields_shouldThrow() throws NonWideneable {
			assertThatExceptionOfType(NonWideneable.class).isThrownBy(() -> GioviTuples.of(
				69,
				69d,
				List.of(69d, 420d))
				.widenTo(new GioviTypeRecord(GioviRecords.signatureOf(
					"n", GioviNatural.TYPE,
					"i", GioviInt.TYPE,
					"li", new GioviTypeList(GioviInt.TYPE)))));
		}


		@Test
		void testWidening_toStruct_with_sameFields() throws NonWideneable {
			var narrow = GioviTuples.of(
				new Natural(69),
				69,
				List.of(69, 420));
			var type = new GioviTypeStruct(GioviStructs.signatureOf(
				"Foo",
				"n", GioviNatural.TYPE,
				"i", GioviInt.TYPE,
				"li", new GioviTypeList(GioviInt.TYPE)));
			assertThat(narrow.widenTo(type)).isEqualTo(new GioviStruct(type, Structs.of(
				"Foo",
				"n", new VarD<>(new GioviNatural(new Natural(69))),
				"i", new VarD<>(new GioviInt(69)),
				"li", new VarD<>(GioviLists.of(69, 420), new GioviList(GioviInt.TYPE)))));
		}

		@Test
		void testWidening_toStruct_with_widerFields() throws NonWideneable {
			var narrow = GioviTuples.of(
				new Natural(69),
				69,
				List.of(69, 420));
			var type = new GioviTypeStruct(GioviStructs.signatureOf(
				"Foo",
				"i", GioviInt.TYPE,
				"r", GioviReal.TYPE,
				"lr", new GioviTypeList(GioviReal.TYPE)));
			assertThat(narrow.widenTo(type)).isEqualTo(new GioviStruct(type, Structs.of(
				"Foo",
				"i", new VarD<>(new GioviInt(69)),
				"r", new VarD<>(new GioviReal(69d)),
				"lr", new VarD<>(GioviLists.of(69d, 420d), new GioviList(GioviReal.TYPE)))));
		}

		@Test
		void testWidening_toStruct_with_moreFields() throws NonWideneable {
			var narrow = GioviTuples.of(
				new Natural(69),
				69,
				List.of(69, 420));
			var type = new GioviTypeStruct(GioviStructs.signatureOf(
				"Foo",
				"i", GioviInt.TYPE,
				"r", GioviReal.TYPE,
				"lr", new GioviTypeList(GioviReal.TYPE),
				"b", GioviBool.TYPE));
			assertThat(narrow.widenTo(type)).isEqualTo(new GioviStruct(type, Structs.of(
				"Foo",
				"i", new VarD<>(new GioviInt(69)),
				"r", new VarD<>(new GioviReal(69d)),
				"lr", new VarD<>(GioviLists.of(69d, 420d), new GioviList(GioviReal.TYPE)),
				"b", new VarD<>(new GioviBool()))));
		}

		@Test
		void widening_toStruct_with_narrowerFields_shouldThrow() throws NonWideneable {
			assertThatExceptionOfType(NonWideneable.class).isThrownBy(() -> GioviTuples.of(
				69,
				69d,
				List.of(69d, 420d))
				.widenTo(new GioviTypeStruct(GioviStructs.signatureOf(
					"Foo",
					"n", GioviNatural.TYPE,
					"i", GioviInt.TYPE,
					"li", new GioviTypeList(GioviInt.TYPE)))));
		}


	}

	@Nested
	class TestGioviRecord {

		@Test
		void testWidening_emptyRecord_to_emptyRecord() throws NonWideneable {
			assertThat(new GioviRecord().widenTo(new GioviTypeRecord(new RecordSignature())))
				.isEqualTo(new GioviRecord());
		}

		@Test
		void widening_toTuple_shouldThrow() throws NonWideneable {
			assertThatExceptionOfType(NonWideneable.class).isThrownBy(() -> GioviRecords.of(
				"n", new Natural(69),
				"i", 69,
				"li", List.of(69, 420))
				.widenTo(new GioviTypeTuple(new TupleSignature())));
		}

		@Test
		void testWidening_toRecord_with_sameFields() throws NonWideneable {
			var narrow = GioviRecords.of(
				"a", new Natural(69),
				"b", 69,
				"c", List.of(69, 420));
			var type = new GioviTypeRecord(GioviRecords.signatureOf(
				"a", GioviNatural.TYPE,
				"b", GioviInt.TYPE,
				"c", new GioviTypeList(GioviInt.TYPE)));
			assertThat(narrow.widenTo(type)).isEqualTo(new GioviRecord(type, Records.of(
				"a", new VarD<>(new GioviNatural(new Natural(69))),
				"b", new VarD<>(new GioviInt(69)),
				"c", new VarD<>(GioviLists.of(69, 420), new GioviList(GioviInt.TYPE)))));
		}

		@Test
		void widening_toRecord_with_differentNames_shouldThrow() {
			assertThatExceptionOfType(NonWideneable.class).isThrownBy(() -> GioviRecords.of(
				"a", new Natural(69),
				"b", 69,
				"c", List.of(69, 420))
				.widenTo(new GioviTypeRecord(GioviRecords.signatureOf(
					"x", GioviNatural.TYPE,
					"y", GioviInt.TYPE,
					"z", new GioviTypeList(GioviInt.TYPE)))));
		}

		@Test
		void testWidening_toRecord_with_moreFields() throws NonWideneable {
			var narrow = GioviRecords.of(
				"a", new Natural(69),
				"b", 69,
				"c", List.of(69, 420));
			var type = new GioviTypeRecord(GioviRecords.signatureOf(
				"a", GioviNatural.TYPE,
				"b", GioviInt.TYPE,
				"c", new GioviTypeList(GioviInt.TYPE),
				"d", GioviBool.TYPE));
			assertThat(narrow.widenTo(type)).isEqualTo(new GioviRecord(type, Records.of(
				"a", new VarD<>(new GioviNatural(new Natural(69))),
				"b", new VarD<>(new GioviInt(69)),
				"c", new VarD<>(GioviLists.of(69, 420), new GioviList(GioviInt.TYPE)),
				"d", new VarD<>(new GioviBool()))));
		}

		@Test
		void testWidening_toStruct_with_sameFields() throws NonWideneable {
			var narrow = GioviRecords.of(
				"a", new Natural(69),
				"b", 69,
				"c", List.of(69, 420));
			var type = new GioviTypeStruct(GioviStructs.signatureOf(
				"Foo",
				"a", GioviInt.TYPE,
				"b", GioviReal.TYPE,
				"c", new GioviTypeList(GioviReal.TYPE)));
			assertThat(narrow.widenTo(type)).isEqualTo(new GioviStruct(type, Structs.of(
				"Foo",
				"a", new VarD<>(new GioviInt(69)),
				"b", new VarD<>(new GioviReal(69d)),
				"c", new VarD<>(GioviLists.of(69d, 420d), new GioviList(GioviReal.TYPE)))));
		}

		@Test
		void widening_toStruct_with_sameFields_but_differentName_shouldThrow() {
			assertThatExceptionOfType(NonWideneable.class).isThrownBy(() -> GioviRecords.of(
				"a", new Natural(69),
				"b", 69,
				"c", List.of(69, 420))
				.widenTo(new GioviTypeStruct(GioviStructs.signatureOf(
					"Foo",
					"x", GioviNatural.TYPE,
					"y", GioviInt.TYPE,
					"z", new GioviTypeList(GioviInt.TYPE)))));
		}

		@Test
		void testWidening_toStruct_with_moreFields() throws NonWideneable {
			var narrow = GioviRecords.of(
				"a", new Natural(69),
				"b", 69,
				"c", List.of(69, 420));
			var type = new GioviTypeStruct(GioviStructs.signatureOf(
				"Foo",
				"a", GioviInt.TYPE,
				"b", GioviReal.TYPE,
				"c", new GioviTypeList(GioviReal.TYPE),
				"d", GioviBool.TYPE));
			assertThat(narrow.widenTo(type)).isEqualTo(new GioviStruct(type, Structs.of(
				"Foo",
				"a", new VarD<>(new GioviInt(69)),
				"b", new VarD<>(new GioviReal(69d)),
				"c", new VarD<>(GioviLists.of(69d, 420d), new GioviList(GioviReal.TYPE)),
				"d", new VarD<>(new GioviBool()))));
		}

	}

	@Nested
	class TestGioviStruct {

		@Test
		void testWidening_emptyStruct_to_emptyStruct() throws NonWideneable {
			assertThat(GioviStructs.of("Foo")
				.widenTo(new GioviTypeStruct(new StructSignature("Foo"))))
				.isEqualTo(GioviStructs.of("Foo"));
		}

		@Test
		void widening_toTuple_shouldThrow() throws NonWideneable {
			assertThatExceptionOfType(NonWideneable.class).isThrownBy(() -> GioviStructs.ofNative(
				"Foo",
				"n", new Natural(69),
				"i", 69,
				"li", List.of(69, 420))
				.widenTo(new GioviTypeTuple(new TupleSignature())));
		}

		@Test
		void widening_toRecord_shouldThrow() throws NonWideneable {
			assertThatExceptionOfType(NonWideneable.class).isThrownBy(() -> GioviStructs.ofNative(
				"Foo",
				"n", new Natural(69),
				"i", 69,
				"li", List.of(69, 420))
				.widenTo(new GioviTypeRecord(new RecordSignature())));
		}


		@Test
		void testWidening_toStruct_of_sameType() throws NonWideneable {
			var narrow = GioviStructs.ofNative(
				"Foo",
				"a", new Natural(69),
				"b", 69,
				"c", List.of(69, 420));
			var type = new GioviTypeStruct(GioviStructs.signatureOf(
				"Foo",
				"a", GioviNatural.TYPE,
				"b", GioviInt.TYPE,
				"c", new GioviTypeList(GioviInt.TYPE)));
			assertThat(narrow.widenTo(type)).isEqualTo(new GioviStruct(type, Structs.of(
				"Foo",
				"a", new VarD<>(new GioviNatural(new Natural(69))),
				"b", new VarD<>(new GioviInt(69)),
				"c", new VarD<>(GioviLists.of(69, 420), new GioviList(GioviInt.TYPE)))));
		}

		// TODO: clarify requirements about widening between different struct types

		@Test
		void testWidening_toStruct_of_differentType() throws NonWideneable {
			assertThatExceptionOfType(NonWideneable.class).isThrownBy(() -> GioviStructs.ofNative(
				"Foo",
				"a", new Natural(69),
				"b", 69,
				"c", List.of(69, 420))
				.widenTo(new GioviTypeStruct(GioviStructs.signatureOf(
					"Bar",
					"a", GioviNatural.TYPE,
					"b", GioviInt.TYPE,
					"c", new GioviTypeList(GioviInt.TYPE)))));
		}


	}

	@Nested
	class TestGioviFunction {

		@Test
		void testWidening_toEqualGFunction_with_noParams_and_noReturnType() throws NonWideneable {
			assertThat(GioviFunctions.of("foo")
				.widenTo(GioviFunctions.typeOf("foo")))
				.isEqualTo(GioviFunctions.of("foo"));
		}

		@Test
		void testWidening_toEqualGFunction_with_params_and_returnType() throws NonWideneable {
			assertThat(GioviFunctions
				.of(
					GioviNatural.TYPE,
					"foo",
					new FormalParameter("a", GioviInt.TYPE),
					new FormalParameter("b", GioviReal.TYPE))
				.widenTo(GioviFunctions.typeOf(
					GioviNatural.TYPE,
					"foo",
					new FormalParameter("a", GioviInt.TYPE),
					new FormalParameter("b", GioviReal.TYPE))))
				.isEqualTo(GioviFunctions.of(
					GioviNatural.TYPE,
					"foo",
					new FormalParameter("a", GioviInt.TYPE),
					new FormalParameter("b", GioviReal.TYPE)));
		}

		@Test
		void testWidening_toGFunction_with_widerParams_and_widerReturnType() throws NonWideneable {
			assertThat(GioviFunctions
				.of(
					GioviNatural.TYPE,
					"foo",
					new FormalParameter("a", GioviInt.TYPE),
					new FormalParameter("b", GioviReal.TYPE))
				.widenTo(GioviFunctions.typeOf(
					GioviInt.TYPE,
					"foo",
					new FormalParameter("a", GioviReal.TYPE),
					new FormalParameter("b", GioviReal.TYPE))))
				.isEqualTo(GioviFunctions.of(
					GioviInt.TYPE,
					"foo",
					new FormalParameter("a", GioviReal.TYPE),
					new FormalParameter("b", GioviReal.TYPE)));
		}

		@Test
		void testWidening_toGFunction_with_moreParams_and_returnType() throws NonWideneable {
			assertThat(GioviFunctions
				.of(
					"foo",
					new FormalParameter("a", GioviInt.TYPE))
				.widenTo(GioviFunctions.typeOf(
					GioviNatural.TYPE,
					"foo",
					new FormalParameter("a", GioviInt.TYPE),
					new FormalParameter("b", GioviReal.TYPE))))
				.isEqualTo(GioviFunctions.of(
					GioviNatural.TYPE,
					"foo",
					new FormalParameter("a", GioviInt.TYPE),
					new FormalParameter("b", GioviReal.TYPE)));
		}

		@Test
		void testWidening_toGFunction_with_moreAndWiderParams_and_widerReturnType()
			throws NonWideneable {
			assertThat(GioviFunctions
				.of(
					"foo",
					new FormalParameter("a", GioviInt.TYPE))
				.widenTo(GioviFunctions.typeOf(
					GioviNatural.TYPE,
					"foo",
					new FormalParameter("a", GioviReal.TYPE),
					new FormalParameter("b", GioviReal.TYPE))))
				.isEqualTo(GioviFunctions.of(
					GioviNatural.TYPE,
					"foo",
					new FormalParameter("a", GioviReal.TYPE),
					new FormalParameter("b", GioviReal.TYPE)));
		}

		@Test
		void widening_toGFunction_with_lessParams_shouldThrow() {
			assertThatExceptionOfType(NonWideneable.class).isThrownBy(() -> GioviFunctions
				.of(
					"foo",
					new FormalParameter("a", GioviInt.TYPE),
					new FormalParameter("b", GioviReal.TYPE))
				.widenTo(GioviFunctions.typeOf(
					"foo",
					new FormalParameter("a", GioviInt.TYPE))));
		}

		@Test
		void widening_toGFunction_with_narrowerParams_shouldThrow() {
			assertThatExceptionOfType(NonWideneable.class).isThrownBy(() -> GioviFunctions
				.of(
					"foo",
					new FormalParameter("a", GioviInt.TYPE))
				.widenTo(GioviFunctions.typeOf(
					"foo",
					new FormalParameter("a", GioviNatural.TYPE))));
		}

		@Test
		void widening_toGFunction_with_narrowerReturnType_shouldThrow() {
			assertThatExceptionOfType(NonWideneable.class).isThrownBy(() -> GioviFunctions
				.of(
					GioviInt.TYPE,
					"foo")
				.widenTo(GioviFunctions.typeOf(
					GioviNatural.TYPE,
					"foo")));
		}

		@Test
		void widening_toGFunction_with_noReturnType_shouldThrow() {
			assertThatExceptionOfType(NonWideneable.class).isThrownBy(() -> GioviFunctions
				.of(
					GioviInt.TYPE,
					"foo")
				.widenTo(GioviFunctions.typeOf(
					"foo")));
		}

	}

	@Nested
	class TestGioviType {
	}

	@Nested
	class TestGioviVoid {
	}

}
