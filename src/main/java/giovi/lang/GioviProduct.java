package giovi.lang;

import giovi.GProduct;
import giovi.GioviParserVisitResult;
import giovi.MemberKey;
import giovi.Range;

public sealed interface GioviProduct<J extends GProduct>
	extends GioviValue<J>
	permits GioviTuple, GioviRecord, GioviStruct {

	@Override
	default GioviParserVisitResult access(GioviParserVisitResult right) {
		return switch (right) {
			case MemberKey(var identifier) when identifier.startsWith("_") -> switch (identifier) {
				case "_count" -> new GioviInt(value().getFieldsCount());
				case "_range" -> Range.of(0, value().getFieldsCount() - 1, 1);
				default -> GioviValue.super.access(right);
			};
			default -> GioviValue.super.access(right);
		};
	}

}

