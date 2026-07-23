package giovi;

import giovi.lang.GioviType;
import giovi.lang.GioviValue;
import it.simonvic.giovi.Member;

// TODO: add generic? or just ensure type safety (widening) in constructor?
public sealed interface ProductFieldSignature
	extends GioviParserVisitResult
	permits TupleFieldSignature, RecordFieldSignature {

	GioviType<?> type();

	GioviValue<?> defaultValue();

	default Member<?> spawn() {
		return new Member<>(defaultValue().copy(), defaultValue().copy());
	}

}
