package it.simonvic.giovi.lang;

import it.simonvic.giovi.exceptions.OperatorNotSupported;
import it.simonvic.giovi.lang.type.Natural;
import it.simonvic.giovi.lang.values.IntValue;
import it.simonvic.giovi.lang.values.NaturalValue;
import it.simonvic.giovi.lang.values.RealValue;
import it.simonvic.giovi.lang.values.Value;

import java.util.ArrayList;
import java.util.List;

public class Sequence<T extends Value<?>> extends GioviObject {

	private final List<T> list = new ArrayList<>();
	public static final char[] ALPHABET = "abcdefghijklmnopqrstuvwxyz".toCharArray();

	public static Sequence<? extends Value<?>> of(Value<?> start, Value<?> end, Value<?> step) {
		if (start instanceof RealValue || end instanceof RealValue || step instanceof RealValue) {
			return Sequence.of(
				start.asDouble(),
				end.asDouble(),
				step == null ? 1d : step.asDouble());
		}

		if (start instanceof IntValue startValue && end instanceof IntValue endValue) {
			return Sequence.of(startValue.get(), endValue.get(), step == null ? 1 : step.asInt());
		}

		if (start instanceof NaturalValue startValue && end instanceof NaturalValue endValue) {
			return Sequence.of(startValue.get(), endValue.get(), step == null ? 1 : step.asInt());
		}

		throw new OperatorNotSupported("change this");
	}

	public static Sequence<RealValue> of(Double start, Double end, Double step) {
		Sequence<RealValue> sequence = new Sequence<>();
		if (start <= end) {
			for (Double i = start; i <= end; i += step) {
				sequence.list.add(new RealValue(i));
			}
		} else {
			for (Double i = start; i >= end; i -= step) {
				sequence.list.add(new RealValue(i));
			}
		}
		return sequence;
	}


	public static Sequence<IntValue> of(Integer start, Integer end, Integer step) {
		Sequence<IntValue> sequence = new Sequence<>();

		if (start <= end) {
			for (Integer i = start; i <= end; i += step) {
				sequence.list.add(new IntValue(i));
			}
		} else {
			for (Integer i = start; i >= end; i -= step) {
				sequence.list.add(new IntValue(i));
			}
		}
		return sequence;
	}


	public static Sequence<NaturalValue> of(Natural start, Natural end, Integer step) {
		Sequence<NaturalValue> sequence = new Sequence<>();
		int startInt = start.intValue();
		int endInt = end.intValue();
		if (startInt <= endInt) {
			for (int i = startInt; i <= endInt; i += step) {
				sequence.list.add(new NaturalValue(new Natural(i)));
			}
		} else {
			for (int i = startInt; i >= endInt; i -= step) {
				sequence.list.add(new NaturalValue(new Natural(i)));
			}
		}
		return sequence;
	}


	public List<T> getValues() {
		return list;
	}
}
