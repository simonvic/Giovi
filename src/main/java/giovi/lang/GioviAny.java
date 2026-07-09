package giovi.lang;

import it.simonvic.giovi.lang.annotation.Native.DescribedBy;

@DescribedBy(GioviTypeAny.class)
public record GioviAny(GioviValue<?> value) implements GioviValue<GioviValue<?>> {

	@Override
	public GioviType<? extends GioviValue<GioviValue<?>>> getGioviType() {
		return GioviType.ANY;
	}

	@Override
	public GioviValue<GioviValue<?>> copy() {
		return new GioviAny(value.copy());
	}

}
