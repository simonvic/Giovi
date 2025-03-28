package giovi.lang;

import giovi.GioviParserVisitResult;
import giovi.MemberKey;
import giovi.StructSignature;
import it.simonvic.giovi.exceptions.NonWideneable;

public record GioviTypeStruct(StructSignature signature) implements GioviType<GioviStruct> {

	@Override
	public String toString() {
		return signature.toString();
	}

	@Override
	public Class<? extends GioviStruct> value() {
		return GioviStruct.class;
	}

	@Override
	public Class<? extends GioviStruct> defaultValue() {
		return GioviStruct.class;
	}

	@Override
	public boolean isWidenableTo(GioviType<?> type) {
		return switch (type) {
			case GioviTypeStruct v -> v.signature.canWiden(this.signature);
			default -> false;
		};
	}

	@Override
	public GioviStruct widen(GioviValue<?> value) throws NonWideneable {
		return switch (value) {
			case GioviTuple v -> new GioviStruct(signature, signature.widen(v.value()));
			case GioviRecord v -> new GioviStruct(signature, signature.widen(v.value()));
			case GioviStruct v -> new GioviStruct(signature, signature.widen(v.value()));
			default -> throw new NonWideneable(value, this);
		};
	}

	@Override
	public GioviStruct spawn() {
		return new GioviStruct(signature, signature.spawn());
	}

	@Override
	public GioviTypeStruct copy() {
		return new GioviTypeStruct(signature);
	}

	@Override
	public GioviParserVisitResult dot(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> new GioviStruct(signature, signature.spawn());
			case MemberKey(var identifier) when signature.containsField(identifier)
				-> signature.getField(identifier).defaultValue().copy();
			case MemberKey(var identifier) when signature.containsConstant(identifier)
				-> signature.getConstant(identifier);
			case MemberKey(var identifier) when signature.containsFunction(identifier)
				-> signature.getFunction(identifier);
			default -> GioviType.super.dot(right);
		};
	}

}
