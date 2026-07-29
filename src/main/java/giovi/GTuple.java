package giovi;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import it.simonvic.giovi.GField;
import it.simonvic.giovi.exceptions.NoSuchField;
import it.simonvic.giovi.exceptions.OperatorNotSupported;

public sealed class GTuple implements GProduct permits GRecord {

	private final List<GField<?>> fields;
	// TODO: should we use Var instead? Same for record?
	// Probably not. What about when we'll implement Tuple type?
	// A function might return => <Int default 69, Int default 420> for example

	public GTuple() {
		fields = new ArrayList<>();
	}

	public GTuple(List<GField<?>> fields) {
		this.fields = fields;
	}

	public GTuple(GField<?>... fields) {
		this.fields = new ArrayList<>(Arrays.asList(fields));
	}

	@Override
	public String toString() {
		var i = fields.iterator();
		if (!i.hasNext()) return "<>";

		StringBuilder sb = new StringBuilder();
		sb.append('<');
		while (i.hasNext()) {
			sb.append(i.next());
			sb.append(i.hasNext() ? ", " : ">");
		}
		return sb.toString();
	}

	@Override
	public int hashCode() {
		return Objects.hash(fields);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		GTuple other = (GTuple) obj;
		return Objects.equals(fields, other.fields);
	}

	@Override
	public GTuple copy() {
		var a = new ArrayList<GField<?>>(fields.size());
		this.fields.forEach(value -> a.add(value.copy()));
		return new GTuple(a);
	}

	public GTuple copy(Range<Integer> range) {
		var list = new ArrayList<GField<?>>();
		for (var index : range) {
			list.add(
				getField(index)
					.orElseThrow(() -> new NoSuchField(index))
					.copy()
			);
		}
		return new GTuple(list);
	}

	@Override
	public GTuple as(TupleSignature signature) {
		var casted = signature.spawn();
		for (int i = 0; i < signature.fields().size(); i++) {
			var castedField = casted.getField(i).orElseThrow();
			if (i < this.getFieldsCount()) {
				this.getField(i).ifPresent(
					field -> castedField.setValueWithRedirection(
						field.as(castedField.getGioviType())
					)
				);
			}
		}
		return casted;
	}

	@Override
	public GRecord as(RecordSignature signature) {
		var casted = signature.spawn();
		for (int i = 0; i < signature.fields().size(); i++) {
			var castedField = casted.getField(i).orElseThrow();
			if (i < this.getFieldsCount()) {
				this.getField(i).ifPresent(
					field -> castedField.setValueWithRedirection(
						field.as(castedField.getGioviType())
					)
				);
			}
		}
		return casted;
	}

	@Override
	public GStruct as(StructSignature signature) {
		var casted = signature.spawn();
		for (int i = 0; i < signature.fields().size(); i++) {
			var castedField = casted.getField(i).orElseThrow();
			if (i < this.getFieldsCount()) {
				this.getField(i).ifPresent(
					field -> castedField.setValueWithRedirection(
						field.as(castedField.getGioviType())
					)
				);
			}
		}
		return casted;
	}


	public int getFieldsCount() {
		return fields.size();
	}

	public Optional<GField<?>> getField(int index) {
		return Optional.ofNullable(fields.get(index));
	}

	public void forEach(Consumer<GField<?>> valueConsumer) {
		fields.forEach(valueConsumer);
	}

	public void forEachIndexed(BiConsumer<Integer, GField<?>> valueConsumer) {
		for (int i = 0; i < fields.size(); i++) {
			valueConsumer.accept(i, fields.get(i));
		}
	}

	// TODO: clarify requirements. Should a Point.<x y z> be equal to a Color.<r g b>?
	// FIXME: also using this.equal(Tuple) is wrong
	public boolean equal(GStruct other) {
		return this.equal((GTuple) other);
	}

	public boolean equal(GRecord other) {
		return this.equal((GTuple) other);
	}

	public boolean equal(GTuple other) {
		if (this.fields.size() != other.fields.size()) return false;
		for (int i = 0; i < fields.size(); i++) {
			var thisField = this.fields.get(i);
			var otherField = other.fields.get(i);
			try {
				if (!thisField.equal(otherField).asNativeBool()) {
					return false;
				}
			} catch (OperatorNotSupported e) {
				return false;
			}
		}
		return true;
	}

}
