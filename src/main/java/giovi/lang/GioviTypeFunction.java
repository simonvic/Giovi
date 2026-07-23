package giovi.lang;

import giovi.Function;
import giovi.GFunction;
import giovi.JFunction;
import it.simonvic.giovi.exceptions.NonWideneable;
import it.simonvic.giovi.lang.FunctionSignature;
import it.simonvic.giovi.lang.annotation.Native.Describes;

@Describes(GioviFunction.class)
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
	public boolean isWidenableTo(GioviType<?> type) {
		return switch (type) {
			case GioviTypeFunction(var signature) -> signature.canWiden(this.signature);
			default -> GioviType.super.isWidenableTo(type);
		};
	}

	@Override
	public GioviFunction widen(GioviValue<?> value) throws NonWideneable {
		return switch (value) {

			case GioviFunction(Function f) when f.signature().equals(this.signature)
				-> new GioviFunction(f.copy());

			case GioviFunction(GFunction(var signature, var bodyCtx))
				-> new GioviFunction(new GFunction(this.signature.widen(signature), bodyCtx));

			// TODO: how to handle widening with native functions?
			// case GioviFunction(JFunction(var signature, var method))
			// 	-> new GioviFunction(new JFunction(this.signature.widen(signature), method));

			default -> GioviType.super.widen(value);
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

}
