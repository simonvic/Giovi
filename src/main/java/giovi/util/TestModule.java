package giovi.util;

import giovi.StructSignature;
import giovi.lang.*;
import giovi.util.Color.Rgb;
import it.simonvic.giovi.lang.annotation.Native.*;
import it.simonvic.giovi.lang.annotation.Native.Func.Param.Label;
import it.simonvic.giovi.lang.annotation.Native.Module;
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

	// @Func
	// public static @TypeMap(
	// 	key = @Type(GioviTypeText.class),
	// 	val = @Type(GioviTypeInt.class)
	// ) GioviMap getMap() {
	// 	return GioviMaps.of("a", 1, "b", 2, "c", 3);
	// }

	@Func
	public static @Type(nativ = Rgb.class) GioviStruct getRgb() {
		return new GioviTypeStruct(Natives.structSignatureOf(Rgb.class)).spawn();
	}

	@Func
	public static @TypeList(@Type(nativ = Rgb.class)) GioviList getListOfRgb() {
		StructSignature signature = Natives.structSignatureOf(Rgb.class);
		var t = new GioviTypeList(new GioviTypeStruct(signature), 3);
		return t.spawn();
	}

}

