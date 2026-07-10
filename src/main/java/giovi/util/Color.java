package giovi.util;

import giovi.StructSignature;
import giovi.lang.GioviInt;
import giovi.lang.GioviStruct;
import giovi.lang.GioviTypeStruct;
import it.simonvic.giovi.lang.annotation.Native;
import it.simonvic.giovi.lang.annotation.Native.Const;
import it.simonvic.giovi.lang.annotation.Native.Func;
import it.simonvic.giovi.lang.annotation.Native.Module;
import it.simonvic.giovi.lang.annotation.Native.Struct.Field;
import it.simonvic.giovi.lang.annotation.Native.Type;
import it.simonvic.giovi.util.Natives;

@Module
public class Color {

	@Const
	public static final GioviInt WHITE = new GioviInt(0xFFFFFF);

	@Func
	public static @Type(nativ = Rgb.class) GioviStruct rgb(
		GioviInt r,
		GioviInt g,
		GioviInt b
	) {
		var struct = new GioviTypeStruct(Natives.structSignatureOf(Rgb.class)).spawn();
		struct.value().getField("r", GioviInt.class).orElseThrow().setValue(r);
		struct.value().getField("g", GioviInt.class).orElseThrow().setValue(g);
		struct.value().getField("b", GioviInt.class).orElseThrow().setValue(b);
		return struct;
	}

	@Func
	public static @Type(nativ = Rgba.class) GioviStruct rgba(
		GioviInt r,
		GioviInt g,
		GioviInt b,
		GioviInt a
	) {
		var struct = new GioviTypeStruct(Natives.structSignatureOf(Rgba.class)).spawn();
		struct.value().getField("r", GioviInt.class).orElseThrow().setValue(r);
		struct.value().getField("g", GioviInt.class).orElseThrow().setValue(g);
		struct.value().getField("b", GioviInt.class).orElseThrow().setValue(b);
		struct.value().getField("a", GioviInt.class).orElseThrow().setValue(a);
		return struct;
	}

	/**
	 * Rgb values represented in 0-255 range
	 */
	@Native.Struct
	public static class Rgb {

		@Field
		public static final GioviInt r = new GioviInt(255);

		@Field
		public static final GioviInt g = new GioviInt(255);

		@Field
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
		public static final StructSignature SIGNATURE = Natives.structSignatureFrom(Rgb.class);

	}


	@Native.Struct(inherit = Rgb.class)
	public static class Rgba {

		@Field
		public static final GioviInt a = new GioviInt(255);

		@Native.Struct.Signature
		public static final StructSignature SIGNATURE = Natives.structSignatureFrom(Rgba.class);

	}


}
