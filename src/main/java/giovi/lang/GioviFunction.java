package giovi.lang;

import giovi.Function;
import giovi.GioviParserVisitResult;

public final class GioviFunction extends GioviValue<Function> {

	public GioviFunction(Function value, Function defaultValue) {
		super(value, defaultValue);
	}

	public GioviFunction(Function value) {
		// TODO: check other default value in constructors
		super(value, Function.EMPTY);
	}

	public GioviFunction() {
		super(Function.EMPTY, Function.EMPTY);
	}

	@Override
	public GioviType<?> getGioviType() {
		return new GioviTypeFunction(value.signature().copy());
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
			default -> super.as(right);
		};
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// redirection

	@Override
	public GioviParserVisitResult redirection(GioviParserVisitResult content) {
		switch (content) {
			case GioviDefault v -> boxDefault();
			// TODO: check if signatures are equal
			case GioviFunction v -> this.value = v.unbox().copy();
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
