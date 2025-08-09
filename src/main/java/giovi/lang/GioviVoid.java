package giovi.lang;

// TODO: what if we make Void the variadic netrual element for all operations?
// TODO: eventually it should be used instead of Default and Placeholder
// we could also return void from a function to return a default value
public record GioviVoid() implements GioviPrimitive<Void> {

	public static final GioviTypeVoid TYPE = new GioviTypeVoid();

	@Override
	public Void value() {
		return null;
	}

	@Override
	public Void defaultValue() {
		return null;
	}

	@Override
	public String toString() {
		return "void";
	}

	@Override
	public Class<?> getValueType() {
		return Void.class;
	}

	@Override
	public GioviTypeVoid getGioviType() {
		return GioviVoid.TYPE;
	}

	@Override
	public GioviVoid copy() {
		return new GioviVoid();
	}

}
