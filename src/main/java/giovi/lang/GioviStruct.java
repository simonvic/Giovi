package giovi.lang;

import java.util.LinkedHashMap;

import giovi.*;
import it.simonvic.giovi.lang.GioviFieldDefinition;
import it.simonvic.giovi.lang.GioviStructDefinition;
import it.simonvic.giovi.util.GioviLists;

public final class GioviStruct extends GioviValue<Struct> {

	public GioviStruct(Struct value, Struct defaultValue) {
		super(value, defaultValue);
	}

	public GioviStruct(Struct value) {
		super(value, value.copy());
	}


	public int getFieldsCount() {
		return value.getFieldsCount();
	}

	public GioviValue<?> getField(String identifier) {
		return value.getField(identifier);
	}

	public void boxField(int index, GioviValue<?> fieldValue) {
		value.getField(index).redirection(fieldValue);
	}

	public void boxField(String field, GioviValue<?> fieldValue) {
		value.getField(field).redirection(fieldValue);
	}

	public boolean containsField(String field) {
		return value.containsField(field);
	}

	// TODO: everywhere, clean up redirections, which should copy lhs and
	// invoke low level setters, which should directly assign to the internal
	// value

	@Override
	public GioviTypeStruct getGioviType() {
		var fields = new LinkedHashMap<String, GioviFieldDefinition>();
		this.value.forEach((identifier, fieldValue) -> fields.put(
			identifier,
			new GioviFieldDefinition(
				identifier,
				fieldValue.getGioviType(),
				GioviValue.of(fieldValue.unboxDefault()))));
		return new GioviTypeStruct(new GioviStructDefinition(this.value.getTypename(), fields));
	}

	@Override
	public GioviStruct copy() {
		return new GioviStruct(value.copy(), defaultValue.copy());
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
			case GioviTypeStruct otherType -> {
				var castedTuple = otherType.spawn();
				this.value.forEach((identifier, fieldValue) -> castedTuple
					.boxField(identifier, fieldValue.copy()));
				yield castedTuple;
			}
			default -> super.as(type);
		};
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// redirection

	@Override
	public GioviParserVisitResult redirection(GioviParserVisitResult content) {
		switch (content) {
			case GioviStruct gioviStruct -> {
				if (!gioviStruct.value.isSubset(this.value)) {
					return super.redirection(content);
				}
				gioviStruct.unbox().forEach((identifier, fieldValue) -> this.boxField(identifier,
					fieldValue.copy()));
			}
			case GioviTuple gioviTuple -> {
				if (!gioviTuple.value.isSubset(this.value)) {
					return super.redirection(content);
				}
				switch (gioviTuple.unbox()) {
					case TupleNamed t -> t.forEach(
						(identifier, fieldValue) -> this.boxField(identifier, fieldValue.copy()));
					case Tuple t -> {
						if (t.getFieldsCount() != this.value.getFieldsCount()) {
							return super.redirection(content);
						}
						t.forEachIndexed((i, fieldValue) -> this.boxField(i, fieldValue.copy()));
					}
				}
			}
			default -> super.redirection(content);
		}
		return this;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// arithmetic
	//////////////////////////////////////////////////////////////////////////////////////////////// boolean
	//////////////////////////////////////////////////////////////////////////////////////////////// relational

	@Override
	public GioviParserVisitResult equal(GioviParserVisitResult right) {
		return super.equal(right);
	}

	@Override
	public GioviParserVisitResult notEqual(GioviParserVisitResult right) {
		return equal(right).not();
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// access

	@Override
	public GioviParserVisitResult dot(GioviParserVisitResult right) {
		return switch (right) {
			case GioviDefault __ -> new GioviStruct(defaultValue.copy());
			case FieldKey(var identifier) -> value.getField(identifier);
			default -> super.dot(right);
		};
	}

	@Override
	public GioviParserVisitResult access(GioviParserVisitResult right) {
		return switch (right) {
			case AccessKey accessKey -> switch (accessKey) {
				case COUNT -> new GioviInt(value.getFieldsCount());
				case KEYS -> Range.of(0, value.getFieldsCount(), 1);
				case VALUES -> GioviLists.of(value.getFieldNames());
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

