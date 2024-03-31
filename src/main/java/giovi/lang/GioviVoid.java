package giovi.lang;

// TODO: what if we make Void the variadic netrual element for all operations?
public final class GioviVoid extends GioviPrimitive<Void> {

	public GioviVoid() {
		super(null, null);
	}

	@Override
	public GioviTypeVoid getGioviType() {
		return new GioviTypeVoid();
	}

	@Override
	public Class<?> getValueType() {
		return Void.class;
	}

	@Override
	public String toString() {
		return "void";
	}

	@Override
	public GioviValue<Void> copy() {
		return new GioviVoid();
	}

}
