package giovi.util;

import giovi.Struct;
import giovi.StructSignature;
import giovi.lang.GioviInt;
import giovi.lang.GioviStruct;
import giovi.lang.GioviTypeInt;
import giovi.util.Color.Foo.Rgba;
import it.simonvic.giovi.lang.annotation.Native;
import it.simonvic.giovi.lang.annotation.Native.Const;
import it.simonvic.giovi.lang.annotation.Native.Func;
import it.simonvic.giovi.lang.annotation.Native.Func.Param;
import it.simonvic.giovi.lang.annotation.Native.Module;
import it.simonvic.giovi.lang.annotation.Native.Struct.Field;
import it.simonvic.giovi.lang.annotation.Native.Type;
import it.simonvic.giovi.lang.annotation.Native.TypeNative;
import it.simonvic.giovi.util.Natives;

@Module
public class Color {

	@Const
	public static final @Type(GioviTypeInt.class) GioviInt WHITE = new GioviInt(0xFFFFFF);

	@Func
	public static void foo() {
		System.out.println("foo");
	}

	@Func
	public static @TypeNative(Rgb.class) GioviStruct rgb(
		@Param("r") @Type(GioviTypeInt.class) GioviInt r,
		@Param("g") @Type(GioviTypeInt.class) GioviInt g,
		@Param("b") @Type(GioviTypeInt.class) GioviInt b
	) {
		StructSignature signature = Natives.structSignatureOf(Rgb.class);
		Struct value = signature.spawn();
		// TODO: use safe getField
		value.getField("r").orElseThrow().setValueWithRedirection(r);
		value.getField("g").orElseThrow().setValueWithRedirection(g);
		value.getField("b").orElseThrow().setValueWithRedirection(b);
		return new GioviStruct(signature, value);
	}

	@Func
	public static @TypeNative(Rgba.class) GioviStruct rgba(
		@Param("r") @Type(GioviTypeInt.class) GioviInt r,
		@Param("g") @Type(GioviTypeInt.class) GioviInt g,
		@Param("b") @Type(GioviTypeInt.class) GioviInt b,
		@Param("a") @Type(GioviTypeInt.class) GioviInt a
	) {
		StructSignature signature = Natives.structSignatureOf(Rgba.class);
		Struct value = signature.spawn();
		// TODO: use safe getField
		value.getField("r").orElseThrow().setValueWithRedirection(r);
		value.getField("g").orElseThrow().setValueWithRedirection(g);
		value.getField("b").orElseThrow().setValueWithRedirection(b);
		value.getField("a").orElseThrow().setValueWithRedirection(a);
		return new GioviStruct(signature, value);
	}

	/**
	 * Rgb values represented in 0-255 range
	 */
	@Native.Struct
	public static class Rgb {

		@Field
		@Type(GioviTypeInt.class)
		public static final GioviInt r = new GioviInt(255);

		@Field
		@Type(GioviTypeInt.class)
		public static final GioviInt g = new GioviInt(255);

		@Field
		@Type(GioviTypeInt.class)
		public static final GioviInt b = new GioviInt(255);

		// @Const
		// @TypeNative(Rgb.class)
		// public static final GioviStruct WHITE = new GioviStruct(Rgb.SIGNATURE, Rgb.SIGNATURE.spawn());

		// @Func
		// public static @Self GioviStruct brigthen(
		// 	@Self GioviStruct self
		// ) {
		// 	return self;
		// }

		// @Func
		// public static @Type(GioviTypeInt.class) GioviInt toInt(
		// 	@Self GioviStruct self
		// ) {
		// }

		// @Func
		// public static @TypeNative(Rgb01.class) GioviStruct to01(
		// 	@Self GioviStruct self
		// ) {
		// }

		@Native.Struct.Signature
		private static final StructSignature SIGNATURE = Natives.structSignatureFrom(Rgb.class);

	}

	@Module
	public static class Foo {

		@Native.Struct
		public static class Rgba extends Rgb {

			@Field
			@Type(GioviTypeInt.class)
			public static final GioviInt a = new GioviInt(255);

		}

	}


}
