package giovi.util;

import java.util.Random;

import giovi.Struct;
import giovi.lang.*;
import it.simonvic.giovi.lang.annotation.Native;
import it.simonvic.giovi.lang.annotation.Native.Func;
import it.simonvic.giovi.lang.annotation.Native.Type;
import it.simonvic.giovi.util.Natives;

@Native.Struct(typename = "Random")
public class GRandom {

	private static Random random = new Random();

	@Func
	public static @Type(GioviTypeInt.class) GioviInt integer() {
		return new GioviInt(random.nextInt());
	}

	@Func
	public static @Type(GioviTypeReal.class) GioviReal real() {
		return new GioviReal(random.nextDouble());
	}

	@Func
	public static @Type(GioviTypeBool.class) GioviBool bool() {
		return new GioviBool(random.nextBoolean());
	}

	@Native.Struct.Signature
	public static final Struct SIGNATURE = Natives.structSignatureFrom(GRandom.class);

}
