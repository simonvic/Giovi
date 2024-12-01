package giovi;

import giovi.lang.GioviObject;
import giovi.lang.GioviReal;
import giovi.lang.GioviTypeReal;
import it.simonvic.giovi.lang.annotation.Native.Const;
import it.simonvic.giovi.lang.annotation.Native.Func;
import it.simonvic.giovi.lang.annotation.Native.Func.Param;
import it.simonvic.giovi.lang.annotation.Native.Struct;
import it.simonvic.giovi.lang.annotation.Native.Type;

@Struct
public class Math implements GioviObject {

	@Const
	public static final GioviReal E = new GioviReal(java.lang.Math.E);

	@Const
	public static final GioviReal PI = new GioviReal(java.lang.Math.PI);

	@Const
	public static final GioviReal TAU = new GioviReal(java.lang.Math.TAU);

	@Func
	public static @Type(GioviTypeReal.class) GioviReal floor(
		@Param(name = "x", type = @Type(GioviTypeReal.class)) GioviReal x) {
		return new GioviReal(java.lang.Math.floor(x.value()));
	}

}
