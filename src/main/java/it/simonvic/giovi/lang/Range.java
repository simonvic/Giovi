package it.simonvic.giovi.lang;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import giovi.GioviParserVisitResult;
import giovi.Natural;
import giovi.lang.*;
import it.simonvic.giovi.exceptions.GioviException;
import it.simonvic.giovi.util.GioviTypes;

public record Range<T>(T start, T end, Number step, IterationStrategy<T> iterationStrategy)
	implements GioviParserVisitResult {

	@FunctionalInterface
	public interface IterationStrategy<V> {

		void iterate(Range<V> range, Consumer<V> callback);

	}

	public void iterate(Consumer<T> callback) {
		iterationStrategy.iterate(this, callback);
	}

	public List<T> expand() {
		List<T> expansion = new ArrayList<>();
		iterate(expansion::add);
		return expansion;
	}

	// TODO: move in util class?
	public List<? extends GioviValue<?>> expandAsGioviValues() {
		return expand().stream().map(GioviValue::of).toList();
	}

	public GioviList expandAsGioviList() {
		var gioviList = new ArrayList<GioviValue<?>>(
			expand().stream().map(GioviValue::of).toList());
		return new GioviList(GioviTypes.inferGioviType(gioviList), gioviList);
	}

	public static Range<?> of(GioviParserVisitResult start, GioviParserVisitResult end,
		GioviParserVisitResult step) {
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

		throw new GioviException("Invalid range: %s .. %s .. %s".formatted(start, end, step));
	}

	public static Range<Integer> of(int start, int end, int step) {
		return new Range<>(start, end, step, Range::iterateInt);
	}

	public static Range<Double> of(double start, double end, double step) {
		return new Range<>(start, end, step, Range::iterateReal);
	}

	public static Range<Natural> of(Natural start, Natural end, int step) {
		return new Range<>(start, end, step, Range::iterateNatural);
	}

	public static Range<String> of(String start, String end, int step) {
		return new Range<>(start, end, step, Range::iterateText);
	}

	public static void iterateReal(Range<Double> range, Consumer<Double> callback) {
		double start = range.start();
		double end = range.end();
		double step = range.step().doubleValue();
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

	public static void iterateInt(Range<Integer> range, Consumer<Integer> callback) {
		int start = range.start();
		int end = range.end();
		int step = range.step().intValue();
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

	public static void iterateNatural(Range<Natural> range, Consumer<Natural> callback) {
		int start = range.start().intValue();
		int end = range.end().intValue();
		int step = range.step().intValue();
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

	public static void iterateText(Range<String> range, Consumer<String> callback) {
		String start = range.start();
		String end = range.end();
		int step = range.step().intValue();
		if (start.length() != 1 || end.length() != 1) {
			throw new GioviException("A Range is only applicable to one characters");
		}
		if (!start.matches("[a-zA-Z]") || !end.matches("[a-zA-Z]")) {
			throw new GioviException(
				"A Range is not applicable to given characters: start=" + start + " end=" + end);
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
