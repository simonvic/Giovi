package giovi.lang;

import it.simonvic.giovi.exceptions.NonWideneable;
import it.simonvic.giovi.lang.annotation.Native.Describes;

@Describes(GioviText.class)
public record GioviTypeText() implements GioviTypePrimitive<GioviText> {

	@Override
	public String toString() {
		return "Text";
	}

	@Override
	public Class<? extends GioviText> value() {
		return GioviText.class;
	}

	@Override
	public boolean isWidenableTo(GioviType<?> type) {
		return type instanceof GioviTypeText;
	}

	@Override
	public GioviText widen(GioviValue<?> value) throws NonWideneable {
		return switch (value) {
			case GioviText v -> v;
			default -> throw new NonWideneable(value, this);
		};
	}

	@Override
	public GioviText spawn() {
		return new GioviText();
	}

	@Override
	public GioviTypeText copy() {
		return GioviType.TEXT;
	}

}
