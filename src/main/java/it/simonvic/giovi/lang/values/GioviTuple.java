package it.simonvic.giovi.lang.values;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import it.simonvic.giovi.lang.AccessKey;
import it.simonvic.giovi.lang.GioviParserVisitResult;
import it.simonvic.giovi.lang.Range;
import it.simonvic.giovi.lang.values.natives.Tuple;
import it.simonvic.giovi.util.GioviLists;

public final class GioviTuple extends GioviValue<Tuple> {

	public GioviTuple(Tuple value, Tuple defaultValue) {
		super(value, defaultValue);
	}

	public GioviTuple(Tuple value) {
		super(value, value.copy());
	}

	@Override
	public GioviTypeTuple getGioviType() {
		if (value instanceof Tuple.Named named) {
			var typesMap = new LinkedHashMap<String, GioviType<?>>();
			named.forEach(
				(identifier, fieldValue) -> typesMap.put(identifier, fieldValue.getGioviType()));
			return new GioviTypeTuple(typesMap);
		}
		var typesList = new ArrayList<GioviType<?>>();
		this.value.forEach(value -> typesList.add(value.getGioviType()));
		return new GioviTypeTuple(typesList);
	}

	@Override
	public GioviTuple copy() {
		return new GioviTuple(value.copy(), defaultValue.copy());
	}

	@Override
	public GioviValue<?> castTo(GioviType<?> type) {
		var thisType = this.getGioviType();
		if (!thisType.canUpcast(type)) {
			return super.castTo(type);
		}
		return switch (type) {
			case GioviTypeTuple otherType -> {
				var castedTuple = otherType.spawn();
				if (castedTuple.unbox() instanceof Tuple.Named named
					&& this.value instanceof Tuple.Named thisTuple) {
					thisTuple.forEach((identifier, fieldValue) -> named
						.getField(identifier)
						.box(fieldValue.copy()));
				} else {
					for (int i = 0; i < this.value.getFieldsCount(); i++) {
						castedTuple.unbox().getField(i).box(this.value.getField(i).copy());
					}
				}
				yield castedTuple;
			}
			default -> super.castTo(type);
		};
	}

	@Override
	public void box(GioviValue<?> gioviValue) {
		if (gioviValue instanceof GioviTuple tuple) {
			this.value = tuple.unbox();
		} else {
			super.box(gioviValue);
		}
	}

	@Override
	public void setDefaultValue(Tuple tuple) {
		defaultValue = tuple;
	}

	@Override
	public void setDefaultValue(GioviValue<?> defaultValue) {
		switch (defaultValue.unbox()) {
			case Tuple v -> setDefaultValue(v);
			default -> super.setDefaultValue(defaultValue);
		}
	}

	@Override
	public void boxDefault() {
		value = defaultValue.copy();
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// OPERATIONS
	//////////////////////////////////////////////////////////////////////////////////////////////// redirection

	@Override
	public GioviParserVisitResult redirection(GioviParserVisitResult content) {
		switch (content) {
			case GioviDefault v -> boxDefault();
			default -> super.redirection(content);
		}
		return this;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// arithmetic
	//////////////////////////////////////////////////////////////////////////////////////////////// boolean
	//////////////////////////////////////////////////////////////////////////////////////////////// relational

	@Override
	public GioviParserVisitResult equal(GioviParserVisitResult right) {
		return switch (right) {
			case GioviTuple r -> {
				if (this.value.getFieldsCount() != r.value.getFieldsCount()) {
					yield new GioviBool(false);
				}
				for (int i = 0; i < value.getFieldsCount(); i++) {
					var thisField = this.value.getField(i);
					var otherField = r.value.getField(i);
					var thisFieldType = thisField.getGioviType();
					var otherFieldType = otherField.getGioviType();
					if (!thisFieldType.equals(otherFieldType)) {
						if (!thisFieldType.canUpcast(otherFieldType)) {
							yield new GioviBool(false);
						}
						if (!thisField.equal(otherField.castTo(thisFieldType)).asNativeBool()) {
							yield new GioviBool(false);
						}
					}
					if (!thisField.equal(otherField).asNativeBool()) {
						yield new GioviBool(false);
					}
				}
				yield new GioviBool(true);
			}
			default -> new GioviBool(false);
		};
	}

	@Override
	public GioviParserVisitResult notEqual(GioviParserVisitResult right) {
		return equal(right).not();
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// access

	@Override
	public GioviParserVisitResult dot(GioviParserVisitResult right) {
		return switch (right) {
			case AccessKey r -> switch (value) {
				case Tuple.Named named -> named.getField(r.text());
				default -> super.dot(right);
			};
			default -> super.dot(right);
		};
	}

	@Override
	public GioviParserVisitResult access(GioviParserVisitResult right) {
		return switch (right) {
			case AccessKey r -> switch (r.text()) {
				case "%" -> new GioviInt(value.getFieldsCount());
				case "@" -> Range.of(0, value.getFieldsCount(), 1);
				case "*" -> switch (value) {
					case Tuple.Named named -> GioviLists.of(named.getFieldNames());
					default -> super.access(right);
				};
				default -> super.access(right);
			};
			case Range<?> range -> {
				if (range.getStart() instanceof Integer start
					&& range.getEnd() instanceof Integer end) {
					yield new GioviTuple(this.value.copy((Range<Integer>) range)); // TODO: RangeInteger
				}
				yield super.access(right);
			}
			case GioviInt i -> {
				int index = i.unbox();
				index = index >= 0 ? index : value.getFieldsCount() + index;
				yield value.getField(index);
			}
			default -> super.access(right);
		};
	}

}
