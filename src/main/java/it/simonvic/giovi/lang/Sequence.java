package it.simonvic.giovi.lang;

import it.simonvic.giovi.exceptions.OperatorNotSupported;

import java.util.ArrayList;
import java.util.List;

public class Sequence {


	public static final char[] ALPHABET = "abcdefghijklmnopqrstuvwxyz".toCharArray();

	public static List<Object> of(Object start, Object end, Object step) {
		if (start instanceof Double || end instanceof Double || step instanceof Double) {
			return Sequence.of(
				Double.parseDouble(start.toString()),
				Double.parseDouble(end.toString()),
				step == null ? 1 : Double.parseDouble(step.toString()));
		}

		if (start instanceof Integer startInt && end instanceof Integer endInt) {
			return Sequence.of(startInt, endInt, step == null ? 1 : (int) step);
		}

		if (start instanceof String startString && end instanceof String endString) {
			return Sequence.of(startString, endString, step == null ? 1 : (int) step);
		}
		throw new OperatorNotSupported("change this");
	}

	public static List<Object> of(Double start, Double end, Double step) {
		List<Object> sequence = new ArrayList<>();
		if (start <= end) {
			for (Double i = start; i <= end; i += step) {
				sequence.add(i);
			}
		} else {
			for (Double i = start; i >= end; i -= step) {
				sequence.add(i);
			}
		}
		return sequence;
	}


	public static List<Object> of(Integer start, Integer end, Integer step) {
		List<Object> sequence = new ArrayList<>();

		if (start <= end) {
			for (Integer i = start; i <= end; i += step) {
				sequence.add(i);
			}
		} else {
			for (Integer i = start; i >= end; i -= step) {
				sequence.add(i);
			}
		}
		return sequence;
	}

	public static List<Object> of(String start, String end, Integer step) {
		List<Object> sequence = new ArrayList<>();
//		for (int i = 0; i < ALPHABET.length; i+=step) {
//			sequence.add(ALPHABET[i]);
//		}
		return sequence;
	}


}
