package giovi.lang;

import giovi.GioviParserVisitResult;
import giovi.MemberKey;
import it.simonvic.giovi.exceptions.NonWideneable;

// TODO: is generic needed? Why not just extends GioviPrimitive<Class<? extends GioviValue<?>>>
// TODO: better integrate types with values instead of having two separate hierarchies (maybe with nested Annotated classes?)
//@DescribedBy(GioviTypeType.class)
public sealed interface GioviType<T extends GioviValue<?>>
	extends GioviPrimitive<Class<? extends T>>
	permits GioviTypeAny, GioviTypePrimitive, GioviTypeList, GioviTypeMap, GioviTypeTuple,
	GioviTypeRecord, GioviTypeStruct, GioviTypeSelf, GioviTypeFunction {

	public static final GioviTypeAny ANY = new GioviTypeAny();
	public static final GioviTypeVoid VOID = new GioviTypeVoid();
	public static final GioviTypeBool BOOL = new GioviTypeBool();
	public static final GioviTypeText TEXT = new GioviTypeText();
	public static final GioviTypeInt INT = new GioviTypeInt();
	public static final GioviTypeNatural NAT = new GioviTypeNatural();
	public static final GioviTypeReal REAL = new GioviTypeReal();
	public static final GioviTypeSelf SELF = new GioviTypeSelf();
	public static final GioviTypeList LIST_OF_INT = new GioviTypeList(INT);
	public static final GioviTypeList LIST_OF_REAL = new GioviTypeList(REAL);
	public static final GioviTypeList LIST_OF_TEXT = new GioviTypeList(TEXT);

	default String getFullyQualifiedName() {
		return value().getName().replace("Giovi", "");
	}

	default String getSimpleName() {
		return value().getSimpleName().replace("Giovi", "");
	}

	@Override
	default GioviTypeType<T> getGioviType() {
		return new GioviTypeType<>(this);
	}

	/**
	 * Check if this type is widenable to the provided type.
	 * @param type
	 * @return true if this type is equal or narrower than the provided type, false otherwise
	 */
	default boolean isWidenableTo(GioviType<?> type) {
		return false;
	}

	// TODO: add isWider(type) or isNarrowableTo(type)

	/**
	 * Widen the provided value to a value whose type is described by this class
	 * @param value
	 * @return widened value
	 * @throws NonWideneable
	 */
	default T widen(GioviValue<?> value) throws NonWideneable {
		throw new NonWideneable(value, this);
	}

	T spawn();

	@Override
	GioviType<T> copy();

	@Override
	default GioviParserVisitResult dot(GioviParserVisitResult right) {
		return switch (right) {
			// NOTE: unlike other values, a Type's default is the zero value rather than its Type's zero (i.e. TypeType.zero)
			// TODO: when types as first-class citizens are implemented, the spawning should be moved to `access` instead
			case GioviDefault _ -> spawn();
			case MemberKey(var identifier)
				-> GioviValue.findSymbol(identifier, this.getClass())
					.orElseGet(() -> GioviPrimitive.super.dot(right));
			default -> GioviPrimitive.super.dot(right);
		};
	}


}
