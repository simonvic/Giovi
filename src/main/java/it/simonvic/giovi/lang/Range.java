package it.simonvic.giovi.lang;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import it.simonvic.giovi.exceptions.GioviException;
import it.simonvic.giovi.lang.values.*;
import it.simonvic.giovi.lang.values.natives.Natural;

public class Range<T> extends GioviObject {

	@FunctionalInterface
	public interface IterationStrategy<V> {
		void iterate(Range<V> sequence, Consumer<V> callback);
	}

	private final T start;
	private final T end;
	private final Number step;
	private final IterationStrategy<T> iterationStrategy;
	private List<T> expansion;

	public Range(T start, T end, Number step, IterationStrategy<T> iterationStrategy) {
		this.start = start;
		this.end = end;
		this.step = step;
		this.iterationStrategy = iterationStrategy;
	}

	public T getStart() {
		return start;
	}

	public T getEnd() {
		return end;
	}

	public Number getStep() {
		return step;
	}

	public void iterate(Consumer<T> callback) {
		iterationStrategy.iterate(this, callback);
	}

	public List<T> expand() {
		if (expansion == null) {
			expansion = new ArrayList<>();
			iterate(expansion::add);
		}
		return expansion;
	}

	// TODO: move in util class?
	public List<? extends GioviValue<?>> expandAsGioviValues() {
		return expand().stream().map(GioviValue::of).toList();
	}

	public static Range<?> of(GioviParserVisitResult start, GioviParserVisitResult end, GioviParserVisitResult step) {
		if (step != null && !(step instanceof GioviNumber<?>)) {
			throw new GioviException("A step must be a valid number but got " + step);
		}

		Number stepNumber = step == null ? null : step.asNativeNumber();
		if (start instanceof GioviReal || end instanceof GioviReal || step instanceof GioviReal) {
			return Range.of(
				start.asNativeNumber().doubleValue(),
				end.asNativeNumber().doubleValue(),
				stepNumber == null ? 1d : stepNumber.doubleValue());
		}

		int stepInt = step == null ? 1 : stepNumber.intValue();
		if (start instanceof GioviInt s && end instanceof GioviInt e) {
			return Range.of(s.unbox(), e.unbox(), stepInt);
		}

		if (start instanceof GioviNatural s && end instanceof GioviNatural e) {
			return Range.of(s.unbox(), e.unbox(), stepInt);
		}

		if (start instanceof GioviText s && end instanceof GioviText e) {
			return Range.of(s.unbox(), e.unbox(), stepInt);
		}

		throw new GioviException("Invalid sequence: %s .. %s .. %s".formatted(start, end, step));
	}

	public static Range<Integer> of(int start, int end, int step) {
		return new Range<Integer>(start, end, step, Range::iterateInt);
	}

	public static Range<Double> of(double start, double end, double step) {
		return new Range<Double>(start, end, step, Range::iterateReal);
	}

	public static Range<Natural> of(Natural start, Natural end, int step) {
		return new Range<Natural>(start, end, step, Range::iterateNatural);
	}

	public static Range<String> of(String start, String end, int step) {
		return new Range<String>(start, end, step, Range::iterateText);
	}

	public static void iterateReal(Range<Double> sequence, Consumer<Double> callback) {
		double start = sequence.getStart();
		double end = sequence.getEnd();
		double step = sequence.getStep().doubleValue();
		if (start <= end) {
			for (double i = start; i <= end; i += step) {
				callback.accept(i);
			}
		} else {
			for (double i = start; i >= end; i -= step) {
				callback.accept(i);
			}
		}
	}

	public static void iterateInt(Range<Integer> sequence, Consumer<Integer> callback) {
		int start = sequence.getStart();
		int end = sequence.getEnd();
		int step = sequence.getStep().intValue();
		if (start <= end) {
			for (int i = start; i <= end; i += step) {
				callback.accept(i);
			}
		} else {
			for (int i = start; i >= end; i -= step) {
				callback.accept(i);
			}
		}
	}

	public static void iterateNatural(Range<Natural> sequence, Consumer<Natural> callback) {
		int start = sequence.getStart().intValue();
		int end = sequence.getEnd().intValue();
		int step = sequence.getStep().intValue();
		if (start <= end) {
			for (int i = start; i <= end; i += step) {
				callback.accept(new Natural(i));
			}
		} else {
			for (int i = start; i >= end; i -= step) {
				callback.accept(new Natural(i));
			}
		}
	}

	public static void iterateText(Range<String> sequence, Consumer<String> callback) {
		String start = sequence.getStart();
		String end = sequence.getEnd();
		int step = sequence.getStep().intValue();
		if (start.length() != 1 || end.length() != 1) {
			throw new GioviException("sequence is only applicable to one characters");
		}
		if (!start.matches("[a-zA-Z]") || !end.matches("[a-zA-Z]")) {
			throw new GioviException(
				"sequence is not applicable to given characters: start=" + start + " end=" + end);
		}

		char startChar = start.toCharArray()[0];
		char endChar = end.toCharArray()[0];
		if (startChar <= endChar) {
			for (char c = startChar; c <= endChar; c += step) {
				if (Character.isAlphabetic(c)) {
					callback.accept(String.valueOf(c));
				}
			}
		} else {
			for (char c = startChar; c >= endChar; c -= step) {
				if (Character.isAlphabetic(c)) {
					callback.accept(String.valueOf(c));
				}
			}
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((start == null) ? 0 : start.hashCode());
		result = prime * result + ((end == null) ? 0 : end.hashCode());
		result = prime * result + ((step == null) ? 0 : step.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Range<?> other = (Range<?>) obj;
		if (start == null) {
			if (other.start != null) return false;
		} else if (!start.equals(other.start)) return false;
		if (end == null) {
			if (other.end != null) return false;
		} else if (!end.equals(other.end)) return false;
		if (step == null) {
			if (other.step != null) return false;
		} else if (!step.equals(other.step)) return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("%s..%s..%s", start, end, step);
	}


}
