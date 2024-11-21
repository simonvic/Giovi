package giovi;

import giovi.lang.GioviObject;
import giovi.lang.GioviReal;
import it.simonvic.giovi.VarD;
import it.simonvic.giovi.lang.annotation.Native.Struct;
import it.simonvic.giovi.lang.annotation.Native.Struct.Field;

@Struct
public class Math implements GioviObject {


	@Field
	public static VarD<GioviReal> pi = new VarD<>(new GioviReal(java.lang.Math.PI));

	// TODO: wait for struct constants and functions

	/*
	@Const
	public static final GioviReal E = new GioviReal(java.lang.Math.E);

	@Const
	public static final GioviReal PI = new GioviReal(java.lang.Math.PI);

	@Const
	public static final GioviReal TAU = new GioviReal(java.lang.Math.TAU);

	@Func
	public @Type(GioviTypeReal.class) GioviReal floor(
		@Param(name = "x", type = @Type(GioviTypeReal.class)) GioviReal x) {
		return new GioviReal(java.lang.Math.floor(x.value()));
	}
	*/
}
