package giovi.lang;

public final class GioviTypeText extends GioviTypePrimitive<GioviText> {

	public GioviTypeText() {
		super(GioviText.class);
	}

	@Override
	public boolean canWidenTo(GioviType<?> to) {
		return to instanceof GioviTypeText;
	}

	@Override
	public GioviText spawn() {
		return new GioviText();
	}

	@Override
	public GioviTypeText copy() {
		return new GioviTypeText();
	}

}
