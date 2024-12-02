package giovi.util;

import java.util.Random;

import giovi.lang.*;
import it.simonvic.giovi.lang.annotation.Native.Func;
import it.simonvic.giovi.lang.annotation.Native.Struct;
import it.simonvic.giovi.lang.annotation.Native.Type;

@Struct(typename = "Random")
public class GRandom implements GioviObject {

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

}
