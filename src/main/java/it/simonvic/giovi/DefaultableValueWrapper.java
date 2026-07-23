package it.simonvic.giovi;

import java.util.Objects;

import giovi.GioviParserVisitResult;
import giovi.lang.GioviDefault;
import giovi.lang.GioviType;
import giovi.lang.GioviValue;

public sealed abstract class DefaultableValueWrapper<G extends GioviValue<?>>
	extends MutableValueWrapper<G>
	permits Var, Member {

	private final G defaultValue; // TODO: eventually, it could be lazily initialized only if it is overriden

	/**
	 * @param value
	 * @param defaultValue
	 */
	public DefaultableValueWrapper(G value, G defaultValue) {
		super(value);
		this.defaultValue = defaultValue;
	}

	/**
	 * The default value will be set to its type's default value
	 * @param value
	 */
	@SuppressWarnings("unchecked")
	public DefaultableValueWrapper(G value) {
		this(value, (G) value.getGioviType().spawn());
	}


	public G unwrapDefault() {
		return defaultValue;
	}

	/**
	 * @return the defaultValue
	 */
	@SuppressWarnings("unchecked")
	public G defaultValue() {
		return (G) defaultValue.copy(); // return copy to ensure immutability
	}

	@Override
	public String toString() {
		return value.toString();
		// return "" + value + " default " + defaultValue + "";
		// return "Var [value=" + value + ", defaultValue=" + defaultValue + ", value()=" + value()
		// 	+ ", defaultValue()=" + defaultValue() + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(defaultValue);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!super.equals(obj)) return false;
		if (getClass() != obj.getClass()) return false;
		var other = (DefaultableValueWrapper<?>) obj;
		return Objects.equals(defaultValue, other.defaultValue);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// OPERATIONS


	//////////////////////////////////////////////////////////////////////////////////////////////// cast

	@Override
	public GioviParserVisitResult as(GioviParserVisitResult right) {
		return switch (right) {
			case GioviType<?> t -> value.as(t);
			default -> super.as(right);
		};
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// redirection

	@SuppressWarnings("unchecked")
	@Override
	public GioviParserVisitResult redirection(GioviParserVisitResult content) {
		this.value = (G) switch (content) {
			case GioviDefault _ -> this.value.redirection(this.defaultValue);
			default -> this.value.redirection(content);
		};
		return this.value;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// bitwise

	@Override
	public GioviParserVisitResult bitwiseAnd(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> this.value.bitwiseAnd(this.defaultValue);
			default -> super.bitwiseAnd(right);
		};
	}


	@Override
	public GioviParserVisitResult bitwiseOr(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> this.value.bitwiseOr(this.defaultValue);
			default -> super.bitwiseOr(right);
		};
	}


	@Override
	public GioviParserVisitResult bitwiseXor(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> this.value.bitwiseXor(this.defaultValue);
			default -> super.bitwiseXor(right);
		};
	}


	@Override
	public GioviParserVisitResult bitwiseLeftShift(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> this.value.bitwiseLeftShift(this.defaultValue);
			default -> super.bitwiseLeftShift(right);
		};
	}


	@Override
	public GioviParserVisitResult bitwiseRightShift(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> this.value.bitwiseRightShift(this.defaultValue);
			default -> super.bitwiseRightShift(right);
		};
	}


	@Override
	public GioviParserVisitResult bitwiseRightUnsignedShift(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> this.value.bitwiseRightUnsignedShift(this.defaultValue);
			default -> super.bitwiseRightUnsignedShift(right);
		};
	}


	//////////////////////////////////////////////////////////////////////////////////////////////// arithmetic

	@Override
	public GioviParserVisitResult plus(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> this.value.plus(this.defaultValue);
			default -> super.plus(right);
		};
	}

	@Override
	public GioviParserVisitResult minus(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> this.value.minus(this.defaultValue);
			default -> super.minus(right);
		};
	}

	@Override
	public GioviParserVisitResult multiply(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> this.value.multiply(this.defaultValue);
			default -> super.multiply(right);
		};
	}

	@Override
	public GioviParserVisitResult divide(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> this.value.divide(this.defaultValue);
			default -> super.divide(right);
		};
	}

	@Override
	public GioviParserVisitResult modulo(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> this.value.modulo(this.defaultValue);
			default -> super.modulo(right);
		};
	}

	@Override
	public GioviParserVisitResult power(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> this.value.power(this.defaultValue);
			default -> super.power(right);
		};
	}


	//////////////////////////////////////////////////////////////////////////////////////////////// boolean

	@Override
	public GioviParserVisitResult and(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> this.value.and(this.defaultValue);
			default -> super.and(right);
		};
	}

	@Override
	public GioviParserVisitResult or(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> this.value.or(this.defaultValue);
			default -> super.or(right);
		};
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// relational

	@Override
	public GioviParserVisitResult equal(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> this.value.equal(this.defaultValue);
			default -> super.equal(right);
		};
	}

	@Override
	public GioviParserVisitResult notEqual(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> this.value.notEqual(this.defaultValue);
			default -> super.notEqual(right);
		};
	}

	@Override
	public GioviParserVisitResult less(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> this.value.less(this.defaultValue);
			default -> super.less(right);
		};
	}

	@Override
	public GioviParserVisitResult lessEqual(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> this.value.lessEqual(this.defaultValue);
			default -> super.lessEqual(right);
		};
	}

	@Override
	public GioviParserVisitResult greater(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> this.value.greater(this.defaultValue);
			default -> super.greater(right);
		};
	}

	@Override
	public GioviParserVisitResult greaterEqual(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> this.value.greaterEqual(this.defaultValue);
			default -> super.greaterEqual(right);
		};
	}

	@Override
	public GioviParserVisitResult contains(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> this.value.contains(this.defaultValue);
			default -> super.contains(right);
		};
	}

	@Override
	public GioviParserVisitResult isContained(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> this.value.isContained(this.defaultValue);
			default -> super.isContained(right);
		};
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// access

	@Override
	public GioviParserVisitResult dot(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault _ -> this.defaultValue;
			default -> super.dot(right);
		};
	}

}
