package giovi.lang;

import giovi.GioviParserVisitResult;
import it.simonvic.giovi.exceptions.NonWideneable;
import it.simonvic.giovi.lang.annotation.Native.Describes;

@Describes(GioviInt.class)
public record GioviTypeInt() implements GioviTypeNumber<GioviInt> {

	@Override
	public String toString() {
		return "Int";
	}

	@Override
	public Class<? extends GioviInt> value() {
		return GioviInt.class;
	}

	@Override
	public Class<? extends GioviInt> zero() {
		return GioviInt.class;
	}

	@Override
	public boolean isWidenableTo(GioviType<?> type) {
		return switch (type) {
			case GioviTypeInt _,GioviTypeReal _ -> true;
			default -> false;
		};
	}

	@Override
	public GioviInt widen(GioviValue<?> value) throws NonWideneable {
		return switch (value) {
			case GioviNatural v -> v.intValue();
			case GioviInt v -> v;
			default -> throw new NonWideneable(value, this);
		};
	}

	@Override
	public GioviInt spawn() {
		return new GioviInt();
	}

	@Override
	public GioviTypeInt copy() {
		return GioviType.INT;
	}

}

