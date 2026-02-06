package giovi.lang;

import it.simonvic.giovi.lang.annotation.Native.DescribedBy;

// TODO: what if we make Void the variadic netrual element for all operations?
// TODO: eventually it should be used instead of Default and Placeholder
// we could also return void from a function to return a default value
@DescribedBy(GioviTypeVoid.class)
public record GioviVoid() implements GioviPrimitive<Void> {

	@Override
	public Void value() {
		return null;
	}

	@Override
	public Void zero() {
		return null;
	}

	@Override
	public String toString() {
		return "void";
	}

	@Override
	public GioviTypeVoid getGioviType() {
		return GioviType.VOID;
	}

	@Override
	public GioviVoid copy() {
		return new GioviVoid();
	}

}
