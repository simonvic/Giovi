package giovi;

import giovi.lang.*;
import it.simonvic.giovi.lang.annotation.Native.Func;
import it.simonvic.giovi.lang.annotation.Native.Struct;
import it.simonvic.giovi.lang.annotation.Native.Type;

@Struct
public class Random implements GioviObject {

	@Func
	public static @Type(GioviTypeInt.class) GioviInt integer() {
		return new GioviInt(new java.util.Random().nextInt());
	}

	@Func
	public static @Type(GioviTypeReal.class) GioviReal real() {
		return new GioviReal(new java.util.Random().nextDouble());
	}

	@Func
	public static @Type(GioviTypeBool.class) GioviBool bool() {
		return new GioviBool(new java.util.Random().nextBoolean());
	}

}
