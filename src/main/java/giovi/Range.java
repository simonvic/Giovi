package giovi;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import giovi.lang.*;
import it.simonvic.giovi.Var;
import it.simonvic.giovi.exceptions.GioviException;
import it.simonvic.giovi.util.GioviTypes;

// TODO: implements iterable
public record Range<T>(T begin, T end, Number step, IterationStrategy<T> iterationStrategy)
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

	public List<? extends GioviValue<?>> expandAsGioviValues() {
		return expand().stream().map(this::toGioviValue).toList();
	}

	public List<? extends Var<?>> expandAsVar() {
		return expand().stream().map(this::toGioviValue).map(Var::new).toList();
	}

	private GioviValue<?> toGioviValue(T value) {
		return switch (value) {
			case Natural v -> new GioviNatural(v);
			case Integer v -> new GioviInt(v);
			case Double v -> new GioviReal(v);
			case String v -> new GioviText(v);
			default -> throw new GioviException("Unsupported value " + value + " in ranges");
		};
	}

	public GioviList expandAsGioviList() {
		var gioviList = new ArrayList<Var<?>>(
			expand().stream().map(Var::of).toList()
		);
		return new GioviList(new GioviTypeList(GioviTypes.inferGioviType(gioviList)), gioviList);
	}

	public static Range<?> of(
		GioviParserVisitResult begin,
		GioviParserVisitResult end,
		GioviParserVisitResult step
	) {
		if (step != null && !(step instanceof GioviNumber<?>)) {
			throw new GioviException("A step must be a valid number but got " + step);
		}

		Number stepNumber = step == null ? null : step.asNativeNumber();
		if (begin instanceof GioviReal || end instanceof GioviReal || step instanceof GioviReal) {
			return Range.of(
				begin.asNativeNumber().doubleValue(),
				end.asNativeNumber().doubleValue(),
				stepNumber == null ? 1d : stepNumber.doubleValue()
			);
		}

		int stepInt = step == null ? 1 : stepNumber.intValue();
		if (begin instanceof GioviInt s && end instanceof GioviInt e) {
			return Range.of(s.value(), e.value(), stepInt);
		}

		if (begin instanceof GioviNatural s && end instanceof GioviNatural e) {
			return Range.of(s.value(), e.value(), stepInt);
		}

		if (begin instanceof GioviText s && end instanceof GioviText e) {
			return Range.of(s.value(), e.value(), stepInt);
		}

		throw new GioviException("Invalid range: %s .. %s .. %s".formatted(begin, end, step));
	}

	public static Range<Integer> of(int begin, int end, int step) {
		return new Range<>(begin, end, step, Range::iterateInt);
	}

	public static Range<Double> of(double begin, double end, double step) {
		return new Range<>(begin, end, step, Range::iterateReal);
	}

	public static Range<Natural> of(Natural begin, Natural end, int step) {
		return new Range<>(begin, end, step, Range::iterateNatural);
	}

	public static Range<String> of(String begin, String end, int step) {
		return new Range<>(begin, end, step, Range::iterateText);
	}

	public static void iterateReal(Range<Double> range, Consumer<Double> callback) {
		double begin = range.begin();
		double end = range.end();
		double step = range.step().doubleValue();
		if (begin <= end) {
			for (double i = begin; i <= end; i += step) {
				callback.accept(i);
			}
		} else {
			for (double i = begin; i >= end; i -= step) {
				callback.accept(i);
			}
		}
	}

	public static void iterateInt(Range<Integer> range, Consumer<Integer> callback) {
		int begin = range.begin();
		int end = range.end();
		int step = range.step().intValue();
		if (begin <= end) {
			for (int i = begin; i <= end; i += step) {
				callback.accept(i);
			}
		} else {
			for (int i = begin; i >= end; i -= step) {
				callback.accept(i);
			}
		}
	}

	public static void iterateNatural(Range<Natural> range, Consumer<Natural> callback) {
		int begin = range.begin().intValue();
		int end = range.end().intValue();
		int step = range.step().intValue();
		if (begin <= end) {
			for (int i = begin; i <= end; i += step) {
				callback.accept(new Natural(i));
			}
		} else {
			for (int i = begin; i >= end; i -= step) {
				callback.accept(new Natural(i));
			}
		}
	}

	public static void iterateText(Range<String> range, Consumer<String> callback) {
		String begin = range.begin();
		String end = range.end();
		int step = range.step().intValue();
		if (begin.length() != 1 || end.length() != 1) {
			throw new GioviException("A Range is only applicable to one characters");
		}
		if (!begin.matches("[a-zA-Z]") || !end.matches("[a-zA-Z]")) {
			throw new GioviException(
				"A Range is not applicable to given characters: begin=" + begin + " end=" + end
			);
		}

		char beginChar = begin.toCharArray()[0];
		char endChar = end.toCharArray()[0];
		if (beginChar <= endChar) {
			for (char c = beginChar; c <= endChar; c += step) {
				if (Character.isAlphabetic(c)) {
					callback.accept(String.valueOf(c));
				}
			}
		} else {
			for (char c = beginChar; c >= endChar; c -= step) {
				if (Character.isAlphabetic(c)) {
					callback.accept(String.valueOf(c));
				}
			}
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(begin, end, step);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Range<?> other = (Range<?>) obj;
		return Objects.equals(begin, other.begin)
			&& Objects.equals(end, other.end)
			&& Objects.equals(step, other.step);
	}

	@Override
	public String toString() {
		return String.format("%s..%s..%s", begin, end, step);
	}


}
