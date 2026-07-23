package giovi.lang;

import java.util.List;
import java.util.Map;

import giovi.*;
import it.simonvic.giovi.ValueWrapper;
import it.simonvic.giovi.exceptions.GioviException;
import it.simonvic.giovi.exceptions.NonWideneable;
import it.simonvic.giovi.lang.annotation.Native.DescribedBy;
import it.simonvic.giovi.util.*;

public sealed interface GioviValue<J>
	extends GioviObject
	permits GioviAny, GioviPrimitive, GioviComposite, GioviProduct, GioviSelf, GioviFunction {

	// TODO: cache primitive instances (e.g. Void)

	public static <J> GioviValue<?> of(J value) {
		return switch (value) {
			case Natural v -> new GioviNatural(v);
			case Integer v -> new GioviInt(v);
			case Double v -> new GioviReal(v);
			case Boolean v -> GioviBool.of(v);
			case String v -> new GioviText(v);
			case List<?> v -> GioviLists.of(v);
			case Map<?, ?> v -> GioviMaps.of(v);
			case GStruct v -> new GioviStruct(GioviStructs.typeFrom(v), v);
			case GRecord v -> new GioviRecord(GioviRecords.typeFrom(v), v);
			case GTuple v -> new GioviTuple(GioviTuples.typeFrom(v), v);
			case Function v -> new GioviFunction(v);
			default -> throw new GioviException(
				"Unknown type " + value + " (" + value.getClass() + ")"
			);
		};
	}

	/**
	 * @return the native value
	 */
	J value();

	/**
	 * Get the Type (in Giovi space) of this value
	 * @return Type
	 */
	GioviType<? extends GioviValue<J>> getGioviType();

	/**
	 * Widen the value to the specified type if it's wider (e.g. {@code Natural <= Int <= Real})
	 * Lossless conversion
	 * @param type
	 * @return widened value
	 * @throws NonWideneable
	 */
	default <V extends GioviValue<?>> V widenTo(GioviType<V> type) throws NonWideneable {
		return type.widen(this);
	}

	// TODO: add narrowTo(type) which should be used along the widenTo(type) inside the as(type)

	/**
	 * Perform a <b>deep copy</b>
	 * @return copy of the object
	 */
	GioviValue<J> copy();
	//public abstract <G extends GioviValue<J>> G copy();

	//////////////////////////////////////////////////////////////////////////////////////////////// OPERATIONS

	//////////////////////////////////////////////////////////////////////////////////////////////// cast

	// TODO: lol definitely find a better way of handling Vars

	default GioviParserVisitResult as(GioviParserVisitResult right) {
		return switch (right) {
			case ValueWrapper<?> v -> as(v.unwrap());
			default -> GioviObject.super.as(right);
		};
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// redirection

	default GioviParserVisitResult redirection(GioviParserVisitResult content) {
		return switch (content) {
			case GioviDefault _ -> redirection(getGioviType().spawn());
			case ValueWrapper<?> v -> redirection(v.unwrap());
			default -> GioviObject.super.redirection(content);
		};
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// bitwise

	default GioviParserVisitResult bitwiseAnd(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> bitwiseAnd(getGioviType().spawn());
			case ValueWrapper<?> v -> bitwiseAnd(v.unwrap());
			default -> GioviObject.super.bitwiseAnd(right);
		};
	}

	default GioviParserVisitResult bitwiseOr(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> bitwiseOr(getGioviType().spawn());
			case ValueWrapper<?> v -> bitwiseOr(v.unwrap());
			default -> GioviObject.super.bitwiseOr(right);
		};
	}

	default GioviParserVisitResult bitwiseXor(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> bitwiseXor(getGioviType().spawn());
			case ValueWrapper<?> v -> bitwiseXor(v.unwrap());
			default -> GioviObject.super.bitwiseXor(right);
		};
	}

	default GioviParserVisitResult bitwiseLeftShift(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> bitwiseLeftShift(getGioviType().spawn());
			case ValueWrapper<?> v -> bitwiseLeftShift(v.unwrap());
			default -> GioviObject.super.bitwiseLeftShift(right);
		};
	}

	default GioviParserVisitResult bitwiseRightShift(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> bitwiseRightShift(getGioviType().spawn());
			case ValueWrapper<?> v -> bitwiseRightShift(v.unwrap());
			default -> GioviObject.super.bitwiseRightShift(right);
		};
	}

	default GioviParserVisitResult bitwiseRightUnsignedShift(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> bitwiseRightUnsignedShift(getGioviType().spawn());
			case ValueWrapper<?> v -> bitwiseRightUnsignedShift(v.unwrap());
			default -> GioviObject.super.bitwiseRightUnsignedShift(right);
		};
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// arithmetic

	default GioviParserVisitResult plus(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> plus(getGioviType().spawn());
			case ValueWrapper<?> v -> plus(v.unwrap());
			default -> GioviObject.super.plus(right);
		};
	}

	default GioviParserVisitResult minus(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> minus(getGioviType().spawn());
			case ValueWrapper<?> v -> minus(v.unwrap());
			default -> GioviObject.super.minus(right);
		};
	}

	default GioviParserVisitResult multiply(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> multiply(getGioviType().spawn());
			case ValueWrapper<?> v -> multiply(v.unwrap());
			default -> GioviObject.super.multiply(right);
		};
	}

	default GioviParserVisitResult divide(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> divide(getGioviType().spawn());
			case ValueWrapper<?> v -> divide(v.unwrap());
			default -> GioviObject.super.divide(right);
		};
	}

	default GioviParserVisitResult modulo(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> modulo(getGioviType().spawn());
			case ValueWrapper<?> v -> modulo(v.unwrap());
			default -> GioviObject.super.modulo(right);
		};
	}

	default GioviParserVisitResult power(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> power(getGioviType().spawn());
			case ValueWrapper<?> v -> power(v.unwrap());
			default -> GioviObject.super.power(right);
		};
	}


	//////////////////////////////////////////////////////////////////////////////////////////////// boolean

	default GioviParserVisitResult and(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> and(getGioviType().spawn());
			case ValueWrapper<?> v -> and(v.unwrap());
			default -> GioviObject.super.and(right);
		};
	}

	default GioviParserVisitResult or(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> or(getGioviType().spawn());
			case ValueWrapper<?> v -> or(v.unwrap());
			default -> GioviObject.super.or(right);
		};
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// relational

	default GioviParserVisitResult equal(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> equal(getGioviType().spawn());
			case ValueWrapper<?> v -> equal(v.unwrap());
			default -> GioviObject.super.equal(right);
		};
	}

	default GioviParserVisitResult notEqual(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> notEqual(getGioviType().spawn());
			case ValueWrapper<?> v -> notEqual(v.unwrap());
			default -> GioviObject.super.notEqual(right);
		};
	}

	default GioviParserVisitResult less(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> less(getGioviType().spawn());
			case ValueWrapper<?> v -> less(v.unwrap());
			default -> GioviObject.super.less(right);
		};
	}

	default GioviParserVisitResult lessEqual(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> lessEqual(getGioviType().spawn());
			case ValueWrapper<?> v -> lessEqual(v.unwrap());
			default -> GioviObject.super.lessEqual(right);
		};
	}

	default GioviParserVisitResult greater(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> greater(getGioviType().spawn());
			case ValueWrapper<?> v -> greater(v.unwrap());
			default -> GioviObject.super.greater(right);
		};
	}

	default GioviParserVisitResult greaterEqual(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> greaterEqual(getGioviType().spawn());
			case ValueWrapper<?> v -> greaterEqual(v.unwrap());
			default -> GioviObject.super.greaterEqual(right);
		};
	}

	default GioviParserVisitResult contains(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> contains(getGioviType().spawn());
			case ValueWrapper<?> v -> contains(v.unwrap());
			default -> GioviObject.super.contains(right);
		};
	}

	default GioviParserVisitResult isContained(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> isContained(getGioviType().spawn());
			default -> right.contains(this);
		};
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// access

	default GioviParserVisitResult access(GioviParserVisitResult right) {
		return switch (right) {
			case ValueWrapper<?> v -> access(v.unwrap());
			default -> GioviObject.super.access(right);
		};
	}

	default GioviParserVisitResult dot(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> getGioviType().spawn();
			case ValueWrapper<?> v -> dot(v.unwrap());
			case MemberKey(var identifier) when this
				.getClass().getAnnotation(DescribedBy.class) instanceof DescribedBy annotation
				-> GioviType.findSymbol(identifier, annotation.value())
					.orElseGet(() -> GioviObject.super.dot(right));
			default -> GioviObject.super.dot(right);
		};
	}

	default GioviParserVisitResult dotRef(GioviParserVisitResult right) {
		return switch (right) {
			case MemberKey(var identifier) when this
				.getClass().getAnnotation(DescribedBy.class) instanceof DescribedBy annotation
				-> GioviType.findFunction(identifier, annotation.value())
					.orElseGet(() -> GioviObject.super.dotRef(right).asGioviFunction());
			default -> GioviObject.super.dotRef(right);
		};
	}

}
