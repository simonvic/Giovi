package giovi.lang;

import giovi.Function;
import giovi.GioviParserVisitResult;
import it.simonvic.giovi.lang.FunctionSignature;

public record GioviTypeFunction(FunctionSignature signature) implements GioviType<GioviFunction> {

	public FunctionSignature getSignature() {
		return signature;
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
	public boolean canWidenTo(GioviType<?> to) {
		return to instanceof GioviTypeFunction f && this.signature.equals(f.signature);
	}

	@Override
	public GioviFunction spawn() {
		return new GioviFunction(new Function(signature.copy()));
	}

	@Override
	public GioviFunction spawn(GioviFunction gioviValue) {
		return new GioviFunction(gioviValue.value(), gioviValue.value().copy());
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
		return GioviType.super.dot(right);
	}

}
