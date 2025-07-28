package giovi.util;

import giovi.lang.*;
import it.simonvic.giovi.lang.annotation.Native.Func;
import it.simonvic.giovi.lang.annotation.Native.Module;
import it.simonvic.giovi.lang.annotation.Native.Type;

@Module
public class Random {

	private static java.util.Random random = new java.util.Random();

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

	@Func
	public static @Type(GioviTypeText.class) GioviText text() {
		return new GioviText(random.ints('a', 'z' + 1)
			.filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
			.limit(8)
			.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
			.toString());
	}

}
