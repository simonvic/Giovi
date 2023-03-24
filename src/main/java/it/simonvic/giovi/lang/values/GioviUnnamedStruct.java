package it.simonvic.giovi.lang.values;

public non-sealed class GioviUnnamedStruct extends GioviStruct {
	public GioviUnnamedStruct(Struct.Unnamed value, Struct.Unnamed defaultValue) {
		super(value, defaultValue);
	}

	public GioviUnnamedStruct(Struct.Unnamed value) {
		super(value, Struct.copyOf(value));
	}

	public GioviUnnamedStruct() {
		super(new Struct.Unnamed(), new Struct.Unnamed());
	}
}
