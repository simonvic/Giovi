package it.simonvic.giovi.lang;

import it.simonvic.giovi.exceptions.OperatorNotSupported;
import it.simonvic.giovi.lang.type.Natural;
import it.simonvic.giovi.lang.values.*;

import java.util.LinkedHashMap;
import java.util.List;

public class GioviObject {

	public GioviValue<?> asGioviValue() {
		return (GioviValue<?>) this;
	}

	public GioviNumber<?> asGioviNumber() {
		return (GioviNumber<?>) this;
	}

	public GioviNatural asGioviNatural() {
		return asGioviNumber().naturalValue();
	}

	public GioviInt asGioviInt() {
		return asGioviNumber().intValue();
	}

	public GioviReal asGioviReal() {
		return asGioviNumber().realValue();
	}

	public GioviBool asGioviBool() {
		return (GioviBool) this;
	}

	public GioviText asGioviText() {
		return (GioviText) this;
	}

	public GioviList<?> asGioviList() {
		return (GioviList<?>) this;
	}

	public GioviMap<?, ?> asGioviMap() {
		return (GioviMap<?, ?>) this;
	}

	public Number asNumber() {
		return asGioviNumber().get();
	}

	public Natural asNatural() {
		return asGioviNatural().get();
	}

	public Integer asInt() {
		return asGioviInt().get();
	}

	public Double asDouble() {
		return asGioviReal().get();
	}

	public Boolean asBool() {
		return asGioviBool().get();
	}

	public String asString() {
		return asGioviText().get();
	}

	public List<?> asList() {
		return asGioviList().get();
	}

	public LinkedHashMap<?, ?> asMap() {
		return asGioviMap().get();
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
