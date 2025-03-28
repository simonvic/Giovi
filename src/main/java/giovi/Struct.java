package giovi;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import giovi.lang.GioviFunction;
import giovi.lang.GioviValue;
import it.simonvic.giovi.VarD;

public final class Struct extends Record {

	// TODO: constants and functions should be associated to the type rather than to the instance
	private final Map<String, GioviValue<?>> constants;
	private final Map<String, GioviFunction> functions;

	private final String typename;

	public Struct(
		String typename,
		LinkedHashMap<String, VarD<?>> fields,
		Map<String, GioviValue<?>> constants,
		Map<String, GioviFunction> functions
	) {
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
		return Objects.equals(typename, other.typename)
			&& Objects.equals(constants, other.constants)
			&& Objects.equals(functions, other.functions);
	}

	@Override
	public Struct copy() {
		var a = new LinkedHashMap<String, VarD<?>>();
		forEach((identifier, value) -> a.put(identifier, value.copy()));
		return new Struct(typename, a, constants, functions);
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
