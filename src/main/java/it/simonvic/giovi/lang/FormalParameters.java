package it.simonvic.giovi.lang;

import java.util.LinkedHashMap;

import giovi.GioviParserVisitResult;
import it.simonvic.giovi.exceptions.NonWideneable;

public record FormalParameters(
	LinkedHashMap<String, FormalParameter> parameters,
	LinkedHashMap<String, FormalParameter> byLabel
) implements GioviParserVisitResult {

	public FormalParameters(LinkedHashMap<String, FormalParameter> parameters) {
		this(parameters, new LinkedHashMap<>());
		for (var p : parameters.values()) {
			if (this.byLabel.put(p.label(), p) != null) {
				throw new IllegalArgumentException("Duplicate formal parameter label " + p.label());
			}
		}
	}

	public FormalParameters(FormalParameter... parameters) {
		this(new LinkedHashMap<>(), new LinkedHashMap<>());
		for (var p : parameters) {
			if (this.parameters.put(p.identifier(), p) != null) {
				throw new IllegalArgumentException("Duplicate formal parameter " + p.identifier());
			}
			if (this.byLabel.put(p.label(), p) != null) {
				throw new IllegalArgumentException("Duplicate formal parameter label " + p.label());
			}
		}
	}

	public FormalParameters copy() {
		var params = new LinkedHashMap<String, FormalParameter>();
		parameters.forEach((identifier, param) -> params.put(identifier, param.copy()));
		return new FormalParameters(params);
	}

	/**
	 * Check if the provided formal parameters can be widened to this formal parameters types
	 * @param narrow
	 * @return true if can be widened, false otherwise
	 */
	public boolean canWiden(FormalParameters narrow) {
		if (narrow.parameters.size() > this.parameters.size()) {
			return false;
		}
		if (!this.parameters.keySet().containsAll(narrow.parameters.keySet())) {
			return false;
		}
		for (var widerParameter : this.parameters.values()) {
			if (!narrow.parameters.containsKey(widerParameter.identifier())) {
				continue;
			}
			var narrowParameter = narrow.parameters.get(widerParameter.identifier());
			if (!widerParameter.isOptional() && narrowParameter.isOptional()) {
				return false; // can't widen an optional to a mandatory param
			}
			if (!narrowParameter.type().isWidenableTo(widerParameter.type())) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Widen the provided formal parameters to this formal parameters
	 * @param narrow
	 * @return widened formal parameters
	 * @throws NonWideneable
	 */
	public FormalParameters widen(FormalParameters narrow) throws NonWideneable {
		var narrowParams = narrow.parameters;
		if (narrowParams.size() > this.parameters.size()) {
			throw new NonWideneable("Signature is wider than signature");
		}
		if (!this.parameters.keySet().containsAll(narrowParams.keySet())) {
			throw new NonWideneable(
				"Signature has parameter not contained in widening target signature"
			);
		}
		var widenedParams = new LinkedHashMap<String, FormalParameter>();
		for (var widerParam : this.parameters.values()) {
			var widerIdentifier = widerParam.identifier();
			var widerType = widerParam.type();
			// TODO: clarify requirements about optionality and labels
			widenedParams.put(
				widerParam.identifier(),
				new FormalParameter(
					widerParam.label(),
					widerIdentifier,
					widerType,
					narrowParams.containsKey(widerIdentifier)
						? narrowParams.get(widerIdentifier).defaultValue().widenTo(widerType)
						: widerParam.defaultValue().copy(),
					widerParam.isOptional()
				)
			);
		}
		return new FormalParameters(widenedParams);
	}

}
