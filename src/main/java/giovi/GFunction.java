package giovi;

import it.simonvic.giovi.antlr.GioviParser.BlockContext;
import it.simonvic.giovi.lang.FunctionSignature;

public record GFunction(
	FunctionSignature signature,
	BlockContext bodyCtx
) implements Function {

	public GFunction(FunctionSignature signature) {
		this(signature, null);
	}

	@Override
	public GFunction copy() {
		return this;
	}

	@Override
	public String toString() {
		String body = bodyCtx != null ? " {" + bodyCtx + "}" : "";
		return signature + body;
	}

}
