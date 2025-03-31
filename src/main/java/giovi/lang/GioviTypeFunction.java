package giovi.lang;

import giovi.Function;
import giovi.GFunction;
import giovi.GioviParserVisitResult;
import it.simonvic.giovi.exceptions.NonWideneable;
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
	public boolean isWidenableTo(GioviType<?> type) {
		return type instanceof GioviTypeFunction f && f.signature.canWiden(this.signature);
	}

	@Override
	public GioviFunction widen(GioviValue<?> value) throws NonWideneable {
		return switch (value) {

			case GioviFunction(Function f) when f.signature().equals(this.signature)
				-> new GioviFunction(f.copy());

			case GioviFunction(GFunction(var signature, var bodyCtx))
				-> new GioviFunction(new GFunction(this.signature.widen(signature), bodyCtx));

			// TODO: how to handle widening with native functions?
			// case GioviFunction(JFunction(var method, var signature))
			// 	-> new GioviFunction(new JFunction(method, this.signature.widen(signature)));

			default -> throw new NonWideneable(value, this);
		};
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
