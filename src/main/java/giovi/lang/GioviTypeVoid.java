package giovi.lang;

import it.simonvic.giovi.exceptions.NonWideneable;
import it.simonvic.giovi.lang.annotation.Native.Describes;

@Describes(GioviVoid.class)
public record GioviTypeVoid() implements GioviTypePrimitive<GioviVoid> {

	@Override
	public String toString() {
		return "Void";
	}

	@Override
	public Class<? extends GioviVoid> value() {
		return GioviVoid.class;
	}

	@Override
	public boolean isWidenableTo(GioviType<?> type) {
		return true;
	}

	@Override
	public GioviVoid widen(GioviValue<?> value) throws NonWideneable {
		return switch (value) {
			case GioviVoid v -> v;
			default -> GioviTypePrimitive.super.widen(value);
		};
	}

	@Override
	public GioviTypeVoid copy() {
		return GioviType.VOID;
	}

	@Override
	public GioviVoid spawn() {
		return new GioviVoid();
	}

}

