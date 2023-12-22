package it.simonvic.giovi.lang.values;

import java.util.LinkedHashMap;

import it.simonvic.giovi.lang.*;
import it.simonvic.giovi.lang.values.natives.Struct;
import it.simonvic.giovi.lang.values.natives.Tuple;
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
		value.getField(index).box(fieldValue);
	}

	public void boxField(String field, GioviValue<?> fieldValue) {
		value.getField(field).box(fieldValue);
	}

	public boolean containsField(String field) {
		return value.containsField(field);
	}

	@Override
	public void box(GioviValue<?> gioviValue) {
		switch (gioviValue) {
			case GioviStruct s -> this.value = s.unbox();
			case GioviTuple gioviTuple -> {
				if (!gioviTuple.value.isSubset(this.value)) {
					super.box(gioviValue);
					return;
				}
				switch (gioviTuple.unbox()) {
					case Tuple.Named t -> {
						t.forEach((identifier, fieldValue) -> this.boxField(identifier,
							fieldValue.copy()));
					}
					case Tuple t -> {
						if (t.getFieldsCount() != this.value.getFieldsCount()) {
							super.box(gioviValue);
							return;
						}
						t.forEachIndexed((i, fieldValue) -> this.boxField(i, fieldValue.copy()));
					}
				}
			}
			default -> super.box(gioviValue);
		}
	}

	@Override
	public GioviTypeStruct getGioviType() {
		var fields = new LinkedHashMap<String, GioviFieldDefinition>();
		this.value.forEach((identifier, fieldValue) -> fields.put(
			identifier,
			new GioviFieldDefinition(
				identifier,
				fieldValue.getGioviType(),
				GioviValue.of(fieldValue.getDefaultValue()))));
		return new GioviTypeStruct(new GioviStructDefinition(this.value.getTypename(), fields));
	}

	@Override
	public GioviStruct copy() {
		return new GioviStruct(value.copy(), defaultValue.copy());
	}

	@Override
	public GioviValue<?> castTo(GioviType<?> type) {
		var thisType = this.getGioviType();
		if (!thisType.canUpcast(type)) {
			return super.castTo(type);
		}
		return switch (type) {
			case GioviTypeStruct otherType -> {
				var castedTuple = otherType.spawn();
				this.value.forEach((identifier, fieldValue) -> castedTuple
					.boxField(identifier, fieldValue.copy()));
				yield castedTuple;
			}
			default -> super.castTo(type);
		};
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// OPERATIONS
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
					case Tuple.Named t -> {
						t.forEach((identifier, fieldValue) -> this.boxField(identifier,
							fieldValue.copy()));
					}
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
			case AccessKey r -> value.getField(r.text());
			default -> super.dot(right);
		};
	}

	@Override
	public GioviParserVisitResult access(GioviParserVisitResult right) {
		return switch (right) {
			case AccessKey r -> switch (r.text()) {
				case "%" -> new GioviInt(value.getFieldsCount());
				case "*" -> GioviLists.of(value.getFieldNames());
				case "@" -> Range.of(0, value.getFieldsCount(), 1);
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

