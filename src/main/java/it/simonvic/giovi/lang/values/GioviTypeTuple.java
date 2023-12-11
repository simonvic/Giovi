package it.simonvic.giovi.lang.values;

import java.util.ArrayList;
import java.util.List;

import it.simonvic.giovi.lang.values.natives.Tuple;

public final class GioviTypeTuple extends GioviType<GioviTuple> {

	private final List<GioviType<?>> fieldTypes;

	public GioviTypeTuple(List<GioviType<?>> fieldTypes) {
		super(GioviTuple.class);
		this.fieldTypes = fieldTypes;
	}

	public GioviTypeTuple() {
		this(new ArrayList<>());
	}

	@Override
	public String toString() {
		var sb = new StringBuilder("Tuple<");
		fieldTypes.forEach(type -> sb.append(type.getSimpleName() + ","));
		return sb.toString() + ">";
	}

	@Override
	public boolean canUpcast(GioviType<?> to) {
		return switch (to) {
			case GioviTypeTuple t -> {
				if (this.fieldTypes.size() > t.fieldTypes.size()) {
					yield false;
				}
				for (int i = 0; i < this.fieldTypes.size(); i++) {
					if (!this.fieldTypes.get(i).canUpcast(t.fieldTypes.get(i))) {
						yield false;
					}
				}
				yield true;
			}
			default -> false;
		};
	}

	@Override
	public GioviValue<?> spawn() {
		var values = new ArrayList<GioviValue<?>>();
		for (var fieldType : fieldTypes) {
			values.add(fieldType.spawn());
		}
		return new GioviTuple(new Tuple.Unnamed(values));
	}

	@Override
	public GioviTypeTuple copy() {
		return new GioviTypeTuple(fieldTypes);
	}

}

