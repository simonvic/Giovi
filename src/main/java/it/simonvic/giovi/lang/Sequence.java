package it.simonvic.giovi.lang;

import it.simonvic.giovi.exceptions.OperatorNotSupported;
import it.simonvic.giovi.lang.type.Natural;
import it.simonvic.giovi.lang.values.GioviInt;
import it.simonvic.giovi.lang.values.GioviNatural;
import it.simonvic.giovi.lang.values.GioviReal;
import it.simonvic.giovi.lang.values.GioviValue;

import java.util.ArrayList;
import java.util.List;

public class Sequence<T extends GioviValue<?>> extends GioviObject {

	private final List<T> list = new ArrayList<>();
	public static final char[] ALPHABET = "abcdefghijklmnopqrstuvwxyz".toCharArray();

	public static Sequence<? extends GioviValue<?>> of(GioviValue<?> start, GioviValue<?> end, GioviValue<?> step) {
		if (start instanceof GioviReal || end instanceof GioviReal || step instanceof GioviReal) {
			return Sequence.of(
				start.asNumber().doubleValue(),
				end.asNumber().doubleValue(),
				step == null ? 1d : step.asNumber().doubleValue());
		}

		if (start instanceof GioviInt startValue && end instanceof GioviInt endValue) {
			return Sequence.of(startValue.get(), endValue.get(), step == null ? 1 : step.asInt());
		}

		if (start instanceof GioviNatural startValue && end instanceof GioviNatural endValue) {
			return Sequence.of(startValue.get(), endValue.get(), step == null ? 1 : step.asInt());
		}

		throw new OperatorNotSupported("change this");
	}

	public static Sequence<GioviReal> of(Double start, Double end, Double step) {
		Sequence<GioviReal> sequence = new Sequence<>();
		if (start <= end) {
			for (Double i = start; i <= end; i += step) {
				sequence.list.add(new GioviReal(i));
			}
		} else {
			for (Double i = start; i >= end; i -= step) {
				sequence.list.add(new GioviReal(i));
			}
		}
		return sequence;
	}


	public static Sequence<GioviInt> of(Integer start, Integer end, Integer step) {
		Sequence<GioviInt> sequence = new Sequence<>();

		if (start <= end) {
			for (Integer i = start; i <= end; i += step) {
				sequence.list.add(new GioviInt(i));
			}
		} else {
			for (Integer i = start; i >= end; i -= step) {
				sequence.list.add(new GioviInt(i));
			}
		}
		return sequence;
	}


	public static Sequence<GioviNatural> of(Natural start, Natural end, Integer step) {
		Sequence<GioviNatural> sequence = new Sequence<>();
		int startInt = start.intValue();
		int endInt = end.intValue();
		if (startInt <= endInt) {
			for (int i = startInt; i <= endInt; i += step) {
				sequence.list.add(new GioviNatural(new Natural(i)));
			}
		} else {
			for (int i = startInt; i >= endInt; i -= step) {
				sequence.list.add(new GioviNatural(new Natural(i)));
			}
		}
		return sequence;
	}


	public List<T> getValues() {
		return list;
	}
}
