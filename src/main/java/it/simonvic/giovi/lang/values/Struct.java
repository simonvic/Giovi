package it.simonvic.giovi.lang.values;

import java.util.LinkedHashMap;

public class Struct {

	private final LinkedHashMap<String, GioviValue<?>> attributes;

	public Struct() {
		this.attributes = new LinkedHashMap<>();
	}

	public Struct(LinkedHashMap<String, GioviValue<?>> attributes) {
		this.attributes = attributes;
	}

	public void put(String identifier, GioviValue<?> value) {
		attributes.put(identifier, value);
	}

	public GioviValue<?> get(String identifier) {
		return attributes.get(identifier);
	}


	@Override
	public String toString() {
		var i = attributes.entrySet().iterator();
		if (!i.hasNext())
			return "()";

		StringBuilder sb = new StringBuilder();
		sb.append('(');
		for (; ; ) {
			var e = i.next();
			var key = e.getKey();
			var value = e.getValue();
			sb.append(key);
			sb.append(':');
			sb.append(value);
			if (!i.hasNext())
				return sb.append(')').toString();
			sb.append(',').append(' ');
		}
	}

}
