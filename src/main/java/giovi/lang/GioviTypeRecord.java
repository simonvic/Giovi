package giovi.lang;

import giovi.RecordSignature;
import it.simonvic.giovi.exceptions.NonWideneable;
import it.simonvic.giovi.lang.annotation.Native.Describes;

@Describes(GioviRecord.class)
public record GioviTypeRecord(RecordSignature signature) implements GioviType<GioviRecord> {

	public GioviTypeRecord() {
		this(new RecordSignature());
	}

	@Override
	public String toString() {
		return "record" + signature.toString();
	}

	@Override
	public Class<? extends GioviRecord> value() {
		return GioviRecord.class;
	}

	@Override
	public boolean isWidenableTo(GioviType<?> type) {
		return switch (type) {
			case GioviTypeStruct(var signature) -> signature.canWiden(this.signature);
			case GioviTypeRecord(var signature) -> signature.canWiden(this.signature);
			default -> false;
		};
	}

	@Override
	public GioviRecord widen(GioviValue<?> value) throws NonWideneable {
		return switch (value) {
			case GioviTuple v -> new GioviRecord(this, signature.widen(v.value()));
			case GioviRecord v -> new GioviRecord(this, signature.widen(v.value()));
			default -> throw new NonWideneable(value, this);
		};
	}

	@Override
	public GioviRecord spawn() {
		return new GioviRecord(this, signature.spawn());
	}

	@Override
	public GioviTypeRecord copy() {
		return new GioviTypeRecord(signature.copy());
	}

}

