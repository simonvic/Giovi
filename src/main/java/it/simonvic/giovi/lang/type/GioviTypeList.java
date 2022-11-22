package it.simonvic.giovi.lang.type;

import it.simonvic.giovi.lang.values.GioviList;

public class GioviTypeList extends GioviType {

	private GioviType entryType;

	public GioviTypeList(GioviType entryType) {
		super(GioviList.class);
		this.entryType = entryType;
	}

	public GioviType getEntryType() {
		return entryType;
	}

	@Override
	public String toString() {
		return super.toString() + "<" + entryType + ">";
	}
}
