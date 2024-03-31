package giovi.lang;

import giovi.Function;
import giovi.GioviParserVisitResult;
import it.simonvic.giovi.lang.FunctionSignature;

public final class GioviTypeFunction extends GioviType<GioviFunction> {

	private final FunctionSignature signature;

	public GioviTypeFunction(FunctionSignature signature) {
		super(GioviFunction.class);
		this.signature = signature;
	}

	@Override
	public boolean canWidenTo(GioviType<?> to) {
		return this.equals(to);
	}

	@Override
	public GioviValue<?> spawn() {
		return new GioviFunction(new Function(signature.copy()));
	}

	@Override
	public GioviType<GioviFunction> copy() {
		return new GioviTypeFunction(signature.copy());
	}

	@Override
	public GioviParserVisitResult dot(GioviParserVisitResult right) {
		if (right instanceof GioviDefault) {
			return new GioviFunction();
		}
		return super.dot(right);
	}


}
