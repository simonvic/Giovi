package giovi.lang;

import giovi.Function;
import giovi.GFunction;
import giovi.GioviParserVisitResult;

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
		return switch (content) {
			case GioviDefault _ -> new GioviFunction(defaultValue());
			case GioviFunction v when v.value.signature().equals(this.value.signature())
				-> new GioviFunction(v.value.copy());

			// equal parameters and return type
			case GioviFunction v when v.value.signature().parameters()
				.equals(value.signature().parameters())
				&& v.value.signature().returnType().equals(value.signature().returnType())
				-> new GioviFunction(v.value.copy());

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
