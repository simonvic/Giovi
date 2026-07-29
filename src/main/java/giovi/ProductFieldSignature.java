package giovi;

import giovi.lang.GioviType;
import giovi.lang.GioviValue;
import it.simonvic.giovi.GField;

// TODO: add generic? or just ensure type safety (widening) in constructor?
public sealed interface ProductFieldSignature
	extends GioviParserVisitResult
	permits TupleFieldSignature, RecordFieldSignature {

	GioviType<?> type();

	GioviValue<?> defaultValue();

	default GField<?> spawn() {
		return new GField<>(defaultValue().copy(), defaultValue().copy());
	}

}
