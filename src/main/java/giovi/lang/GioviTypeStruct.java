package giovi.lang;

import giovi.GioviParserVisitResult;
import giovi.MemberKey;
import giovi.StructSignature;
import it.simonvic.giovi.exceptions.NonWideneable;
import it.simonvic.giovi.lang.annotation.Native.Describes;

@Describes(GioviStruct.class)
public record GioviTypeStruct(StructSignature signature) implements GioviTypeProduct<GioviStruct> {

	@Override
	public String toString() {
		return signature.toString();
	}

	@Override
	public Class<? extends GioviStruct> value() {
		return GioviStruct.class;
	}

	@Override
	public boolean isWidenableTo(GioviType<?> type) {
		return switch (type) {
			case GioviTypeStruct(var signature) -> signature.canWiden(this.signature);
			default -> GioviTypeProduct.super.isWidenableTo(type);
		};
	}

	@Override
	public GioviStruct widen(GioviValue<?> value) throws NonWideneable {
		return switch (value) {
			case GioviTuple v -> new GioviStruct(this, signature.widen(v.value()));
			case GioviRecord v -> new GioviStruct(this, signature.widen(v.value()));
			case GioviStruct v -> new GioviStruct(this, signature.widen(v.value()));
			default -> GioviTypeProduct.super.widen(value);
		};
	}

	@Override
	public GioviStruct spawn() {
		return new GioviStruct(this, signature.spawn());
	}

	@Override
	public GioviTypeStruct copy() {
		return new GioviTypeStruct(signature);
	}

	@Override
	public GioviParserVisitResult dot(GioviParserVisitResult right) {
		return switch (right) {
			case MemberKey(var identifier) when signature.containsField(identifier)
				-> signature.getField(identifier).defaultValue().copy();
			case MemberKey(var identifier) when signature.containsConstant(identifier)
				-> signature.getConstant(identifier);
			case MemberKey(var identifier) when signature.containsFunction(identifier)
				-> signature.getFunction(identifier);
			default -> GioviTypeProduct.super.dot(right);
		};
	}

	@Override
	public GioviParserVisitResult dotRef(GioviParserVisitResult right) {
		return switch (right) {
			case MemberKey(var identifier) when signature.containsFunction(identifier)
				-> signature.getFunction(identifier);
			default -> GioviTypeProduct.super.dotRef(right);
		};
	}

}
