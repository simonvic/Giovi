package it.simonvic.giovi.lang;

import it.simonvic.giovi.exceptions.GioviException;
import it.simonvic.giovi.exceptions.OperatorNotSupported;
import it.simonvic.giovi.lang.type.Natural;
import it.simonvic.giovi.lang.values.*;

import java.util.ArrayList;
import java.util.List;

public class Sequence<T extends GioviValue<?>> extends GioviObject {

	private final List<T> list = new ArrayList<>();

	public static Sequence<? extends GioviValue<?>> of(GioviValue<?> start, GioviValue<?> end, GioviValue<?> step) {
		if (start instanceof GioviReal || end instanceof GioviReal || step instanceof GioviReal) {
			return Sequence.of(
				start.asNumber().doubleValue(),
				end.asNumber().doubleValue(),
				step == null ? 1d : step.asNumber().doubleValue());
		}

		if (start instanceof GioviInt startValue && end instanceof GioviInt endValue) {
			return Sequence.of(startValue.unbox(), endValue.unbox(), step == null ? 1 : step.asInteger());
		}

		if (start instanceof GioviNatural startValue && end instanceof GioviNatural endValue) {
			return Sequence.of(startValue.unbox(), endValue.unbox(), step == null ? 1 : step.asInteger());
		}

		if (start instanceof GioviText startValue && end instanceof GioviText endValue) {
			return Sequence.of(startValue.unbox(), endValue.unbox(), step == null ? 1 : step.asInteger());
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

	public static Sequence<GioviText> of(String start, String end, Integer step) {
		if (start.length() != 1 || end.length() != 1)
			throw new GioviException("sequence is only applicable to one characters");
		if (!start.matches("[a-zA-Z]") || !end.matches("[a-zA-Z]"))
			throw new GioviException("sequence is not applicable to given characters: start=" + start + " end=" + end);
		Sequence<GioviText> sequence = new Sequence<>();
		char startChar = start.toCharArray()[0];
		char endChar = end.toCharArray()[0];
		if (startChar <= endChar) {
			for (char c = startChar; c <= endChar; c += step) {
				if (Character.isAlphabetic(c)) {
					sequence.list.add(new GioviText(String.valueOf(c)));
				}
			}
		} else {
			for (char c = startChar; c >= endChar; c -= step) {
				if (Character.isAlphabetic(c)) {
					sequence.list.add(new GioviText(String.valueOf(c)));
				}
			}
		}
		return sequence;
	}


	public List<T> getValues() {
		return list;
	}
}
