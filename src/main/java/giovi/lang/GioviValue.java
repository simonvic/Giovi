package giovi.lang;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import giovi.*;
import giovi.Record;
import it.simonvic.giovi.Typed;
import it.simonvic.giovi.Var;
import it.simonvic.giovi.exceptions.GioviException;
import it.simonvic.giovi.exceptions.NonWideneable;
import it.simonvic.giovi.lang.annotation.Native.Const;
import it.simonvic.giovi.lang.annotation.Native.Func;
import it.simonvic.giovi.util.*;

public sealed interface GioviValue<T>
	extends GioviObject, Typed
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
				"Unknown type " + value + " (" + value.getClass() + ")");
		};
	}

	default Class<?> getValueType() {
		return value().getClass();
	}

	/**
	 * TODO: maybe rename to unbox() or native() to avoid confusion from the Var.value()
	 * @return
	 */
	T value();

	/**
	 * TODO: remove this and move it to the Type hierarchy (when eventually it
	 * gets cached in a Var)
	 * @return default value of the type
	 */
	T defaultValue();

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
			case Var<?> v -> as(v.value());
			default -> GioviObject.super.as(right);
		};
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// redirection

	default GioviParserVisitResult redirection(GioviParserVisitResult content) {
		return switch (content) {
			case Var<?> v -> redirection(v.value());
			default -> GioviObject.super.redirection(content);
		};
	}

	default GioviParserVisitResult redirectionPlus(GioviParserVisitResult right) {
		return switch (right) {
			case Var<?> v -> redirectionPlus(v.value());
			default -> GioviObject.super.redirectionPlus(right);
		};
	}

	default GioviParserVisitResult redirectionMinus(GioviParserVisitResult right) {
		return switch (right) {
			case Var<?> v -> redirectionMinus(v.value());
			default -> GioviObject.super.redirectionMinus(right);
		};
	}

	default GioviParserVisitResult redirectionMultiply(GioviParserVisitResult right) {
		return switch (right) {
			case Var<?> v -> redirectionMultiply(v.value());
			default -> GioviObject.super.redirectionMultiply(right);
		};
	}

	default GioviParserVisitResult redirectionDivide(GioviParserVisitResult right) {
		return switch (right) {
			case Var<?> v -> redirectionDivide(v.value());
			default -> GioviObject.super.redirectionDivide(right);
		};
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// arithmetic

	default GioviParserVisitResult plus(GioviParserVisitResult right) {
		return switch (right) {
			case Var<?> v -> plus(v.value());
			default -> GioviObject.super.plus(right);
		};
	}

	default GioviParserVisitResult minus(GioviParserVisitResult right) {
		return switch (right) {
			case Var<?> v -> minus(v.value());
			default -> GioviObject.super.minus(right);
		};
	}

	default GioviParserVisitResult multiply(GioviParserVisitResult right) {
		return switch (right) {
			case Var<?> v -> multiply(v.value());
			default -> GioviObject.super.multiply(right);
		};
	}

	default GioviParserVisitResult divide(GioviParserVisitResult right) {
		return switch (right) {
			case Var<?> v -> divide(v.value());
			default -> GioviObject.super.divide(right);
		};
	}

	default GioviParserVisitResult modulo(GioviParserVisitResult right) {
		return switch (right) {
			case Var<?> v -> modulo(v.value());
			default -> GioviObject.super.modulo(right);
		};
	}

	default GioviParserVisitResult power(GioviParserVisitResult right) {
		return switch (right) {
			case Var<?> v -> power(v.value());
			default -> GioviObject.super.power(right);
		};
	}


	//////////////////////////////////////////////////////////////////////////////////////////////// boolean

	default GioviParserVisitResult and(GioviParserVisitResult right) {
		return switch (right) {
			case Var<?> v -> and(v.value());
			default -> GioviObject.super.and(right);
		};
	}

	default GioviParserVisitResult or(GioviParserVisitResult right) {
		return switch (right) {
			case Var<?> v -> or(v.value());
			default -> GioviObject.super.or(right);
		};
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// relational

	default GioviParserVisitResult equal(GioviParserVisitResult right) {
		return switch (right) {
			case Var<?> v -> equal(v.value());
			default -> GioviObject.super.equal(right);
		};
	}

	default GioviParserVisitResult notEqual(GioviParserVisitResult right) {
		return switch (right) {
			case Var<?> v -> notEqual(v.value());
			default -> GioviObject.super.notEqual(right);
		};
	}

	default GioviParserVisitResult less(GioviParserVisitResult right) {
		return switch (right) {
			case Var<?> v -> less(v.value());
			default -> GioviObject.super.less(right);
		};
	}

	default GioviParserVisitResult lessEqual(GioviParserVisitResult right) {
		return switch (right) {
			case Var<?> v -> lessEqual(v.value());
			default -> GioviObject.super.lessEqual(right);
		};
	}

	default GioviParserVisitResult greater(GioviParserVisitResult right) {
		return switch (right) {
			case Var<?> v -> greater(v.value());
			default -> GioviObject.super.greater(right);
		};
	}

	default GioviParserVisitResult greaterEqual(GioviParserVisitResult right) {
		return switch (right) {
			case Var<?> v -> greaterEqual(v.value());
			default -> GioviObject.super.greaterEqual(right);
		};
	}

	default GioviParserVisitResult contains(GioviParserVisitResult right) {
		return switch (right) {
			case Var<?> v -> contains(v.value());
			default -> GioviObject.super.contains(right);
		};
	}

	default GioviParserVisitResult isContained(GioviParserVisitResult right) {
		return right.contains(this);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// access

	default GioviParserVisitResult access(GioviParserVisitResult right) {
		return switch (right) {
			case Var<?> v -> access(v.value());
			default -> GioviObject.super.access(right);
		};
	}

	default GioviParserVisitResult dot(GioviParserVisitResult right) {
		return switch (right) {
			case Var<?> v -> dot(v.value());
			case MemberKey(var identifier) -> {
				// Field
				try {
					Field field = this.getClass().getField(identifier);
					if (field.isAnnotationPresent(Const.class)
						&& field.get(this) instanceof GioviParserVisitResult v) {
						yield v;
					}
				} catch (NoSuchFieldException
					| IllegalArgumentException
					| IllegalAccessException _) {
				}

				// Method
				for (Method method : this.getClass().getMethods()) {
					if (method.getName().equals(identifier)
						&& method.isAnnotationPresent(Func.class)) {
						// TODO: fails on overloaded methods
						yield new GioviFunction(new JFunction(method));
					}
				}

				throw new GioviException("No member with name: " + identifier);
			}
			default -> GioviObject.super.dot(right);
		};
	}

}
