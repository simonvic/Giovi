package giovi;

import it.simonvic.giovi.lang.FunctionSignature;

public sealed interface Function
	extends GioviParserVisitResult
	permits GFunction, JFunction {

	public static final Function EMPTY = new GFunction(FunctionSignature.EMPTY, null);

	FunctionSignature signature();

	Function copy();

}
