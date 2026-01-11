package giovi.lang;

import giovi.GioviParserVisitResult;
import it.simonvic.giovi.lang.annotation.Native.DescribedBy;

@DescribedBy(GioviTypeSelf.class)
public record GioviSelf() implements GioviValue<Void> {

	public static final GioviTypeSelf TYPE = new GioviTypeSelf();

	@Override
	public String toString() {
		return "self";
	}

	@Override
	public Void value() {
		return null;
	}

	@Override
	public Void zero() {
		return null;
	}

	@Override
	public GioviType<? extends GioviValue<Void>> getGioviType() {
		return GioviSelf.TYPE;
	}

	@Override
	public GioviValue<Void> copy() {
		return new GioviSelf();
	}

	@Override
	public GioviParserVisitResult redirection(GioviParserVisitResult content) {
		if (content instanceof GioviSelf) {
			return content;
		}
		return GioviValue.super.redirection(content);
	}


}
