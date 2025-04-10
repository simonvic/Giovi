package giovi.lang;

import giovi.Function;
import giovi.GFunction;
import giovi.GioviParserVisitResult;
import giovi.JFunction;
import it.simonvic.giovi.exceptions.NonWideneable;

public record GioviFunction(Function value) implements GioviValue<Function> {

	public GioviFunction() {
		this(Function.EMPTY);
	}

	@Override
	public Function defaultValue() {
		return new GFunction(value.signature());
	}

	@Override
	public String toString() {
		return "function " + value.toString();
	}

	@Override
	public GioviTypeFunction getGioviType() {
		return new GioviTypeFunction(value.signature().copy());
	}

	@Override
	public GioviFunction copy() {
		return new GioviFunction(value.copy());
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// OPERATIONS

	//////////////////////////////////////////////////////////////////////////////////////////////// cast

	//////////////////////////////////////////////////////////////////////////////////////////////// redirection

	@Override
	public GioviParserVisitResult redirection(GioviParserVisitResult content) {
		if (this.value instanceof JFunction) {
			return GioviValue.super.redirection(content);
		}
		return switch (content) {
			case GioviDefault _ -> new GioviFunction(defaultValue());

			case GioviFunction(Function f) when f.signature().equals(this.value.signature())
				-> new GioviFunction(f.copy());

			// TODO: this might all be wrong! Consider the domain of the functions

			case GioviFunction(GFunction(var signature, var bodyCtx)) -> {
				try {
					yield new GioviFunction(
						new GFunction(this.value.signature().widen(signature), bodyCtx));
				} catch (NonWideneable e) {
					yield GioviValue.super.redirection(content);
				}
			}

			default -> GioviValue.super.redirection(content);
		};
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// arithmetic

	//////////////////////////////////////////////////////////////////////////////////////////////// boolean

	//////////////////////////////////////////////////////////////////////////////////////////////// relational

	//////////////////////////////////////////////////////////////////////////////////////////////// access

	@Override
	public GioviParserVisitResult dot(GioviParserVisitResult right) {
		if (right instanceof GioviDefault) {
			return new GioviFunction();
		}
		return GioviValue.super.dot(right);
	}

}
