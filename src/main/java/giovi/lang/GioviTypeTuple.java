package giovi.lang;

import giovi.TupleSignature;
import it.simonvic.giovi.exceptions.NonWideneable;

public record GioviTypeTuple(TupleSignature signature) implements GioviType<GioviTuple> {

	@Override
	public String toString() {
		return signature.toString();
	}

	@Override
	public Class<? extends GioviTuple> value() {
		return GioviTuple.class;
	}

	@Override
	public Class<? extends GioviTuple> defaultValue() {
		return GioviTuple.class;
	}

	@Override
	public boolean isWidenableTo(GioviType<?> type) {
		return switch (type) {
			case GioviTypeStruct(var signature) -> signature.canWiden(this.signature);
			case GioviTypeRecord(var signature) -> signature.canWiden(this.signature);
			case GioviTypeTuple(var signature) -> signature.canWiden(this.signature);
			default -> false;
		};
	}

	@Override
	public GioviTuple widen(GioviValue<?> value) throws NonWideneable {
		return switch (value) {
			case GioviTuple v -> new GioviTuple(signature, signature.widen(v.value()));
			default -> throw new NonWideneable(value, this);
		};
	}

	@Override
	public GioviTuple spawn() {
		return new GioviTuple(signature, signature.spawn());
	}

	@Override
	public GioviType<GioviTuple> copy() {
		return new GioviTypeTuple(signature);
	}

}

