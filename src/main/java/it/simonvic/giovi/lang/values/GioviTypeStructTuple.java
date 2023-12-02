package it.simonvic.giovi.lang.values;

import it.simonvic.giovi.lang.primitives.Type;

public final class GioviTypeStructTuple extends GioviTypeStruct<Type.Struct.Tuple> {

	public GioviTypeStructTuple(Type.Struct.Tuple value, Type.Struct.Tuple defaultValue) {
		super(value, defaultValue);
	}

	public GioviTypeStructTuple(Type.Struct.Tuple value) {
		this(value, value.copy());
	}

	public GioviTypeStructTuple() {
		this(new Type.Struct.Tuple(), new Type.Struct.Tuple());
	}


	@Override
	public GioviValue<?> spawn() {
		throw new UnsupportedOperationException("Unimplemented method 'spawn'");
	}

	@Override
	public GioviType<Type.Struct.Tuple> copy() {
		return new GioviTypeStructTuple(value.copy(), defaultValue.copy());
	}

	@Override
	public boolean canUpcast(GioviType<?> to) {
		return false;
	}

}

