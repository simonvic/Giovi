package it.simonvic.giovi.lang;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import giovi.GioviParserVisitResult;
import giovi.lang.GioviType;
import giovi.lang.GioviVoid;
import it.simonvic.giovi.antlr.GioviParser.BlockContext;
import it.simonvic.giovi.util.GioviTypes;

public record GioviFunctionSignature(
	String identifier,
	BlockContext body, // TODO: when implementing GioviFunction value, remove the body from the signature
	GioviType<?> returnType,
	LinkedHashMap<String, GioviFunctionSignatureParameter> parameters)
	implements GioviParserVisitResult {

	public GioviFunctionSignature(String identifier, GioviType<?> returnType, BlockContext body) {
		this(identifier, body, returnType, new LinkedHashMap<>());
	}

	public GioviFunctionSignature(
		String identifier,
		LinkedHashMap<String, GioviFunctionSignatureParameter> parameters,
		BlockContext body) {
		this(identifier, body, GioviTypes.of(GioviVoid.class), parameters);
	}

	public GioviFunctionSignature(String identifier, BlockContext body) {
		this(identifier, body, GioviTypes.of(GioviVoid.class), new LinkedHashMap<>());
	}

	public GioviFunctionSignature(
		String identifier,
		LinkedHashMap<String, GioviFunctionSignatureParameter> parameters,
		GioviType<?> returnType,
		BlockContext body) {
		this(identifier, body, returnType, parameters);
	}

	public List<GioviFunctionSignatureParameter> getParametersList() {
		return new ArrayList<>(parameters.values());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((identifier == null) ? 0 : identifier.hashCode());
		result = prime * result + ((returnType == null) ? 0 : returnType.hashCode());
		result = prime * result + ((parameters == null) ? 0 : parameters.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		GioviFunctionSignature other = (GioviFunctionSignature) obj;
		if (identifier == null) {
			if (other.identifier != null) return false;
		} else if (!identifier.equals(other.identifier)) return false;
		if (returnType == null) {
			if (other.returnType != null) return false;
		} else if (!returnType.equals(other.returnType)) return false;
		if (parameters == null) {
			if (other.parameters != null) return false;
		} else if (!parameters.equals(other.parameters)) return false;
		return true;
	}

}
