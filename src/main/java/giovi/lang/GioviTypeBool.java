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
		return type instanceof GioviTypeBool;
	}

	@Override
	public GioviBool widen(GioviValue<?> value) throws NonWideneable {
		return switch (value) {
			case GioviBool v -> v;
			default -> throw new NonWideneable(value, this);
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
