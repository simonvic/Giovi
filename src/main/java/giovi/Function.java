package giovi;

import it.simonvic.giovi.lang.FunctionSignature;

public sealed interface Function permits GFunction, JFunction {

	public static final Function EMPTY = new GFunction(FunctionSignature.EMPTY, null);

	FunctionSignature signature();

	Function copy();

}
