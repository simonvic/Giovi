package it.simonvic.giovi.lang.values;

import java.util.ArrayList;
import java.util.List;

import it.simonvic.giovi.exceptions.GioviException;
import it.simonvic.giovi.lang.Accessor;
import it.simonvic.giovi.lang.GioviObject;
import it.simonvic.giovi.lang.Range;

public final class GioviList extends GioviValue<List<GioviValue<?>>> {

	private final GioviType<?> entryType;

	public GioviList(GioviType<?> entryType) {
		this(entryType, new ArrayList<>(), new ArrayList<>());
	}

	public GioviList(List<GioviValue<?>> entries) {
		this(GioviType.inferGioviType(entries), entries, new ArrayList<>());
	}

	public GioviList(
		GioviType<?> entryType,
		List<GioviValue<?>> value,
		List<GioviValue<?>> defaultValue) {

		super(value, defaultValue);
		this.entryType = entryType;
		this.value.forEach(entry -> entry.castTo(this.entryType));
		this.defaultValue.forEach(entry -> entry.castTo(this.entryType));
	}


	public void add(GioviValue<?> entry) {
		value.add(entry.castTo(entryType));
	}

	public void addAll(List<? extends GioviValue<?>> entries) {
		entries.forEach(this::add);
	}

	public void remove(GioviValue<?> entry) {
		value.remove(entry);
	}

	public void removeAll(List<? extends GioviValue<?>> seq) {
		value.removeAll(seq);
	}

	public void clear() {
		value.clear();
	}

	public void init(List<GioviValue<?>> entries) {
		clear();
		addAll(entries);
	}

	public GioviType<?> getEntryType() {
		return entryType;
	}

	@Override
	public GioviType<?> getGioviType() {
		return new GioviTypeList(entryType);
	}

	@Override
	public GioviList copy() {
		var v = new ArrayList<GioviValue<?>>();
		this.value.forEach(value -> v.add(value.copy()));

		var d = new ArrayList<GioviValue<?>>();
		this.defaultValue.forEach(value -> v.add(value.copy()));

		return new GioviList(this.entryType, v, d);
	}

	@Override
	public GioviValue<?> castTo(GioviType<?> type) {
		if (type instanceof GioviTypeList t) {
			var list = new GioviList(t.getEntryType());
			this.value.forEach(list::add);
			return new GioviList(value);
		}
		return super.castTo(type);
	}

	@Override
	public void box(GioviValue<?> gioviValue) {
		switch (gioviValue) {
			case GioviList v -> value = v.unbox();
			default -> super.box(gioviValue);
		}
	}

	@Override
	public void setDefaultValue(GioviValue<?> defaultValue) {
		switch (defaultValue) {
			case GioviList v -> setDefaultValue(v.unbox());
			default -> super.setDefaultValue(defaultValue);
		}
	}

	@Override
	public void boxDefault() {
		this.init(defaultValue);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final GioviList other = (GioviList) obj;
		return this.value.equals(other.value);
	}

	//                                                                                OPERATORATION
	//                                                                                  redirection
	@Override
	public GioviObject redirection(GioviObject content) {
		switch (content) {
			case GioviDefault v -> boxDefault();
			case GioviList v -> this.init(v.unbox());
			default -> super.redirection(content);
		}
		return this;
	}

	@Override
	public GioviObject redirectionPlus(GioviObject right) {
		switch (right) {
			case GioviDefault v -> addAll(defaultValue);
			case GioviList v -> addAll(v.unbox());
			case GioviValue<?> v -> add(v);
			default -> super.redirectionPlus(right);
		}
		return this;
	}

	@Override
	public GioviObject redirectionMinus(GioviObject right) {
		switch (right) {
			case GioviDefault v -> removeAll(defaultValue);
			case GioviList v -> removeAll(v.unbox());
			case GioviValue<?> v -> remove(v);
			default -> super.redirectionPlus(right);
		}
		return this;
	}

	//                                                                                   arithmetic
	//                                                                                      boolean
	//                                                                                   relational
	@Override
	public GioviObject equal(GioviObject right) {
		return switch (right) {
			case GioviDefault v -> new GioviBool(value.equals(defaultValue));
			case GioviList v -> new GioviBool(value.equals(v.unbox()));
			default -> super.equal(right);
		};
	}

	@Override
	public GioviObject contains(GioviObject right) {
		return new GioviBool(value.contains(right.asGioviValue()));
	}

	//                                                                                       access
	@Override
	public GioviObject access(GioviObject right) {
		return switch (right) {
			case GioviValue<?> gioviValue -> switch (gioviValue) {
				case GioviInt index -> value.get(index.unbox());
				default -> throw new GioviException("Invalid accessor: " + right);
			};
			case Accessor accessor -> switch (accessor.get()) {
				case "%" -> new GioviInt(unbox().size());
				case "@" -> {
					GioviList indeces = new GioviList(new GioviTypePrimitive(GioviInt.class));
					indeces.addAll(Range.of(0, unbox().size() - 1, 1).expandAsGioviValues());
					yield indeces;
				}
				default -> throw new GioviException("Invalid accessor: " + accessor);
			};
			default -> throw new GioviException("Invalid accessor: " + right);
		};
	}

}
