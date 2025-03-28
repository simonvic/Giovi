package giovi;

import giovi.lang.GioviType;
import giovi.lang.GioviValue;

// TODO: add generic? or just ensure type safety (widening) in constructor?
public interface FieldSignature {

	GioviType<?> type();
	GioviValue<?> defaultValue();

}
