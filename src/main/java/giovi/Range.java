package giovi;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import giovi.lang.*;
import it.simonvic.giovi.Entry;
import it.simonvic.giovi.exceptions.GioviException;
import it.simonvic.giovi.util.GioviTypes;

public record Range<J>(J begin, J end, Number step)
	implements GioviParserVisitResult, Iterable<J> {

	/**
	 * Iterate on the range and invoke the callback on each iteration
	 * @param callback
	 */
	@Deprecated
	public void iterate(Consumer<J> callback) {
		forEach(callback);
	}

	/**
	 * Expand the range to a list.
	 * For example 0..3 becomes List.of(0,1,2,3)
	 * @return list of values
	 * @see Range#expand()
	 */
	public List<J> expand() {
		var expansion = new ArrayList<J>();
		forEach(expansion::add);
		return expansion;
	}

	/**
	 * Expand the range to a list of giovi values
	 * @return list of giovi values
	 * @see Range#expand()
	 */
	public List<? extends GioviValue<?>> expandAsGioviValues() {
		return expand().stream().map(this::toGioviValue).toList();
	}

	/**
	 * Expand the range to a list of VarEntry of giovi values
	 * @return list of VarEntry
	 * @see Range#expand()
	 */
	public List<? extends Entry<?>> expandAsVarEntries() {
		return expand().stream().map(this::toGioviValue).map(Entry::new).toList();
	}

	private GioviValue<?> toGioviValue(J value) {
		return switch (value) {
			case Natural v -> new GioviNatural(v);
			case Integer v -> new GioviInt(v);
			case Double v -> new GioviReal(v);
			case String v -> new GioviText(v);
			default -> throw new GioviException("Unsupported value " + value + " in ranges");
		};
	}

	/**
	 * Expand the range to a giovi list
	 * @return giovi list
	 * @see Range#expand()
	 */
	public GioviList expandAsGioviList() {
		var gioviList = new ArrayList<Entry<?>>(expandAsVarEntries());
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
		return new Range<>(begin, end, step);
	}

	public static Range<Double> of(double begin, double end, double step) {
		return new Range<>(begin, end, step);
	}

	public static Range<Natural> of(Natural begin, Natural end, int step) {
		return new Range<>(begin, end, step);
	}

	public static Range<String> of(String begin, String end, int step) {
		return new Range<>(begin, end, step);
	}

	@Override
	public String toString() {
		return String.format("%s..%s..%s", begin, end, step);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Iterator<J> iterator() {
		return (Iterator<J>) switch (begin) {
			case String _ -> new IteratorText();
			case Natural _ -> new IteratorNatural();
			case Integer _ -> new IteratorInt();
			case Double _ -> new IteratorReal();
			default -> throw new GioviException("Unsupported iterator");
		};

	}

	private class IteratorText implements Iterator<String> {

		private final char begin;
		private final char end;
		private final int step;
		private final boolean reverse;
		private char cursor;

		public IteratorText() {
			this.begin = ((String) begin()).toCharArray()[0];
			this.end = ((String) end()).toCharArray()[0];
			this.step = step().intValue();
			this.reverse = (end < begin);
			this.cursor = (reverse) // range is inclusive, so we start one step behind
				? (char) (begin + step)
				: (char) (begin - step);
		}

		@Override
		public boolean hasNext() {
			return (reverse)
				? (cursor >= (end + step))
				: (cursor <= (end - step));
		}

		@Override
		public String next() {
			cursor += (reverse) ? -step : step;
			return String.valueOf(cursor);
		}

	}

	private class IteratorNatural implements Iterator<Natural> {

		private final int begin;
		private final int end;
		private final int step;
		private final boolean reverse;
		private int cursor;

		public IteratorNatural() {
			this.begin = ((Natural) begin()).intValue();
			this.end = ((Natural) end()).intValue();
			this.step = step().intValue();
			this.reverse = (end < begin);
			this.cursor = (reverse) // range is inclusive, so we start one step behind
				? (begin + step)
				: (begin - step);
		}

		@Override
		public boolean hasNext() {
			return (reverse)
				? (cursor >= (end + step))
				: (cursor <= (end - step));
		}

		@Override
		public Natural next() {
			cursor += (reverse) ? -step : step;
			return new Natural(cursor);
		}


	}

	private class IteratorInt implements Iterator<Integer> {

		private final int begin;
		private final int end;
		private final int step;
		private final boolean reverse;
		private int cursor;

		public IteratorInt() {
			this.begin = (int) begin();
			this.end = (int) end();
			this.step = step().intValue();
			this.reverse = (end < begin);
			this.cursor = (reverse) // range is inclusive, so we start one step behind
				? (begin + step)
				: (begin - step);
		}

		@Override
		public boolean hasNext() {
			return (reverse)
				? (cursor >= (end + step))
				: (cursor <= (end - step));
		}

		@Override
		public Integer next() {
			cursor += (reverse) ? -step : step;
			return cursor;
		}

	}

	private class IteratorReal implements Iterator<Double> {

		private final double begin;
		private final double end;
		private final double step;
		private final boolean reverse;
		private double cursor;

		public IteratorReal() {
			this.begin = (double) begin();
			this.end = (double) end();
			this.step = step().doubleValue();
			this.reverse = (end < begin);
			this.cursor = (reverse) // range is inclusive, so we start one step behind
				? (begin + step)
				: (begin - step);
		}

		@Override
		public boolean hasNext() {
			return (reverse)
				? (cursor >= (end + step))
				: (cursor <= (end - step));
		}

		@Override
		public Double next() {
			cursor += (reverse) ? -step : step;
			return cursor;
		}

	}

}
