package giovi.lang;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import giovi.*;
import it.simonvic.giovi.util.GioviLists;

public final class GioviTuple extends GioviValue<Tuple> {

	public GioviTuple(Tuple value, Tuple defaultValue) {
		super(value, defaultValue);
	}

	public GioviTuple(Tuple value) {
		super(value, new Tuple());
	}

	public GioviTuple() {
		super(new Tuple(), new Tuple());
	}

	@Override
	public GioviTypeTuple getGioviType() {
		if (value instanceof TupleNamed named) {
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

	//////////////////////////////////////////////////////////////////////////////////////////////// OPERATIONS

	//////////////////////////////////////////////////////////////////////////////////////////////// cast

	@Override
	public GioviParserVisitResult as(GioviParserVisitResult right) {
		if (!(right instanceof GioviType<?> type)) return super.as(right);
		var thisType = this.getGioviType();
		if (!thisType.canWidenTo(type)) {
			return super.as(type);
		}
		return switch (type) {
			case GioviTypeTuple otherType -> {
				var castedTuple = otherType.spawn();
				if (castedTuple.unbox() instanceof TupleNamed named
					&& this.value instanceof TupleNamed thisTuple) {
					thisTuple.forEach((identifier, fieldValue) -> named
						.getField(identifier)
						.redirection(fieldValue));
				} else {
					for (int i = 0; i < this.value.getFieldsCount(); i++) {
						castedTuple.unbox().getField(i).redirection(this.value.getField(i));
					}
				}
				yield castedTuple;
			}
			default -> super.as(type);
		};
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// redirection

	@Override
	public GioviParserVisitResult redirection(GioviParserVisitResult content) {
		switch (content) {
			case GioviDefault v -> value = defaultValue.copy();
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
						if (!thisFieldType.canWidenTo(otherFieldType)) {
							yield new GioviBool(false);
						}
						if (!thisField.equal(otherField.as(thisFieldType)).asNativeBool()) {
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
			case GioviDefault __ -> new GioviTuple(defaultValue.copy());
			case FieldKey(var identifier) -> switch (value) {
				case TupleNamed named -> named.getField(identifier);
				default -> super.dot(right);
			};
			default -> super.dot(right);
		};
	}

	@Override
	public GioviParserVisitResult access(GioviParserVisitResult right) {
		return switch (right) {
			case AccessKey accessKey -> switch (accessKey) {
				case COUNT -> new GioviInt(value.getFieldsCount());
				case KEYS -> Range.of(0, value.getFieldsCount(), 1);
				case VALUES -> switch (value) {
					case TupleNamed named -> GioviLists.of(named.getFieldNames());
					default -> super.access(right);
				};
			};
			case Range(Integer start, Integer end, Integer step, var strategy) -> new GioviTuple(
				this.value.copy(Range.of(start, end, step)));
			case GioviInt i -> {
				int index = i.unbox();
				index = index >= 0 ? index : value.getFieldsCount() + index;
				yield value.getField(index);
			}
			default -> super.access(right);
		};
	}

}
