package it.simonvic.giovi;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import giovi.Natural;
import giovi.lang.*;
import it.simonvic.giovi.exceptions.NonWideneable;
import it.simonvic.giovi.util.GioviLists;
import it.simonvic.giovi.util.GioviMaps;

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
		void testWideningRealToText() throws NonWideneable {
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
			assertThat(GioviLists.of(1, 2, 3)
				.widenTo(new GioviTypeList(new GioviTypeReal())))
					.isEqualTo(GioviLists.of(1d, 2d, 3d));
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
	}

	@Nested
	class TestGioviStruct {
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
