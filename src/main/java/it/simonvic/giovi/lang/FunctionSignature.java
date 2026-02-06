package it.simonvic.giovi.lang;

import java.util.ArrayList;
import java.util.List;

import giovi.GioviParserVisitResult;
import giovi.lang.GioviType;
import giovi.lang.GioviVoid;
import it.simonvic.giovi.exceptions.NonWideneable;

public record FunctionSignature(
	String identifier,
	FormalParameters parameters,
	GioviType<?> returnType
) implements GioviParserVisitResult {

	public static final FunctionSignature EMPTY = new FunctionSignature(
		"",
		new FormalParameters(),
		GioviType.VOID);

	public FunctionSignature(String identifier) {
		this(identifier, new FormalParameters(), GioviType.VOID);
	}

	public FunctionSignature(String identifier, GioviType<?> returnType) {
		this(identifier, new FormalParameters(), returnType);
	}

	public FunctionSignature(
		String identifier,
		FormalParameters parameters
	) {
		this(identifier, parameters, GioviType.VOID);
	}

	@Override
	public String toString() {
		var sb = new StringBuilder(identifier);
		sb.append('(');
		var i = parameters.parameters().values().iterator();
		if (!i.hasNext()) {
			sb.append(')');
		} else {
			while (true) {
				var param = i.next();
				sb.append(param.toString());
				if (!i.hasNext()) {
					sb.append(')');
					break;
				}
				sb.append(", ");
			}
		}
		sb.append(" => ");
		sb.append(returnType);
		return sb.toString();
	}

	public List<FormalParameter> getParametersList() {
		return new ArrayList<>(parameters.parameters().values());
	}

	public boolean hasParameter(String identifier) {
		return parameters.parameters().containsKey(identifier);
	}

	public boolean hasParameterLabeled(String identifier) {
		return parameters.byLabel().containsKey(identifier);
	}

	public FormalParameter getParameterLabeled(String label) {
		return parameters.byLabel().get(label);
	}

	public FunctionSignature copy() {
		return new FunctionSignature(identifier, parameters.copy(), returnType.copy());
	}

	public FormalParameter getParameter(String identifier) {
		return parameters.parameters().get(identifier);
	}

	/**
	 * Check if the provided signature can be widened to this signature
	 * @param narrow
	 * @return true if can be widened
	 */
	public boolean canWiden(FunctionSignature narrow) {
		return this.parameters.canWiden(narrow.parameters)
			&& narrow.returnType.isWidenableTo(this.returnType);
	}

	/**
	 * Widen the provided signature to this signature
	 * @param narrow
	 * @return
	 * @throws NonWideneable
	 */
	public FunctionSignature widen(FunctionSignature narrow) throws NonWideneable {
		if (!narrow.returnType.isWidenableTo(this.returnType)) {
			throw new NonWideneable(
				"Can't widen return type " + narrow.returnType + " to " + this.returnType);
		}
		return new FunctionSignature(
			this.identifier,
			this.parameters.widen(narrow.parameters),
			this.returnType); // TODO: this should be this.returnType.getGioviType().widen(narrow.returnType));
	}

}
