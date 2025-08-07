package giovi.lang;

import giovi.GioviParserVisitResult;
import it.simonvic.giovi.lang.annotation.Native.DescribedBy;

@DescribedBy(GioviTypeSelf.class)
public record GioviSelf() implements GioviValue<Void> {

	@Override
	public String toString() {
		return "self";
	}

	@Override
	public Void value() {
		return null;
	}

	@Override
	public Void defaultValue() {
		return null;
	}

	@Override
	public GioviType<? extends GioviValue<Void>> getGioviType() {
		return new GioviTypeSelf();
	}

	@Override
	public GioviValue<Void> copy() {
		return new GioviSelf();
	}

	@Override
	public GioviParserVisitResult redirection(GioviParserVisitResult content) {
		return content;
	}


}
