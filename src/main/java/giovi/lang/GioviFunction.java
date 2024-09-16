package giovi.lang;

import giovi.Function;
import giovi.GioviParserVisitResult;
import it.simonvic.giovi.exceptions.NonWideneable;

public record GioviFunction(Function value, Function defaultValue) implements GioviValue<Function> {

	public GioviFunction(Function value) {
		this(value, Function.EMPTY);
	}

	public GioviFunction() {
		this(Function.EMPTY, Function.EMPTY);
	}

	@Override
	public GioviType<?> getGioviType() {
		return new GioviTypeFunction(value.signature().copy());
	}

	@Override
	public GioviValue<?> widenTo(GioviType<?> type) throws NonWideneable {
		if (type instanceof GioviTypeFunction t
			&& t.getSignature().equals(this.value.signature())) {
			return this;
		}
		throw new NonWideneable(this, type);
	}

	@Override
	public GioviValue<Function> copy() {
		return new GioviFunction(value.copy(), defaultValue.copy());
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// OPERATIONS

	//////////////////////////////////////////////////////////////////////////////////////////////// cast

	@Override
	public GioviParserVisitResult as(GioviParserVisitResult right) {
		// TODO: do this properly
		return switch (right) {
			case GioviTypeFunction v -> this;
			default -> GioviValue.super.as(right);
		};
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// redirection

	@Override
	public GioviParserVisitResult redirection(GioviParserVisitResult content) {
		return switch (content) {
			case GioviDefault _ -> new GioviFunction(defaultValue.copy(), defaultValue);
			case GioviFunction v when v.value.signature()
				.equals(this.value.signature()) -> new GioviFunction(v.value.copy(), defaultValue);

			// equal parameters and return type
			case GioviFunction v when v.value.signature().parameters()
				.equals(value.signature().parameters())
				&& v.value.signature().returnType()
					.equals(value.signature().returnType()) -> new GioviFunction(v.value.copy(), defaultValue);

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
			return new GioviFunction(defaultValue.copy());
		}
		return GioviValue.super.dot(right);
	}

}
