package it.simonvic.giovi.lang.values;

import it.simonvic.giovi.lang.GioviObject;
import it.simonvic.giovi.lang.Sequence;

import java.util.ArrayList;
import java.util.List;

public class ListValue<T extends GioviObject> extends Value<List<T>> {

	public ListValue(List<T> value) {
		super(value);
	}

	public ListValue() {
		super(new ArrayList<T>());
	}

	@Override
	public GioviObject contains(GioviObject right) {
		return new BoolValue(value.contains(right));
	}

	public void add(T entry) {
		value.add(entry);
	}

	public void addAll(List<? extends T> seq) {
		value.addAll(seq);
	}
}
