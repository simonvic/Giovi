package giovi.lang;

import it.simonvic.giovi.exceptions.NonWideneable;
import it.simonvic.giovi.lang.annotation.Native.Describes;

@Describes(GioviNatural.class)
public record GioviTypeNatural() implements GioviTypeNumber<GioviNatural> {

	@Override
	public String toString() {
		return "Natural";
	}

	@Override
	public Class<? extends GioviNatural> value() {
		return GioviNatural.class;
	}

	@Override
	public boolean isWidenableTo(GioviType<?> type) {
		return switch (type) {
			case GioviTypeNatural _, GioviTypeInt _, GioviTypeReal _ -> true;
			default -> GioviTypeNumber.super.isWidenableTo(type);
		};
	}

	@Override
	public GioviNatural widen(GioviValue<?> value) throws NonWideneable {
		return switch (value) {
			case GioviNatural v -> v;
			default -> GioviTypeNumber.super.widen(value);
		};
	}

	@Override
	public GioviNatural spawn() {
		return new GioviNatural();
	}

	@Override
	public GioviTypeNatural copy() {
		return GioviType.NAT;
	}

}
