package giovi.lang;

import giovi.GFunction;
import giovi.GioviParserVisitResult;
import it.simonvic.giovi.lang.FunctionSignature;

public record GioviTypeFunction(FunctionSignature signature) implements GioviType<GioviFunction> {

	public FunctionSignature getSignature() {
		return signature;
	}

	@Override
	public String toString() {
		return signature.toString();
	}

	@Override
	public Class<? extends GioviFunction> value() {
		return GioviFunction.class;
	}

	@Override
	public Class<? extends GioviFunction> defaultValue() {
		return GioviFunction.class;
	}

	@Override
	public boolean isWider(GioviType<?> type) {
		return type instanceof GioviTypeFunction f && this.signature.equals(f.signature);
	}

	@Override
	public GioviFunction spawn() {
		return new GioviFunction(new GFunction(signature.copy()));
	}

	@Override
	public GioviTypeFunction copy() {
		return new GioviTypeFunction(signature.copy());
	}

	@Override
	public GioviParserVisitResult dot(GioviParserVisitResult right) {
		if (right instanceof GioviDefault) {
			return new GioviFunction();
		}
		return GioviType.super.dot(right);
	}

}
