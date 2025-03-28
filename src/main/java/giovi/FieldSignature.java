package giovi;

import giovi.lang.GioviType;
import giovi.lang.GioviValue;

// TODO: add generic? or just ensure type safety (widening) in constructor?
public interface FieldSignature extends GioviParserVisitResult {

	GioviType<?> type();
	GioviValue<?> defaultValue();

}
