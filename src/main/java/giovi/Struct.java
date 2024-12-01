package giovi;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import giovi.lang.GioviFunction;
import giovi.lang.GioviValue;
import it.simonvic.giovi.VarD;
import it.simonvic.giovi.exceptions.NonWideneable;

public final class Struct extends TupleNamed {

	private final Map<String, GioviValue<?>> constants;
	private final Map<String, GioviFunction> functions;

	private final String typename;

	public Struct(
		String typename,
		LinkedHashMap<String, VarD<?>> fields,
		Map<String, GioviValue<?>> constants,
		Map<String, GioviFunction> functions) {
		super(fields);
		this.typename = typename;
		this.constants = constants;
		this.functions = functions;
	}

	public Struct(String typename, LinkedHashMap<String, VarD<?>> fields) {
		super(fields);
		this.typename = typename;
		this.constants = new HashMap<>();
		this.functions = new HashMap<>();
	}

	public Struct(String typename) {
		super();
		this.typename = typename;
		this.constants = new HashMap<>();
		this.functions = new HashMap<>();
	}

	public String getTypename() {
		return typename;
	}

	@Override
	public String toString() {
		return typename + "." + super.toString();
	}

	@Override
	public String toStringDefinition() {
		var i = this.entrySet().iterator();
		if (!i.hasNext()) return typename + "<>";
		StringBuilder sb = new StringBuilder(typename);
		sb.append('<');
		while (i.hasNext()) {
			var next = i.next();
			sb.append(next.getKey());
			sb.append(" ");
			sb.append(next.getValue().getGioviType());
			sb.append(i.hasNext() ? ", " : ">");
		}
		return sb.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(constants, functions, typename);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!super.equals(obj)) return false;
		if (getClass() != obj.getClass()) return false;
		Struct other = (Struct) obj;
		return Objects.equals(constants, other.constants)
			&& Objects.equals(functions, other.functions)
			&& Objects.equals(typename, other.typename);
	}

	@Override
	public Struct copy() {
		var a = new LinkedHashMap<String, VarD<?>>();
		forEach((identifier, value) -> a.put(identifier, value.copy()));
		return new Struct(typename, a, constants, functions);
	}

	@Override
	public Struct widenTo(Struct to) throws NonWideneable {
		if (!to.typename.equals(this.typename)) {
			throw new NonWideneable("Can't widen " + this.typename + " to " + to.typename);
		}
		return super.widenTo(to);
	}

	public boolean containsConstant(String identifier) {
		return constants.containsKey(identifier);
	}

	public GioviValue<?> getConstant(String identifier) {
		return constants.get(identifier);
	}

	public boolean containsFunction(String identifier) {
		return functions.containsKey(identifier);
	}

	public GioviFunction getFunction(String identifier) {
		return functions.get(identifier);
	}

	// TODO: we shouldn't expose these two; fix once we rework Tuple, Record and Struct
    public Map<String, GioviValue<?>> getConstants() {
        return constants;
    }

    public Map<String, GioviFunction> getFunctions() {
        return functions;
    }

}
