package it.simonvic.giovi.lang;

import it.simonvic.giovi.exceptions.OperatorNotSupported;

import java.util.List;

public class Operation {
	public static Object add(Object left, Object right) {

		if (left instanceof String || right instanceof String) {
			return "" + left + right;
		}

		if (left instanceof Integer l && right instanceof Integer r) {
			return l + r;
		}

		if (left instanceof Double l && right instanceof Double r) {
			return l + r;
		}

		if (left instanceof Integer l && right instanceof Double r) {
			return l + r;
		}

		if (left instanceof Double l && right instanceof Integer r) {
			return l + r;
		}

		throw new OperatorNotSupported("+");
	}

	public static Object subtract(Object left, Object right) {

		if (left instanceof String l) {
			return l.replace((String) right, "");
		}

		if (left instanceof Integer l && right instanceof Integer r) {
			return l - r;
		}

		if (left instanceof Double l && right instanceof Double r) {
			return l - r;
		}

		if (left instanceof Integer l && right instanceof Double r) {
			return l - r;
		}

		if (left instanceof Double l && right instanceof Integer r) {
			return l - r;
		}

		throw new OperatorNotSupported("-");
	}

	public static Object multiply(Object left, Object right) {

		if (left instanceof Integer l && right instanceof Integer r) {
			return l * r;
		}

		if (left instanceof Double l && right instanceof Double r) {
			return l * r;
		}

		if (left instanceof Integer l && right instanceof Double r) {
			return l * r;
		}

		if (left instanceof Double l && right instanceof Integer r) {
			return l * r;
		}

		throw new OperatorNotSupported("*");
	}

	public static Object divide(Object left, Object right) {

		if (left instanceof Integer l && right instanceof Integer r) {
			return l / r;
		}

		if (left instanceof Double l && right instanceof Double r) {
			return l / r;
		}

		if (left instanceof Integer l && right instanceof Double r) {
			return l / r;
		}

		if (left instanceof Double l && right instanceof Integer r) {
			return l / r;
		}

		throw new OperatorNotSupported("/");
	}

	public static Object modulo(Object left, Object right) {
		if (left instanceof Integer l && right instanceof Integer r) {
			return l % r;
		}

		if (left instanceof Double l && right instanceof Double r) {
			return l % r;
		}

		if (left instanceof Integer l && right instanceof Double r) {
			return l % r;
		}

		if (left instanceof Double l && right instanceof Integer r) {
			return l % r;
		}

		throw new OperatorNotSupported("%");
	}

	public static Object power(Object base, Object exponent) {
		if (base instanceof Integer b && exponent instanceof Integer e) {
			return Math.pow(b, e);
		}

		if (base instanceof Double b && exponent instanceof Double e) {
			return Math.pow(b, e);
		}

		if (base instanceof Integer b && exponent instanceof Double e) {
			return Math.pow(b, e);
		}

		if (base instanceof Double b && exponent instanceof Integer e) {
			return Math.pow(b, e);
		}

		throw new OperatorNotSupported("^");
	}

	public static boolean lessEqual(Object left, Object right) {
		if (left instanceof String l && right instanceof String r) {
			return l.compareTo(r) <= 0;
		}

		if (left instanceof Integer l && right instanceof Integer r) {
			return l <= r;
		}

		if (left instanceof Double l && right instanceof Double r) {
			return l <= r;
		}

		if (left instanceof Integer l && right instanceof Double r) {
			return l <= r;
		}

		if (left instanceof Double l && right instanceof Integer r) {
			return l <= r;
		}

		throw new OperatorNotSupported("<=");
	}

	public static boolean greaterEqual(Object left, Object right) {
		if (left instanceof String l && right instanceof String r) {
			return l.compareTo(r) >= 0;
		}

		if (left instanceof Integer l && right instanceof Integer r) {
			return l >= r;
		}

		if (left instanceof Double l && right instanceof Double r) {
			return l >= r;
		}

		if (left instanceof Integer l && right instanceof Double r) {
			return l >= r;
		}

		if (left instanceof Double l && right instanceof Integer r) {
			return l >= r;
		}

		throw new OperatorNotSupported(">=");
	}

	public static boolean greater(Object left, Object right) {
		if (left instanceof String l && right instanceof String r) {
			return l.compareTo(r) > 0;
		}

		if (left instanceof Integer l && right instanceof Integer r) {
			return l > r;
		}

		if (left instanceof Double l && right instanceof Double r) {
			return l > r;
		}

		if (left instanceof Integer l && right instanceof Double r) {
			return l > r;
		}

		if (left instanceof Double l && right instanceof Integer r) {
			return l > r;
		}

		throw new OperatorNotSupported(">");
	}

	public static boolean less(Object left, Object right) {
		if (left instanceof String l && right instanceof String r) {
			return l.compareTo(r) < 0;
		}

		if (left instanceof Integer l && right instanceof Integer r) {
			return l < r;
		}

		if (left instanceof Double l && right instanceof Double r) {
			return l < r;
		}

		if (left instanceof Integer l && right instanceof Double r) {
			return l < r;
		}

		if (left instanceof Double l && right instanceof Integer r) {
			return l < r;
		}

		throw new OperatorNotSupported("<");
	}

	public static boolean equal(Object left, Object right) {
		if (left instanceof String l && right instanceof String r) {
			return l.equals(r);
		}

		if (left instanceof Boolean l && right instanceof Boolean r) {
			return l.equals(r);
		}

		if (left instanceof Integer l && right instanceof Integer r) {
			return l.equals(r);
		}

		if (left instanceof Double l && right instanceof Double r) {
			return l.equals(r);
		}

		if (left instanceof Integer l && right instanceof Double r) {
			return l.doubleValue() == r;
		}

		if (left instanceof Double l && right instanceof Integer r) {
			return l.equals(r.doubleValue());
		}

		throw new OperatorNotSupported("=");
	}

	public static boolean notEqual(Object left, Object right) {
		return !equal(left, right);
	}

	public static boolean isContained(Object left, Object right) {
		return contains(right, left);
	}

	public static boolean contains(Object left, Object right) {
		if (left instanceof String l && right instanceof String r) {
			return l.contains(r);
		}

		if (left instanceof List l) {
			return l.contains(right);
		}

		throw new OperatorNotSupported("~=");
	}
}
