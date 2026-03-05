package giovi.lang;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import giovi.*;
import giovi.Record;
import it.simonvic.giovi.Var;
import it.simonvic.giovi.exceptions.GioviException;
import it.simonvic.giovi.exceptions.NonWideneable;
import it.simonvic.giovi.lang.Scope;
import it.simonvic.giovi.lang.annotation.Native.Const;
import it.simonvic.giovi.lang.annotation.Native.DescribedBy;
import it.simonvic.giovi.lang.annotation.Native.Func;
import it.simonvic.giovi.util.*;

public sealed interface GioviValue<T>
	extends GioviObject
	permits GioviPrimitive, GioviList, GioviMap, GioviTuple, GioviRecord, GioviStruct, GioviSelf,
	GioviFunction {

	public static <U> GioviValue<?> of(U value) {
		return switch (value) {
			case Natural v -> new GioviNatural(v);
			case Integer v -> new GioviInt(v);
			case Double v -> new GioviReal(v);
			case Boolean v -> new GioviBool(v);
			case String v -> new GioviText(v);
			case List<?> v -> GioviLists.of(v);
			case Map<?, ?> v -> GioviMaps.of(v);
			case Struct v -> new GioviStruct(GioviStructs.typeFrom(v), v);
			case Record v -> new GioviRecord(GioviRecords.typeFrom(v), v);
			case Tuple v -> new GioviTuple(GioviTuples.typeFrom(v), v);
			case Function v -> new GioviFunction(v);
			default -> throw new GioviException(
				"Unknown type " + value + " (" + value.getClass() + ")"
			);
		};
	}

	/**
	 * @return the native value
	 */
	T value();

	/**
	 * Get the Type (in Giovi space) of this value
	 * @return Type
	 */
	GioviType<? extends GioviValue<T>> getGioviType();

	/**
	 * Widen the value to the specified type if it's wider (e.g. Natural <= Int <= Real)
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
	GioviValue<T> copy();
	//public abstract <U extends GioviValue<T>> U copy();

	//////////////////////////////////////////////////////////////////////////////////////////////// OPERATIONS

	//////////////////////////////////////////////////////////////////////////////////////////////// cast

	// TODO: lol definitely find a better way of handling Vars

	default GioviParserVisitResult as(GioviParserVisitResult right) {
		return switch (right) {
			case Var<?> v -> as(v.unwrap());
			default -> GioviObject.super.as(right);
		};
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// redirection

	default GioviParserVisitResult redirection(GioviParserVisitResult content) {
		return switch (content) {
			case GioviDefault _ -> redirection(getGioviType().spawn());
			case Var<?> v -> redirection(v.unwrap());
			default -> GioviObject.super.redirection(content);
		};
	}

	default GioviParserVisitResult redirectionPlus(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> redirectionPlus(getGioviType().spawn());
			case Var<?> v -> redirectionPlus(v.unwrap());
			default -> GioviObject.super.redirectionPlus(right);
		};
	}

	default GioviParserVisitResult redirectionMinus(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> redirectionMinus(getGioviType().spawn());
			case Var<?> v -> redirectionMinus(v.unwrap());
			default -> GioviObject.super.redirectionMinus(right);
		};
	}

	default GioviParserVisitResult redirectionMultiply(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> redirectionMultiply(getGioviType().spawn());
			case Var<?> v -> redirectionMultiply(v.unwrap());
			default -> GioviObject.super.redirectionMultiply(right);
		};
	}

	default GioviParserVisitResult redirectionDivide(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> redirectionDivide(getGioviType().spawn());
			case Var<?> v -> redirectionDivide(v.unwrap());
			default -> GioviObject.super.redirectionDivide(right);
		};
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// arithmetic

	default GioviParserVisitResult plus(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> plus(getGioviType().spawn());
			case Var<?> v -> plus(v.unwrap());
			default -> GioviObject.super.plus(right);
		};
	}

	default GioviParserVisitResult minus(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> minus(getGioviType().spawn());
			case Var<?> v -> minus(v.unwrap());
			default -> GioviObject.super.minus(right);
		};
	}

	default GioviParserVisitResult multiply(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> multiply(getGioviType().spawn());
			case Var<?> v -> multiply(v.unwrap());
			default -> GioviObject.super.multiply(right);
		};
	}

	default GioviParserVisitResult divide(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> divide(getGioviType().spawn());
			case Var<?> v -> divide(v.unwrap());
			default -> GioviObject.super.divide(right);
		};
	}

	default GioviParserVisitResult modulo(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> modulo(getGioviType().spawn());
			case Var<?> v -> modulo(v.unwrap());
			default -> GioviObject.super.modulo(right);
		};
	}

	default GioviParserVisitResult power(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> power(getGioviType().spawn());
			case Var<?> v -> power(v.unwrap());
			default -> GioviObject.super.power(right);
		};
	}


	//////////////////////////////////////////////////////////////////////////////////////////////// boolean

	default GioviParserVisitResult and(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> and(getGioviType().spawn());
			case Var<?> v -> and(v.unwrap());
			default -> GioviObject.super.and(right);
		};
	}

	default GioviParserVisitResult or(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> or(getGioviType().spawn());
			case Var<?> v -> or(v.unwrap());
			default -> GioviObject.super.or(right);
		};
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// relational

	default GioviParserVisitResult equal(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> equal(getGioviType().spawn());
			case Var<?> v -> equal(v.unwrap());
			default -> GioviObject.super.equal(right);
		};
	}

	default GioviParserVisitResult notEqual(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> notEqual(getGioviType().spawn());
			case Var<?> v -> notEqual(v.unwrap());
			default -> GioviObject.super.notEqual(right);
		};
	}

	default GioviParserVisitResult less(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> less(getGioviType().spawn());
			case Var<?> v -> less(v.unwrap());
			default -> GioviObject.super.less(right);
		};
	}

	default GioviParserVisitResult lessEqual(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> lessEqual(getGioviType().spawn());
			case Var<?> v -> lessEqual(v.unwrap());
			default -> GioviObject.super.lessEqual(right);
		};
	}

	default GioviParserVisitResult greater(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> greater(getGioviType().spawn());
			case Var<?> v -> greater(v.unwrap());
			default -> GioviObject.super.greater(right);
		};
	}

	default GioviParserVisitResult greaterEqual(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> greaterEqual(getGioviType().spawn());
			case Var<?> v -> greaterEqual(v.unwrap());
			default -> GioviObject.super.greaterEqual(right);
		};
	}

	default GioviParserVisitResult contains(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> contains(getGioviType().spawn());
			case Var<?> v -> contains(v.unwrap());
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
			case Var<?> v -> access(v.unwrap());
			default -> GioviObject.super.access(right);
		};
	}

	default GioviParserVisitResult dot(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> getGioviType().spawn();
			case Var<?> v -> dot(v.unwrap());
			// TODO: lookup for self functions in the Value instance
			case MemberKey(var identifier) when this
				.getClass().getAnnotation(DescribedBy.class) instanceof DescribedBy annotation
				-> findSymbol(identifier, annotation.value())
					.orElseGet(() -> GioviObject.super.dot(right));
			default -> GioviObject.super.dot(right);
		};
	}

	/// Lookup a native symbol
	///
	/// Lookup order is the same of [Scope]
	public static Optional<GioviParserVisitResult> findSymbol(
		String identifier,
		Class<?> clazz
	) {
		// Constants
		try {
			Field field = clazz.getField(identifier);
			if (field.isAnnotationPresent(Const.class)
				&& field.get(null) instanceof GioviParserVisitResult v) {
				return Optional.of(v);
			}
		} catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException _) {
		}

		// Method
		for (Method method : clazz.getMethods()) {
			if (method.getName().equals(identifier)
				&& method.isAnnotationPresent(Func.class)) {
				// TODO: fails on overloaded methods
				return Optional.of(new GioviFunction(new JFunction(method)));
			}
		}

		return Optional.empty();
	}


}
