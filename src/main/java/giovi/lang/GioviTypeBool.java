package giovi.lang;

import it.simonvic.giovi.exceptions.NonWideneable;
import it.simonvic.giovi.lang.annotation.Native.Describes;

@Describes(GioviBool.class)
public record GioviTypeBool() implements GioviTypePrimitive<GioviBool> {

	@Override
	public String toString() {
		return "Bool";
	}

	@Override
	public Class<? extends GioviBool> value() {
		return GioviBool.class;
	}

	@Override
	public boolean isWidenableTo(GioviType<?> type) {
		return switch (type) {
			case GioviTypeBool _ -> true;
			default -> GioviTypePrimitive.super.isWidenableTo(type);
		};
	}

	@Override
	public GioviBool widen(GioviValue<?> value) throws NonWideneable {
		return switch (value) {
			case GioviBool v -> v;
			default -> GioviTypePrimitive.super.widen(value);
		};
	}

	@Override
	public GioviBool spawn() {
		return GioviBool.FALSE;
	}

	@Override
	public GioviTypeBool copy() {
		return GioviType.BOOL;
	}

}
