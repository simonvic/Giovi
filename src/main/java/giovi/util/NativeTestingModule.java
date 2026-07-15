package giovi.util;

import giovi.StructSignature;
import giovi.lang.*;
import giovi.util.Color.Rgb;
import it.simonvic.giovi.lang.annotation.Native;
import it.simonvic.giovi.lang.annotation.Native.*;
import it.simonvic.giovi.lang.annotation.Native.Func.Param.Label;
import it.simonvic.giovi.lang.annotation.Native.Module;
import it.simonvic.giovi.lang.annotation.Native.Struct.Field;
import it.simonvic.giovi.lang.annotation.Native.TypeRecord.Member;
import it.simonvic.giovi.util.*;

@Module
public class NativeTestingModule {

	@Const
	public static final GioviText TEST_CONST = new GioviText("test const");

	@Func
	public static void minimal() {
	}

	@Func
	public static GioviText onlyReturn() {
		return new GioviText("onlyReturn");
	}

	@Func
	public static GioviText oneParamWithReturn(GioviText t) {
		return new GioviText("param1");
	}

	@Func
	public static void manyParams(GioviText t, GioviInt i, GioviReal r) {
	}

	@Func
	public static void paramWithLabel(@Label("label") GioviText t) {
	}

	@Func
	public static GioviInt complex(
		GioviInt integer,
		@Type(nativ = Rgb.class) GioviStruct rgb,
		@TypeList(@Type(GioviTypeInt.class)) GioviList list,
		@TypeMap(
			key = @Type(GioviTypeText.class),
			val = @Type(nativ = Rgb.class)
		) GioviMap map,
		@TypeType(@Type) GioviType<?> type,
		@TypeTuple({ @Type(GioviTypeReal.class), @Type(GioviTypeBool.class) }) GioviTuple tuple,
		@TypeRecord({
			@Member(name = "foo", type = @Type(GioviTypeReal.class)),
			@Member(name = "bar", type = @Type(GioviTypeBool.class)),
		}) GioviRecord record
	) {
		System.out.println(integer);
		System.out.println(rgb);
		System.out.println(list);
		System.out.println(map);
		System.out.println(type);
		System.out.println(tuple);
		System.out.println(record);
		return new GioviInt(69);
	}

	@Func
	public static @TypeList(@Type(GioviTypeInt.class)) GioviList getListOfPrimitive() {
		return GioviLists.of(1, 2, 3);
	}

	@Func
	@TypeMap(
		key = @Type(GioviTypeText.class),
		val = @Type(GioviTypeInt.class)
	)
	public static GioviMap getMapOfPrimitive() {
		return GioviMaps.of(
			"a", 1,
			"b", 2,
			"c", 3
		);
	}

	@Func
	@TypeTuple({ @Type(GioviTypeReal.class), @Type(GioviTypeBool.class) })
	public static GioviTuple getTuple() {
		return GioviTuples.of(1.0, true);
	}

	@Func
	@TypeRecord({
		@Member(name = "foo", type = @Type(GioviTypeReal.class)),
		@Member(name = "bar", type = @Type(GioviTypeBool.class))
	})
	public static GioviRecord getRecord() {
		return GioviRecords.of(
			"foo", 1.0,
			"bar", true
		);
	}

	@Func
	public static @TypeType(@Type) GioviType<?> getTypeAny() {
		return GioviType.ANY;
	}

	@Func
	public static @TypeType(@Type(GioviTypeInt.class)) GioviType<?> getTypeInt() {
		return GioviType.INT;
	}

	@Struct
	public static class TestStruct {

		@Field
		public static final GioviInt i = new GioviInt();

		@Field
		public static final GioviText t = new GioviText("some default");

		@Field
		public static final GioviList l = new GioviList(GioviType.LIST_OF_INT);

		@Field
		public static final GioviMap m = new GioviMap(
			new GioviTypeMap(GioviType.TEXT, GioviType.INT)
		);

		@Const
		public static final GioviInt SOME_CONST = new GioviInt(69);

		@Func
		public static GioviText someFunc() {
			return new GioviText("hello from someFunc");
		}

		@Native.Struct.Signature
		public static final StructSignature SIGNATURE = Natives
			.structSignatureFrom(TestStruct.class);

	}

	@Module
	public static class TestSubModule {

		@Native.Struct(inherit = TestStruct.class)
		public static class TestStructExtends {

			@Field
			public static final GioviInt i2 = new GioviInt(420);

			@Native.Struct.Signature
			public static final StructSignature SIGNATURE = Natives
				.structSignatureFrom(TestStructExtends.class);

		}

	}

	@Struct
	public static class Node {

		@Field
		public static GioviText data = new GioviText();

		@Field
		public static GioviSelf next = new GioviSelf();

		@Func
		public static GioviText toText(@Self GioviStruct self) {
			System.out.println("hello from print");
			System.out.println(self);
			return new GioviText(self.toString());
		}

		@Func
		public static @Self GioviStruct newSelf() {
			return new GioviStruct(new GioviTypeStruct(SIGNATURE), SIGNATURE.spawn());
		}

		@Func
		public static @Self GioviStruct capitalize(@Self GioviStruct self) {
			var dataField = self.value().getField("data", GioviText.class).orElseThrow();
			dataField.setValue(new GioviText(dataField.unwrap().value().toUpperCase()));
			return self;
		}

		@Native.Struct.Signature
		public static final StructSignature SIGNATURE = Natives.structSignatureFrom(Node.class);

	}


}

