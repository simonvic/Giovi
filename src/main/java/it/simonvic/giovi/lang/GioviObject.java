package it.simonvic.giovi.lang;

import it.simonvic.giovi.exceptions.OperatorNotSupported;
import it.simonvic.giovi.lang.type.Natural;
import it.simonvic.giovi.lang.values.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeMap;

public class GioviObject {

	public Value<?> asValue() {
		return (Value<?>) this;
	}

	protected NaturalValue asNaturalValue() {
		return (NaturalValue) this;
	}

	protected IntValue asIntValue() {
		return (IntValue) this;
	}

	protected RealValue asRealValue() {
		return (RealValue) this;
	}

	protected BoolValue asBoolValue() {
		return (BoolValue) this;
	}

	protected TextValue asTextValue() {
		return (TextValue) this;
	}

	protected ListValue<?> asListValue() {
		return (ListValue<?>) this;
	}

	public MapValue<?,?> asMapValue() {
		return (MapValue<?,?>) this;
	}

	public Natural asNatural() {
		return asNaturalValue().get();
	}

	public Integer asInt() {
		return asIntValue().get();
	}

	public Double asDouble() {
		return asRealValue().get();
	}

	public Boolean asBool() {
		return asBoolValue().get();
	}

	public String asString() {
		return asTextValue().get();
	}

	public List<?> asList() {
		return asListValue().get();
	}

	public LinkedHashMap<?, ?> asMap() {
		return asMapValue().get();
	}

	public GioviObject positive() {
		throw new OperatorNotSupported("+");
	}

	public GioviObject negative() {
		throw new OperatorNotSupported("-");
	}

	public GioviObject plus(GioviObject right) {
		throw new OperatorNotSupported("+");
	}

	public GioviObject minus(GioviObject right) {
		throw new OperatorNotSupported("-");
	}

	public GioviObject multiply(GioviObject right) {
		throw new OperatorNotSupported("*");
	}

	public GioviObject divide(GioviObject right) {
		throw new OperatorNotSupported("/");
	}

	public GioviObject modulo(GioviObject right) {
		throw new OperatorNotSupported("/");
	}

	public GioviObject power(GioviObject right) {
		throw new OperatorNotSupported("^");
	}

	public GioviObject lessEqual(GioviObject right) {
		throw new OperatorNotSupported("<=");
	}

	public GioviObject greaterEqual(GioviObject right) {
		throw new OperatorNotSupported(">=");
	}

	public GioviObject greater(GioviObject right) {
		throw new OperatorNotSupported(">");
	}

	public GioviObject less(GioviObject right) {
		throw new OperatorNotSupported("<");
	}

	public GioviObject not() {
		throw new OperatorNotSupported("!");
	}

	public GioviObject and(GioviObject right) {
		throw new OperatorNotSupported("&");
	}

	public GioviObject or(GioviObject right) {
		throw new OperatorNotSupported("|");
	}

	public GioviObject equal(GioviObject right) {
		throw new OperatorNotSupported("=");
	}

	public GioviObject notEqual(GioviObject right) {
		throw new OperatorNotSupported("!=");
	}

	public GioviObject isContained(GioviObject right) {
		return right.contains(this);
	}

	public GioviObject contains(GioviObject right) {
		throw new OperatorNotSupported("=~");
	}


}
