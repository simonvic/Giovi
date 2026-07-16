package giovi.lang;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;

import giovi.GioviParserVisitResult;
import giovi.JFunction;
import giovi.MemberKey;
import it.simonvic.giovi.exceptions.NonWideneable;
import it.simonvic.giovi.lang.Scope;
import it.simonvic.giovi.lang.annotation.Native.Const;
import it.simonvic.giovi.lang.annotation.Native.Func;

// TODO: is generic needed? Why not just extends GioviPrimitive<Class<? extends GioviValue<?>>>
// TODO: better integrate types with values instead of having two separate hierarchies (maybe with nested Annotated classes?)
//@DescribedBy(GioviTypeType.class)
public sealed interface GioviType<T extends GioviValue<?>>
	extends GioviPrimitive<Class<? extends T>>
	permits GioviTypeAny, GioviTypePrimitive, GioviTypeComposite, GioviTypeProduct, GioviTypeSelf,
	GioviTypeFunction {

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
		return type instanceof GioviTypeAny;
	}

	// TODO: add isWider(type) or isNarrowableTo(type)

	/**
	 * Widen the provided value to a value whose type is described by this class
	 * @param value
	 * @return widened value
	 * @throws NonWideneable
	 */
	default T widen(GioviValue<?> value) throws NonWideneable {
		return switch (value) {
			case GioviAny(var anyValue) -> widen(anyValue);
			default -> throw new NonWideneable(value, this);
		};
	}

	/**
	 * Spawn a new instance of a value described by this type.
	 * The value should be equal to the "zero" value of the type
	 * @return zero value
	 */
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
				-> findSymbol(identifier, this.getClass())
					.orElseGet(() -> GioviPrimitive.super.dot(right));
			default -> GioviPrimitive.super.dot(right);
		};
	}


	/// Lookup a native symbol
	///
	/// Lookup order is the same of [Scope#getSymbol(String)]
	/// 
	/// @param identifier identifier of the symbol
	/// @param clazz      class to inspect
	/// @return optional symbol
	public static Optional<GioviParserVisitResult> findSymbol(
		String identifier,
		Class<?> clazz
	) {
		return findConstant(identifier, clazz).map(GioviParserVisitResult.class::cast)
			.or(() -> findFunction(identifier, clazz));
	}

	/// Lookup a native constant
	///
	/// @param identifier identifier of the constant
	/// @param clazz      class to inspect
	/// @return optional constant
	public static Optional<GioviValue<?>> findConstant(
		String identifier,
		Class<?> clazz
	) {
		try {
			Field field = clazz.getField(identifier);
			if (field.isAnnotationPresent(Const.class)
				&& field.get(null) instanceof GioviValue<?> v) {
				return Optional.of(v);
			}
		} catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException _) {
		}
		return Optional.empty();
	}

	/// Lookup a native function
	///
	/// @param identifier identifier of the function
	/// @param clazz      class to inspect
	/// @return optional function
	public static Optional<GioviFunction> findFunction(
		String identifier,
		Class<?> clazz
	) {
		// Method
		for (Method method : clazz.getMethods()) {
			if (method.getName().equals(identifier)
				&& method.isAnnotationPresent(Func.class)) {
				return Optional.of(new GioviFunction(new JFunction(method)));
			}
		}
		return Optional.empty();
	}

}
