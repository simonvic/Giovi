package giovi.lang;

import giovi.Function;
import giovi.GioviParserVisitResult;
import it.simonvic.giovi.exceptions.NonWideneable;

public final class GioviFunction extends GioviValue<Function> {

	public GioviFunction(Function value, Function defaultValue) {
		super(value, defaultValue);
	}

	public GioviFunction(Function value) {
		super(value, Function.EMPTY);
	}

	public GioviFunction() {
		super(Function.EMPTY, Function.EMPTY);
	}

	@Override
	public GioviTypeFunction getGioviType() {
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
	public GioviFunction copy() {
		return new GioviFunction(value.copy(), defaultValue.copy());
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// OPERATIONS

	//////////////////////////////////////////////////////////////////////////////////////////////// cast

	@Override
	public GioviParserVisitResult as(GioviParserVisitResult right) {
		// TODO: do this properly
		return switch (right) {
			case GioviTypeFunction v -> this;
			default -> super.as(right);
		};
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// redirection

	@Override
	public GioviParserVisitResult redirection(GioviParserVisitResult content) {
		switch (content) {
			case GioviDefault _ -> value = defaultValue.copy();
			case GioviFunction v when v.value.signature()
				.equals(this.value.signature()) -> this.value = v.value.copy();

			// equal parameters and return type
			case GioviFunction v when v.value.signature().parameters()
				.equals(value.signature().parameters())
				&& v.value.signature().returnType()
					.equals(value.signature().returnType()) -> this.value = v.value.copy();

			default -> super.redirection(content);
		}
		return this;
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
		return super.dot(right);
	}

}
