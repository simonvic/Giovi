package it.simonvic.giovi.lang;

import it.simonvic.giovi.exceptions.OperatorNotSupported;
import it.simonvic.giovi.lang.type.GioviType;
import it.simonvic.giovi.lang.type.Natural;
import it.simonvic.giovi.lang.values.*;

import java.util.LinkedHashMap;
import java.util.List;

public class GioviObject {

	public GioviValue<?> asGioviValue() {
		return (GioviValue<?>) this;
	}

	public GioviType asGioviType() {
		return (GioviType) this;
	}

	public GioviStruct asGioviStruct() {
		return (GioviStruct) this;
	}

	public GioviCollection<?> asGioviCollection() {
		return (GioviCollection<?>) this;
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

	public GioviList asGioviList() {
		return (GioviList) this;
	}

	public GioviMap asGioviMap() {
		return (GioviMap) this;
	}

	public Number asNumber() {
		return asGioviNumber().unbox();
	}

	public Natural asNatural() {
		return asGioviNatural().unbox();
	}

	public Integer asInteger() {
		return asGioviInt().unbox();
	}

	public Double asDouble() {
		return asGioviReal().unbox();
	}

	public Boolean asBoolean() {
		return asGioviBool().unbox();
	}

	public String asString() {
		return asGioviText().unbox();
	}

	public List<?> asList() {
		return asGioviList().unbox();
	}

	public LinkedHashMap<?, ?> asMap() {
		return asGioviMap().unbox();
	}

	public GioviObject positive() {
		throw new OperatorNotSupported("+");
	}

	public GioviObject negative() {
		throw new OperatorNotSupported("-");
	}

	public GioviObject redirection(GioviObject content) {
		throw new OperatorNotSupported(">");
	}

	public GioviObject redirectionPlus(GioviObject right) {
		throw new OperatorNotSupported("+");
	}

	public GioviObject redirectionMinus(GioviObject right) {
		throw new OperatorNotSupported("-");
	}

	public GioviObject redirectionMultiply(GioviObject right) {
		throw new OperatorNotSupported("*");
	}

	public GioviObject redirectionDivide(GioviObject right) {
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


	public GioviObject multiply(GioviObject right) {
		throw new OperatorNotSupported("*");
	}

	public GioviObject divide(GioviObject right) {
		throw new OperatorNotSupported("*");
	}

	public GioviObject plus(GioviObject right) {
		throw new OperatorNotSupported("+");
	}

	public GioviObject minus(GioviObject right) {
		throw new OperatorNotSupported("-");
	}
}
