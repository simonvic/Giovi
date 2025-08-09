package giovi.lang;

import giovi.GioviParserVisitResult;
import it.simonvic.giovi.exceptions.NonWideneable;
import it.simonvic.giovi.lang.annotation.Native.Const;
import it.simonvic.giovi.lang.annotation.Native.Describes;

@Describes(GioviReal.class)
public record GioviTypeReal() implements GioviTypeNumber<GioviReal> {

	@Override
	public String toString() {
		return "Real";
	}

	@Override
	public Class<? extends GioviReal> value() {
		return GioviReal.class;
	}

	@Override
	public Class<? extends GioviReal> defaultValue() {
		return GioviReal.class;
	}

	@Override
	public boolean isWidenableTo(GioviType<?> type) {
		return type instanceof GioviTypeReal;
	}

	@Override
	public GioviReal widen(GioviValue<?> value) throws NonWideneable {
		return switch (value) {
			case GioviNumber<?> v -> v.realValue();
			default -> throw new NonWideneable(value, this);
		};
	}

	@Override
	public GioviReal spawn() {
		return new GioviReal();
	}

	@Override
	public GioviTypeReal copy() {
		return GioviReal.TYPE;
	}

	@Override
	public GioviParserVisitResult dot(GioviParserVisitResult right) {
		if (right instanceof GioviDefault) {
			return new GioviReal();
		}
		return GioviTypeNumber.super.dot(right);
	}

	@Const
	public static final GioviReal MAX_VALUE = new GioviReal(Double.MAX_VALUE);

	@Const
	public static final GioviReal MIN_VALUE = new GioviReal(Double.MIN_VALUE);

	@Const
	public static final GioviReal INF = new GioviReal(Double.POSITIVE_INFINITY);

	@Const
	public static final GioviReal NAN = new GioviReal(Double.NaN);

}
