package giovi.util;

import giovi.lang.GioviReal;
import it.simonvic.giovi.lang.annotation.Native.Const;
import it.simonvic.giovi.lang.annotation.Native.Func;
import it.simonvic.giovi.lang.annotation.Native.Module;

@Module
public class Math {

	@Const
	public static final GioviReal E = new GioviReal(java.lang.Math.E);

	@Const
	public static final GioviReal PI = new GioviReal(java.lang.Math.PI);

	@Const
	public static final GioviReal TAU = new GioviReal(java.lang.Math.TAU);

	@Func
	public static GioviReal floor(GioviReal x) {
		return new GioviReal(java.lang.Math.floor(x.value()));
	}

	@Func
	public static GioviReal ceil(GioviReal x) {
		return new GioviReal(java.lang.Math.ceil(x.value()));
	}

	@Func
	public static GioviReal abs(GioviReal x) {
		return new GioviReal(java.lang.Math.abs(x.value()));
	}

}
