package it.simonvic.giovi.lang.values;

import java.util.LinkedHashMap;

import it.simonvic.giovi.exceptions.IncompatibleTypes;
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

	@Override
	public GioviTypeStruct getGioviType() {
		var fields = new LinkedHashMap<String, GioviFieldDefinition>();
		value.tuple().forEach((identifier, value) -> fields.put(
			identifier,
			new GioviFieldDefinition(
				identifier,
				value.getGioviType(),
				GioviValue.of(value.getDefaultValue()))));
		return new GioviTypeStruct(new GioviStructDefinition(value.typename(), fields));
	}

	@Override
	public GioviStruct copy() {
		return new GioviStruct(value.copy(), defaultValue.copy());
	}

	public GioviValue<?> getField(String identifier) {
		return value.getField(identifier);
	}

	public GioviValue<?> getField(int index) {
		return value.getField(index);
	}

	public void setField(String identifier, GioviValue<?> fieldValue) {
		this.value.setField(identifier, fieldValue);
	}

	public void setField(int index, GioviValue<?> fieldValue) {
		this.value.setField(index, fieldValue);
	}

	public int getFieldsCount() {
		return this.value.getFieldsCount();
	}

	public boolean containsField(String identifier) {
		return this.value.containsField(identifier);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// OPERATIONS
	//////////////////////////////////////////////////////////////////////////////////////////////// redirection

	@Override
	public GioviParserVisitResult redirection(GioviParserVisitResult content) {
		switch (content) {
			case GioviDefault v -> boxDefault();
			case GioviValue<?> v -> {
				switch (v.unbox()) {

					case Tuple.Named t -> {
						for (String fieldName : t.getFieldNames()) {
							if (!this.value.containsField(fieldName)) {
								throw new IncompatibleTypes(this.getGioviType(), v.getGioviType());
							}
							var thisField = this.value.getField(fieldName);
							var otherField = t.getField(fieldName);
							var thisFieldType = thisField.getGioviType();
							var otherFieldType = otherField.getGioviType();
							if (thisFieldType.equals(otherFieldType)) {
								thisField.box(otherField.copy());
								continue;
							}
							if (thisFieldType.canUpcast(otherFieldType)) {
								thisField.box(otherField.castTo(thisFieldType));
								continue;
							}
							throw new IncompatibleTypes(this.getGioviType(), v.getGioviType());
						}
					}

					case Tuple.Unnamed t -> {
						if (this.value.getFieldsCount() != t.getFieldsCount()) {
							throw new IncompatibleTypes(this.getGioviType(), v.getGioviType());
						}
						for (int i = 0; i < this.value.getFieldsCount(); i++) {
							var thisField = this.value.getField(i);
							var otherField = t.getField(i);
							var thisFieldType = thisField.getGioviType();
							var otherFieldType = otherField.getGioviType();
							if (thisFieldType.equals(otherFieldType)) {
								thisField.box(otherField.copy());
								continue;
							}
							if (thisFieldType.canUpcast(otherFieldType)) {
								thisField.box(otherField.castTo(thisFieldType));
								continue;
							}
							throw new IncompatibleTypes(this.getGioviType(), v.getGioviType());
						}
					}
					default -> super.redirection(content);
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
		return switch (right) {
			case GioviStruct r -> {
				if (this.value.getFieldsCount() != r.value.getFieldsCount()) {
					yield new GioviBool(false);
				}
				var thisFieldNames = this.value.getFieldNames();
				for (String fieldName : thisFieldNames) {
					if (!r.containsField(fieldName)) {
						yield new GioviBool(false);
					}
					var thisField = this.value.getField(fieldName);
					var otherField = r.value.getField(fieldName);
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
					yield new GioviStruct(this.value.copy((Range<Integer>) range)); // TODO: RangeInteger
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

