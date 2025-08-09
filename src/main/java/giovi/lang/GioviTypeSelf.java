package giovi.lang;

import it.simonvic.giovi.exceptions.NonWideneable;
import it.simonvic.giovi.lang.annotation.Native.Describes;


/// Lazily fetched type, used in incomplete type declarations
@Describes(GioviSelf.class)
public record GioviTypeSelf() implements GioviType<GioviSelf> {

	@Override
	public String toString() {
		return "Self";
	}

	@Override
	public Class<? extends GioviSelf> value() {
		return GioviSelf.class;
	}

	@Override
	public Class<? extends GioviSelf> defaultValue() {
		return GioviSelf.class;
	}

	@Override
	public boolean isWidenableTo(GioviType<?> type) {
		return type instanceof GioviTypeStruct;
	}

	@Override
	public GioviSelf widen(GioviValue<?> value) throws NonWideneable {
		throw new NonWideneable(value, this);
	}

	@Override
	public GioviSelf spawn() {
		return new GioviSelf();
	}

	@Override
	public GioviType<GioviSelf> copy() {
		return GioviSelf.TYPE;
	}

}
