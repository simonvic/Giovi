package it.simonvic.giovi;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.List;

import org.junit.jupiter.api.Disabled;
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
			assertThat(new GioviNatural(new Natural(69)).widenTo(GioviType.NAT))
				.isEqualTo(new GioviNatural(new Natural(69)));
		}

		@Test
		void testWideningNaturalToInt() throws NonWideneable {
			assertThat(new GioviNatural(new Natural(69)).widenTo(GioviType.INT))
				.isEqualTo(new GioviInt(69));
		}

		@Test
		void testWideningNaturalToReal() throws NonWideneable {
			assertThat(new GioviNatural(new Natural(69)).widenTo(GioviType.REAL))
				.isEqualTo(new GioviReal(69d));
		}

	}

	@Nested
	class TestGioviInt {

		@Test
		void testWideningIntToInt() throws NonWideneable {
			assertThat(new GioviInt(69).widenTo(GioviType.INT))
				.isEqualTo(new GioviInt(69));
		}

		@Test
		void testWideningIntToReal() throws NonWideneable {
			assertThat(new GioviInt(69).widenTo(GioviType.REAL))
				.isEqualTo(new GioviReal(69d));
		}

	}

	@Nested
	class TestGioviReal {

		@Test
		void testWideningRealToReal() throws NonWideneable {
			assertThat(new GioviReal(69d).widenTo(GioviType.REAL))
				.isEqualTo(new GioviReal(69d));
		}

	}

	@Nested
	class TestGioviText {

		@Test
		void testWideningTextToText() throws NonWideneable {
			assertThat(new GioviText("hello").widenTo(GioviType.TEXT))
				.isEqualTo(new GioviText("hello"));
		}

	}

	@Nested
	class TestGioviBool {

		@Test
		void testWideningBoolToBool() throws NonWideneable {
			assertThat(GioviBool.TRUE.widenTo(GioviType.BOOL))
				.isEqualTo(GioviBool.TRUE);
		}

	}

	@Nested
	class TestGioviList {

		@Test
		void testWideningIntListToRealList() throws NonWideneable {
			assertThat(GioviLists.of(1, 2, 3).widenTo(GioviType.LIST_OF_REAL))
				.isEqualTo(GioviLists.of(1d, 2d, 3d));
		}

		@Test
		void wideningRealListToIntList_shouldThrow() {
			assertThatExceptionOfType(NonWideneable.class).isThrownBy(
				() -> GioviLists.of(1d, 2d, 3d)
					.widenTo(GioviType.LIST_OF_INT)
			);
		}

		@Test
		void testWideningIntMatrixToRealMatrix() throws NonWideneable {
			assertThat(
				GioviLists.of(
					List.of(1, 2, 3),
					List.of(1, 2, 3)
				).widenTo(new GioviTypeList(GioviType.LIST_OF_REAL))
			).isEqualTo(
				GioviLists.of(
					List.of(1d, 2d, 3d),
					List.of(1d, 2d, 3d)
				)
			);
		}

		@Test
		void wideningRealMatrixToIntMatrix_shouldThrow() {
			assertThatExceptionOfType(NonWideneable.class).isThrownBy(
				() -> GioviLists.of(
					List.of(1d, 2d, 3d),
					List.of(1d, 2d, 3d)
				).widenTo(new GioviTypeList(GioviType.LIST_OF_INT))
			);
		}

	}

	@Nested
	class TestGioviMap {

		@Test
		void testWidening_TextIntMap_to_TextRealMap() throws NonWideneable {
			assertThat(
				GioviMaps.of(
					"a", 1,
					"b", 2,
					"c", 3
				).widenTo(new GioviTypeMap(GioviType.TEXT, GioviType.REAL))
			).isEqualTo(
				GioviMaps.of(
					"a", 1d,
					"b", 2d,
					"c", 3d
				)
			);
		}

		@Test
		void widening_TextRealMap_to_TextIntMap_shouldThrow() {
			assertThatExceptionOfType(NonWideneable.class).isThrownBy(
				() -> GioviMaps.of(
					"a", 1d,
					"b", 2d,
					"c", 3d
				).widenTo(new GioviTypeMap(GioviType.INT, GioviType.INT))
			);
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
				"foo"
			);
			var type = GioviTuples.typeOf(
				GioviType.INT,
				GioviType.BOOL,
				GioviType.TEXT
			);
			assertThat(narrow.widenTo(type))
				.isEqualTo(
					GioviTuples.of(
						69,
						true,
						"foo"
					)
				);
		}

		@Test
		void testWidening_toTuple_with_widerFields() throws NonWideneable {
			var narrow = GioviTuples.of(
				new Natural(69),
				69,
				List.of(69, 420)
			);
			var type = GioviTuples.typeOf(
				GioviType.INT,
				GioviType.REAL,
				GioviType.LIST_OF_REAL
			);
			assertThat(narrow.widenTo(type).value())
				.isEqualTo(
					new Tuple(
						VarD.of(69),
						VarD.of(69d),
						new VarD<>(GioviLists.of(69d, 420d), new GioviList(GioviType.LIST_OF_REAL))
					)
				);
		}

		@Test
		void testWidening_toTuple_with_moreFields() throws NonWideneable {
			var narrow = GioviTuples.of(
				new Natural(69),
				69,
				List.of(69, 420)
			);
			var type = GioviTuples.typeOf(
				GioviType.INT,
				GioviType.REAL,
				GioviType.LIST_OF_REAL,
				GioviType.BOOL
			);
			assertThat(narrow.widenTo(type).value())
				.isEqualTo(
					new Tuple(
						VarD.of(69),
						VarD.of(69d),
						new VarD<>(GioviLists.of(69d, 420d), new GioviList(GioviType.LIST_OF_REAL)),
						VarD.of(false)
					)
				);
		}

		@Test
		void widening_toTuple_with_narrowerFields_shouldThrow() throws NonWideneable {
			assertThatExceptionOfType(NonWideneable.class).isThrownBy(
				() -> GioviTuples.of(
					69,
					69d,
					List.of(69d, 420d)
				).widenTo(
					GioviTuples.typeOf(
						GioviType.NAT,
						GioviType.INT,
						GioviType.LIST_OF_INT
					)
				)
			);
		}

		@Test
		void testWidening_toRecord_with_sameFields() throws NonWideneable {
			var narrow = GioviTuples.of(
				new Natural(69),
				69,
				List.of(69, 420)
			);
			var type = new GioviTypeRecord(
				GioviRecords.signatureOf(
					"n", GioviType.NAT,
					"i", GioviType.INT,
					"li", GioviType.LIST_OF_INT
				)
			);
			assertThat(narrow.widenTo(type).value()).isEqualTo(
				Records.of(
					"n", new VarD<>(new GioviNatural(new Natural(69))),
					"i", new VarD<>(new GioviInt(69)),
					"li", new VarD<>(GioviLists.of(69, 420), new GioviList(GioviType.LIST_OF_INT))
				)
			);
		}

		@Test
		void testWidening_toRecord_with_widerFields() throws NonWideneable {
			var narrow = GioviTuples.of(
				new Natural(69),
				69,
				List.of(69, 420)
			);
			var type = new GioviTypeRecord(
				GioviRecords.signatureOf(
					"i", GioviType.INT,
					"r", GioviType.REAL,
					"lr", GioviType.LIST_OF_REAL
				)
			);
			assertThat(narrow.widenTo(type)).isEqualTo(
				new GioviRecord(
					type, Records.of(
						"i", new VarD<>(new GioviInt(69)),
						"r", new VarD<>(new GioviReal(69d)),
						"lr",
						new VarD<>(GioviLists.of(69d, 420d), new GioviList(GioviType.LIST_OF_REAL))
					)
				)
			);
		}

		@Test
		void testWidening_toRecord_with_moreFields() throws NonWideneable {
			var narrow = GioviTuples.of(
				new Natural(69),
				69,
				List.of(69, 420)
			);
			var type = new GioviTypeRecord(
				GioviRecords.signatureOf(
					"i", GioviType.INT,
					"r", GioviType.REAL,
					"lr", GioviType.LIST_OF_REAL,
					"b", GioviType.BOOL
				)
			);
			assertThat(narrow.widenTo(type)).isEqualTo(
				new GioviRecord(
					type, Records.of(
						"i", new VarD<>(new GioviInt(69)),
						"r", new VarD<>(new GioviReal(69d)),
						"lr",
						new VarD<>(GioviLists.of(69d, 420d), new GioviList(GioviType.LIST_OF_REAL)),
						"b", new VarD<>(GioviBool.FALSE)
					)
				)
			);
		}

		@Test
		void widening_toRecord_with_narrowerFields_shouldThrow() throws NonWideneable {
			assertThatExceptionOfType(NonWideneable.class).isThrownBy(
				() -> GioviTuples.of(
					69,
					69d,
					List.of(69d, 420d)
				).widenTo(
					new GioviTypeRecord(
						GioviRecords.signatureOf(
							"n", GioviType.NAT,
							"i", GioviType.INT,
							"li", GioviType.LIST_OF_INT
						)
					)
				)
			);
		}


		@Test
		void testWidening_toStruct_with_sameFields() throws NonWideneable {
			var narrow = GioviTuples.of(
				new Natural(69),
				69,
				List.of(69, 420)
			);
			var type = new GioviTypeStruct(
				GioviStructs.signatureOf(
					"Foo",
					"n", GioviType.NAT,
					"i", GioviType.INT,
					"li", GioviType.LIST_OF_INT
				)
			);
			assertThat(narrow.widenTo(type)).isEqualTo(
				new GioviStruct(
					type, Structs.of(
						"Foo",
						"n", new VarD<>(new GioviNatural(new Natural(69))),
						"i", new VarD<>(new GioviInt(69)),
						"li",
						new VarD<>(GioviLists.of(69, 420), new GioviList(GioviType.LIST_OF_INT))
					)
				)
			);
		}

		@Test
		void testWidening_toStruct_with_widerFields() throws NonWideneable {
			var narrow = GioviTuples.of(
				new Natural(69),
				69,
				List.of(69, 420)
			);
			var type = new GioviTypeStruct(
				GioviStructs.signatureOf(
					"Foo",
					"i", GioviType.INT,
					"r", GioviType.REAL,
					"lr", GioviType.LIST_OF_REAL
				)
			);
			assertThat(narrow.widenTo(type))
				.isEqualTo(
					new GioviStruct(
						type,
						Structs.of(
							"Foo",
							"i", new VarD<>(new GioviInt(69)),
							"r", new VarD<>(new GioviReal(69d)),
							"lr",
							new VarD<>(
								GioviLists.of(69d, 420d),
								new GioviList(GioviType.LIST_OF_REAL)
							)
						)
					)
				);
		}

		@Test
		void testWidening_toStruct_with_moreFields() throws NonWideneable {
			var narrow = GioviTuples.of(
				new Natural(69),
				69,
				List.of(69, 420)
			);
			var type = new GioviTypeStruct(
				GioviStructs.signatureOf(
					"Foo",
					"i", GioviType.INT,
					"r", GioviType.REAL,
					"lr", GioviType.LIST_OF_REAL,
					"b", GioviType.BOOL
				)
			);
			assertThat(narrow.widenTo(type))
				.isEqualTo(
					new GioviStruct(
						type, Structs.of(
							"Foo",
							"i", new VarD<>(new GioviInt(69)),
							"r", new VarD<>(new GioviReal(69d)),
							"lr", new VarD<>(
								GioviLists.of(69d, 420d),
								new GioviList(GioviType.LIST_OF_REAL)
							),
							"b", new VarD<>(GioviBool.FALSE)
						)
					)
				);
		}

		@Test
		void widening_toStruct_with_narrowerFields_shouldThrow() throws NonWideneable {
			assertThatExceptionOfType(NonWideneable.class).isThrownBy(
				() -> GioviTuples.of(
					69,
					69d,
					List.of(69d, 420d)
				).widenTo(
					new GioviTypeStruct(
						GioviStructs.signatureOf(
							"Foo",
							"n", GioviType.NAT,
							"i", GioviType.INT,
							"li", GioviType.LIST_OF_INT
						)
					)
				)
			);
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
			assertThatExceptionOfType(NonWideneable.class).isThrownBy(
				() -> GioviRecords.of(
					"n", new Natural(69),
					"i", 69,
					"li", List.of(69, 420)
				).widenTo(new GioviTypeTuple(new TupleSignature()))
			);
		}

		@Test
		void testWidening_toRecord_with_sameFields() throws NonWideneable {
			var narrow = GioviRecords.of(
				"a", new Natural(69),
				"b", 69,
				"c", List.of(69, 420)
			);
			var type = new GioviTypeRecord(
				GioviRecords.signatureOf(
					"a", GioviType.NAT,
					"b", GioviType.INT,
					"c", GioviType.LIST_OF_INT
				)
			);
			assertThat(narrow.widenTo(type)).isEqualTo(
				new GioviRecord(
					type, Records.of(
						"a", new VarD<>(new GioviNatural(new Natural(69))),
						"b", new VarD<>(new GioviInt(69)),
						"c", new VarD<>(
							GioviLists.of(69, 420),
							new GioviList(GioviType.LIST_OF_INT)
						)
					)
				)
			);
		}

		@Test
		void widening_toRecord_with_differentNames_shouldThrow() {
			assertThatExceptionOfType(NonWideneable.class).isThrownBy(
				() -> GioviRecords.of(
					"a", new Natural(69),
					"b", 69,
					"c", List.of(69, 420)
				).widenTo(
					new GioviTypeRecord(
						GioviRecords.signatureOf(
							"x", GioviType.NAT,
							"y", GioviType.INT,
							"z", GioviType.LIST_OF_INT
						)
					)
				)
			);
		}

		@Test
		void testWidening_toRecord_with_moreFields() throws NonWideneable {
			var narrow = GioviRecords.of(
				"a", new Natural(69),
				"b", 69,
				"c", List.of(69, 420)
			);
			var type = new GioviTypeRecord(
				GioviRecords.signatureOf(
					"a", GioviType.NAT,
					"b", GioviType.INT,
					"c", GioviType.LIST_OF_INT,
					"d", GioviType.BOOL
				)
			);
			assertThat(narrow.widenTo(type))
				.isEqualTo(
					new GioviRecord(
						type,
						Records.of(
							"a", new VarD<>(new GioviNatural(new Natural(69))),
							"b", new VarD<>(new GioviInt(69)),
							"c", new VarD<>(
								GioviLists.of(69, 420),
								new GioviList(GioviType.LIST_OF_INT)
							),
							"d", new VarD<>(GioviBool.FALSE)
						)
					)
				);
		}

		@Test
		void testWidening_toStruct_with_sameFields() throws NonWideneable {
			var narrow = GioviRecords.of(
				"a", new Natural(69),
				"b", 69,
				"c", List.of(69, 420)
			);
			var type = new GioviTypeStruct(
				GioviStructs.signatureOf(
					"Foo",
					"a", GioviType.INT,
					"b", GioviType.REAL,
					"c", GioviType.LIST_OF_REAL
				)
			);
			assertThat(narrow.widenTo(type)).isEqualTo(
				new GioviStruct(
					type,
					Structs.of(
						"Foo",
						"a", new VarD<>(new GioviInt(69)),
						"b", new VarD<>(new GioviReal(69d)),
						"c",
						new VarD<>(GioviLists.of(69d, 420d), new GioviList(GioviType.LIST_OF_REAL))
					)
				)
			);
		}

		@Test
		void widening_toStruct_with_sameFields_but_differentName_shouldThrow() {
			assertThatExceptionOfType(NonWideneable.class).isThrownBy(
				() -> GioviRecords.of(
					"a", new Natural(69),
					"b", 69,
					"c", List.of(69, 420)
				).widenTo(
					new GioviTypeStruct(
						GioviStructs.signatureOf(
							"Foo",
							"x", GioviType.NAT,
							"y", GioviType.INT,
							"z", GioviType.LIST_OF_INT
						)
					)
				)
			);
		}

		@Test
		void testWidening_toStruct_with_moreFields() throws NonWideneable {
			var narrow = GioviRecords.of(
				"a", new Natural(69),
				"b", 69,
				"c", List.of(69, 420)
			);
			var type = new GioviTypeStruct(
				GioviStructs.signatureOf(
					"Foo",
					"a", GioviType.INT,
					"b", GioviType.REAL,
					"c", GioviType.LIST_OF_REAL,
					"d", GioviType.BOOL
				)
			);
			assertThat(narrow.widenTo(type))
				.isEqualTo(
					new GioviStruct(
						type,
						Structs.of(
							"Foo",
							"a", new VarD<>(new GioviInt(69)),
							"b", new VarD<>(new GioviReal(69d)),
							"c", new VarD<>(
								GioviLists.of(69d, 420d),
								new GioviList(GioviType.LIST_OF_REAL)
							),
							"d", new VarD<>(GioviBool.FALSE)
						)
					)
				);
		}

		@Disabled
		@Test
		void testWidening_with_disjointRecord() throws NonWideneable {
			var narrow = GioviRecords.of(
				"a", new Natural(69),
				"b", List.of(69, 420)
			);
			var type = new GioviTypeRecord(
				GioviRecords.signatureOf(
					"a", GioviType.INT,
					"c", GioviType.REAL,
					"d", GioviType.BOOL
				)
			);
			assertThat(narrow.widenTo(type))
				.isEqualTo(
					new GioviRecord(
						type,
						Records.of(
							"a", new VarD<>(new GioviInt(69)),
							"b", new VarD<>(
								GioviLists.of(69d, 420d),
								new GioviList(GioviType.LIST_OF_INT)
							),
							"c", new VarD<>(new GioviReal()),
							"d", new VarD<>(GioviBool.FALSE)
						)
					)
				);
		}

		@Disabled
		@Test
		void testWidening_with_fullDisjointRecord() throws NonWideneable {
			var narrow = GioviRecords.of(
				"a", new Natural(69),
				"b", List.of(69, 420)
			);
			var type = new GioviTypeRecord(
				GioviRecords.signatureOf(
					"c", GioviType.REAL,
					"d", GioviType.BOOL
				)
			);
			assertThat(narrow.widenTo(type)).isEqualTo(
				new GioviRecord(
					type,
					Records.of(
						"a", new VarD<>(new GioviNatural(new Natural(69))),
						"b", new VarD<>(
							GioviLists.of(69d, 420d),
							new GioviList(GioviType.LIST_OF_INT)
						),
						"c", new VarD<>(new GioviReal()),
						"d", new VarD<>(GioviBool.FALSE)
					)
				)
			);
		}

	}

	@Nested
	class TestGioviStruct {

		@Test
		void testWidening_emptyStruct_to_emptyStruct() throws NonWideneable {
			assertThat(
				GioviStructs.of("Foo")
					.widenTo(new GioviTypeStruct(new StructSignature("Foo")))
			).isEqualTo(GioviStructs.of("Foo"));
		}

		@Test
		void widening_toTuple_shouldThrow() throws NonWideneable {
			assertThatExceptionOfType(NonWideneable.class).isThrownBy(
				() -> GioviStructs.ofNative(
					"Foo",
					"n", new Natural(69),
					"i", 69,
					"li", List.of(69, 420)
				).widenTo(new GioviTypeTuple(new TupleSignature()))
			);
		}

		@Test
		void widening_toRecord_shouldThrow() throws NonWideneable {
			assertThatExceptionOfType(NonWideneable.class).isThrownBy(
				() -> GioviStructs.ofNative(
					"Foo",
					"n", new Natural(69),
					"i", 69,
					"li", List.of(69, 420)
				).widenTo(new GioviTypeRecord(new RecordSignature()))
			);
		}


		@Test
		void testWidening_toStruct_of_sameType() throws NonWideneable {
			var narrow = GioviStructs.ofNative(
				"Foo",
				"a", new Natural(69),
				"b", 69,
				"c", List.of(69, 420)
			);
			var type = new GioviTypeStruct(
				GioviStructs.signatureOf(
					"Foo",
					"a", GioviType.NAT,
					"b", GioviType.INT,
					"c", GioviType.LIST_OF_INT
				)
			);
			assertThat(narrow.widenTo(type)).isEqualTo(
				new GioviStruct(
					type,
					Structs.of(
						"Foo",
						"a", new VarD<>(new GioviNatural(new Natural(69))),
						"b", new VarD<>(new GioviInt(69)),
						"c", new VarD<>(
							GioviLists.of(69, 420),
							new GioviList(GioviType.LIST_OF_INT)
						)
					)
				)
			);
		}

		// TODO: clarify requirements about widening between different struct types

		@Test
		void testWidening_toStruct_of_differentType() throws NonWideneable {
			assertThatExceptionOfType(NonWideneable.class).isThrownBy(
				() -> GioviStructs.ofNative(
					"Foo",
					"a", new Natural(69),
					"b", 69,
					"c", List.of(69, 420)
				).widenTo(
					new GioviTypeStruct(
						GioviStructs.signatureOf(
							"Bar",
							"a", GioviType.NAT,
							"b", GioviType.INT,
							"c", GioviType.LIST_OF_INT
						)
					)
				)
			);
		}


	}

	@Nested
	class TestGioviFunction {

		@Test
		void testWidening_toEqualGFunction_with_noParams_and_noReturnType() throws NonWideneable {
			assertThat(
				GioviFunctions.of("foo")
					.widenTo(GioviFunctions.typeOf("foo"))
			).isEqualTo(GioviFunctions.of("foo"));
		}

		@Test
		void testWidening_toEqualGFunction_with_params_and_returnType() throws NonWideneable {
			assertThat(
				GioviFunctions.of(
					GioviType.NAT,
					"foo",
					new FormalParameter("a", GioviType.INT),
					new FormalParameter("b", GioviType.REAL)
				).widenTo(
					GioviFunctions.typeOf(
						GioviType.NAT,
						"foo",
						new FormalParameter("a", GioviType.INT),
						new FormalParameter("b", GioviType.REAL)
					)
				)
			).isEqualTo(
				GioviFunctions.of(
					GioviType.NAT,
					"foo",
					new FormalParameter("a", GioviType.INT),
					new FormalParameter("b", GioviType.REAL)
				)
			);
		}

		@Test
		void testWidening_toGFunction_with_widerParams_and_widerReturnType() throws NonWideneable {
			assertThat(
				GioviFunctions.of(
					GioviType.NAT,
					"foo",
					new FormalParameter("a", GioviType.INT),
					new FormalParameter("b", GioviType.REAL)
				).widenTo(
					GioviFunctions.typeOf(
						GioviType.INT,
						"foo",
						new FormalParameter("a", GioviType.REAL),
						new FormalParameter("b", GioviType.REAL)
					)
				)
			).isEqualTo(
				GioviFunctions.of(
					GioviType.INT,
					"foo",
					new FormalParameter("a", GioviType.REAL),
					new FormalParameter("b", GioviType.REAL)
				)
			);
		}

		@Test
		void testWidening_toGFunction_with_moreParams_and_returnType() throws NonWideneable {
			assertThat(
				GioviFunctions.of(
					"foo",
					new FormalParameter("a", GioviType.INT)
				).widenTo(
					GioviFunctions.typeOf(
						GioviType.NAT,
						"foo",
						new FormalParameter("a", GioviType.INT),
						new FormalParameter("b", GioviType.REAL)
					)
				)
			).isEqualTo(
				GioviFunctions.of(
					GioviType.NAT,
					"foo",
					new FormalParameter("a", GioviType.INT),
					new FormalParameter("b", GioviType.REAL)
				)
			);
		}

		@Test
		void testWidening_toGFunction_with_moreAndWiderParams_and_widerReturnType()
			throws NonWideneable {
			assertThat(
				GioviFunctions.of(
					"foo",
					new FormalParameter("a", GioviType.INT)
				).widenTo(
					GioviFunctions.typeOf(
						GioviType.NAT,
						"foo",
						new FormalParameter("a", GioviType.REAL),
						new FormalParameter("b", GioviType.REAL)
					)
				)
			).isEqualTo(
				GioviFunctions.of(
					GioviType.NAT,
					"foo",
					new FormalParameter("a", GioviType.REAL),
					new FormalParameter("b", GioviType.REAL)
				)
			);
		}

		@Test
		void widening_toGFunction_with_lessParams_shouldThrow() {
			assertThatExceptionOfType(NonWideneable.class).isThrownBy(
				() -> GioviFunctions.of(
					"foo",
					new FormalParameter("a", GioviType.INT),
					new FormalParameter("b", GioviType.REAL)
				).widenTo(
					GioviFunctions.typeOf(
						"foo",
						new FormalParameter("a", GioviType.INT)
					)
				)
			);
		}

		@Test
		void widening_toGFunction_with_narrowerParams_shouldThrow() {
			assertThatExceptionOfType(NonWideneable.class).isThrownBy(
				() -> GioviFunctions.of(
					"foo",
					new FormalParameter("a", GioviType.INT)
				).widenTo(
					GioviFunctions.typeOf(
						"foo",
						new FormalParameter("a", GioviType.NAT)
					)
				)
			);
		}

		@Test
		void widening_toGFunction_with_narrowerReturnType_shouldThrow() {
			assertThatExceptionOfType(NonWideneable.class).isThrownBy(
				() -> GioviFunctions.of(
					GioviType.INT,
					"foo"
				).widenTo(
					GioviFunctions.typeOf(
						GioviType.NAT,
						"foo"
					)
				)
			);
		}

		@Test
		void widening_toGFunction_with_noReturnType_shouldThrow() {
			assertThatExceptionOfType(NonWideneable.class).isThrownBy(
				() -> GioviFunctions.of(
					GioviType.INT,
					"foo"
				).widenTo(
					GioviFunctions.typeOf(
						"foo"
					)
				)
			);
		}

	}

	@Nested
	class TestGioviType {
	}

	@Nested
	class TestGioviVoid {
	}

}
