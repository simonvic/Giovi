package giovi;

import it.simonvic.giovi.antlr.GioviParser.BlockContext;
import it.simonvic.giovi.lang.FunctionSignature;

public record Function(
	FunctionSignature signature,
	BlockContext bodyCtx)
	implements GioviParserVisitResult {

	public static final Function EMPTY = new Function(FunctionSignature.EMPTY, null);

	public Function copy() {
		return new Function(signature.copy(), bodyCtx);
	}

	@Override
	public String toString() {
		String body = bodyCtx != null ? "{" + bodyCtx + "}" : "";
		return signature + body;
	}

}
