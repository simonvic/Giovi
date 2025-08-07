package giovi.util;

import giovi.StructSignature;
import giovi.lang.*;
import giovi.util.Color.Rgb;
import it.simonvic.giovi.lang.annotation.Native;
import it.simonvic.giovi.lang.annotation.Native.*;
import it.simonvic.giovi.lang.annotation.Native.Func.Param.Label;
import it.simonvic.giovi.lang.annotation.Native.Module;
import it.simonvic.giovi.lang.annotation.Native.Struct.Field;
import it.simonvic.giovi.util.Natives;

@Module
public class TestModule {

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
		GioviInt i,
		@Type(nativ = Rgb.class) GioviStruct s,
		@TypeList(@Type(GioviTypeInt.class)) GioviList l,
		@TypeMap(
			key = @Type(GioviTypeText.class),
			val = @Type(nativ = Rgb.class)
		) GioviMap m
	) {
		System.out.println(i);
		System.out.println(s);
		System.out.println(l);
		System.out.println(m);
		return new GioviInt(69);
	}

	// TODO: can't repeat Type
	// @Func
	// public static @TypeMap(
	// 	key = @Type(GioviTypeText.class),
	// 	val = @Type(GioviTypeInt.class)
	// ) GioviMap getMap() {
	// 	return GioviMaps.of("a", 1, "b", 2, "c", 3);
	// }

	@Struct
	public static class TestStruct {

		@Field
		public static final GioviInt i = new GioviInt();

		@Field
		public static final GioviText t = new GioviText("some default");

		@Field
		public static final GioviList l = new GioviList(new GioviTypeInt());

		@Field
		public static final GioviMap m = new GioviMap(new GioviTypeMap(
			new GioviTypeText(),
			new GioviTypeInt()));

		@Const
		public static final GioviInt SOME_CONST = new GioviInt(69);

		@Func
		public static GioviText someFunc() {
			return new GioviText("hello from someFunc");
		}

		@Native.Struct.Signature
		private static final StructSignature _SIGNATURE = Natives
			.structSignatureFrom(TestStruct.class);

	}

	@Module
	public static class TestSubModule {

		@Native.Struct(inherit = TestStruct.class)
		public static class TestStructExtends {

			@Field
			public static final GioviInt i2 = new GioviInt(420);

			@Native.Struct.Signature
			private static final StructSignature _SIGNATURE = Natives
				.structSignatureFrom(TestStructExtends.class);

		}

	}


}

